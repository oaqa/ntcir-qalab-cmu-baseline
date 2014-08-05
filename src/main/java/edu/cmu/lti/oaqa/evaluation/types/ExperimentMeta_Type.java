
/* First created by JCasGen Mon Apr 14 12:54:41 EDT 2014 */
package edu.cmu.lti.oaqa.evaluation.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** Metadata for an experiment
 * Updated by JCasGen Mon Aug 04 11:44:10 EDT 2014
 * @generated */
public class ExperimentMeta_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ExperimentMeta_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ExperimentMeta_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ExperimentMeta(addr, ExperimentMeta_Type.this);
  			   ExperimentMeta_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ExperimentMeta(addr, ExperimentMeta_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ExperimentMeta.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
 
  /** @generated */
  final Feature casFeat_ExperimentId;
  /** @generated */
  final int     casFeatCode_ExperimentId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getExperimentId(int addr) {
        if (featOkTst && casFeat_ExperimentId == null)
      jcas.throwFeatMissing("ExperimentId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ExperimentId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setExperimentId(int addr, String v) {
        if (featOkTst && casFeat_ExperimentId == null)
      jcas.throwFeatMissing("ExperimentId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    ll_cas.ll_setStringValue(addr, casFeatCode_ExperimentId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_DatasetId;
  /** @generated */
  final int     casFeatCode_DatasetId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDatasetId(int addr) {
        if (featOkTst && casFeat_DatasetId == null)
      jcas.throwFeatMissing("DatasetId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return ll_cas.ll_getStringValue(addr, casFeatCode_DatasetId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDatasetId(int addr, String v) {
        if (featOkTst && casFeat_DatasetId == null)
      jcas.throwFeatMissing("DatasetId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    ll_cas.ll_setStringValue(addr, casFeatCode_DatasetId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ExperimentName;
  /** @generated */
  final int     casFeatCode_ExperimentName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getExperimentName(int addr) {
        if (featOkTst && casFeat_ExperimentName == null)
      jcas.throwFeatMissing("ExperimentName", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ExperimentName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setExperimentName(int addr, String v) {
        if (featOkTst && casFeat_ExperimentName == null)
      jcas.throwFeatMissing("ExperimentName", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    ll_cas.ll_setStringValue(addr, casFeatCode_ExperimentName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ExperimentInvoker;
  /** @generated */
  final int     casFeatCode_ExperimentInvoker;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getExperimentInvoker(int addr) {
        if (featOkTst && casFeat_ExperimentInvoker == null)
      jcas.throwFeatMissing("ExperimentInvoker", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ExperimentInvoker);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setExperimentInvoker(int addr, String v) {
        if (featOkTst && casFeat_ExperimentInvoker == null)
      jcas.throwFeatMissing("ExperimentInvoker", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    ll_cas.ll_setStringValue(addr, casFeatCode_ExperimentInvoker, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ExperimentMeta_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ExperimentId = jcas.getRequiredFeatureDE(casType, "ExperimentId", "uima.cas.String", featOkTst);
    casFeatCode_ExperimentId  = (null == casFeat_ExperimentId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ExperimentId).getCode();

 
    casFeat_DatasetId = jcas.getRequiredFeatureDE(casType, "DatasetId", "uima.cas.String", featOkTst);
    casFeatCode_DatasetId  = (null == casFeat_DatasetId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_DatasetId).getCode();

 
    casFeat_ExperimentName = jcas.getRequiredFeatureDE(casType, "ExperimentName", "uima.cas.String", featOkTst);
    casFeatCode_ExperimentName  = (null == casFeat_ExperimentName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ExperimentName).getCode();

 
    casFeat_ExperimentInvoker = jcas.getRequiredFeatureDE(casType, "ExperimentInvoker", "uima.cas.String", featOkTst);
    casFeatCode_ExperimentInvoker  = (null == casFeat_ExperimentInvoker) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ExperimentInvoker).getCode();

  }
}



    