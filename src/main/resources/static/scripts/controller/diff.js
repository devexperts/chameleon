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

app.controller('DiffController',
    function ($http, $scope, $location, $uibModal, changesService, versionService) {
        var $controller = this;
        $controller.model= {};
        $controller.model.paletteName = "";
        $controller.model.commitsView = {} ;

        $controller.alerts = $scope.alerts;
        $controller.closeAlert = function(index) {
            $controller.alerts.splice(index, 1);
        };

        $controller.noDiffSelected = {
            type : 'info',
            msg: "Please select palette for diff"
        };

        if(versionService.getVersions() != null && versionService.getVersions().from != null && versionService.getVersions().to != null) {
            getSelectedCommitsView();
        } else {
            $scope.alerts.push($controller.noDiffSelected);
        }

        $controller.showVariable = function (variable) {
            $uibModal.open({
                animation: true,
                templateUrl: 'templates/variable.html',
                controller: 'VariableModalInstanceCtrl',
                controllerAs: '$controller',
                appendTo: undefined,
                resolve: {
                    selectedPalettes: function () {
                        return $controller.model.selectedPalettes;
                    },
                    variable: function () {
                        return variable;
                    }
                }
            });
        };

        function getSelectedCommitsView() {

            var commitIds = '/'+versionService.getVersions().from.id+'/'+versionService.getVersions().to.id;

            $http.get(variableSnapshotPath + '/' + variableSnapshotDiffPath + '/' + commitIds
            ).then(function(response) {
                if ( response.status === HttpCodes.success ) {
                    $controller.model.commitsView = response.data;
                    $controller.model.paletteName = versionService.getVersions().palette_name;
                } else {
                    $controller.errorMessage = "Something is wrong, status code:" + response.statusText;
                }
            }, function(exception) {
                $controller.errorMessage = "Something is wrong, status code:" + exception.data.message;
            });
        }
    }
);
