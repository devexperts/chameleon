package com.devexperts.chameleon.configuration;

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

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for confluence importer {@link com.devexperts.chameleon.service.ConfluenceImportPaletteService}
 *
 */
@Component
@ConfigurationProperties(prefix = "confluence")
public class ConfluenceImportConfiguration {

	@NotBlank
	private String loginPage;
	private String username;
	private String password;

	@NotBlank
	private String usageParseField;
	@NotBlank
	private String valueParseField;

	public String getLoginPage() {
		return loginPage;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getUsageParseField() {
		return usageParseField;
	}

	public String getValueParseField() {
		return valueParseField;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsageParseField(String usageParseField) {
		this.usageParseField = usageParseField;
	}

	public void setValueParseField(String valueParseField) {
		this.valueParseField = valueParseField;
	}
}
