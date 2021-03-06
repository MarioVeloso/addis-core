package org.drugis.addis.analyses;

import org.drugis.addis.analyses.repository.AnalysisRepository;
import org.drugis.addis.analyses.service.AnalysisService;
import org.drugis.addis.analyses.service.impl.AnalysisServiceImpl;
import org.drugis.addis.exception.MethodNotAllowedException;
import org.drugis.addis.exception.ResourceDoesNotExistException;
import org.drugis.addis.models.Model;
import org.drugis.addis.models.repository.ModelRepository;
import org.drugis.addis.outcomes.Outcome;
import org.drugis.addis.projects.service.ProjectService;
import org.drugis.addis.security.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AnalysisServiceTest {

  @Mock
  AnalysisRepository analysisRepository;

  @Mock
  ProjectService projectService;

  @Mock
  ModelRepository modelRepository;

  @InjectMocks
  private AnalysisService analysisService;

  private Integer projectId = 3;
  private Integer analysisId = 2;

  @Before
  public void setUp() {
    analysisRepository = mock(AnalysisRepository.class);
    analysisService = new AnalysisServiceImpl();
    initMocks(this);
  }

  @Test
  public void testCheckCoordinatesSSBR() throws ResourceDoesNotExistException {
    SingleStudyBenefitRiskAnalysis singleStudyBenefitRiskAnalysis = mock(SingleStudyBenefitRiskAnalysis.class);
    when(singleStudyBenefitRiskAnalysis.getProjectId()).thenReturn(projectId);
    when(analysisRepository.get(projectId, analysisId)).thenReturn(singleStudyBenefitRiskAnalysis);

    analysisService.checkCoordinates(projectId, analysisId);

    verify(analysisRepository).get(projectId, analysisId);
    verifyNoMoreInteractions(analysisRepository);
  }

  @Test
  public void testCheckCoordinatesNMA() throws ResourceDoesNotExistException {
    NetworkMetaAnalysis networkMetaAnalysis = mock(NetworkMetaAnalysis.class);
    when(networkMetaAnalysis.getProjectId()).thenReturn(projectId);
    when(analysisRepository.get(projectId, analysisId)).thenReturn(networkMetaAnalysis);

    analysisService.checkCoordinates(projectId, analysisId);

    verify(analysisRepository).get(projectId, analysisId);
    verifyNoMoreInteractions(analysisRepository);
  }

  @Test(expected = ResourceDoesNotExistException.class)
  public void testCheckAnalysisNotInProject() throws ResourceDoesNotExistException {
    NetworkMetaAnalysis networkMetaAnalysis = mock(NetworkMetaAnalysis.class);
    when(analysisRepository.get(projectId, analysisId)).thenReturn(networkMetaAnalysis);

    analysisService.checkCoordinates(projectId, analysisId);
  }

  @Test(expected = ResourceDoesNotExistException.class)
  public void testUpdateWrongProjectFails() throws ResourceDoesNotExistException, MethodNotAllowedException {
    Integer analysisId = -6;
    Account user = mock(Account.class);
    Integer wrongProject = 2;
    Outcome outcome = mock(Outcome.class);
    NetworkMetaAnalysis analysis = new NetworkMetaAnalysis(analysisId, wrongProject, "new name", outcome);
    NetworkMetaAnalysis oldAnalysis = mock(NetworkMetaAnalysis.class);

    when(modelRepository.findByAnalysis(analysis.getId())).thenReturn(null);
    when(oldAnalysis.getProjectId()).thenReturn(projectId);
    when(analysisRepository.get(wrongProject, analysisId)).thenReturn(oldAnalysis);

    analysisService.updateNetworkMetaAnalysis(user, analysis);
  }

  @Test(expected = MethodNotAllowedException.class)
  public void testUpdateLockedAnalysisFails() throws ResourceDoesNotExistException, MethodNotAllowedException {
    Integer analysisId = -6;
    Account user = mock(Account.class);
    Integer projectId = 1;
    Outcome outcome = mock(Outcome.class);
    Integer modelId = 83473458;
    NetworkMetaAnalysis analysis = new NetworkMetaAnalysis(analysisId, projectId, "new name", outcome);
    Model model = mock(Model.class);
    when(modelRepository.findByAnalysis(analysis.getId())).thenReturn(model);
    when(modelRepository.find(modelId)).thenReturn(null);
    analysisService.updateNetworkMetaAnalysis(user, analysis);
  }

  @Test(expected = ResourceDoesNotExistException.class)
  public void testUpdateWithOutcomeInWrongProjectFails() throws ResourceDoesNotExistException, MethodNotAllowedException {
    Account user = mock(Account.class);
    Outcome outcome = mock(Outcome.class);
    when(outcome.getProject()).thenReturn(projectId + 1);
    NetworkMetaAnalysis analysis = new NetworkMetaAnalysis(analysisId, projectId, "new name", outcome);
    AbstractAnalysis oldAnalysis = mock(NetworkMetaAnalysis.class);
    when(oldAnalysis.getProjectId()).thenReturn(projectId);
    when(analysisRepository.get(projectId, analysisId)).thenReturn(oldAnalysis);
    analysisService.updateNetworkMetaAnalysis(user, analysis);
  }

}