package edu.cmu.lti.ntcir.qalab.annotators;

public class SearchRes {
  float score;
  float disScore;
  long qty;

  public SearchRes(Float score, Float discScore, long qty) {
    super();
    this.score = score;
    this.disScore = discScore;
    this.qty = qty;
  }
}
