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

app.service('changesService', function () {
    var changes = {};
    var changesCount = 0;
    var prints = {};
    var changesPrint = "";
    var savedPaletteVariables = [];
    var changesToShow = [];

    function isSameSnapshot(snapshot) {
        var paletteIndex = getPaletteIndex(snapshot);
        if (paletteIndex === -1) {
            return false;
        }
        var palette = savedPaletteVariables[paletteIndex];
        var result = false;
        palette.variables.forEach(function(variable) {
            if (variable.id === snapshot.variableId && variable.color === snapshot.color
                && variable.opacity === snapshot.opacity) {
                result = true;
            }
        });
        return result;
    }

    function getPaletteIndex(snapshot) {
        var paletteIndex = -1;
        savedPaletteVariables.forEach(function(palette, i) {
            if (palette.id === snapshot.paletteId) {
                paletteIndex = i;
            }
        });
        return paletteIndex;
    }

    return {
        getChanges:function() {
            return changes;
        },
        printChanges:function() {
            return changesPrint;
        },
        addChanges:function(url, value, compare, merge, printFunc) {
            if (!Array.isArray(changes[url])) {
                changes[url] = [];
            }
            if (!Array.isArray(prints[url])) {
                prints[url] = [];
            }
            var values = changes[url];
            var valueIndex = -1;
            values.forEach(function(element, index) {
                if (compare(element, value)) {
                    valueIndex = index;
                }
            });

            let noChanges = value.snapshots.every(function(snapshot) {
                return isSameSnapshot(snapshot);
            });

            if (!noChanges) {
                if (valueIndex == -1) {
                    changes[url].push(value);
                } else {
                    merge(changes[url][valueIndex], value);
                }
            } else {
                value.snapshots.forEach(function(snapshot) {
                    var removeIndex = -1;
                    changes[url].forEach(function (element, index) {
                        element.snapshots.forEach(function(elementSnapshot) {
                            if (elementSnapshot.id === snapshot.id) {
                                removeIndex = index;
                            }
                        });
                    });
                    changes[url].splice(removeIndex, 1);
                });
            }

            this.recalculate();
        },
        recalculate:function() {
            changesCount = 0;
            Object.keys(changes).forEach(function(url) {
                changesCount += changes[url].length;
            });
            changesPrint = "";
            Object.keys(prints).forEach(function(url) {
                changesPrint += prints[url].join("\n") + "\n";
            });
        },
        getCount:function () {
            return changesCount;
        },
        reset:function() {
            changes = {};
            changesCount = 0;
        },
        changePaletteVariableSnapshot: function(snapshot) {
            var paletteIndex = getPaletteIndex(snapshot);
            if (paletteIndex === -1) {
                savedPaletteVariables.push({
                    id: snapshot.paletteId,
                    variables: []
                });
                paletteIndex = savedPaletteVariables.length - 1;
            }
            var found = false;
            savedPaletteVariables[paletteIndex].variables.forEach(function(variable) {
                if (variable.id === snapshot.variableId) {
                    variable.color = snapshot.color;
                    variable.opacity = snapshot.opacity;
                    found = true;
                }
            });
            if (!found) {
                savedPaletteVariables[paletteIndex].variables.push({
                    id: snapshot.variableId,
                    color: snapshot.color,
                    opacity: snapshot.opacity
                });
            }
        },
        getVariableValueBySnapshotAndName: function(snapshot, name) {
            var paletteIndex = getPaletteIndex(snapshot);
            if (paletteIndex === -1) {
                return '';
            }
            var result = '';
            savedPaletteVariables[paletteIndex].variables.forEach(function(variable) {
                if (variable.id === snapshot.variableId) {
                    result = variable[name];
                }
            });
            return result;
        },
        addChangesToShow: function(variableId, paletteId, type) {
            changesToShow.push({
                paletteId: paletteId,
                variableId: variableId,
                type: type
            });
        },
        removeChangesToShow: function (variableId, paletteId, type) {
            var index = -1;
            changesToShow.forEach(function (elem, i) {
                if (elem.variableId === variableId && elem.paletteId === paletteId && elem.type === type) {
                    index = i;
                }
            });
            if (index !== -1) {
                changesToShow.splice(index, 1);
            }
        },
        isInChangesToShow: function(type, variableId, paletteId) {
            var result = false;
            changesToShow.forEach(function (elem) {
                if (elem.variableId === variableId && elem.paletteId === paletteId && elem.type === type) {
                    result = true;
                }
            });
            return result;
        },
        setAttributes: function (source) {
            if (!!source) {
                changes = source.changes;
                changesToShow = source.changesToShow;
                prints = source.prints;
                savedPaletteVariables = source.savedPaletteVariables;
                this.recalculate();
            }
        },
        getAttributes: function () {
            return {
                changes: changes,
                changesToShow: changesToShow,
                prints: prints,
                savedPaletteVariables:savedPaletteVariables
            }
        }
    };
});