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

package net.roboconf.messaging.client;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;
import net.roboconf.messaging.internal.IgnoringMessageProcessor;
import net.roboconf.messaging.messages.Message;
import net.roboconf.messaging.messages.from_dm_to_agent.MsgCmdResynchronize;
import net.roboconf.messaging.messages.from_dm_to_agent.MsgCmdSendInstances;

import org.junit.Test;

/**
 * @author Vincent Zurczak - Linagora
 */
public class AbstractMessageProcessorTest {

	@Test
	public void testCustomName() {

		AbstractMessageProcessor processor = new IgnoringMessageProcessor( "yo" );

		Assert.assertEquals( "yo", processor.getName());
		Assert.assertTrue( processor.hasNoMessage());

		processor.storeMessage( new MsgCmdSendInstances());
		Assert.assertFalse( processor.hasNoMessage());
	}


	@Test
	public void testStartAndStop() throws Exception {

		AbstractMessageProcessor processor = new IgnoringMessageProcessor();
		Assert.assertFalse( processor.isRunning());
		processor.start();

		Thread.sleep( 200 );
		Assert.assertTrue( processor.isRunning());

		processor.interrupt();
		Thread.sleep( 200 );
		Assert.assertFalse( processor.isRunning());
	}


	@Test
	public void testInvalidProcessing() throws Exception {

		// The message is processed...
		AbstractMessageProcessor processor = new IgnoringMessageProcessor();
		processor.start();
		processor.storeMessage( new MsgCmdResynchronize());
		Thread.sleep( AbstractMessageProcessor.MESSAGE_POLLING_PERIOD );
		processor.stopProcessor();

		Assert.assertTrue( processor.hasNoMessage());

		// This one should not work, the message should remain in the queue
		processor = new IgnoringMessageProcessor() {
			@Override
			protected boolean processMessage( Message message ) {
				return false;
			}
		};

		processor.start();
		processor.storeMessage( new MsgCmdResynchronize());
		Thread.sleep( AbstractMessageProcessor.MESSAGE_POLLING_PERIOD );
		processor.stopProcessor();

		Assert.assertFalse( processor.hasNoMessage());
	}


	@Test
	public void testDoNotProcessNewMessages() throws Exception {

		final AtomicBoolean stop = new AtomicBoolean( false );
		AbstractMessageProcessor processor = new IgnoringMessageProcessor() {
			@Override
			protected boolean doNotProcessNewMessages() {
				return stop.get();
			}
		};

		processor.start();
		Thread.sleep( 200 );

		Assert.assertTrue( processor.isRunning());
		Assert.assertTrue( processor.isAlive());

		processor.storeMessage( new MsgCmdResynchronize());
		stop.set( true );
		Thread.sleep( AbstractMessageProcessor.MESSAGE_POLLING_PERIOD );

		// The process ended
		Assert.assertFalse( processor.isRunning());
		Assert.assertFalse( processor.isAlive());
		Assert.assertFalse( processor.hasNoMessage());
	}
}
