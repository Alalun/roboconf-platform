/**
 * Copyright 2014 Linagora, Université Joseph Fourier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.roboconf.core.model.runtime;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Vincent Zurczak - Linagora
 */
public class ImportTest {

	@Test
	public void testToString() {

		Map<String,String> exportedVars = null;
		Import imp = new Import( "/root/node", "comp1", exportedVars );
		Assert.assertNotNull( imp.toString());

		exportedVars = new HashMap<String,String> ();
		imp = new Import( "/root/node", "comp1", exportedVars );
		Assert.assertNotNull( imp.toString());

		exportedVars.put( "comp.ip", "127.0.0.1" );
		exportedVars.put( "comp.data", null );
		imp = new Import( "/root/node", "comp1", exportedVars );
		Assert.assertNotNull( imp.toString());

		imp = new Import( new Instance( "my VM" ));
		Assert.assertEquals( "/my VM", imp.getInstancePath());
	}
}
