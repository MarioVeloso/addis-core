define(['angular', 'angular-mocks', 'controllers'], function () {
  describe("The analysisController", function () {
    var scope,
      analysisService, projectsService, outcomeService,
      select2UtilService,
      mockOutcome1 = {
        id: 1,
        name: 'mockOutcome1',
        semanticOutcome: 'mockSemantic1'
      },
      mockOutcome2 = {
        id: 2,
        name: 'mockOutcome2',
        semanticOutcome: 'mockSemantic2'
      },
      mockIntervention1 = {
        id: 1,
        name: 'mockIntervention1',
        semanticIntervention: 'mockSemantic1'
      },
      mockIntervention2 = {
        id: 2,
        name: 'mockIntervention2',
        semanticIntervention: 'mockSemantic2'
      },
      mockAnalysis = {
        name: 'analysisName',
        type: 'Single-study Benefit-Risk',
        study: null,
        selectedOutcomes: [mockOutcome1],
        selectedInterventions: [mockIntervention1],
        $save: function () {}
      },
      mockProject = {
        id: 1,
        name: 'projectName'
      },
      mockStudy1 = {
        id: 1
      },
      mockStudy2 = {
        id: 2
      },
      mockStudies = [mockStudy1, mockStudy2],
      mockOutcomes = [mockOutcome1, mockOutcome2],
      mockInterventions = [mockIntervention1, mockIntervention2],
      projectDeferred,
      analysisDeferred,
      ctrl;

    beforeEach(module('addis.controllers'));

    beforeEach(inject(function ($controller, $q, $rootScope) {
      var mockStateParams = {
        projectId: mockProject.id,
        analysisId: mockAnalysis.id
      };

      scope = $rootScope;
      analysisService = jasmine.createSpyObj('analysisService', ['get', 'save']);
      analysisService.get.and.returnValue(mockAnalysis);
      projectsService = jasmine.createSpyObj('projectService', ['get']);
      projectsService.get.and.returnValue(mockProject);
      outcomeService = jasmine.createSpyObj('outcomeService', ['query']);
      outcomeService.query.and.returnValue(mockOutcomes);
      interventionService = jasmine.createSpyObj('interventionService', ['query']);
      interventionService.query.and.returnValue(mockInterventions);
      select2UtilService = jasmine.createSpyObj('select2UtilService', ['idsToObjects', 'objectsToIds']);
      select2UtilService.objectsToIds.and.returnValue(['1']);
      trialverseStudyService = jasmine.createSpyObj('trialverseStudyService', ['query']);
      trialverseStudyService.query.and.returnValue(mockStudies);

      projectDeferred = $q.defer();
      mockProject.$promise = projectDeferred.promise;
      analysisDeferred = $q.defer();
      mockAnalysis.$promise = analysisDeferred.promise;

      spyOn(mockAnalysis, '$save');

      ctrl = $controller('AnalysisController', {
        $scope: scope,
        $stateParams: mockStateParams,
        'ProjectsService': projectsService,
        'AnalysisService': analysisService,
        'OutcomeService': outcomeService,
        'InterventionService': interventionService,
        'Select2UtilService': select2UtilService,
        'TrialverseStudyService': trialverseStudyService
      });
    }));


    it('should only make loading.loaded true when both project and analysis are loaded', function () {
      expect(scope.loading.loaded).toBeFalsy();
      projectDeferred.resolve();
      scope.$apply();
      expect(scope.loading.loaded).toBeFalsy();

      analysisDeferred.resolve();
      scope.$apply();
      expect(scope.loading.loaded).toBeTruthy();
    });

    it('should place a list of outcomes on the scope when it is loaded', function () {
      expect(scope.outcomes).toEqual(mockOutcomes);
    });

    it('should place a list of interventions on the scope when it is loaded', function () {
      expect(scope.interventions).toEqual(mockInterventions);
    });

    it('should not set project and analysis information before they are loaded', function () {
      expect(scope.selectedOutcomeIds).toEqual([]);
      expect(scope.selectedInterventionIds).toEqual([]);
    });

    it('should place project information on the scope', function () {
      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      expect(scope.project).toEqual(mockProject);
    });

    it('should place analysis information on the scope', function () {
      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      expect(scope.analysis).toEqual(mockAnalysis);
    });

    it('should place selectedOutcomeId information on the scope', function () {
      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      expect(scope.selectedOutcomeIds).toEqual(['1']);
    });

    it('should place selectedOutcomeId information on the scope', function () {
      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      expect(scope.selectedInterventionIds).toEqual(['1']);
    });

    it('should place a list of studies on the scope when the project is loaded', function () {
      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      expect(scope.studies).toEqual(mockStudies);
    });

    it('should save the analysis when the selected outcomes change', function () {
      select2UtilService.idsToObjects.and.returnValue([mockOutcome1, mockOutcome2]);

      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      scope.analysis.$save.calls.reset();

      scope.selectedOutcomeIds = ['1', '2'];
      scope.$apply();
      scope.dirty = true;
      expect(scope.analysis.selectedOutcomes).toEqual([mockOutcome1, mockOutcome2]);
      expect(scope.analysis.$save).toHaveBeenCalled();
    });

    it('should save the analysis when the selected interventions change', function () {
      select2UtilService.idsToObjects.and.returnValue([mockIntervention1, mockIntervention2]);

      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      scope.analysis.$save.calls.reset();

      scope.selectedInterventionIds = ['1', '2'];
      scope.$apply();
      expect(scope.analysis.selectedInterventions).toEqual([mockIntervention1, mockIntervention2]);
      expect(scope.analysis.$save).toHaveBeenCalled();
    });

    it('should save the analysis when the selected study changes', function () {
      analysisDeferred.resolve();
      projectDeferred.resolve();
      scope.$apply();
      scope.analysis.$save.calls.reset();

      scope.analysis.studyId = 1;
      scope.$apply();
      expect(scope.analysis.$save).toHaveBeenCalled();
    });


  });
});