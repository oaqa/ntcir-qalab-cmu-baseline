package edu.cmu.lti.ntcir.qalab.casconsumers;

import edu.cmu.lti.ntcir.qalab.types.TestDocument;
import edu.cmu.lti.ntcir.qalab.utils.Utils;
import edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author diwang
 *
 */
public class XmiCasConsumer extends CasConsumer_ImplBase {

	int mDocNum;
	File mOutputDir = null;

	@Override
	public void initialize() {

		mDocNum = 0;
		mOutputDir = new File(
				(String) getConfigParameterValue("OutputDirectory"));
		if (!mOutputDir.exists()) {
			mOutputDir.mkdirs();
		}

		// delete all .xmi files in the output dir
		for (File f : mOutputDir.listFiles()) {
			if (f.getName().endsWith(".xmi")) {
				f.delete();
			}
		}
	}

	@Override
	public void processCas(CAS aCAS) throws ResourceProcessException {

		JCas jCas;
		try {
			jCas = aCAS.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		TestDocument srcDoc = Utils.getTestDocumentFromCAS(jCas);
		ExperimentMeta meta = JCasUtil.selectSingle(jCas, ExperimentMeta.class);
		String docId = srcDoc.getId();
		// String outFileName = mOutputDir + "/" + meta.getDatasetId() + "/" +
		// docId + ".xmi";
		String outFileName = docId + ".xmi";
		System.out.println(outFileName);
		try {
			File outFile = new File(mOutputDir, outFileName);
			this.writeXmi(aCAS, outFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeXmi(CAS aCas, File outFile) throws IOException,
			SAXException {
		FileOutputStream out = null;

		try {
			// write XMI
			out = new FileOutputStream(outFile);
			XmiCasSerializer ser = new XmiCasSerializer(aCas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			ser.serialize(aCas, xmlSer.getContentHandler());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Closes the file and other resources initialized during the process
	 *
	 */
	@Override
	public void destroy() {

	}
}
