package com.devexperts.chameleon.repository;

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

import com.devexperts.chameleon.entity.UiComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link UiComponentEntity}.
 *
 */
@org.springframework.stereotype.Repository
public interface UiComponentRepository extends JpaRepository<UiComponentEntity, Long> {

    /**
     * Find UI component by name
     *
     * @param name
     * @return {@link UiComponentEntity}
     */
    UiComponentEntity findFirstByNameIgnoreCase(String name);
}
