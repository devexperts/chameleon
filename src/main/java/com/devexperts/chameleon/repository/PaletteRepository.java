package com.devexperts.chameleon.repository;

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

import com.devexperts.chameleon.entity.PaletteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for the {@link PaletteEntity}.
 *
 */
@org.springframework.stereotype.Repository
public interface PaletteRepository extends JpaRepository<PaletteEntity, Long> {

    /**
     * Find palette by name
     *
     * @param name
     * @return {@link PaletteEntity}
     */
    PaletteEntity findFirstByActiveTrueAndNameIgnoreCase(String name);

    PaletteEntity findAllByName(String name);

    /**
     * Find all palettes ordered by name
     *
     * @return list of {@link PaletteEntity}
     */
    List<PaletteEntity> findAllByActiveTrueOrderByNameAsc();

    /**
     * Find all palettes by ids
     *
     * @param ids
     * @return list of {@link PaletteEntity}
     */
    List<PaletteEntity> findAllByIdInAndActiveTrue(List<Long> ids);

    /**
     * Returns native query result produced by cross join variables and palettes
     *
     * result:
     *
     *     variable  | palette    | snapshot
     *    -----------+------------+-------------
     *     variable1 | palette1   | snapshot1
     *     variable1 | palette2   | snapshot2
     *     variable1 | palette3   | snapshot3
     *     variable2 | palette1   | snapshot4
     *     variable2 | palette2   | snapshot5
     *     variable2 | palette4   | snapshot6
     *
     * view:
     *              | palette1  | palette2  | palette3
     *    ----------+------------+------------+----------
     *    variable1 | snapshot1 | snapshot2 | snapshot3
     *    variable2 | snapshot4 | snapshot5 | snapshot6
     *
     * @param paletteIds palette ids
     * @param commitIds commit ids
     * @return list of native query result. Will be converted by {@link com.devexperts.chameleon.converter.PaletteViewCellConverter}
     */
    @Deprecated
    @Query(value =
            "SELECT " +
            "      ALL_PALETTES_VARS.VARIABLE_ID, " +
            "      VARIABLE.NAME, " +
            "      VARIABLE.USAGE, " +
            "      ALL_PALETTES_VARS.PALETTE_ID," +
            "      ALL_PALETTES_VARS.PALETTE_NAME, " +
            "      SNAPSHOT.SNAPSHOT_ID, " +
            "      SNAPSHOT.COLOR, SNAPSHOT.OPACITY " +
            "FROM " +
            "      (SELECT " +
            "             VAR_SNAP.VARIABLE_ID, " +
            "             PAL.ID AS PALETTE_ID, " +
            "             PAL.NAME AS PALETTE_NAME " +
            "       FROM " +
            "             (SELECT " +
            "                    ID, " +
            "                    NAME " +
            "              FROM " +
            "                    PALETTE " +
            "                    WHERE ID IN ?1 AND IS_ACTIVE = TRUE) AS PAL " +
            "              CROSS JOIN " +
            "                    (SELECT " +
            "                           DISTINCT VARIABLE_ID " +
            "                     FROM VARIABLE_SNAPSHOT " +
            "                     WHERE COMMIT_ID IN ?2 ) AS VAR_SNAP) AS ALL_PALETTES_VARS " +
            "LEFT JOIN " +
            "      (SELECT " +
            "             VARIABLE_ID, " +
            "             PALETTE_ID, " +
            "             COLOR, " +
            "             OPACITY, " +
            "             ID AS SNAPSHOT_ID " +
            "       FROM  " +
            "             VARIABLE_SNAPSHOT " +
            "       WHERE COMMIT_ID IN ?2) AS SNAPSHOT " +
            "ON ALL_PALETTES_VARS.VARIABLE_ID = SNAPSHOT.VARIABLE_ID AND ALL_PALETTES_VARS.PALETTE_ID= SNAPSHOT.PALETTE_ID " +
            "LEFT JOIN " +
            "       VARIABLE " +
            "ON VARIABLE.ID = ALL_PALETTES_VARS.VARIABLE_ID " +
            "ORDER BY VARIABLE.NAME,  ALL_PALETTES_VARS.PALETTE_NAME", nativeQuery = true)
    List<Object[]> getPaletteVariableView(List<Long> paletteIds, List<Long> commitIds);

    /**
     * Returns native query result produced by cross join variables and commits
     *
     * result:
     *
     *     variable  | commit     | snapshot
     *    -----------+------------+-------------
     *     variable1 | commit1   | snapshot1
     *     variable1 | commit2   | snapshot2
     *     variable1 | commit3   | snapshot3
     *     variable2 | commit1   | snapshot4
     *     variable2 | commit2   | snapshot5
     *     variable2 | commit4   | snapshot6
     *
     * view:
     *              | commit1   | commit2   | commit3
     *    ----------+-----------+-----------+----------
     *    variable1 | snapshot1 | snapshot2 | snapshot3
     *    variable2 | snapshot4 | snapshot5 | snapshot6
     *
     * @param commitIds commit ids
     * @param variableIds variable ids
     * @return list of native query results. Will be converted by {@link com.devexperts.chameleon.converter.CommitViewCellConverter}
     */
    @Query(value =
            "SELECT " +
            "      ALL_PALETTES_VARS.VARIABLE_ID, " +
            "      VARIABLE.NAME, " +
            "      VARIABLE.USAGE, " +
            "      ALL_PALETTES_VARS.PALETTE_ID," +
            "      ALL_PALETTES_VARS.COMMIT_ID, " +
            "      ALL_PALETTES_VARS.UPDATE_TIME, " +
            "      SNAPSHOT.SNAPSHOT_ID, " +
            "      SNAPSHOT.COLOR, SNAPSHOT.OPACITY " +
            "FROM " +
            "      (SELECT " +
            "             VAR_SNAP.VARIABLE_ID, " +
            "             COM.PALETTE_ID AS PALETTE_ID, " +
            "             COM.ID AS COMMIT_ID, " +
            "             COM.UPDATE_TIME AS UPDATE_TIME " +
            "       FROM " +
            "             (SELECT " +
            "                    PALETTE_ID, " +
            "                    ID, " +
            "                    UPDATE_TIME " +
            "              FROM " +
            "                    COMMIT " +
            "                    WHERE ID IN ?1) AS COM " +
            "              CROSS JOIN " +
            "                    (SELECT " +
            "                           DISTINCT VARIABLE_ID " +
            "                     FROM VARIABLE_SNAPSHOT " +
            "                     WHERE COMMIT_ID IN ?1 AND VARIABLE_ID  IN ?2 ) AS VAR_SNAP) AS ALL_PALETTES_VARS " +
            "LEFT JOIN " +
            "      (SELECT " +
            "             VARIABLE_ID, " +
            "             COMMIT_ID, " +
            "             COLOR, " +
            "             OPACITY, " +
            "             ID AS SNAPSHOT_ID " +
            "       FROM  " +
            "             VARIABLE_SNAPSHOT " +
            "       WHERE COMMIT_ID IN ?1 AND VARIABLE_ID  IN ?2) AS SNAPSHOT " +
            "ON ALL_PALETTES_VARS.VARIABLE_ID = SNAPSHOT.VARIABLE_ID AND ALL_PALETTES_VARS.COMMIT_ID= SNAPSHOT.COMMIT_ID " +
            "LEFT JOIN " +
            "       VARIABLE " +
            "ON VARIABLE.ID = ALL_PALETTES_VARS.VARIABLE_ID " +
            "ORDER BY VARIABLE.NAME,  ALL_PALETTES_VARS.COMMIT_ID", nativeQuery = true)
    List<Object[]> getPaletteDiffView(List<Long> commitIds, List<Long> variableIds);

}
