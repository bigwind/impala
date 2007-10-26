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

package tests;

import interfaces.WineMerchant;

import java.util.Collection;

import test.BaseDataTest;

import net.java.impala.spring.plugin.ParentSpec;
import net.java.impala.spring.plugin.SimpleBeansetPluginSpec;
import net.java.impala.spring.plugin.SpringContextSpec;
import net.java.impala.spring.plugin.SimpleSpringContextSpec;
import net.java.impala.testrun.DynamicContextHolder;
import net.java.impala.testrun.PluginTestRunner;

import classes.Wine;

public class WineMerchantTest extends BaseDataTest {

	public static void main(String[] args) {
		PluginTestRunner.run(WineMerchantTest.class);
	}

	public void testVintage() {

		WineMerchant merchant = DynamicContextHolder.getBean(this, "wineMerchant", WineMerchant.class);

		Wine wine = new Wine();
		wine.setId(1L);
		wine.setColor("red");
		wine.setVineyard("Chateau X");
		wine.setTitle("Cabernet");
		wine.setVintage(1996);
		merchant.addWine(wine);

		Collection<Wine> wines = merchant.getWinesOfVintage(1996);
		assertEquals(1, wines.size());

	}

	public SpringContextSpec getPluginSpec() {
		SimpleSpringContextSpec spec = new SimpleSpringContextSpec(new String[] { "parent-context.xml", "merchant-context.xml" }, 
						new String[] {
						"wineorder-hibernate", "wineorder-dao" });
		
		ParentSpec parent = spec.getParentSpec();
		new SimpleBeansetPluginSpec(parent, "wineorder-merchant");
		
		return spec;
	}

}