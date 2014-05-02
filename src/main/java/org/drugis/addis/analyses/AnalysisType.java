package org.drugis.addis.analyses;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by connor on 3/11/14.
 */
public enum AnalysisType {
  SINGLE_STUDY_BENEFIT_RISK("Single-study Benefit-Risk"),
  NETWORK_META_ANALYSIS("Network meta-analysis");

  public static final String SINGLE_STUDY_BENEFIT_RISK_LABEL = "Single-study Benefit-Risk";
  public static final String NETWORK_META_ANALYSIS_LABEL = "Network meta-analysis";

  private String label;

  AnalysisType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  @Override
  @JsonValue
  public String toString() {
    return this.label;
  }

  @JsonCreator
  public static AnalysisType getByLabel(String label) {
    switch (label) {
      case SINGLE_STUDY_BENEFIT_RISK_LABEL:
        return SINGLE_STUDY_BENEFIT_RISK;
      case NETWORK_META_ANALYSIS_LABEL:
        return NETWORK_META_ANALYSIS;
      default:
        throw new EnumConstantNotPresentException(AnalysisType.class, label);
    }
  }
}
