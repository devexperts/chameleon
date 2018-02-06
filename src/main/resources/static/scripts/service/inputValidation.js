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

const HEX_PATTERN = /(^[A-Fa-f0-9]{3}$|^[A-Fa-f0-9]{6}$)/;
const OPACITY_PATTERN = /^([1-9]?[0-9]|100)$/;

app.service('inputValidationService', function (changesService) {
    return {
        hexInputOnBlur: function (snapshot) {
            if (snapshot !== null) {
                if (snapshot.color) {
                    const length = snapshot.color.length;
                    let oldValue = snapshot.color;
                    if (length === 3) {
                        snapshot.color = oldValue + oldValue;
                    } else if (length > 6) {
                        snapshot.color = oldValue.substring(0, 6);
                    }

                    snapshot.color = snapshot.color.toUpperCase();
                    if (!snapshot.opacity) {
                        snapshot.opacity = 100;
                    }
                }
            }
        },

        setAlphaDefaultIfHexExists: function (snapshot) {
            if (!snapshot.opacity && snapshot.color) {
                snapshot.opacity = 100;
            }
        },

        trimInput: function (type, snapshot) {
            if (snapshot !== null) {
                switch (type) {
                    case 'color':
                        if (snapshot.color) {
                            snapshot.color = snapshot.color.startsWith("#") ? snapshot.color.substring(1): snapshot.color;
                            snapshot.color = snapshot.color.replace(/\W/, "");
                            snapshot.color = snapshot.color.length > 6 ? snapshot.color.substring(0, 6) : snapshot.color;
                        }
                        break;
                    case 'opacity':
                        if (snapshot.opacity) {
                            snapshot.opacity = snapshot.opacity.length > 3 ? snapshot.opacity.substring(0, 3) : snapshot.opacity;
                        }
                        break;
                }
            }
        },

        validateAndGetStyle: function(type, snapshot) {
            const isValidValue = function (type, snapshot) {
                if (snapshot !== null) {
                    switch (type) {
                        case 'color': {
                            let color = snapshot.color;
                            return color === null || HEX_PATTERN.test(color);
                        }
                        case 'opacity': {
                            let opacity = snapshot.opacity;
                            return opacity === null || OPACITY_PATTERN.test(opacity);
                        }
                    }
                } else {
                    return true;
                }
            };

            if (!isValidValue(type, snapshot)) {
                return INPUT_STYLE.ERROR;
            } else {
                return snapshot !== null && changesService.isInChangesToShow(type, snapshot.variableId, snapshot.paletteId) ? INPUT_STYLE.CHANGED : INPUT_STYLE.DEFAULT;
            }
        },

        alphaOnBlur: function (snapshot, previous) {
            if (snapshot !== null) {
                if (snapshot.opacity) {
                    snapshot.opacity = snapshot.opacity.toString();
                    snapshot.opacity = snapshot.opacity.startsWith("0") && snapshot.opacity.length > 1
                        ? snapshot.opacity.replace("^0+", "")
                        : snapshot.opacity;
                    snapshot.opacity = snapshot.opacity.replace(/\D/g, "");
                    snapshot.opacity = snapshot.opacity.length < 1? previous.toString(): snapshot.opacity;
                    let parsed = parseInt(snapshot.opacity);
                    snapshot.opacity = parsed > 100 || parsed < 0 ? previous : parsed;
                } else {
                    snapshot.opacity = previous ? previous : 100;
                }
            }
        },
    }
});