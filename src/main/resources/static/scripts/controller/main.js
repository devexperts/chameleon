/*-
 * #%L
 * Chameleon. Color Palette Management Tool
 * %%
 * Copyright (C) 2016 - 2018 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

var app = angular.module('chameleon', ['ngAnimate', 'ngSanitize', 'ui.bootstrap', 'ngRoute', 'ngMaterial']);

var palettePath = '/api/palettes';
var renamePath = 'api/palettes/rename';
var removePalettePath = 'api/palettes/remove/';
var selectedPaletteViewPath = '/view/variables';
var variablePath = '/api/variables';
var variableSnapshotPath = '/api/variablesnapshots';
var commitsPath = 'api/commits';
var variableSnapshotDeleteVariablePath = '/deletevariable';
var variableSnapshotViewPath = variableSnapshotPath + '/view';
var variableSnapshotCopyPath = '/copy';
var variableSnapshotDiffPath = '/diff';
var variableNamePath = "/name";
var variableSaveChangesPath = variableSnapshotPath + "/save";
var commitsByPaletteIdPath = commitsPath + '/palette';

var HttpCodes = {
    success: 200,
    created: 201,
    notModified: 304
};

var INPUT_STYLE = {
    ERROR: 'input-validation-error',
    DEFAULT: 'input-default',
    CHANGED: 'input-data-changed'
};

app.config(['$locationProvider', '$routeProvider', function ($locationProvider, $routeProvider) {
    $locationProvider.html5Mode(false);
    $locationProvider.hashPrefix('');

    $routeProvider
        .when('/', {
            templateUrl: 'templates/palettes.html'
        })
        .when('/palettes', {
            templateUrl: 'templates/palettes.html'
        })
        .when('/diff', {
            templateUrl: 'templates/diff.html'
        })
        .otherwise({
            redirectTo: '/palettes'
        });
}]);

app.controller('MainController',
    function ($uibModal, $document, $http, $scope, $window, changesService, layoutService) {
        $window.onbeforeunload = function () {
            layoutService.saveLayout($scope.model, changesService.getAttributes());
        };

        $scope.changes = [];
        $scope.saveChangesPath = "";
        $scope.alerts = [];
        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };

        var layout = layoutService.loadLayout();
        $scope.model = layout.model;
        changesService.setAttributes(layout.changes);
    }
);


