package edu.cmu.lti.ntcir.qalab.annotators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.ntcir.qalab.types.*;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ConfigurationParameter;

import edu.cmu.lti.ntcir.qalab.types.AnalyzedAnswerChoice;
import edu.cmu.lti.ntcir.qalab.utils.Utils;

/**
 * @author Alkesh
 */
public class HypothesisGenerator extends JCasAnnotator_ImplBase {
  public static final String PARAM_VERBOSE_OUTPUT = "verboseOutput";

  Pattern pattern = Pattern
          .compile("([(]symbol-term_[a-z]+[)]([*][0-9])*){1,4}");// (symbol-term_other)*2

  @ConfigurationParameter(name = PARAM_VERBOSE_OUTPUT)
  private boolean verbose;

  @Override
  public void initialize(UimaContext context)
          throws ResourceInitializationException {
    super.initialize(context);

    verbose = (Boolean) context
            .getConfigParameterValue(PARAM_VERBOSE_OUTPUT);
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    TestDocument testDoc = Utils.getTestDocumentFromCAS(aJCas);

    ArrayList<QuestionAnswerSet> qaSet = Utils.fromFSListToCollection(
            testDoc.getQAList(), QuestionAnswerSet.class);

    for (int i = 0; i < qaSet.size(); i++) {

      ArrayList<AnalyzedAnswerChoice> analyzedAnswerChoiceList = new ArrayList<AnalyzedAnswerChoice>();
      QuestionAnswerSet qa = qaSet.get(i);

      // Extracting Question
      Question question = qa.getQuestion();
      SetInstruction setInstr = question.getSetinstruction();
      String questionType = question.getQuestionType();
      // Extracting contextual section
      Data qContext = question.getContextData();
      // String qContextText=qContext.getText().replace("", "").trim();
      // qContext.setText(qContextText);

      ArrayList<Underlined> underlined = new ArrayList<Underlined>();
      if (qContext.getUnderlinedList() != null) {
        underlined = Utils.fromFSListToCollection(
                qContext.getUnderlinedList(), Underlined.class);
      }

      ArrayList<Gaps> gaps = new ArrayList<Gaps>();

      if (qContext.getGapList() != null) {
        gaps = Utils.fromFSListToCollection(qContext.getGapList(),
                Gaps.class);
      }

      // Extracting instruction related with question
      Instruction qInstruction = question.getInstruction();
      // System.out.println(qInstruction.getText());
      ArrayList<Refs> refList = Utils.fromFSListToCollection(
              qInstruction.getRefList(), Refs.class);

      if (verbose) {
        System.out.println("Question: " + question.getId());
        System.out.println("Instruction: " + setInstr.getText());
        System.out.println("Topic: " + setInstr.getTopic());
        System.out.println("QuestionType: "
                + question.getQuestionType());
        System.out.println(qContext.getText());
        for (int j = 0; j < underlined.size(); j++) {
          System.out.println("Underline: "
                  + underlined.get(j).getText());
        }
        for (int j = 0; j < gaps.size(); j++) {
          System.out.println("Gaps: " + gaps.get(j).getText());
        }
        for (int j = 0; j < refList.size(); j++) {
          Refs ref = refList.get(j);
          System.out.println("Target: " + ref.getId() + "\t"
                  + ref.getLabel());
        }

        ArrayList<QData> qDataList = Utils.fromFSListToCollection(
                question.getQdataList(), QData.class);
        for (int j = 0; j < qDataList.size(); j++) {
          QData qData = qDataList.get(j);
          ArrayList<Gaps> gapList = new ArrayList<Gaps>();
          if (qData.getGaps() != null) {
            gapList = Utils.fromFSListToCollection(qData.getGaps(),
                    Gaps.class);
          }

          ArrayList<ListItem> listItems = new ArrayList<ListItem>();
          if (qData.getListItems() != null) {
            listItems = Utils.fromFSListToCollection(
                    qData.getListItems(), ListItem.class);
          }

          System.out.println("QuestionData: " + qData.getText());
          for (int k = 0; k < gapList.size(); k++) {
            System.out.println("Blank: " + gapList.get(k).getId()
                    + "\t" + gapList.get(k).getLabel() + "\t"
                    + gapList.get(k).getText());
          }
          for (int k = 0; k < listItems.size(); k++) {
            System.out.println("List: " + listItems.get(k).getId()
                    + "\t" + listItems.get(k).getLabel() + "\t"
                    + listItems.get(k).getText());
          }
        }
      }

      // Extracting answer choice associated with question
      ArrayList<AnswerChoice> answerChoiceList = Utils
              .fromFSListToCollection(qa.getAnswerChoiceList(),
                      AnswerChoice.class);
      for (int j = 0; j < answerChoiceList.size(); j++) {
        String ansId = answerChoiceList.get(j).getId();

        if (verbose) {
          ArrayList<Refs> rfList = new ArrayList<Refs>();
          if (answerChoiceList.get(j).getRefList() != null) {
            rfList = Utils.fromFSListToCollection(answerChoiceList
                    .get(j).getRefList(), Refs.class);
          }

          System.out.println(answerChoiceList.get(j).getText());

          for (int k = 0; k < rfList.size(); k++) {
            System.out.println(rfList.get(k).getId() + "\t"
                    + rfList.get(k).getLabel() + "\t"
                    + rfList.get(k).getText());
          }
        }

        AnalyzedAnswerChoice analyzedAnswerChoice = new AnalyzedAnswerChoice(aJCas);
        analyzedAnswerChoice.setQId(question.getId());
        analyzedAnswerChoice.setTopic(question.getSetinstruction().getTopic());
        analyzedAnswerChoice.setQuestionType(questionType);
        analyzedAnswerChoice.setAnsChoiceId(ansId);
        analyzedAnswerChoice.setHighLevelContext(qContext.getText());
        // add specific object, not only string
        analyzedAnswerChoice.setQuestionContext(qContext);

        String specificContext = "";
        try {
          specificContext = extractSpecificContext(question);
        } catch (Exception e) {
          // e.printStackTrace();
          System.err.println("Could not extract specific context ");

        }
        if (verbose) {
          System.out.println(specificContext);
        }
        analyzedAnswerChoice.setSpecificContext(specificContext);

        ArrayList<Assertion> assertionList = new ArrayList<Assertion>();
        if (question.getQuestionType().equalsIgnoreCase("sentence")) {
          // String assertionStatement = answerChoiceList.get(j)
          // .getText();

          ArrayList<Assertion> assertList = null;
          try {
            assertList = generateHypotheisStatement(qa,
                    answerChoiceList.get(j));
          } catch (Exception e) {
            e.printStackTrace();
          }

          assertionList.addAll(assertList);
        } else {

          ArrayList<Assertion> assertList = null;
          try {
            assertList = prepareHypotheisStatement(qa,
                    answerChoiceList.get(j));
          } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
          }

          assertionList.addAll(assertList);
        }

        analyzedAnswerChoice.setAssertionList(Utils.fromCollectionToFSList(aJCas,
                assertionList));
        analyzedAnswerChoiceList.add(analyzedAnswerChoice);
      }

      FSList fsHypList = Utils.fromCollectionToFSList(aJCas,
              analyzedAnswerChoiceList);
      qa.setAnalyzedAnswerChoiceList(fsHypList);
      if (verbose) {
        System.out
                .println(
                        "==========================================================================");
      }
      qaSet.set(i, qa);
    }
    testDoc.setQAList(Utils.fromCollectionToFSList(aJCas, qaSet));

    System.out.println("Done preparing hypothesis.");
  }

  public ArrayList<Assertion> fillEmptyAssertion(QuestionAnswerSet qaSet,
          AnswerChoice answerChoice) throws Exception {

    ArrayList<Assertion> assertionList = new ArrayList<Assertion>();
    String assertionStatement = answerChoice.getText();
    ArrayList<Refs> refList = Utils.fromFSListToCollection(
            answerChoice.getRefList(), Refs.class);
    ArrayList<QData> qDataList = Utils.fromFSListToCollection(qaSet
            .getQuestion().getQdataList(), QData.class);
    if (refList != null && refList.size() > 0) {
      assertionStatement = "";
      for (int i = 0; i < refList.size(); i++) {
        assertionStatement += refList.get(i).getText() + " ";
        if (qDataList != null && qDataList.size() > 0) {
          for (int j = 0; j < qDataList.size(); j++) {
            ArrayList<Refs> qDataRefs = Utils
                    .fromFSListToCollection(qDataList.get(j)
                            .getRefs(), Refs.class);
            ArrayList<ListItem> qDatalst = Utils
                    .fromFSListToCollection(qDataList.get(j)
                            .getListItems(), ListItem.class);
            ArrayList<Gaps> qDatagaps = Utils
                    .fromFSListToCollection(qDataList.get(j)
                            .getGaps(), Gaps.class);

            for (int k = 0; k < qDataRefs.size(); k++) {
              if (qDataRefs.get(k).getId()
                      .equals(refList.get(i).getId())) {
                assertionStatement += qDataRefs.get(k)
                        .getText() + " ";
              }
            }
            for (int k = 0; k < qDatalst.size(); k++) {
              if (qDatalst.get(k).getId()
                      .equals(refList.get(i).getId())) {
                assertionStatement += qDatalst.get(k).getText()
                        + " ";
              }
            }
            for (int k = 0; k < qDatagaps.size(); k++) {
              if (qDatagaps.get(k).getId()
                      .equals(refList.get(i).getId())) {
                assertionStatement += qDatagaps.get(k)
                        .getText() + " ";
              }
            }
          }
        }//

      }

    }

    assertionStatement = assertionStatement.trim();
    Assertion assertion = new Assertion(answerChoice.getCAS().getJCas());
    assertionStatement = assertionStatement.replaceAll("^([(][0-9]+[)])",
            "").trim();

    if (assertionStatement.equals("")) {

      assertionStatement = answerChoice.getText();
    }

    assertion.setText(assertionStatement);

    assertion.setIsAffirmative(checkQuestionNegativity(qaSet.getQuestion()));
    assertionList.add(assertion);
    return assertionList;
  }

  public ArrayList<Assertion> generateHypotheisStatement(
          QuestionAnswerSet qaSet, AnswerChoice answerChoice)
          throws Exception {

    Question question = qaSet.getQuestion();

    Instruction instruction = qaSet.getQuestion().getInstruction();
    Data contextData = qaSet.getQuestion().getContextData();

    String context = "";
    if (contextData.getUnderlinedList() != null) {
      ArrayList<Underlined> underlined = Utils.fromFSListToCollection(
              contextData.getUnderlinedList(), Underlined.class);

      ArrayList<Refs> refList = Utils.fromFSListToCollection(
              instruction.getRefList(), Refs.class);

      for (int i = 0; i < refList.size(); i++) {
        Refs refs = refList.get(i);

        for (int j = 0; j < underlined.size(); j++) {

          if (refs.getId().equals(underlined.get(j).getId())) {

            context += underlined.get(j).getText() + " ";
          }
        }
      }
    }
    ArrayList<Assertion> assertionList = new ArrayList<Assertion>();
    String assertionStatement = answerChoice.getText() + " " + context;
    assertionStatement = assertionStatement.trim();
    Assertion assertion = new Assertion(answerChoice.getCAS().getJCas());
    assertionStatement = assertionStatement.replaceAll("^([(][0-9]+[)])",
            "").trim();
    assertion.setText(assertionStatement);
    assertion.setIsAffirmative(checkQuestionNegativity(question));
    assertionList.add(assertion);

    return assertionList;
  }

  public String extractSpecificContext(Question question) {

    String specificContext = "";

    Instruction qInstruction = question.getInstruction();

    ArrayList<Refs> refList = Utils.fromFSListToCollection(
            qInstruction.getRefList(), Refs.class);

    Data contextData = question.getContextData();
    ArrayList<Underlined> underlined = Utils.fromFSListToCollection(
            contextData.getUnderlinedList(), Underlined.class);

    if (refList.size() > 0) {
      for (int i = 0; i < refList.size(); i++) {

        String refId = refList.get(i).getId();
        for (int j = 0; j < underlined.size(); j++) {

          if (underlined.get(j).getId().equals(refId)
                  && refId.startsWith("U")) {
            specificContext = underlined.get(j).getText();
            break;
          }
        }
      }

    }
    return specificContext;
  }

  public ArrayList<Assertion> prepareHypotheisStatement(
          QuestionAnswerSet qaSet, AnswerChoice answerChoice)
          throws Exception {

    Question question = qaSet.getQuestion();
    Instruction qInstruction = question.getInstruction();

		/*
     * ArrayList<AnswerChoice> ansChoiceList = Utils.fromFSListToCollection(
		 * qaSet.getAnswerChoiceList(), AnswerChoice.class);
		 */

    ArrayList<QData> qDataList = Utils.fromFSListToCollection(
            question.getQdataList(), QData.class);
    String questionType = question.getQuestionType();
    ArrayList<Assertion> assertionList = new ArrayList<Assertion>();
    // for (int i = 0; i < qDataList.size(); i++) {

    Matcher matcher = pattern.matcher(questionType);
    if (matcher.find()) {// questionType.indexOf("(symbol-term_other)*2")
      // != -1
      if (qDataList.size() > 0) {
        for (int i = 0; i < qDataList.size(); i++) {
          ArrayList<Gaps> gapsList = new ArrayList<Gaps>();
          if (qDataList.get(i).getGaps() != null) {
            gapsList = Utils.fromFSListToCollection(qDataList
                    .get(i).getGaps(), Gaps.class);
          }

          String qDataText = qDataList.get(i).getText();
          for (int l = 0; l < gapsList.size(); l++) {
            String gapId = gapsList.get(l).getId();

            ArrayList<Refs> refList = Utils.fromFSListToCollection(
                    answerChoice.getRefList(), Refs.class);
            for (int m = 0; m < refList.size(); m++) {
              if (gapId.equals(refList.get(m).getId())) {
                String refText = refList.get(m).getText();
                qDataText = qDataText.replace(refList.get(m)
                        .getLabel(), refText);
              }
            }

          }

          if (verbose) {
            System.out.println("AnalyzedAnswerChoice: " + qDataText);
          }
          String assertions[] = qDataText.split("[.]");
          String ansText = answerChoice.getText();

          ansText = ansText.replaceAll("^([(][0-9]+[)])", "").trim();
          for (int l = 0; l < gapsList.size(); l++) {

            ansText = ansText.replace(gapsList.get(l).getLabel(),
                    "|");
            // list.add(gapsList.get(l).getLabel());

          }
          String ans[] = ansText.split("[|]");
          ArrayList<String> ansList = new ArrayList<String>();
          for (int l = 0; l < ans.length; l++) {
            if (ans[l].equals("")) {
              continue;
            }
            ansList.add(ans[l]);
          }

          for (int l = 0; l < assertions.length; l++) {

            boolean found = false;
            HashSet<String> hsh = new HashSet<String>();// /sometimes
            // more than
            // fill in
            // the
            // blanks
            // comes
            // with same
            // ref
            int n = 0;
            for (int m = 0; m < gapsList.size(); m++) {
              if (hsh.contains(gapsList.get(m).getLabel())) {
                continue;
              }
              hsh.add(gapsList.get(m).getLabel());
              if (assertions[l].contains(gapsList.get(m)
                      .getLabel())
                      || assertions[l].contains(ansList.get(n))) {
                assertions[l] = assertions[l].replace(gapsList
                        .get(m).getLabel(), ansList.get(n));
                found = true;
              }
              n++;
            }

            if (verbose) {
              System.out.println("##Modified: " + assertions[l]);
            }
            if (!found) {
              continue;
            }

            Assertion assertion = new Assertion(question.getCAS()
                    .getJCas());
            assertions[l] = assertions[l].replaceAll(
                    "^([(][0-9]+[)])", "").trim();
            assertion.setText(assertions[l]);
            assertion.setIsAffirmative(true);
            assertionList.add(assertion);
          }
          // assertionStatement=qDataText;
        }
      } else {

        Data data = question.getContextData();
        String assertionText = data.getText();
        ArrayList<Refs> qInstrRefList = Utils.fromFSListToCollection(
                qInstruction.getRefList(), Refs.class);
        // ArrayList<Underlined>
        // underlined=Utils.fromFSListToCollection(data.getUnderlinedList(),Underlined.class);
        ArrayList<Gaps> gaps = Utils.fromFSListToCollection(
                data.getGapList(), Gaps.class);
        Assertion assertion = new Assertion(question.getCAS().getJCas());
        String assertions[] = assertionText.split("[.]");
        ArrayList<Refs> ansRefs = Utils.fromFSListToCollection(
                answerChoice.getRefList(), Refs.class);

        for (int m = 0; m < ansRefs.size(); m++) {
          if (ansRefs.get(m).getText() == null) {
            continue;
          }
          for (int l = 0; l < assertions.length; l++) {
            if (assertions[l].trim().equals("")) {
              continue;
            }
            String updatedAssertions = "";
            for (int i = 0; i < qInstrRefList.size(); i++) {

              for (int j = 0; j < gaps.size(); j++) {

                // if (qInstrRefList.get(i).getId()
                // .equalsIgnoreCase(gaps.get(j).getId())) {

                if ((assertions[l].contains(gaps.get(j)
                        .getLabel()) || assertions[l]
                        .contains("__"))
                        && gaps.get(j).getId()
                        .equals(ansRefs.get(m).getId())) {

                  if (qInstrRefList
                          .get(i)
                          .getId()
                          .equalsIgnoreCase(
                                  ansRefs.get(m).getId())) {
                    if (updatedAssertions.equals("")) {
                      updatedAssertions = assertions[l];
                    }

                    updatedAssertions = updatedAssertions
                            .replace(
                                    gaps.get(j).getLabel(),
                                    ansRefs.get(m)
                                            .getText())
                            .trim();
                    updatedAssertions = updatedAssertions
                            .replaceFirst(
                                    "[_]+",
                                    ansRefs.get(m)
                                            .getText())
                            .trim();

                    assertions[l] = updatedAssertions;
                    // updatedAssertions=assertions[l];
                  }

                }

              }
            }

            if (!updatedAssertions.equals("")
                    && !(updatedAssertions.contains("__") || updatedAssertions
                    .contains(ansRefs.get(m).getLabel()))) {
              updatedAssertions = updatedAssertions.replaceAll(
                      "^([(][0-9]+[)])", "").trim();
              assertion.setText(updatedAssertions);
              if (verbose) {
                System.out.println("Updated Assertion: "
                        + updatedAssertions);
              }
              assertion.setIsAffirmative(true);
              assertionList.add(assertion);
              break;
            }
            // }//

          }

        }
      }

    } else if (questionType.equals("(symbol-TF)*2")) {

      for (int i = 0; i < qDataList.size(); i++) {

        ArrayList<Refs> refList = Utils.fromFSListToCollection(
                answerChoice.getRefList(), Refs.class);
        // String chText = answerChoice.getText();
        ArrayList<ListItem> listItemsList = Utils
                .fromFSListToCollection(
                        qDataList.get(i).getListItems(), ListItem.class);

        for (int k = 0; k < listItemsList.size(); k++) {
          String lId = listItemsList.get(k).getId();
          String lText = listItemsList.get(k).getText();
          for (int l = 0; l < refList.size(); l++) {
            if (refList.get(l).getId().equals(lId)) {
              Assertion assertion = new Assertion(question
                      .getCAS().getJCas());
              lText = lText.replaceAll("^([(][0-9]+[)])", "")
                      .trim();
              assertion.setText(lText);
              if (refList.get(l).getText().endsWith("Correct")) {
                assertion.setIsAffirmative(true);
              } else {
                assertion.setIsAffirmative(false);
              }
              assertionList.add(assertion);
            }
          }
        }
      }
    } else if (questionType.equals("term_location")
            || questionType.equals("term_other")
            || questionType.equals("term_person")) {
      String instruction = qaSet.getQuestion().getInstruction().getText();
      String ansChoiceText = answerChoice.getText()
              .replace(answerChoice.getId(), "").trim();
      ArrayList<Refs> refList = Utils.fromFSListToCollection(qaSet
              .getQuestion().getInstruction().getRefList(), Refs.class);
      if (refList.size() == 0) {

        if (qDataList.size() == 0) {

          String assertions[] = question.getContextData().getText()
                  .split("[.]");
          boolean found = false;
          String foundAssert = "";
          for (int a = 0; a < assertions.length; a++) {
            if (assertions[a].trim().equals("")) {
              continue;
            }

            assertions[a] = assertions[a].replaceAll(
                    "^([(][0-9]+[)])", "").trim();
            int len = assertions[a].length();
            assertions[a] = assertions[a].replaceAll("[_]+{2,}",
                    ansChoiceText).trim();
            if (len != assertions[a].length()) {
              found = true;
              foundAssert = assertions[a];
              break;
            }
          }
          if (found) {
            Assertion assertion = new Assertion(question.getCAS()
                    .getJCas());

            assertion.setText(foundAssert);
            assertion.setIsAffirmative(true);
            assertionList.add(assertion);
          }

        } else {

          for (int l = 0; l < qDataList.size(); l++) {
            String qData = qDataList.get(l).getText();
            ArrayList<Refs> gapList = Utils.fromFSListToCollection(
                    qDataList.get(l).getRefs(), Refs.class);

            for (int m = 0; m < gapList.size(); m++) {

              qData = qData.replace(gapList.get(m).getLabel(),
                      ansChoiceText);
              qData = qData.replace("[_]+{2,}", ansChoiceText);
            }
            String assertions[] = qData.split("[.]");
            for (int a = 0; a < assertions.length; a++) {
              if (assertions[a].trim().equals("")) {
                continue;
              }
              Assertion assertion = new Assertion(question
                      .getCAS().getJCas());
              assertions[a] = assertions[a].replaceAll(
                      "^([(][0-9]+[)])", "").trim();
              assertion.setText(assertions[a]);
              assertion.setIsAffirmative(true);
              assertionList.add(assertion);
            }
          }

        }

      } else {

        for (int k = 0; k < refList.size(); k++) {
          Refs target = refList.get(k);
          String targetId = target.getId();
          String targetLabel = target.getLabel();

          if (targetId.startsWith("U")) {

            String underlined = extractSpecificContext(question);
            underlined = underlined.replaceAll("[(][0-9][)]", "")
                    .trim();

            boolean boolVal = checkQuestionNegativity(question);

            String filteredInstruction = filterInstruction(instruction);

            Assertion assertion = new Assertion(question.getCAS()
                    .getJCas());
            String assertText = filteredInstruction + " "
                    + underlined + " " + ansChoiceText;
            assertion.setText(assertText);
            assertion.setIsAffirmative(boolVal);
            assertionList.add(assertion);

          } else if (targetId.startsWith("B")) {

            Data contextData = question.getContextData();
            String contextText = contextData.getText();
            contextText = contextText.replaceAll("^ａ", "")
                    .replaceFirst("^b", "").replaceFirst("^ｃ", "")
                    .trim();

            String choiceText = answerChoice.getText()
                    .replaceAll("^([(][0-9]+{1,2}[)])", "").trim();
            String assertionText = contextText.replace(targetLabel,
                    choiceText);
            assertionText = assertionText.replaceFirst("[_]+{2,}",
                    choiceText);

            String assertions[] = assertionText.split("[.]");
            for (int l = 0; l < assertions.length; l++) {

              boolean found = false;

              if (assertions[l].contains(choiceText)) {
                found = true;
              }

              if (!found) {
                continue;
              }
              Assertion assertion = new Assertion(question
                      .getCAS().getJCas());

              assertion.setText(assertions[l]);
              assertion.setIsAffirmative(true);
              assertionList.add(assertion);
            }

          } else {
            System.err.println(question.getId()
                    + ": Image question not supproted");
            // throw new Exception("Unsupported RefTarget type");
          }
        }
      }
    } else if (questionType.equals("term_location-term_location")
            || questionType.equals("term_other-term_other")) {
      ArrayList<Refs> refList = Utils.fromFSListToCollection(qaSet
              .getQuestion().getInstruction().getRefList(), Refs.class);
      String contextData = question.getContextData().getText();

      String assertText = contextData;
      String ansText = answerChoice.getText();
      String ans[] = ansText.split("[-]");
      for (int k = 0; k < refList.size(); k++) {
        Refs target = refList.get(k);
        String targetId = target.getId();
        String targetLabel = target.getLabel();
        ans[k] = ans[k].replaceAll("^([(][0-9]+[)])", "").trim();
        ans[k] = ans[k].replaceAll("[①②③④⑤⑥]", " ").trim();

        assertText = assertText.replace(targetLabel, ans[k]);
        assertText = assertText.replaceFirst("[_]+{2,}", ans[k]);

      }
      String assertions[] = assertText.trim().split("[.]");
      for (int l = 0; l < assertions.length; l++) {
        boolean found = false;
        for (int m = 0; m < ans.length; m++) {
          if (assertions[l].contains(ans[m])) {
            found = true;
          }
        }

        if (!found) {
          continue;
        }
        Assertion assertion = new Assertion(question.getCAS().getJCas());
        assertion.setText(assertions[l]);
        assertion.setIsAffirmative(true);
        assertionList.add(assertion);
      }

    } else if (questionType
            .equals("o(sentence-sentence-sentence-sentence)")) {

    }

    return assertionList;
  }

  private boolean checkQuestionNegativity(Question question) {
    String qInstrText = question.getInstruction().getText().toLowerCase();

    boolean res = (qInstrText.indexOf("that incorrectly") == -1
            && qInstrText.indexOf("that was not") == -1
            && qInstrText.indexOf("incorrect sentence") == -1
            && qInstrText.indexOf("wrong sentence") == -1
            && // not seen aded by Leo
            qInstrText.indexOf("is incorrect") == -1
            && qInstrText.indexOf("is wrong") == -1 && // not seen aded by
            // Leo
            qInstrText.indexOf("incorrectly describes") == -1 && qInstrText
            .indexOf("contains a mistake") == -1);
    return res;

  }

  public String filterInstruction(String instruction) throws Exception {

    instruction = instruction.toLowerCase().replaceAll("[(][0-9][)]", " ")
            .replaceAll("[\\W]+", " ").trim();
    String wordList[] = { "in", "regard", "to", "the", "underlined",
            "portion", "from", "①~④", "below", "choose", "one", "correct",
            "incorrect", "option", "that", "is", "name", "a", "an", };
    HashSet<String> hshFilterSet = new HashSet<String>();
    hshFilterSet.addAll(Arrays.asList(wordList));

    String words[] = instruction.split("[\\W]");
    String instr = "";
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].trim();
      if (words[i].equals("")) {
        continue;
      }
      if (hshFilterSet.contains(words[i])) {
        continue;
      }
      instr += words[i] + " ";
    }

    return instr;
  }
}
