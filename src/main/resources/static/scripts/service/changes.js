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

app.service('changesService', function () {
    var changes = {};
    var changesCount = 0;
    var prints = {};
    var changesPrint = "";

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
                    return;
                }
            });

            if (valueIndex == -1) {
                changes[url].push(value);
            } else {
                merge(changes[url][valueIndex], value);
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
        }
    };
});