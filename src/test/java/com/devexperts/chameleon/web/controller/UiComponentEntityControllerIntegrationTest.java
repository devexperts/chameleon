package com.devexperts.chameleon.web.controller;

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

import com.devexperts.chameleon.dto.UiComponentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.devexperts.chameleon.web.controller.UiComponentController.UICOMPONENT_PATH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UiComponentEntityControllerIntegrationTest {

	private static final Long NULL_ID = null;

	private static final String NAME_FIELD = "name";

	private static final String COLOR_FIELD = "color";

	private static final String ROOT_PATH = "/";

	private final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void testEmptyComponentsList() throws Exception {
		MvcResult result = mvc.perform(get(ROOT_PATH + UICOMPONENT_PATH))
				.andExpect(status().isOk())
				.andReturn();

		Assert.assertEquals("[]", result.getResponse().getContentAsString());
	}

	@Test
	public void testAddTwoDifferentComponentsAndGetList() throws Exception {
		String componentName1 = "componentForList1";
		String componentName2 = "componentForList2";
		String colorCode1 = "123456";
		String colorCode2 = "654321";
		UiComponentDTO dto1 = new UiComponentDTO(NULL_ID, componentName1, colorCode1);
		UiComponentDTO dto2 = new UiComponentDTO(NULL_ID, componentName2, colorCode2);

		mvc.perform(post(ROOT_PATH + UICOMPONENT_PATH)
				.content(mapper.writeValueAsString(dto1))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		mvc.perform(post(ROOT_PATH + UICOMPONENT_PATH)
				.content(mapper.writeValueAsString(dto2))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		mvc.perform(get(ROOT_PATH + UICOMPONENT_PATH))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testAddOneComponentWithEmptyName() throws Exception {
		String componentName = "";
		String colorCode = "123456";
		UiComponentDTO dto = new UiComponentDTO(NULL_ID, componentName, colorCode);

		mvc.perform(post(ROOT_PATH + UICOMPONENT_PATH)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testAddOneComponentWithWrongColorCode() throws Exception {
		String componentName = "componentWithWrongColorCode";
		String wrongColorCode = "12345U";
		UiComponentDTO dto = new UiComponentDTO(NULL_ID, componentName, wrongColorCode);

		mvc.perform(post(ROOT_PATH + UICOMPONENT_PATH)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testAddOneComponentAndGetIt() throws Exception {
		String componentName = "component1";
		String colorCode = "123456";
		UiComponentDTO dto = new UiComponentDTO(NULL_ID, componentName, colorCode);

		MvcResult result =  mvc.perform(post(ROOT_PATH + UICOMPONENT_PATH)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String id = result.getResponse().getContentAsString();

		mvc.perform(get(ROOT_PATH + UICOMPONENT_PATH + "/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath(NAME_FIELD, equalTo(componentName)))
				.andExpect(jsonPath(COLOR_FIELD, equalTo(colorCode)));
	}

	@Test
	public void testAddTwoComponentWithTheSameName() throws Exception {
		String componentName = "componentWithTheSameName";
		String colorCode = "123456";
		UiComponentDTO dto = new UiComponentDTO(NULL_ID, componentName, colorCode);

		ObjectMapper mapper = new ObjectMapper();

		mvc.perform(post(ROOT_PATH + UICOMPONENT_PATH)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		mvc.perform(post(ROOT_PATH + UICOMPONENT_PATH)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
}
