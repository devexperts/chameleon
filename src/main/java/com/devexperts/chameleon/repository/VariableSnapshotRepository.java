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

import com.devexperts.chameleon.entity.VariableEntity;
import com.devexperts.chameleon.entity.VariableSnapshotEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * Repository for the {@link VariableSnapshotEntity}.
 *
 */
@org.springframework.stereotype.Repository
public interface VariableSnapshotRepository extends JpaRepository<VariableSnapshotEntity, Long> {

    /**
     * Find all variable snapshots by palette id and variable id
     *
     * @param paletteId palette id
     * @param variables variable id
     * @return list of {@link VariableSnapshotEntity}
     */
    List<VariableSnapshotEntity> findAllByPaletteEntityIdAndVariableEntityIn(Long paletteId, Collection<VariableEntity> variables);

    /**
     * Find all variable snapshots by variable id and commit ids
     *
     * @param variableId variable id
     * @param commitIds commit ids
     * @return list of {@link VariableSnapshotEntity}
     */
    @Query(value = "SELECT s FROM VariableSnapshotEntity s WHERE s.variableEntity.id = ?1 AND s.commitEntity.id IN ?2")
    List<VariableSnapshotEntity> findAllVariableSnapshotEntityByVariableEntityIdAndCommitEntityIdIn(Long variableId, List<Long> commitIds);

    /**
     * Find all by commit id
     *
     * @param commitId commit id
     * @return list of {@link VariableSnapshotEntity}
     */
    @Query(value = "SELECT v FROM VariableSnapshotEntity v WHERE v.commitEntity.id = :commitId")
    List<VariableSnapshotEntity> findAllByCommitEntityId(@Param("commitId") Long commitId);

    /**
     * Returns variable ids which have different values in both commits
     *
     * @param firstCommitId  first id of compared commit
     * @param secondCommitId  second id of compared commit
     * @return list of changed variable ids
     */
    @Query(value =
            "SELECT " +
            "      S1.VARIABLE_ID " +
            "FROM " +
            "      VARIABLE_SNAPSHOT AS S1 " +
            "LEFT JOIN " +
            "      (SELECT " +
            "             COLOR , " +
            "             OPACITY, " +
            "             VARIABLE_ID " +
            "       FROM  " +
            "             VARIABLE_SNAPSHOT " +
            "       WHERE COMMIT_ID = ?1) AS S2 " +
            "ON S1.VARIABLE_ID = S2.VARIABLE_ID " +
            "WHERE S1.COMMIT_ID = ?2 AND (S1.COLOR <> S2.COLOR OR (S1.OPACITY IS NULL AND S2.OPACITY IS NOT NULL) OR (S1.OPACITY IS NOT NULL AND S2.OPACITY IS NULL) OR S1.OPACITY<> S2.OPACITY OR S2.COLOR IS NULL) " +
            "UNION " +
            "SELECT " +
            "      S1.VARIABLE_ID " +
            "FROM " +
            "      VARIABLE_SNAPSHOT AS S1 " +
            "LEFT JOIN " +
            "      (SELECT " +
            "             COLOR , " +
            "             VARIABLE_ID " +
            "       FROM  " +
            "             VARIABLE_SNAPSHOT " +
            "       WHERE COMMIT_ID = ?2) AS S2 " +
            "ON S1.VARIABLE_ID = S2.VARIABLE_ID " +
            "WHERE S1.COMMIT_ID = ?1 AND S2.COLOR IS NULL ", nativeQuery = true)
    List<BigInteger> getChangedVariablesByCommitIds(Long firstCommitId, Long secondCommitId);

    @Query(value = "SELECT v FROM VariableSnapshotEntity v WHERE v.commitEntity.id IN ?1")
    List<VariableSnapshotEntity> findAllByCommitEntityIdIn(List<Long> ids);

    @Query(value = "SELECT DISTINCT i.variableEntity.id FROM VariableSnapshotEntity i WHERE ids IN ?1")
    List<Long> getVariableIdsByCommitEntityIdIn(List<Long> ids);


}
