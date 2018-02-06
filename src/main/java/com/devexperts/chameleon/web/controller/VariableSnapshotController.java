package com.devexperts.chameleon.web.controller;

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

import com.devexperts.chameleon.dto.PaletteDiffViewDTO;
import com.devexperts.chameleon.dto.PaletteViewDTO;
import com.devexperts.chameleon.dto.SaveVariableDTO;
import com.devexperts.chameleon.dto.VariableSnapshotDTO;
import com.devexperts.chameleon.service.VariableSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * This class is used as rest service for the {@link VariableSnapshotService}.
 * Handle actions with snapshots and build view for palettes and commits
 */
@RestController
@RequestMapping(VariableSnapshotController.PATH)
public class VariableSnapshotController {

    public static final String PATH = "api/variablesnapshots";

    public static final String SAVE_CHANGES_PATH = "save";

    public static final String COPY_PATH = "copy";

    public static final String DIFF_PATH = "diff";

    public static final String VIEW_PATH = "view";

    public static final String SNAPSHOTS_BY_COMMIT_PATH = "commit";

    public static final String DELETE_VARIABLE_PATH = "deletevariable";

    private final VariableSnapshotService variableSnapshotService;

    @Autowired
    public VariableSnapshotController(VariableSnapshotService variableSnapshotService) {
        this.variableSnapshotService = variableSnapshotService;
    }

    /**
     * Retruns variable snapshot by id
     *
     * @param id variable snapshot id
     * @return {@link VariableSnapshotDTO}
     */
    @RequestMapping(method = GET, value = "/{variableSnapshotId}")
    public ResponseEntity<VariableSnapshotDTO> read(@PathVariable("variableSnapshotId") Long id) {
        return new ResponseEntity<>(variableSnapshotService.getById(id), HttpStatus.OK);
    }

    /**
     * Copy snapshots from source palette to target palette
     *
     * @param sourcePaletteId source palette id
     * @param targetPaletteId target palette id
     * @return list ids of new snapshots
     */
    @RequestMapping(path = COPY_PATH + "/{sourcePaletteId}/{targetPaletteId}", method = PUT)
    public ResponseEntity<?> copy(@PathVariable Long sourcePaletteId, @PathVariable Long targetPaletteId) {
        List<Long> snapshotsIds = variableSnapshotService.copy(sourcePaletteId, targetPaletteId);
        return new ResponseEntity<>(snapshotsIds, HttpStatus.OK);
    }

    /**
     * Return varible snapshot view by variable id
     *
     * @param variableId variable id
     * @return {@link com.devexperts.chameleon.dto.VariableViewDTO}
     */
    @RequestMapping(path = VIEW_PATH + "/{variableId}", method = GET)
    public ResponseEntity<?> getVariableSnapshotView(@PathVariable("variableId") Long variableId) {
        return new ResponseEntity<>(variableSnapshotService.buildVariableSnapshotView(variableId), HttpStatus.OK);
    }

    /**
     * Get variable for add variable action
     *
     * @return {@link com.devexperts.chameleon.dto.VariableViewDTO}
     */
    @RequestMapping(path = VIEW_PATH, method = GET)
    public ResponseEntity<?> getVariableSnapshotViewForAddVariable() {
        return new ResponseEntity<>(variableSnapshotService.buildVariableSnapshotView(null), HttpStatus.OK);
    }

    /**
     * Get all variable snapshots by commit id
     *
     * @return {@link com.devexperts.chameleon.dto.VariableViewDTO}
     */
    @RequestMapping(path = SNAPSHOTS_BY_COMMIT_PATH + "/{commitId}", method = GET)
    public ResponseEntity<?> getAllVariableSnapshotsByCommitId(@PathVariable("commitId") Long commitId) {
        return new ResponseEntity<>(variableSnapshotService.findAllByCommitId(commitId), HttpStatus.OK);
    }

    /**
     * Delete variable by id
     *
     * @param variableId variable id
     * @return count of palettes was contained this variable
     */
    @RequestMapping(path = DELETE_VARIABLE_PATH + "/{variableId}", method = DELETE)
    public ResponseEntity<?> deleteByVariableId(@PathVariable("variableId") Long variableId) {
        return new ResponseEntity<>(variableSnapshotService.deleteByVariableId(variableId), HttpStatus.OK);
    }

    /**
     * Save variables action
     *
     * @param variables list of {@link SaveVariableDTO}
     * @return list of saved variable snapshot ids
     */
    @RequestMapping(path = SAVE_CHANGES_PATH, method = POST)
    public ResponseEntity<?> saveVariablesWithVariableSnapshots(@RequestBody @Valid List<SaveVariableDTO> variables) {
        return new ResponseEntity<>(variableSnapshotService.save(variables), HttpStatus.OK);
    }

    /**
     * Return diff view of commits
     *
     * @param firstCommitId first commit id
     * @param secondCommitId second commit id
     * @return {@link PaletteDiffViewDTO}
     */
    @RequestMapping(path = DIFF_PATH + "/{firstCommitId}/{secondCommitId}", method = GET)
    public ResponseEntity<PaletteDiffViewDTO> diff(@PathVariable("firstCommitId") Long firstCommitId, @PathVariable("secondCommitId") Long secondCommitId) {
        return new ResponseEntity<>(variableSnapshotService.buildPaletteDiffView(firstCommitId, secondCommitId), HttpStatus.OK);
    }
}
