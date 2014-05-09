package org.drugis.addis.trialverse.model;

import java.util.List;

/**
 * Created by connor on 8-5-14.
 */
public class TrialData {

  private List<Study> studies;

  public TrialData() {
  }

  public TrialData(List<Study> studies) {
    this.studies = studies;
  }

  public List<Study> getStudies() {
    return studies;
  }


  abstract class TrialDataMeasurement {
    private Long sampleSize;

    public Long getSampleSize() {
      return sampleSize;
    }
  }

  class TrialDataRateMeasurement extends TrialDataMeasurement {
    private Long rate;

    public Long getRate() {
      return rate;
    }
  }

  class TrialDataContinuousMeasurement extends TrialDataMeasurement {
    Double mean;
    Double sigma;

    public Double getSigma() {
      return sigma;
    }

    public Double getMean() {
      return mean;
    }
  }

}

