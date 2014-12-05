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

package net.roboconf.messaging.internal.client.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.roboconf.core.model.beans.Instance;
import net.roboconf.messaging.client.IAgentClient;
import net.roboconf.messaging.messages.Message;

/**
 * @author Vincent Zurczak - Linagora
 */
public class TestClientAgent implements IAgentClient {

	public final List<Message> messagesForTheDm = new ArrayList<Message> ();
	public AtomicInteger messagesForAgentsCount = new AtomicInteger();
	public AtomicBoolean connected = new AtomicBoolean( false );
	public AtomicBoolean failMessageSending = new AtomicBoolean( false );

	private String messageServerIp, messageServerUsername, messageServerPassword, applicationName, rootInstanceName;



	@Override
	public void setParameters( String messageServerIp, String messageServerUsername, String messageServerPassword ) {
		this.messageServerIp = messageServerIp;
		this.messageServerPassword = messageServerPassword;
		this.messageServerUsername = messageServerUsername;
	}

	@Override
	public boolean isConnected() {
		return this.connected.get();
	}

	@Override
	public void openConnection() throws IOException {
		this.connected.set( true );
	}

	@Override
	public void closeConnection() throws IOException {
		this.connected.set( false );
	}

	@Override
	public void setApplicationName( String applicationName ) {
		this.applicationName = applicationName;
	}

	@Override
	public void setRootInstanceName( String rootInstanceName ) {
		this.rootInstanceName = rootInstanceName;
	}

	@Override
	public void publishExports( Instance instance ) throws IOException {

		if( this.failMessageSending.get())
			throw new IOException( "Message sending was configured to fail." );

		this.messagesForAgentsCount.incrementAndGet();
	}

	@Override
	public void publishExports( Instance instance, String facetOrComponentName ) throws IOException {

		if( this.failMessageSending.get())
			throw new IOException( "Message sending was configured to fail." );

		this.messagesForAgentsCount.incrementAndGet();
	}

	@Override
	public void unpublishExports( Instance instance ) throws IOException {

		if( this.failMessageSending.get())
			throw new IOException( "Message sending was configured to fail." );

		this.messagesForAgentsCount.incrementAndGet();
	}

	@Override
	public void listenToRequestsFromOtherAgents( ListenerCommand command, Instance instance )
	throws IOException {
		// nothing
	}

	@Override
	public void requestExportsFromOtherAgents( Instance instance ) throws IOException {

		if( this.failMessageSending.get())
			throw new IOException( "Message sending was configured to fail." );

		this.messagesForAgentsCount.incrementAndGet();
	}

	@Override
	public void listenToExportsFromOtherAgents( ListenerCommand command, Instance instance )
	throws IOException {
		// nothing
	}

	@Override
	public void sendMessageToTheDm( Message message ) throws IOException {

		if( this.failMessageSending.get())
			throw new IOException( "Message sending was configured to fail." );

		this.messagesForTheDm.add( message );
	}

	@Override
	public void listenToTheDm( ListenerCommand command ) throws IOException {
		// nothing
	}

	@Override
	public void setMessageQueue( LinkedBlockingQueue<Message> messageQueue ) {
		// nothing
	}

	/**
	 * @return the messageServerIp
	 */
	public String getMessageServerIp() {
		return this.messageServerIp;
	}

	/**
	 * @return the messageServerUsername
	 */
	public String getMessageServerUsername() {
		return this.messageServerUsername;
	}

	/**
	 * @return the messageServerPassword
	 */
	public String getMessageServerPassword() {
		return this.messageServerPassword;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return this.applicationName;
	}

	/**
	 * @return the rootInstanceName
	 */
	public String getRootInstanceName() {
		return this.rootInstanceName;
	}
}
