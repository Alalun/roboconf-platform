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

package net.roboconf.messaging.internal.client.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.roboconf.core.model.runtime.Application;
import net.roboconf.core.model.runtime.Instance;
import net.roboconf.messaging.client.IDmClient;
import net.roboconf.messaging.messages.Message;

/**
 * A class to mock the messaging server and the IaaS.
 * @author Vincent Zurczak - Linagora
 */
public class TestClientDm implements IDmClient {

	public final List<Message> sentMessages = new ArrayList<Message> ();
	public AtomicBoolean connected = new AtomicBoolean( false );
	public AtomicBoolean failMessageSending = new AtomicBoolean( false );

	private String messageServerIp, messageServerUsername, messageServerPassword;



	@Override
	public void setParameters( String messageServerIp, String messageServerUsername, String messageServerPassword ) {
		this.messageServerIp = messageServerIp;
		this.messageServerPassword = messageServerPassword;
		this.messageServerUsername = messageServerUsername;
	}

	@Override
	public void closeConnection() throws IOException {
		this.connected.set( false );
	}

	@Override
	public void openConnection() throws IOException {
		this.connected.set( true );
	}

	@Override
	public void sendMessageToAgent( Application application, Instance instance, Message message )
	throws IOException {

		if( this.failMessageSending.get())
			throw new IOException( "Message sending was configured to fail." );

		this.sentMessages.add( message );
	}

	@Override
	public void listenToAgentMessages( Application application, ListenerCommand command )
	throws IOException {
		// nothing, we do not care
	}

	@Override
	public void deleteMessagingServerArtifacts( Application application )
	throws IOException {
		// nothing, we do not care
	}

	@Override
	public boolean isConnected() {
		return this.connected.get();
	}

	@Override
	public void setMessageQueue( LinkedBlockingQueue<Message> messageQueue ) {
		// nothing
	}

	@Override
	public void propagateAgentTermination() {
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
}
