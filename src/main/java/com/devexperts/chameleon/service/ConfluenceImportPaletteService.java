package com.devexperts.chameleon.service;

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

import com.devexperts.chameleon.configuration.ConfluenceImportConfiguration;
import com.devexperts.chameleon.entity.CommitEntity;
import com.devexperts.chameleon.entity.PaletteEntity;
import com.devexperts.chameleon.entity.VariableEntity;
import com.devexperts.chameleon.entity.VariableSnapshotEntity;
import com.devexperts.chameleon.exception.BaseCustomException;
import com.devexperts.chameleon.util.PreconditionUtils;
import com.google.common.collect.ImmutableList;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.devexperts.chameleon.exception.BaseCustomException.buildUnexpected;
import static com.devexperts.chameleon.exception.ErrorCode.NOT_AUTHORIZED;
import static com.devexperts.chameleon.util.ConverterUtils.percentToOpacity;

/**
 * Html parser for custom written confluence pages.
 * Main purpose for one time import existing color palettes
 * from existing confluence pages into this project.
 */
@Service
@Transactional
public class ConfluenceImportPaletteService {

	private PaletteService paletteService;
	private VariableService variableService;
	private VariableSnapshotService snapshotService;
	private CommitService commitService;

	private ConfluenceImportConfiguration configuration;

	private static final Logger logger = LoggerFactory.getLogger(ConfluenceImportPaletteService.class);

	private static final String TABLE_BODY = "table tbody";
	private static final String TABLE_TR = "tr";
	private static final String TABLE_TD = "td";
	private static final String TABLE_TR_TH = "tr th";
	private static final String AUTH_ERROR_MESSAGE = "Cannot authenticate on url";
	private static final String NO_RESULT_MESSAGE = "No variables added";

	private static final Integer NAME_POSITION = 0;
	private static final Integer OPACITY_POSITION = 1;

	private static final Float DEFAULT_OPACITY = 1.0f;

	private static Function<String, String[]> parseVariable = string -> string.split("\\(");
	private static Function<String, String> opacitySubstring = string -> string.substring(0, string.indexOf("%")).replace(" ", "");
	private static BiFunction<String, List<Long>, String> generateResultText = (string, list) -> string + "\n found " + list.size() + " variables";

	@Autowired
	public ConfluenceImportPaletteService(PaletteService paletteService,
										  VariableService variableService,
										  VariableSnapshotService snapshotService,
										  CommitService commitService,
										  ConfluenceImportConfiguration configuration) {
		this.paletteService = paletteService;
		this.variableService = variableService;
		this.snapshotService = snapshotService;
		this.commitService = commitService;
		this.configuration = configuration;
	}

	public String importPalette(String url, String paletteName, String username, String password) {
		PreconditionUtils.checkIsAnyEmpty(url, username, password);
		PaletteEntity palette = paletteService.getPalette(paletteName);

		Document doc = getDocument(url, username, password);

		PreconditionUtils.checkNotNull(doc);

		List<ParsedVariable> parsedVariables = parse(doc);

		if (!parsedVariables.isEmpty()) {
			List<Long> ids = saveVariables(parsedVariables, palette);
			if (!ids.isEmpty()) {
				return generateResultText.apply(doc.title(), ids);
			}
		}

		return NO_RESULT_MESSAGE;
	}

	private List<ParsedVariable> parse(Document doc) {
		List<ParsedVariable> result = new ArrayList<>();

		doc.select(TABLE_BODY).forEach(table -> {
			Map<String, Integer> headers = parseHeaders(table);
			if (!CollectionUtils.isEmpty(headers)) {
				table.select(TABLE_TR).stream()
						.map(row -> row.select(TABLE_TD))
						.filter(cols -> cols.size() > 0)
						.forEach(cols -> {
							String names = cols.get(headers.get(configuration.getUsageParseField())).text();
							String color = cols.get(headers.get(configuration.getValueParseField())).text();

							result.addAll(Arrays.stream(names.split(","))
									.map(variable -> createParsedVariable(variable, color))
									.filter(Objects::nonNull)
									.collect(Collectors.toList()));
						});
			}
		});

		return result;
	}

	private List<Long> saveVariables(List<ParsedVariable> parsedVariables, PaletteEntity palette) {
		CommitEntity commitEntity = commitService.createNewCommit(palette);
		Map<String, VariableEntity> namesMap = variableService.getMapByNames(parsedVariables.stream().map(pv -> pv.name).collect(Collectors.toList()));
		Map<VariableEntity, List<VariableSnapshotEntity>> snapshotMap = snapshotService.getMapByVariables(palette.getId(), namesMap.values());
		List<VariableSnapshotEntity> variableSnapshots = parsedVariables.stream()
				.distinct()
				.map(pv -> convertToSnapshotVariable(pv, snapshotMap, namesMap, palette, commitEntity))
				.collect(Collectors.toList());
		return snapshotService.saveEntityList(variableSnapshots);
	}

	private VariableSnapshotEntity convertToSnapshotVariable(ParsedVariable variable,
															 Map<VariableEntity, List<VariableSnapshotEntity>> snapshotMap,
															 Map<String, VariableEntity> namesMap,
															 PaletteEntity palette,
															 CommitEntity commitEntity) {
		VariableEntity variableEntity = namesMap.get(variable.name);
		if (variableEntity == null) {
			variableEntity = new VariableEntity(variable.name, "");
		}
		return snapshotMap.getOrDefault(variableEntity, Collections.emptyList()).stream()
				.filter(snapshotEntity -> snapshotEntity.getPaletteEntity().equals(palette))
				.findAny()
				.orElse(new VariableSnapshotEntity(variable.color, variable.opacity, palette, variableEntity, commitEntity));

	}

	private Map<String, Integer> parseHeaders(Element table) {
		Map<String, Integer> headersMap = new HashMap<>();
		Elements select = table.select(TABLE_TR_TH);

		for (int i = 0; i < select.size(); i++) {
			Element header = select.get(i);
			headersMap.put(header.text().trim().toUpperCase(), i);
		}
		return headersMap;
	}

	private Document getDocument(String url, String username, String password) {
		try {
			Document document = Jsoup.connect(url)
					.data(configuration.getUsername(), username, configuration.getPassword(), password)
					.method(Connection.Method.POST)
					.get();

			if (document.location().contains(configuration.getLoginPage())) {
				throw new BaseCustomException(NOT_AUTHORIZED, AUTH_ERROR_MESSAGE, ImmutableList.of(url));
			}

			return document;
		} catch (IOException e) {
			throw buildUnexpected(e);
		}
	}

	private static Optional<String> parseName(String[] variable) {
		if (variable.length > 0) {
			return Optional.of(variable[NAME_POSITION]);
		}
		return Optional.empty();
	}

	private static Float getOpacity(String[] variable) {
		try {
			if (variable.length > 1 && !variable[OPACITY_POSITION].isEmpty()) {
				return percentToOpacity(Integer.parseInt(opacitySubstring.apply(variable[OPACITY_POSITION]))).orElse(DEFAULT_OPACITY);
			}
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			logger.warn("Cannot parse opacity", e);
		}
		return DEFAULT_OPACITY;
	}

	private ParsedVariable createParsedVariable(String variable, String color) {
		String[] parsed = parseVariable.apply(variable);
		Optional<String> maybeName = parseName(parsed);
		return maybeName.map(name -> new ParsedVariable(name.trim(), color, getOpacity(parsed))).orElse(null);
	}

	private class ParsedVariable {
		final String name;
		final String color;
		final Float opacity;

		ParsedVariable(String name, String color, Float opacity) {
			this.name = name;
			this.color = color;
			this.opacity = opacity;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			ParsedVariable that = (ParsedVariable) o;

			return name.equals(that.name);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("ParsedVariable{");
			sb.append("name='").append(name).append('\'');
			sb.append(", color='").append(color).append('\'');
			sb.append(", opacity=").append(opacity);
			sb.append('}');
			return sb.toString();
		}
	}
}
