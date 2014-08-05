/**
 * 
 */
package edu.cmu.lti.ntcir.qalab.annotators;

import java.util.UUID;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta;

/**
 * @author zhengzhongliu
 * 
 */
public class ExperimentIdChanger extends JCasAnnotator_ImplBase {
	String experimentId;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		String customIdentifier = (String) aContext
				.getConfigParameterValue("customIdentifier");

		boolean useRandomTail = (Boolean) aContext
				.getConfigParameterValue("useRandomTail");

		if (useRandomTail) {
			experimentId = customIdentifier + "_"
					+ UUID.randomUUID().toString();
		} else {
			experimentId = customIdentifier;
		}
		System.err.println("Change the current experiment id to "
				+ experimentId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		ExperimentMeta meta = JCasUtil
				.selectSingle(aJCas, ExperimentMeta.class);
		meta.setExperimentId(experimentId);
	}
}
