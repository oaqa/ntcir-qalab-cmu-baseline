package edu.cmu.lti.ntcir.qalab.annotators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.cmu.lti.ntcir.qalab.types.*;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.ntcir.qalab.types.AnalyzedAnswerChoice;

import edu.cmu.lti.ntcir.qalab.utils.Utils;

public class SimpleHypothesisScorer extends JCasAnnotator_ImplBase {

	SolrServer solrServer = null;
	String serverUrl;
	// IndexSchema indexSchema;
	String coreName;
	String schemaName;
	int TOP_SEARCH_RESULTS = 10;
	HashSet<String> stopwords = new HashSet<String>();
	static boolean DEBUG_INFO = false;
	float assertSlopeCoeff = 3;

	public HashSet<String> loadStopWords(String stopfile) throws Exception {
		BufferedReader bfr = new BufferedReader(new FileReader(stopfile));
		String str;
		HashSet<String> hshStopWords = new HashSet<String>();
		while ((str = bfr.readLine()) != null) {
			hshStopWords.add(str.trim());
		}

		bfr.close();
		bfr = null;
		return hshStopWords;
	}

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		serverUrl = (String) context.getConfigParameterValue("SOLR_SERVER_URL");
		coreName = (String) context.getConfigParameterValue("SOLR_CORE");
		schemaName = (String) context.getConfigParameterValue("SCHEMA_NAME");
		TOP_SEARCH_RESULTS = (Integer) context
				.getConfigParameterValue("TOP_SEARCH_RESULTS");
		try {
			this.solrServer = new HttpSolrServer(serverUrl + coreName);
			this.stopwords = loadStopWords("./data/stopwords.txt");

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Server url: " + serverUrl);

	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		TestDocument testDoc = Utils.getTestDocumentFromCAS(aJCas);
		try {
			FSList qaList = testDoc.getQAList();
			ArrayList<QuestionAnswerSet> qaSet = Utils.fromFSListToCollection(
					qaList, QuestionAnswerSet.class);

			for (int i = 0; i < qaSet.size(); i++) {

				Question question = qaSet.get(i).getQuestion();
				Instruction instruction = question.getInstruction();
				String questionId = question.getId();
				ArrayList<AnalyzedAnswerChoice> analyzedAnswerChoiceList = Utils
						.fromFSListToCollection(qaSet.get(i)
								.getAnalyzedAnswerChoiceList(), AnalyzedAnswerChoice.class);

				System.out
						.println("========================================================");
				System.out.println("qid: " + questionId + "type: "
						+ question.getQuestionType());

				String text = question.getContextData().getText();

				// System.out.println(text);

				text = text.replaceAll("[\\n\\r ]+", " ").trim();

				ArrayList<AnswerChoice> ansChoiceList = Utils
						.fromFSListToCollection(qaSet.get(i)
								.getAnswerChoiceList(), AnswerChoice.class);
				for (int j = 0; j < analyzedAnswerChoiceList.size(); j++) {
					AnalyzedAnswerChoice analyzedAnswerChoice = analyzedAnswerChoiceList.get(j);
					String topic = analyzedAnswerChoice.getTopic();
					String specificContext = analyzedAnswerChoice.getSpecificContext();
					String questionType = analyzedAnswerChoice.getQuestionType();

					String ansId = analyzedAnswerChoice.getAnsChoiceId().replace("(", "")
							.replace(")", "").trim();

					ArrayList<Assertion> assertionList = Utils
							.fromFSListToCollection(
									analyzedAnswerChoice.getAssertionList(),
									Assertion.class);

					for (int k = 0; k < assertionList.size(); k++) {
						Assertion assertion = assertionList.get(k);
						HashMap<String, String> hshParams = new HashMap<String, String>();

						String qryText = assertion.getText()
								.replaceAll(":", "").replace("[", "")
								.replace("]", "").replace("\"", "").trim();

						String assertionText = cleanQuestion(assertion
								.getText().trim());

						if (DEBUG_INFO) {
							System.out.println("Question id: " + questionId
									+ "\nInstruction: " + instruction
									+ "\nAssertion text : " + assertionText);
						}

						ArrayList<AssertionScore> assertionScoreList = new ArrayList<AssertionScore>();

						if (assertion.getAssertScoreList() == null) {
							assertionScoreList = new ArrayList<AssertionScore>();
						} else {
							assertionScoreList = Utils.fromFSListToCollection(
									assertion.getAssertScoreList(),
									AssertionScore.class);
						}

						qryText = qryText.replaceAll("[.]$", "").trim();
						// ///////////////////////////////////

						hshParams.put("q", qryText);
						hshParams.put("rows",
								String.valueOf(TOP_SEARCH_RESULTS));
						hshParams.put("fl", "ID,score");
						SolrParams solrParams = new MapSolrParams(hshParams);
						QueryResponse qryResponse = solrServer.query(
								solrParams, METHOD.POST);
						SolrDocumentList results = qryResponse.getResults();
						// System.out.println(results.getNumFound() + "\t"
						// + results.getMaxScore() + "\t" +
						// assertion.getText());

						// finalChoiceScore=results.getMaxScore();
						double finalChoiceScore = (5.423054523084871 + (results
								.getNumFound() * 2.233913410772671E-4) + (results
								.getMaxScore() * 4.371154110186279));

						AssertionScore assertScore = new AssertionScore(aJCas);
						assertScore.setComponentId("simple-evidence-subset");
						assertScore.setScore(finalChoiceScore);
						// System.out.println("Tuple: "+(results.getNumFound()/11217.0)+"\t"+results.getMaxScore()+"\t"+finalChoiceScore+"\t"+label+"\t"+questionType);

						if (assertion.getAssertScoreList() == null) {
							assertionScoreList = new ArrayList<AssertionScore>();
						} else {
							assertionScoreList = Utils.fromFSListToCollection(
									assertion.getAssertScoreList(),
									AssertionScore.class);
						}
						assertionScoreList.add(assertScore);
						assertion.setAssertScoreList(Utils
								.fromCollectionToFSList(aJCas,
										assertionScoreList));

						assertionList.set(k, assertion);
					}

					analyzedAnswerChoice.setAssertionList(Utils.fromCollectionToFSList(
							aJCas, assertionList));
					analyzedAnswerChoiceList.set(j, analyzedAnswerChoice);
				}

				System.out
						.println("=========================================================");

			}

		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private SearchRes scoreQuery(String assertionText, String assertDesc, // e.g.
			// assertion
			// 1,
			// or
			// context
			String fieldName, int matchPct, // how many words should be present
			float slopeCoeff) throws SolrServerException {

		if (assertionText.isEmpty()) {
			System.out.println("Empty " + assertDesc);
			return new SearchRes(0f, 0f, 0);
		}
		int topSearchResult = 10;

		QueryParseRes q = createBagOfWordQuery(assertionText);

		String query = String.format(
				"_query_: \"{!edismax df=%s mm=%d%c pf=%s ps=%d} %s \"",
				fieldName, matchPct, '%', fieldName,
				(int) Math.round(slopeCoeff * q.wordQty), q.query);

		HashMap<String, String> hshParams = new HashMap<String, String>();

		if (DEBUG_INFO) {
			System.out.println("Query: " + query);
		}

		hshParams.put("q", query);
		hshParams.put("rows", String.valueOf(topSearchResult));
		hshParams.put("fl", "ID,score");
		SolrParams solrParams = new MapSolrParams(hshParams);
		QueryResponse qryResponse = solrServer.query(solrParams, METHOD.POST);
		SolrDocumentList results = qryResponse.getResults();

		float discScore = 0f; // discounted score

		float discountCoeff = 1;

		for (int i = 0; i < Math.min(results.getNumFound(), topSearchResult); ++i) {
			float v = (Float) results.get(i).getFieldValue("score");
			discScore += v * discountCoeff;
			discountCoeff *= 0.5;
		}

		SearchRes res = new SearchRes(results.getMaxScore(), discScore,
				results.getNumFound());

		return res;
	}

	QueryParseRes createBagOfWordQuery(String question) {
		ArrayList<String> nostop = getQueryWords(question);

		StringBuilder res = new StringBuilder();

		for (int i = 0; i < nostop.size(); ++i) {
			if (i > 0)
				res.append(' ');
			res.append(nostop.get(i));
		}

		return new QueryParseRes(res.toString(), nostop.size());
	}

	ArrayList<String> getQueryWords(String question) {
		String[] qWords = question.split(" +");

		ArrayList<String> nostop = new ArrayList<String>();

		for (String s : qWords) {
			if (!stopwords.contains(s)) {
				nostop.add(s);
			}
		}

		return nostop;
	}

	private String cleanQuestion(String question) {
		question = question.replaceAll("[①②③④⑤]", " ");
		question = question.replaceAll("[(][0-9]*[)]", " ");
		question = question.replaceAll("[(][a-zA-Z][)]", " ");
		question = question.replaceAll("[\"()\\[\\]:]", " ");
		question = question.replaceAll("[\\n\\r ]+", " ");

		return question.trim().toLowerCase();
	}

	public String preProcess(String query) throws Exception {

		String preProcessed = query.replaceAll("[(][A-Za-z0-9]+[)]", "").trim();

		String words[] = preProcessed.split("[\\W]");

		preProcessed = "";
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() < 2
					|| stopwords.contains(words[i].toLowerCase().trim())) {
				continue;
			}
			preProcessed += words[i] + " ";
		}
		return preProcessed.trim();
	}

	public ArrayList<String> getNGrams(String text, int maxGrams,
			String delimeter) throws Exception {

		String preprocessedText = "";
		String words[] = text.split(delimeter);
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() < 1
					|| stopwords.contains(words[i].toLowerCase())) {
				continue;
			}
			preprocessedText += words[i] + " ";
		}
		preprocessedText = preprocessedText.trim();

		ArrayList<String> allNGrams = new ArrayList<String>();
		for (int i = 1; i <= maxGrams; i++) {
			allNGrams.addAll(ngrams(i, preprocessedText, delimeter));
		}
		return allNGrams;
	}

	public static ArrayList<String> ngrams(int n, String str, String delimeter) {
		ArrayList<String> ngrams = new ArrayList<String>();
		// str=str.replaceAll(delimeter, " ").trim();
		String[] rawwords = str.split(delimeter);

		String newStr = "";
		for (int i = 0; i < rawwords.length; i++) {
			if (rawwords[i].trim().equals("")) {
				continue;
			}
			newStr += rawwords[i] + " ";
		}
		String words[] = newStr.split(delimeter);
		for (int i = 0; i < words.length - n + 1; i++) {
			ngrams.add(concat(words, i, i + n));
		}
		return ngrams;
	}

	public static String concat(String[] words, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++) {

			sb.append((i > start ? " " : "") + words[i].trim());
		}
		return sb.toString();
	}

}
