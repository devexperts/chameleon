/*-
 * #%L
 * Chameleon. Color Palette Management Tool
 * %%
 * Copyright (C) 2016 - 2017 Devexperts, LLC
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

var app = angular.module('chameleon', ['ngAnimate', 'ngSanitize', 'ui.bootstrap', 'ngRoute']);

var palettePath = '/api/palettes';
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
    success : 200,
    created : 201
};

app.config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
    $locationProvider.html5Mode(false);
    $locationProvider.hashPrefix('');

    $routeProvider
        .when('/', {
            templateUrl : 'templates/palettes.html'
        })
        .when('/palettes', {
            templateUrl : 'templates/palettes.html'
        })
        .when('/diff', {
            templateUrl : 'templates/diff.html'
        })
        .otherwise({
            redirectTo: '/palettes'
        })
    ;
}]);

app.controller('MainController',
    function ($uibModal, $document, $http, $scope) {

        const model = {};
        model.selectedPalettes = [];
        model.palettes = [];
        model.variablesView = {};
        model.variablesView.columns = {};
        model.variablesView.rows = {};
        $scope.model = model;
        $scope.changes = [];
        $scope.saveChangesPath = "";

        $scope.alerts = [];
    }
);


