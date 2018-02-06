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

app.directive('colors', function () {
    return {
        template: ' <div class="col-xs-1 color-container">' +
        ' <div class="color-block" style="background:#{{snapshot.color}}; border-right: 1px solid #E6E6E6;"></div><div class="color-block" style="background:#{{snapshot.color}}; opacity: {{(snapshot.opacity === null || snapshot.opacity === 0) ? 1 : snapshot.opacity / 100}};"></div>' +
        '</div>',
        link: function(scope, elm, attrs) {}
    };
});