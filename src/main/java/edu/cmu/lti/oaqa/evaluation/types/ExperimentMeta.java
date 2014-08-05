

/* First created by JCasGen Mon Apr 14 12:54:41 EDT 2014 */
package edu.cmu.lti.oaqa.evaluation.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** Metadata for an experiment
 * Updated by JCasGen Mon Aug 04 11:44:10 EDT 2014
 * XML source: /home/diwang/Dropbox/oaqa-workspace/ntcir-qalab-cmu-baseline/src/main/resources/WorldHistoryTypesDescriptor.xml
 * @generated */
public class ExperimentMeta extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ExperimentMeta.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ExperimentMeta() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ExperimentMeta(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ExperimentMeta(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: ExperimentId

  /** getter for ExperimentId - gets Unique ID for an experiment.
   * @generated
   * @return value of the feature 
   */
  public String getExperimentId() {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_ExperimentId == null)
      jcasType.jcas.throwFeatMissing("ExperimentId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_ExperimentId);}
    
  /** setter for ExperimentId - sets Unique ID for an experiment. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setExperimentId(String v) {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_ExperimentId == null)
      jcasType.jcas.throwFeatMissing("ExperimentId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    jcasType.ll_cas.ll_setStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_ExperimentId, v);}    
   
    
  //*--------------*
  //* Feature: DatasetId

  /** getter for DatasetId - gets Unique ID for the dataset.
   * @generated
   * @return value of the feature 
   */
  public String getDatasetId() {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_DatasetId == null)
      jcasType.jcas.throwFeatMissing("DatasetId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_DatasetId);}
    
  /** setter for DatasetId - sets Unique ID for the dataset. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDatasetId(String v) {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_DatasetId == null)
      jcasType.jcas.throwFeatMissing("DatasetId", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    jcasType.ll_cas.ll_setStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_DatasetId, v);}    
   
    
  //*--------------*
  //* Feature: ExperimentName

  /** getter for ExperimentName - gets Name for current kind of experiment (e.g. purpose)
   * @generated
   * @return value of the feature 
   */
  public String getExperimentName() {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_ExperimentName == null)
      jcasType.jcas.throwFeatMissing("ExperimentName", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_ExperimentName);}
    
  /** setter for ExperimentName - sets Name for current kind of experiment (e.g. purpose) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setExperimentName(String v) {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_ExperimentName == null)
      jcasType.jcas.throwFeatMissing("ExperimentName", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    jcasType.ll_cas.ll_setStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_ExperimentName, v);}    
   
    
  //*--------------*
  //* Feature: ExperimentInvoker

  /** getter for ExperimentInvoker - gets Name of the user, program, or machine that initiates current experiment
   * @generated
   * @return value of the feature 
   */
  public String getExperimentInvoker() {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_ExperimentInvoker == null)
      jcasType.jcas.throwFeatMissing("ExperimentInvoker", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_ExperimentInvoker);}
    
  /** setter for ExperimentInvoker - sets Name of the user, program, or machine that initiates current experiment 
   * @generated
   * @param v value to set into the feature 
   */
  public void setExperimentInvoker(String v) {
    if (ExperimentMeta_Type.featOkTst && ((ExperimentMeta_Type)jcasType).casFeat_ExperimentInvoker == null)
      jcasType.jcas.throwFeatMissing("ExperimentInvoker", "edu.cmu.lti.oaqa.evaluation.types.ExperimentMeta");
    jcasType.ll_cas.ll_setStringValue(addr, ((ExperimentMeta_Type)jcasType).casFeatCode_ExperimentInvoker, v);}    
  }

    