/*
 * Copyright 2007-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.impalaframework.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.impalaframework.config.PropertyValue;
import org.springframework.util.ObjectUtils;

public class ConfigurationSettings {

	private static Log logger = LogFactory.getLog(ConfigurationSettings.class);

	private final ArrayList<String> contextLocations = new ArrayList<String>();

	private final Map<String,PropertyValue> propertyValues = new HashMap<String, PropertyValue>();
	
	public ConfigurationSettings() {
		super();
	}

	public void addProperty(String property, PropertyValue propertyValue) {
		propertyValues.put(property, propertyValue);
	}
	
	public void add(String location) {
		contextLocations.add(location);
	}
	
	public void logProperties() {
		List<String> keyList = sortKeys();
		for (String key : keyList) {
			String stringValue = propertyValue(key);
			logger.info("Value for '" + key + "': " + stringValue);
		}
	}
	
	public List<String> getContextLocations() {
		return Collections.unmodifiableList(contextLocations);
	}

	public Map<String, PropertyValue> getPropertyValues() {
		return Collections.unmodifiableMap(propertyValues);
	}

	private List<String> sortKeys() {
		final Set<String> keys = propertyValues.keySet();
		List<String> keyList = new ArrayList<String>(keys);
		Collections.sort(keyList);
		return keyList;
	}

	private String propertyValue(String key) {
		PropertyValue value = propertyValues.get(key);
		
		String stringValue = null;

		if (value != null) {
			stringValue = value.getRawValue();
			if (stringValue == null) {
				stringValue = value.getRawDefaultValue();
				
				if (stringValue != null) {
					stringValue = stringValue + " (default)";
				}
			}
		}

		if (stringValue == null) {
			return "[null]";
		}
		return stringValue;
	}
	
	public String toString() {
		final String newLine = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer(ObjectUtils.identityToString(this));
		buffer.append(newLine);
		buffer.append("Context locations: " + contextLocations);
		buffer.append(newLine);
		buffer.append("Property settings: ");
		buffer.append(newLine);
		final List<String> sortKeys = sortKeys();
		for (String key : sortKeys) {
			String stringValue = propertyValue(key);
			buffer.append("  ").append(key).append(": ").append(stringValue).append(newLine);
		}
		buffer.append("--------");
		return buffer.toString();
	}
	
}
