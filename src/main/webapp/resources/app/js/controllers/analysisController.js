'use strict';
define(['underscore'], function () {
  var dependencies = ['$scope', '$stateParams', '$q',
    'ProjectsService', 'AnalysisService', 'OutcomeService', 'InterventionService', 'Select2UtilService'
  ];
  var AnalysisController = function ($scope, $stateParams, $q,
    ProjectsService, AnalysisService, OutcomeService, InterventionService, Select2UtilService) {

    $scope.loading = {
      loaded: false
    };



    $scope.project = ProjectsService.get($stateParams);
    $scope.analysis = AnalysisService.get($stateParams);

    $scope.outcomes = OutcomeService.query($stateParams);
    $scope.interventions = InterventionService.query($stateParams);
    $scope.selectedOutcomeIds = [];
    $scope.selectedInterventionIds = [];

    $scope.dirty = false;



    $q.all([
      $scope.project.$promise,
      $scope.analysis.$promise
    ]).then(function () {
      $scope.loading.loaded = true;
      $scope.selectedOutcomeIds = Select2UtilService.objectsToIds($scope.analysis.selectedOutcomes);
      $scope.selectedInterventionIds = Select2UtilService.objectsToIds($scope.analysis.selectedInterventions);

      $scope.$watchCollection('selectedOutcomeIds', function () {
        $scope.analysis.selectedOutcomes = Select2UtilService.idsToObjects($scope.selectedOutcomeIds, $scope.outcomes);
        $scope.dirty = true;
      });

      $scope.$watchCollection('selectedInterventionIds', function () {
        $scope.analysis.selectedInterventions = Select2UtilService.idsToObjects($scope.selectedInterventionIds, $scope.interventions);
        $scope.dirty = true;
      });

      $scope.$watch('dirty', function () {
        if ($scope.dirty) {
          AnalysisService.save($scope.analysis, function () {
            $scope.dirty = false;
          });
        }

      });
    });

  };
  return dependencies.concat(AnalysisController);
});