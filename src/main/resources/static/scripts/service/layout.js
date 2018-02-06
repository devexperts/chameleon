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
const EMPTY_LAYOUT = {
    timestamp: {},
    model: {
        selectedPalettes: [],
        palettes: [],
        variablesView: {
            columns: [],
            rows: []
        }
    },
    changes: {
        changes: {},
        prints: {},
        savedPaletteVariables: [],
        changesToShow: []
    }
};
const LAYOUT_EXPIRATION_PERIOD_IN_DAYS = 7;
const LAYOUT_LOCAL_STORAGE_NAME = 'chameleon_layout';

app.service('layoutService', function ($http, $window) {
    function removeLayout() {
        $window.localStorage.removeItem(LAYOUT_LOCAL_STORAGE_NAME);
    }

    function resolveLayout(layout) {
        if (!!layout && !!layout.model && !!layout.changes && !!layout.timestamp) {
            let currentDate = new Date();
            const layoutDate = new Date(layout.timestamp);
            if (layoutDate > currentDate.setDate(currentDate.getDate() - LAYOUT_EXPIRATION_PERIOD_IN_DAYS)) {
                return layout;
            } else {
                removeLayout();
                return EMPTY_LAYOUT;
            }
        }
        removeLayout();
        return EMPTY_LAYOUT;
    }

    return {
        saveLayout: function (model, changes) {
            $window.localStorage.setItem(LAYOUT_LOCAL_STORAGE_NAME, angular.toJson({
                model: model,
                changes: changes,
                timestamp: new Date()

            }));
        },
        loadLayout: function () {
            var layout = angular.fromJson($window.localStorage.getItem(LAYOUT_LOCAL_STORAGE_NAME));
            return resolveLayout(layout);
        }
    };
});