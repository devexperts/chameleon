/*
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

app.directive('patternValidation', function() {
    return {
        require: '?ngModel',
        link: function(scope, elm, attrs, ctrl) {
            if (ctrl) {
                ctrl.$validators.pattern = function(modelValue) {
                    let inputPattern = /(^.*\W+.*$|^[A-Za-z0-9]{3}$|^[A-Za-z0-9]{6,}$|^(#[A-Za-z0-9]){1,7})/;
                    return modelValue === null || inputPattern.test(modelValue);
                };
            }
        }
    };
});