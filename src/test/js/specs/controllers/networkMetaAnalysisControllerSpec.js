define(['angular', 'angular-mocks', 'controllers'], function() {
  describe('the network meta-analysis controller', function() {
    var scope,
      analysisDeferred,
      mockAnalysis = {$save: function(){},
      outcome: {id: 2}},
      projectDeferred,
      mockProject = {
        id: 11
      },
      mockStateParams = {
        analysisId: 1,
        projectId: 11
      },
      mockOutcomes = [{id: 1}, {id: 2}],
      mockTrialData = {studies: [1,2,3]},
      outcomeResource,
      trialverseTrialDataResource;

    beforeEach(module('addis.controllers'));

    beforeEach(inject(function($rootScope, $controller, $q) {
      analysisDeferred = $q.defer();
      mockAnalysis.$promise = analysisDeferred.promise;

      spyOn(mockAnalysis, '$save');

      projectDeferred = $q.defer();
      mockProject.$promise = projectDeferred.promise;

      outcomesDeferred = $q.defer();
      mockOutcomes.$promise = outcomesDeferred.promise;

      scope = $rootScope;
      scope.$parent = {
        analysis: mockAnalysis,
        project: mockProject
      };
      outcomeResource = jasmine.createSpyObj('OutcomeResource', ['query']);
      outcomeResource.query.and.returnValue(mockOutcomes);

      trialverseTrialDataResource = jasmine.createSpyObj('TrialverseTrialDataResource', ['query']);
      trialverseTrialDataResource.query.and.returnValue(mockTrialData);


      $controller('NetworkMetaAnalysisController', {
        $scope: scope,
        $stateParams: mockStateParams,
        OutcomeResource: outcomeResource,
        TrialverseTrialDataResource: trialverseTrialDataResource
      });
    }));

    describe('when first initialised', function() {
      it('should inherit the parent\'s analysis and project', function() {
        expect(scope.analysis).toEqual(scope.$parent.analysis);
        expect(scope.project).toEqual(scope.$parent.project);
      });

      it('should place the list of selectable outcomes on the scope', function() {
        expect(outcomeResource.query).toHaveBeenCalledWith({
          projectId: mockProject.id
        });
        expect(scope.outcomes).toEqual(mockOutcomes);
      });

    });

    describe('when the analysis, outcomes and project are loaded', function() {

      beforeEach(inject(function($controller) {
        analysisDeferred.resolve(mockAnalysis);
        projectDeferred.resolve(mockProject);
        outcomesDeferred.resolve(mockOutcomes);
        scope.$apply();
      }));

      it('should save the analysis when the selected outcome changes', function() {
        scope.analysis.outcome = {id: 1};
        scope.saveAnalysis();
        expect(scope.analysis.$save).toHaveBeenCalled();
      });

    });

  });
});