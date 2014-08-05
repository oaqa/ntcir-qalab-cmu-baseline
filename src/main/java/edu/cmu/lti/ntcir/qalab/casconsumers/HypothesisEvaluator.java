package edu.cmu.lti.ntcir.qalab.casconsumers;

import edu.cmu.lti.ntcir.qalab.types.*;
import edu.cmu.lti.ntcir.qalab.utils.Utils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import java.io.IOException;
import java.util.ArrayList;

public class HypothesisEvaluator extends CasConsumer_ImplBase {

	static int correct = 0;
	static int total = 0;
	static int noAssert = 0;
	static ArrayList<String> correctAnswers = new ArrayList<String>();
	static ArrayList<String> predictedAnswers = new ArrayList<String>();
	static ArrayList<Double> predictedScores = new ArrayList<Double>();

	public void initialize() throws ResourceInitializationException {
		super.initialize();
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas = aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator<Annotation> it = jcas.getAnnotationIndex(TestDocument.type)
				.iterator();

		while (it.hasNext()) {
			TestDocument doc = (TestDocument) it.next();

			ArrayList<QuestionAnswerSet> qaList = Utils.fromFSListToCollection(
					doc.getQAList(), QuestionAnswerSet.class);

			for (int i = 0; i < qaList.size(); i++) {

				QuestionAnswerSet qns = qaList.get(i);


				System.out
						.println("================================================");
				// String question = qaList.get(i).getQuestion().getId();
				/*
				 * if(!qaList.get(i).getQuestion().getQuestionType().
				 * equalsIgnoreCase("sentence")){ continue; }
				 */



				ArrayList<AnalyzedAnswerChoice> hypotheses = Utils
						.fromFSListToCollection(qns.getAnalyzedAnswerChoiceList(),
								AnalyzedAnswerChoice.class);

				ArrayList<AnswerChoice> correctList = Utils
						.fromFSListToCollection(qaList.get(i)
								.getAnswerChoiceList(), AnswerChoice.class);


				double maxScore = Double.NEGATIVE_INFINITY;
				String maxHypothesisAnsId = "NoScoredAssert";
				boolean hasAssert = false;
				for (int j = 0; j < hypotheses.size(); j++) {
					AnalyzedAnswerChoice oneAnalyzedAnswerChoice = hypotheses.get(j);
					ArrayList<Assertion> assertionList = Utils
							.fromFSListToCollection(
									oneAnalyzedAnswerChoice.getAssertionList(),
									Assertion.class);

					//int choiceIdx=Integer.parseInt(oneAnalyzedAnswerChoice.getAnsChoiceId());
					//AnswerChoice ansChoice=correctList.get(choiceIdx);

					double score = 0;
					double negativity=1.0;
					for (Assertion assertion : assertionList) {
						hasAssert = true;
						if (null == assertion.getAssertScoreList()) {
							continue;
						}
            float scoreSign = assertion.getIsAffirmative() ? 1 : - 1;

						ArrayList<AssertionScore> assertionScoreList = Utils
								.fromFSListToCollection(
										assertion.getAssertScoreList(),
										AssertionScore.class);

						for (AssertionScore hypoScore : assertionScoreList) {
							String source = hypoScore.getComponentId();
							Double srcScore = scoreSign * hypoScore.getScore();

							if (source.startsWith("simple-evidence")) {
								score += srcScore * 1.0;// weight 1.0 for simple
														// evidence
							}else {
								System.out
										.println("Got non simple-evidence source:"
												+ source
												+ " with score:"
												+ srcScore);
								score += srcScore*1.0;
							}
						}
					}
					System.out.println(
                  oneAnalyzedAnswerChoice.getQId()+"\t"+score+"\t"+ oneAnalyzedAnswerChoice.getAnsChoiceId());
					if (score > maxScore) {
						maxScore = score;
						maxHypothesisAnsId = oneAnalyzedAnswerChoice.getAnsChoiceId();
					}
				}

				for (int j = 0; j < correctList.size(); j++) {

					if (correctList.get(j).getIsCorrect()) {
						String correctId = correctList.get(j).getId();
						String displayCorrectId = qns.getQuestion().getId()
								+ "-" + correctId;
						if (hasAssert) {
							if (correctId.equalsIgnoreCase(maxHypothesisAnsId)) {
								correct++;
							}
							predictedAnswers.add(qns.getQuestion().getId()
									+ "-" + maxHypothesisAnsId);
							predictedScores.add(maxScore);

							correctAnswers.add(displayCorrectId);
							total++;
						} else {
							noAssert++;
							predictedAnswers.add(qns.getQuestion().getId()
									+ "-" + maxHypothesisAnsId);
							predictedScores.add(maxScore);
							correctAnswers.add("NoAssert-" + displayCorrectId);
						}
					}

					if(correctList.get(j).getId().equalsIgnoreCase(maxHypothesisAnsId)){
						correctList.get(j).setIsSelected(true);
					}
				}
				qaList.get(i).setAnswerChoiceList(Utils.fromCollectionToFSList(jcas, correctList));
			}

		}

	}

	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);

		System.out.println("Correct: " + correct + "/" + total);
		System.out.println("No assertions: " + noAssert);

		System.out.println("Accuracy: " + (correct / (double) total));

		System.out.println("================================");
		System.out.println("Answers\nCorrect\t\t/\tGiven:\tmaxScore");
		System.out.println("--------------------------------");

		for (int i = 0; i < correctAnswers.size(); ++i) {
			System.out.println(correctAnswers.get(i) + "\t/\t"
					+ predictedAnswers.get(i) +"\t" + predictedScores.get(i));
		}

		System.out.println();

    System.out.println("Data for p-value computation. The first row has true question answers, the second row has predicted answers.");

    for (int i = 0; i < correctAnswers.size(); ++i) {
      System.out.print(correctAnswers.get(i) +  " ");
    }
    System.out.println();
    for (int i = 0; i < correctAnswers.size(); ++i) {
      System.out.print(predictedAnswers.get(i) +  " ");
    }
    System.out.println();

	}

}
