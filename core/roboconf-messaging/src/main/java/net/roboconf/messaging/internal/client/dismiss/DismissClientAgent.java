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

package net.roboconf.messaging.internal.client.dismiss;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import net.roboconf.core.model.runtime.Instance;
import net.roboconf.messaging.MessagingConstants;
import net.roboconf.messaging.client.IAgentClient;
import net.roboconf.messaging.messages.Message;

/**
 * @author Vincent Zurczak - Linagora
 */
public class DismissClientAgent implements IAgentClient {

	private final Logger logger = Logger.getLogger( getClass().getName());


	@Override
	public void setParameters( String messageServerIp, String messageServerUsername, String messageServerPassword ) {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void setMessageQueue( LinkedBlockingQueue<Message> messageQueue ) {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public boolean isConnected() {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
		return false;
	}


	@Override
	public void openConnection() throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void closeConnection() throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void setApplicationName( String applicationName ) {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void setRootInstanceName( String rootInstanceName ) {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void publishExports( Instance instance ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void publishExports( Instance instance, String facetOrComponentName ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void unpublishExports( Instance instance ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void listenToRequestsFromOtherAgents( ListenerCommand command, Instance instance ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void requestExportsFromOtherAgents( Instance instance ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void listenToExportsFromOtherAgents( ListenerCommand command, Instance instance ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void sendMessageToTheDm( Message message ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}


	@Override
	public void listenToTheDm( ListenerCommand command ) throws IOException {
		this.logger.info( MessagingConstants.DISMISSED_MESSAGE );
	}
}
