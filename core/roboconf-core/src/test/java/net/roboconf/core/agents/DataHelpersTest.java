/**
 * Copyright 2014 Linagora, Université Joseph Fourier, Floralis
 *
 * The present code is developed in the scope of the joint LINAGORA -
 * Université Joseph Fourier - Floralis research program and is designated
 * as a "Result" pursuant to the terms and conditions of the LINAGORA
 * - Université Joseph Fourier - Floralis research program. Each copyright
 * holder of Results enumerated here above fully & independently holds complete
 * ownership of the complete Intellectual Property rights applicable to the whole
 * of said Results, and may freely exploit it in any manner which does not infringe
 * the moral rights of the other copyright holders.
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

package net.roboconf.core.agents;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Vincent Zurczak - Linagora
 */
public class DataHelpersTest {

	@Test
	public void testWriteAndRead() throws Exception {

		String rawProperties = DataHelpers.writeUserDataAsString( "192.168.1.24", "user", "pwd", "app", "root" );
		Properties props = DataHelpers.readUserData( rawProperties );
		Assert.assertEquals( "app", props.getProperty( DataHelpers.APPLICATION_NAME ));
		Assert.assertEquals( "root", props.getProperty( DataHelpers.ROOT_INSTANCE_NAME ));
		Assert.assertEquals( "192.168.1.24", props.getProperty( DataHelpers.MESSAGING_IP ));
		Assert.assertEquals( "pwd", props.getProperty( DataHelpers.MESSAGING_PASSWORD ));
		Assert.assertEquals( "user", props.getProperty( DataHelpers.MESSAGING_USERNAME ));

		rawProperties = DataHelpers.writeUserDataAsString( null, "user", "pwd", "app", "root" );
		props = DataHelpers.readUserData( rawProperties );
		Assert.assertEquals( "app", props.getProperty( DataHelpers.APPLICATION_NAME ));
		Assert.assertEquals( "root", props.getProperty( DataHelpers.ROOT_INSTANCE_NAME ));
		Assert.assertEquals( null, props.getProperty( DataHelpers.MESSAGING_IP ));
		Assert.assertEquals( "pwd", props.getProperty( DataHelpers.MESSAGING_PASSWORD ));
		Assert.assertEquals( "user", props.getProperty( DataHelpers.MESSAGING_USERNAME ));

		rawProperties = DataHelpers.writeUserDataAsString( "192.168.1.24", null, "pwd", "app", "root" );
		props = DataHelpers.readUserData( rawProperties );
		Assert.assertEquals( "app", props.getProperty( DataHelpers.APPLICATION_NAME ));
		Assert.assertEquals( "root", props.getProperty( DataHelpers.ROOT_INSTANCE_NAME ));
		Assert.assertEquals( "192.168.1.24", props.getProperty( DataHelpers.MESSAGING_IP ));
		Assert.assertEquals( "pwd", props.getProperty( DataHelpers.MESSAGING_PASSWORD ));
		Assert.assertEquals( null, props.getProperty( DataHelpers.MESSAGING_USERNAME ));

		rawProperties = DataHelpers.writeUserDataAsString( "192.168.1.24", "user", null, "app", "root" );
		props = DataHelpers.readUserData( rawProperties );
		Assert.assertEquals( "app", props.getProperty( DataHelpers.APPLICATION_NAME ));
		Assert.assertEquals( "root", props.getProperty( DataHelpers.ROOT_INSTANCE_NAME ));
		Assert.assertEquals( "192.168.1.24", props.getProperty( DataHelpers.MESSAGING_IP ));
		Assert.assertEquals( null, props.getProperty( DataHelpers.MESSAGING_PASSWORD ));
		Assert.assertEquals( "user", props.getProperty( DataHelpers.MESSAGING_USERNAME ));

		rawProperties = DataHelpers.writeUserDataAsString( "192.168.1.24", "user", "pwd", null, "root" );
		props = DataHelpers.readUserData( rawProperties );
		Assert.assertEquals( null, props.getProperty( DataHelpers.APPLICATION_NAME ));
		Assert.assertEquals( "root", props.getProperty( DataHelpers.ROOT_INSTANCE_NAME ));
		Assert.assertEquals( "192.168.1.24", props.getProperty( DataHelpers.MESSAGING_IP ));
		Assert.assertEquals( "pwd", props.getProperty( DataHelpers.MESSAGING_PASSWORD ));
		Assert.assertEquals( "user", props.getProperty( DataHelpers.MESSAGING_USERNAME ));

		rawProperties = DataHelpers.writeUserDataAsString( "192.168.1.24", "user", "pwd", "app", null );
		props = DataHelpers.readUserData( rawProperties );
		Assert.assertEquals( "app", props.getProperty( DataHelpers.APPLICATION_NAME ));
		Assert.assertEquals( null, props.getProperty( DataHelpers.ROOT_INSTANCE_NAME ));
		Assert.assertEquals( "192.168.1.24", props.getProperty( DataHelpers.MESSAGING_IP ));
		Assert.assertEquals( "pwd", props.getProperty( DataHelpers.MESSAGING_PASSWORD ));
		Assert.assertEquals( "user", props.getProperty( DataHelpers.MESSAGING_USERNAME ));
	}
}