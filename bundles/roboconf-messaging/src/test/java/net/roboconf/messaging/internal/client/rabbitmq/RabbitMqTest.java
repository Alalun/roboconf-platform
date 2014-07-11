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

package net.roboconf.messaging.internal.client.rabbitmq;

import net.roboconf.messaging.internal.AbstractRabbitMqTest;
import net.roboconf.messaging.internal.MessagingTestUtils;

import org.junit.Assume;
import org.junit.Test;

/**
 * @author Vincent Zurczak - Linagora
 */
public class RabbitMqTest extends AbstractRabbitMqTest {

	@Test
	public void testExchangesBetweenTheDmAndOneAgent() throws Exception {
		Assume.assumeTrue( this.rabbitMqIsRunning );
		MessagingTestUtils.testExchangesBetweenTheDmAndOneAgent();
	}


	@Test
	public void testExchangesBetweenTheDmAndThreeAgents() throws Exception {
		Assume.assumeTrue( this.rabbitMqIsRunning );
		MessagingTestUtils.testExchangesBetweenTheDmAndThreeAgents();
	}


	@Test
	public void testExportsBetweenAgents() throws Exception {
		Assume.assumeTrue( this.rabbitMqIsRunning );
		MessagingTestUtils.testExportsBetweenAgents();
	}


	@Test
	public void testExportsRequestsBetweenAgents() throws Exception {
		Assume.assumeTrue( this.rabbitMqIsRunning );
		MessagingTestUtils.testExportsRequestsBetweenAgents();
	}


	@Test
	public void testExportsBetweenSiblingAgents() throws Exception {
		Assume.assumeTrue( this.rabbitMqIsRunning );
		MessagingTestUtils.testExportsBetweenSiblingAgents();
	}
}
