/*
 * Copyright 2007 the original author or authors.
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

package net.java.impala.spring.plugin;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Phil Zoio
 */
public class SimpleBeansetAwarePluginTest extends TestCase {

	public void testEqualsObject() {
		Map<String, String> map1 = new HashMap<String, String>();
		SimpleBeansetPluginSpec p1a = new SimpleBeansetPluginSpec("p1", map1);
		Map<String, String> map2 = new HashMap<String, String>();
		SimpleBeansetPluginSpec p1b = new SimpleBeansetPluginSpec("p1", map2);
		assertEquals(p1a, p1b);

		SimpleBeansetPluginSpec p2b = new SimpleBeansetPluginSpec("p2", map2);
		assertFalse(p1b.equals(p2b));
		
		map1.put("bean1", "context1-a.xml");
		map1.put("bean2", "context2-a.xml");
		
		map2.put("bean1", "context1-a.xml");
		map2.put("bean2", "context2-a.xml");
		
		//these contain the same overrides, so use these
		p1a = new SimpleBeansetPluginSpec("p1", map1);
	    p1b = new SimpleBeansetPluginSpec("p1", map2);
		assertEquals(p1a, p1b);
		
		//now change bean2 in map2
		map2.put("bean2", "context2-b.xml");
	    p1b = new SimpleBeansetPluginSpec("p1", map2);
		assertFalse(p1b.equals(p2b));
		
		map2.remove("bean2");
	    p1b = new SimpleBeansetPluginSpec("p1", map2);
		assertFalse(p1b.equals(p2b));
	}

}
