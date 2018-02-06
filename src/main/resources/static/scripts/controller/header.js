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

app.controller('HeaderController',
    function ($http, $scope, $mdToast, $location, changesService, versionService) {
        var $controller = this;

        $controller.savingInProgress = false;

        $controller.unexpectedError = {
            type : 'danger',
            msg: "Save error, please try again later"
        };

        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };

        $controller.saveChanges = function () {
            var changes = changesService.getChanges();
            $controller.savingInProgress = true;
            Object.keys(changes).forEach(function(url) {
                $http.post(url, changes[url]
                ).then(function (response) {
                    if (response.status === HttpCodes.success) {
                        changes[url].forEach(function(change) {
                            change.snapshots.forEach(function(snapshot) {
                                changesService.changePaletteVariableSnapshot(snapshot);
                                changesService.removeChangesToShow(snapshot.variableId, snapshot.paletteId, 'color');
                                changesService.removeChangesToShow(snapshot.variableId, snapshot.paletteId, 'opacity');
                            });
                        });
                        changesService.reset();
                        $scope.alerts.push({
                            type: 'success',
                            msg: "Saved successfuly!"
                        });
                        $controller.savingInProgress = false;
                    } else {
                        $scope.alerts.push($controller.unexpectedError);
                        $controller.savingInProgress = false;
                    }
                }, function (exception) {
                    console.log(exception.data.message);//TODO better error logging
                    $scope.alerts.push($controller.unexpectedError);
                    $controller.savingInProgress = false;
                });
            });
        };

        $controller.getChanges = function () {
            return changesService.printChanges();
        };

        $controller.getChangesCount = function () {
            return changesService.getCount();
        };

        $controller.getVersions = function () {
            return versionService.getVersions;
        };

        $controller.hasVersions = function () {
            return versionService.getVersions.length > 1;
        };
    }
);