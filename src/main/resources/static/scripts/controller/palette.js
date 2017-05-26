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

app.controller('AddPaletteModalInstanceCtrl', function ($uibModalInstance, $http) {
    var $controller = this;

    $controller.name = null;
    $controller.errorMessage = null;

    $controller.ok = function () {
        var parameter = {
            id: null,
            name: $controller.name
        };

        $http.post(palettePath, parameter)
            .then(function (response) {
                if (response.status === HttpCodes.created) {
                    $uibModalInstance.close();
                }
            }, function (exception) {
                $controller.errorMessage = exception.data.message;
            });
    };

    $controller.cancel = function () {
        $uibModalInstance.dismiss();
    };
});

app.controller('VariableModalInstanceCtrl', function ($uibModalInstance, $http, $uibModal, selectedPalettes, variable, changesService) {
    var $controller = this;

    $controller.errorMessage = null;

    $controller.view = {};
    $controller.view.variable = {};
    $controller.view.variable.id = null;
    $controller.view.variable.name = null;
    $controller.view.variable.usage = null;
    $controller.view.palettes = [];
    $controller.view.snapshots = [];

    setDialogMode(variable);
    getAllPalettesWithSnapshots(variable);

    function getAllPalettesWithSnapshots(variable) {
        $http.get(variableSnapshotViewPath + getVariablePath()
        ).then(function (response) {
            if (response.status === HttpCodes.success) {
                $controller.view = response.data;
                $controller.view.variable = $controller.view.variable == null ? variable : $controller.view.variable;
                replaceByChanges();
            } else {
                $controller.errorMessage = "Something is wrong, status code:" + response.statusText;
            }
        }, function (exception) {
            $controller.errorMessage = "Something is wrong, status code:" + exception.data.message;
        });

        function getVariablePath() {
            return (variable == null || variable.id == undefined ? '' : '/' + variable.id);
        }
    }

    function replaceByChanges() {
        var changes = changesService.getChanges()[variableSaveChangesPath];
        if (changes != undefined && $controller.view) {
            changes.forEach(function(element, index) {
                if (compareVariableViews(element, $controller.view)) {
                    $controller.view.snapshots = changes[index].snapshots;
                    return;
                }
            });
        }
    }

    function setDialogMode(variable) {
        if (variable) {
            $controller.dialogTitle = "Edit Variable";
            $controller.okButtonTitle = "Save";
        } else {
            $controller.dialogTitle = "Add Variable";
            $controller.okButtonTitle = "Add";
        }
    }

    $controller.getSnapshot = function(paletteId) {
        return getSnapshot(paletteId, $controller.view.snapshots)
    }

    $controller.checkVariableName = function () {
        $http.get(variablePath + variableNamePath +'/'+ $controller.view.variable.name)
            .then(function(response) {
                if ( response.status === HttpCodes.success ) {
                    if (response.data) {
                        $controller.errorMessage = null;
                    }
                } else {
                    $controller.errorMessage = "Something is wrong, status code:" + response.statusText;
                }

                setDialogMode(response.data);

                if (response.data) {
                    getAllPalettesWithSnapshots(response.data);
                }

            }, function(exception) {
                $controller.errorMessage = "Something is wrong, status code:" + exception.data.message;
            });
    };

    $controller.ok = function () {
        var validVariableView = {};
        validVariableView.variable = $controller.view.variable;
        validVariableView.snapshots = $controller.view.snapshots.filter(function(x){return x.color != null});

        changesService.addChanges(variableSaveChangesPath, validVariableView,
            compareVariableViews,
            function(element) {
                return 'VariableView - id: ' + element.variable.id + ' name: ' + element.variable.name;
            });
        $uibModalInstance.close();
    };

    $controller.cancel = function () {
        $uibModalInstance.dismiss();
    };

    $controller.delete = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'templates/variableDeleteConfirmation.html',
            controller: 'DeleteVariableConfirmationModalInstanceCtrl',
            controllerAs: '$controller',
            appendTo: undefined,
            size: 'md',
            resolve: {
                variable: function () {
                    return $controller.view.variable;
                },
                snapshots: function () {
                    return $controller.view.snapshots;
                }
            }
        });

        modalInstance.result.then(function () {
                $uibModalInstance.close();
            },
            angular.noop
        );
    };
});

app.controller('DeleteVariableConfirmationModalInstanceCtrl', function ($uibModalInstance, $http, variable, snapshots) {
    var $controller = this;

    $controller.errorMessage = null;
    $controller.snapshotsCount = snapshots.filter(function(x){return x.color != null}).length;

    $controller.ok = function () {
        $http.delete(variableSnapshotPath + variableSnapshotDeleteVariablePath + '/' + variable.id
        ).then(function(response) {
            if ( response.status === HttpCodes.success ) {
                $uibModalInstance.close();
            } else {
                $controller.errorMessage = response.data;
            }
        }, function(exception) {
            $controller.errorMessage = exception.data.message;
        });
    };

    $controller.cancel = function () {
        $uibModalInstance.dismiss();
    };
});

app.controller('CopyPaletteConfirmationModalInstanceCtrl', function ($uibModalInstance, $http, sourcePalette, targetPalette) {
    var $controller = this;

    $controller.errorMessage = null;
    $controller.sourcePalette = sourcePalette;
    $controller.targetPalette = targetPalette;

    $controller.ok = function () {
        $http.put(variableSnapshotPath + variableSnapshotCopyPath + '/' + sourcePalette.id + '/' + targetPalette.id
        ).then(function(response) {
            if ( response.status === HttpCodes.success ) {
                $uibModalInstance.close();
            } else {
                $controller.errorMessage = response.data;
            }
        }, function(exception) {
            $controller.errorMessage = exception.data.message;
        });
    };

    $controller.cancel = function () {
        $uibModalInstance.dismiss();
    };
});

app.controller('SelectPalettesModalInstanceCtrl', function ($uibModalInstance, $http, selectedPalettes, $uibModal) {
    var $controller = this;

    $controller.sourcePalette = null;
    $controller.name = null;
    $controller.errorMessage = null;
    $controller.palettes = [];
    $controller.selectedPalettes = selectedPalettes;
    $controller.copyDialogIsOpen = [];
    $controller.hover = [];
    $controller.commits = [];

    getAllPalettes();

    function getAllPalettes() {
        $http.get(palettePath
        ).then(function(response) {
            if ( response.status === HttpCodes.success ) {
                $controller.palettes = response.data;
                $controller.copyDialogIsOpen.fill(false, 0, $controller.palettes - 1);
                $controller.hover.fill(false, 0, $controller.palettes - 1 );
                $controller.sourcePalette = $controller.palettes[0];
            } else {
                $controller.errorMessage = "Something is wrong, status code:" + response.statusText;
            }
        }, function(exception) {
            $controller.errorMessage = "Something is wrong, status code:" + exception.data.message;
        });
    }

    $controller.isSelectedPalette = function (palette) {
        return $controller.selectedPalettes.some(function (selectedPalette) {
            return palette.name === selectedPalette.name;
        })
    };

    function arrayObjectIndexOf(array, property, item) {
        for(var i = 0, len = array.length; i < len; i++) {
            if (array[i][property] === item[property])
                return i;
        }
        return -1;
    }

    $controller.toggleSelection = function toggleSelection(palette) {

        var paletteIndex = arrayObjectIndexOf(selectedPalettes,"id", palette);

        if (paletteIndex > -1) {
            $controller.selectedPalettes.splice(paletteIndex, 1);
        } else {
            $controller.selectedPalettes.push(palette);
        }

        $controller.selectedPalettes.sort(function (a, b) {
            if (a.name > b.name) {
                return 1;
            }
            if (a.name < b.name) {
                return -1;
            }
            return 0;
        });
    };

    $controller.ok = function () {
        $uibModalInstance.close($controller.selectedPalettes)
    };

    $controller.cancel = function () {
        $uibModalInstance.dismiss();
    };

    $controller.copy = function (targetPaletteIndex) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'templates/paletteCopyConfirmation.html',
            controller: 'CopyPaletteConfirmationModalInstanceCtrl',
            controllerAs: '$controller',
            appendTo: undefined,
            size: 'md',
            resolve: {
                sourcePalette: function () {
                    return $controller.sourcePalette;
                },
                targetPalette: function () {
                    return $controller.palettes[targetPaletteIndex];
                }
            }
        });

        modalInstance.result.then(function () {
            $uibModalInstance.close($controller.selectedPalettes);
        }, angular.noop);
    };

    $controller.cancelCopy = function (index) {
        $controller.copyDialogIsOpen[index] = false;
        $controller.hover[index] = false;
    };

    $controller.hoverIn = function(index){
        function isAllPopoversClosed() {
            return $controller.copyDialogIsOpen.every(function (value) {
                return !value;
            });
        }

        if (isAllPopoversClosed()) {
            $controller.hover[index] = true;
        }
    };

    $controller.hoverOut = function(index){
        if (!$controller.copyDialogIsOpen.some(function (isOpen) {return isOpen})) {
            $controller.hover[index] =  false;
        }
    };

    $controller.selectCommits = function (palette_id, palette_name) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'templates/dialog/selectCommits.html',
            controller: 'CommitController',
            controllerAs: '$controller',
            size: 'md',
            appendTo: undefined,
            resolve: {
                palette_id: function() {
                    return palette_id;
                },
                palette_name: function() {
                    return palette_name;
                }
            }
        });

        modalInstance.result.then(function () {
            $uibModalInstance.dismiss();
        }, angular.noop);
    };
});

app.controller('CommitController',
    function ($uibModalInstance, $http, $location, palette_id, palette_name, versionService) {
        var $controller = this;

        $controller.commits = null;
        $controller.fromCommit = null;
        $controller.toCommit = null;

        recieveCommits();

        function recieveCommits() {
            $http.get(commitsByPaletteIdPath + '/' + palette_id
            ).then(function(response) {
                if ( response.status === HttpCodes.success ) {
                    $controller.commits = response.data;
                } else {
                    $controller.errorMessage = "Something is wrong, status code:" + response.statusText;
                }
            }, function(exception) {
                $controller.errorMessage = "Something is wrong, status code:" + exception.data.message;
            });
        }

        $controller.save = function(){
            versionService.addVersion('from', $controller.fromCommit);
            versionService.addVersion('to', $controller.toCommit);
            versionService.addVersion('palette_name', palette_name);
            $uibModalInstance.close();
            $location.path(variableSnapshotDiffPath);
        };

        $controller.cancel = function () {
            $uibModalInstance.dismiss();
        };
    });

app.controller('PaletteController',
    function ($uibModal, $http, $scope, changesService) {
        var $controller = this;
        $controller.model = $scope.model;

        $controller.errorMessage = null;
        $controller.alerts = $scope.alerts;
        $controller.onlyNumbers = /^\d+$/;
        $controller.closeAlert = function(index) {
            $controller.alerts.splice(index, 1);
        };

        $controller.addPalette = function () {
            $uibModal.open({
                animation: true,
                templateUrl: 'templates/addPalette.html',
                controller: 'AddPaletteModalInstanceCtrl',
                controllerAs: '$controller',
                size: 'md',
                appendTo: undefined
            });
        };

        $controller.showVariable = function (variable) {
            var modalInstance = $uibModal.open({
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

            modalInstance.result.then(function () {
                    getSelectedPalettesView();
                },
                angular.noop
            );
        };

        $controller.selectPalettes = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'templates/selectPalettes.html',
                controller: 'SelectPalettesModalInstanceCtrl',
                controllerAs: '$controller',
                size: 'md',
                appendTo: undefined,
                resolve: {
                    selectedPalettes: function () {
                        return $controller.model.selectedPalettes;
                    }
                }
            });

            modalInstance.result.then(function (selectedPalettes) {
                    $controller.model.selectedPalettes = selectedPalettes;
                    getSelectedPalettesView();
                },
                angular.noop
            );
        };

        function getPaletteId(palette) {
            return palette.id;
        }

        function getSelectedPalettesView() {

            if ($controller.model.selectedPalettes.length === 0) {
                $controller.model.palettesView = {};
                return;
            }
            var palettesIds = $controller.model.selectedPalettes.map(getPaletteId).join();

            $http.get(palettePath + selectedPaletteViewPath +'/'+ palettesIds
            ).then(function(response) {
                if ( response.status === HttpCodes.success ) {
                    $controller.model.palettesView = response.data;
                    replaceByChanges();
                } else {
                    $controller.errorMessage = "Something is wrong, status code:" + response.statusText;
                }
            }, function(exception) {
                $controller.errorMessage = "Something is wrong, status code:" + exception.data.message;
            });
        }

        function replaceByChanges() {
            var changes = changesService.getChanges()[variableSaveChangesPath];
            if (changes) {
                changes.forEach(function(changeElement, changeIndex) {
                    matchedSnapshots = filterSnapshotsByPalettes($controller.model.selectedPalettes, changeElement.snapshots);

                    if (matchedSnapshots.length > 0) {
                        var found = false
                        $controller.model.palettesView.rows.forEach(function(dataElement, dataIndex) {
                            if (compareVariableViews(changeElement, dataElement)) {
                                $controller.model.palettesView.rows[dataIndex].snapshots = matchedSnapshots;
                                found = true;
                            }
                        });

                        if (!found) {
                            $controller.model.palettesView.rows.push({
                                variable: changeElement.variable,
                                snapshots: matchedSnapshots
                            })
                        }
                    }
                });
            }
        }

        function filterSnapshotsByPalettes(palettes, snapshots) {
            var result = [];
            var selectedPalettesIds = $controller.model.selectedPalettes.map(getPaletteId);
            snapshots.forEach(function(snapshot, index) {
                if (selectedPalettesIds.indexOf(snapshot.paletteId) >= 0) {
                    result.push(snapshot);
                }
            });
            return result;
        }

        $controller.getSnapshot = function(paletteId, snapshots) {
            return getSnapshot(paletteId, snapshots);
        };

        $controller.addChange = function (variable, snapshots) {
            changesService.addChanges(variableSaveChangesPath,
                {
                    variable: variable,
                    snapshots: snapshots
                },
                compareVariableViews,
                mergeVariableViews,
                function(element) {
                    return 'VariableView - id: ' + element.variable.id + ' name: ' + element.variable.name;
                });
        };
    }
);

function getSnapshot(paletteId, snapshots) {
    var result = null;
    snapshots.forEach(function(snapshot, index) {
        if (snapshot.paletteId == paletteId) {
            result = snapshot;
        }
    });
    return result;
}

function compareVariableViews(left, right) {
    return left === right || left.variable.name == right.variable.name;
}

function mergeVariableViews(oldValue, newValue) {
    newValue.snapshots.forEach(function(element2){
        var isEquals = oldValue.snapshots.some(function(element1){
            return element1.paletteId === element2.paletteId;
        });
        if(!isEquals){
            oldValue.snapshots.push(element2);
        }
    });
}