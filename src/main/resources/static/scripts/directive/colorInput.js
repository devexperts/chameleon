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

app.directive('colorInput', function () {
    return {
        template: '<input type="text" class="form-control" ' +
        'id="snapshot_color_input_{{snapshot.variableId}}_{{snapshot.paletteId}}"' +
        'pattern-validation ' +
        'ng-model="snapshot.color" ' +
        'ng-change="palette_controller.trimInput(\'color\', snapshot); ' +
        'palette_controller.setAlphaDefaultIfHexExists(snapshot); ' +
        'palette_controller.addChange(row.variable, [snapshot])" ' +
        'ng-focus="palette_controller.focusOnElement(\'color\', snapshot.variableId, snapshot.paletteId); $event.target.select()" ' +
        'ng-blur="palette_controller.hexInputOnBlur(snapshot); ' +
        'palette_controller.removeFocusFromElement(\'color\', snapshot.variableId, snapshot.paletteId); ' +
        'palette_controller.addChange(row.variable, [snapshot])" ' +
        'ng-class="palette_controller.validateAndGetStyle(\'color\', snapshot)" ' +
        'placeholder="Not set">',
        link: function(scope, elm, attrs) {}
    };
});