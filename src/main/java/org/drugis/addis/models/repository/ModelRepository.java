package org.drugis.addis.models.repository;

import org.drugis.addis.models.Model;

/**
 * Created by daan on 22-5-14.
 */
public interface ModelRepository {

  public Model create(Integer analysisId);

  public Model find(Integer modelId);

  public Model findByAnalysis(Integer networkMetaAnalysisId);
}
