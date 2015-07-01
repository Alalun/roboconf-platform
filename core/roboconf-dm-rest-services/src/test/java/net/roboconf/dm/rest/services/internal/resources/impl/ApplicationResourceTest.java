/**
 * Copyright 2014-2015 Linagora, Université Joseph Fourier, Floralis
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

package net.roboconf.dm.rest.services.internal.resources.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.roboconf.core.internal.tests.TestApplication;
import net.roboconf.core.internal.tests.TestUtils;
import net.roboconf.core.model.beans.Component;
import net.roboconf.core.model.beans.Instance;
import net.roboconf.core.model.beans.Instance.InstanceStatus;
import net.roboconf.core.model.helpers.ComponentHelpers;
import net.roboconf.core.model.helpers.InstanceHelpers;
import net.roboconf.dm.internal.test.TestTargetResolver;
import net.roboconf.dm.management.ManagedApplication;
import net.roboconf.dm.management.Manager;
import net.roboconf.dm.rest.services.internal.resources.IApplicationResource;
import net.roboconf.messaging.api.MessagingConstants;
import net.roboconf.messaging.api.factory.MessagingClientFactoryRegistry;
import net.roboconf.messaging.api.internal.client.test.TestClientDm;
import net.roboconf.messaging.api.internal.client.test.TestClientFactory;
import net.roboconf.messaging.api.messages.Message;
import net.roboconf.messaging.api.messages.from_dm_to_agent.MsgCmdChangeInstanceState;
import net.roboconf.messaging.api.messages.from_dm_to_agent.MsgCmdResynchronize;
import net.roboconf.target.api.TargetException;
import net.roboconf.target.api.TargetHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Vincent Zurczak - Linagora
 */
public class ApplicationResourceTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private IApplicationResource resource;
	private TestApplication app;
	private ManagedApplication ma;
	private Manager manager;
	private TestClientDm msgClient;
	private MessagingClientFactoryRegistry registry = new MessagingClientFactoryRegistry();


	@After
	public void after() {
		this.manager.stop();
	}


	@Before
	public void before() throws Exception {
		this.registry.addMessagingClientFactory(new TestClientFactory());

		this.manager = new Manager();
		this.manager.setMessagingType(MessagingConstants.TEST_FACTORY_TYPE);
		this.manager.setTargetResolver( new TestTargetResolver());
		this.manager.setConfigurationDirectoryLocation( this.folder.newFolder().getAbsolutePath());
		this.manager.start();

		// Reconfigure with the messaging client factory registry set.
		this.manager.getMessagingClient().setRegistry(this.registry);
		this.manager.reconfigure();

		this.msgClient = TestUtils.getInternalField( this.manager.getMessagingClient(), "messagingClient", TestClientDm.class );
		this.msgClient.sentMessages.clear();

		// Disable the messages timer for predictability
		TestUtils.getInternalField( this.manager, "timer", Timer.class).cancel();

		// Create our resource
		this.resource = new ApplicationResource( this.manager );

		// Load an application
		this.app = new TestApplication();
		this.ma = new ManagedApplication( this.app );
		this.manager.getNameToManagedApplication().put( this.app.getName(), this.ma );
	}


	@Test
	public void testChangeState_inexistingApplication() throws Exception {

		Response resp = this.resource.changeInstanceState( "inexisting", InstanceStatus.DEPLOYED_STARTED.toString(), null );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testChangeState_inexistingInstance_null() throws Exception {

		Response resp = this.resource.changeInstanceState( this.app.getName(), InstanceStatus.DEPLOYED_STARTED.toString(), null );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testChangeState_inexistingInstance() throws Exception {

		Response resp = this.resource.changeInstanceState( this.app.getName(), InstanceStatus.DEPLOYED_STARTED.toString(), "/bip/bip" );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testChangeState_invalidAction() throws Exception {

		Response resp = this.resource.changeInstanceState( this.app.getName(), null, null );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testChangeState_invalidActionAgain() throws Exception {

		Response resp = this.resource.changeInstanceState( this.app.getName(), "oops", null );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testChangeState_deployRoot_success() throws Exception {

		TestTargetResolver iaasResolver = new TestTargetResolver();
		this.manager.setTargetResolver( iaasResolver );

		Assert.assertEquals( 0, iaasResolver.instanceToRunningStatus.size());
		Response resp = this.resource.changeInstanceState(
				this.app.getName(),
				InstanceStatus.DEPLOYED_STARTED.toString(),
				InstanceHelpers.computeInstancePath( this.app.getMySqlVm()));

		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 1, iaasResolver.instanceToRunningStatus.size());
		Assert.assertTrue( iaasResolver.instanceToRunningStatus.get( this.app.getMySqlVm()));
	}


	@Test
	public void testChangeState_deploy_success() throws Exception {

		Assert.assertEquals( 0, this.msgClient.sentMessages.size());
		Assert.assertEquals( 0, this.ma.removeAwaitingMessages( this.app.getTomcatVm()).size());

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.changeInstanceState( this.app.getName(), InstanceStatus.DEPLOYED_STOPPED.toString(), instancePath );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 0, this.msgClient.sentMessages.size());

		List<Message> messages = this.ma.removeAwaitingMessages( this.app.getTomcatVm());
		Assert.assertEquals( 1, messages.size());
		Assert.assertEquals( MsgCmdChangeInstanceState.class, messages.get( 0 ).getClass());
		Assert.assertEquals( instancePath, ((MsgCmdChangeInstanceState) messages.get( 0 )).getInstancePath());
	}


	@Test
	public void testChangeState_TargetException() throws Exception {

		TestTargetResolver newResolver = new TestTargetResolver() {
			@Override
			public Target findTargetHandler( List<TargetHandler> target, ManagedApplication ma, Instance instance )
			throws TargetException {
				throw new TargetException( "For test purpose!" );
			}
		};

		this.manager.setTargetResolver( newResolver );
		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcatVm());

		Assert.assertEquals( InstanceStatus.NOT_DEPLOYED, this.app.getTomcatVm().getStatus());
		Response resp = this.resource.changeInstanceState( this.app.getName(), InstanceStatus.DEPLOYED_STARTED.toString(), instancePath );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
		Assert.assertEquals( InstanceStatus.NOT_DEPLOYED, this.app.getTomcatVm().getStatus());
	}


	@Test
	public void testChangeState_IOException() throws Exception {

		this.msgClient.connected.set( false );
		Assert.assertEquals( 0, this.msgClient.sentMessages.size());
		Assert.assertEquals( 0, this.ma.removeAwaitingMessages( this.app.getTomcatVm()).size());

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.changeInstanceState( this.app.getName(), InstanceStatus.DEPLOYED_STARTED.toString(), instancePath );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 0, this.msgClient.sentMessages.size());

		List<Message> messages = this.ma.removeAwaitingMessages( this.app.getTomcatVm());
		Assert.assertEquals( 0, messages.size());
	}


	@Test
	public void testStopAll() throws Exception {

		TestClientDm msgClient = getInternalClient();
		Assert.assertEquals( 0, msgClient.sentMessages.size());
		Assert.assertEquals( 0, this.ma.removeAwaitingMessages( this.app.getTomcatVm()).size());

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.stopAll( this.app.getName(), instancePath );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 0, msgClient.sentMessages.size());

		List<Message> messages = this.ma.removeAwaitingMessages( this.app.getTomcatVm());
		Assert.assertEquals( 1, messages.size());
		Assert.assertEquals( MsgCmdChangeInstanceState.class, messages.get( 0 ).getClass());
		Assert.assertEquals( instancePath, ((MsgCmdChangeInstanceState) messages.get( 0 )).getInstancePath());
	}


	@Test
	public void testStopAll_invalidApp() throws Exception {

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.stopAll( "oops", instancePath );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testStopAll_IOException() throws Exception {

		this.msgClient.connected.set( false );
		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.stopAll( this.app.getName(), instancePath );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testUndeployAll() throws Exception {

		TestClientDm msgClient = getInternalClient();
		Assert.assertEquals( 0, msgClient.sentMessages.size());
		Assert.assertEquals( 0, this.ma.removeAwaitingMessages( this.app.getTomcatVm()).size());

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.undeployAll( this.app.getName(), instancePath );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 0, msgClient.sentMessages.size());

		List<Message> messages = this.ma.removeAwaitingMessages( this.app.getTomcatVm());
		Assert.assertEquals( 1, messages.size());
		Assert.assertEquals( MsgCmdChangeInstanceState.class, messages.get( 0 ).getClass());
		Assert.assertEquals( instancePath, ((MsgCmdChangeInstanceState) messages.get( 0 )).getInstancePath());
	}


	@Test
	public void testUndeployAll_invalidApp() throws Exception {

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.undeployAll( "oops", instancePath );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testUndeployAll_IOException() throws Exception {

		this.msgClient.connected.set( false );
		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.undeployAll( this.app.getName(), instancePath );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testDeployAndStartAll() throws Exception {

		TestClientDm msgClient = getInternalClient();
		Assert.assertEquals( 0, msgClient.sentMessages.size());
		Assert.assertEquals( 0, this.ma.removeAwaitingMessages( this.app.getTomcatVm()).size());

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.deployAndStartAll( this.app.getName(), instancePath );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 0, msgClient.sentMessages.size());

		List<Message> messages = this.ma.removeAwaitingMessages( this.app.getTomcatVm());
		Assert.assertEquals( 2, messages.size());

		Assert.assertEquals( MsgCmdChangeInstanceState.class, messages.get( 0 ).getClass());
		Assert.assertEquals( InstanceHelpers.computeInstancePath( this.app.getTomcat()), ((MsgCmdChangeInstanceState) messages.get( 0 )).getInstancePath());

		Assert.assertEquals( MsgCmdChangeInstanceState.class, messages.get( 1 ).getClass());
		Assert.assertEquals( InstanceHelpers.computeInstancePath( this.app.getWar()), ((MsgCmdChangeInstanceState) messages.get( 1 )).getInstancePath());
	}


	@Test
	public void testDeployAndStartAll_invalidApp() throws Exception {

		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.deployAndStartAll( "oops", instancePath );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testDeployAndStartAll_IOException() throws Exception {

		this.msgClient.connected.set( false );
		String instancePath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Response resp = this.resource.deployAndStartAll( this.app.getName(), instancePath );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testListChildrenInstances() throws Exception {

		List<Instance> instances = this.resource.listChildrenInstances( this.app.getName(), "/bip/bip", false );
		Assert.assertEquals( 0, instances.size());

		instances = this.resource.listChildrenInstances( this.app.getName(), "/bip/bip", true );
		Assert.assertEquals( 0, instances.size());

		instances = this.resource.listChildrenInstances( "inexisting", "/bip/bip", false );
		Assert.assertEquals( 0, instances.size());

		instances = this.resource.listChildrenInstances( this.app.getName(), null, false );
		Assert.assertEquals( this.app.getRootInstances().size(), instances.size());

		instances = this.resource.listChildrenInstances( this.app.getName(), null, true );
		Assert.assertEquals( InstanceHelpers.getAllInstances( this.app ).size(), instances.size());

		instances = this.resource.listChildrenInstances( this.app.getName(), InstanceHelpers.computeInstancePath( this.app.getTomcatVm()), false );
		Assert.assertEquals( 1, instances.size());

		instances = this.resource.listChildrenInstances( this.app.getName(), InstanceHelpers.computeInstancePath( this.app.getTomcatVm()), true );
		Assert.assertEquals( 2, instances.size());
	}


	@Test
	public void testListComponents() throws Exception {

		List<Component> components = this.resource.listComponents( "inexisting" );
		Assert.assertEquals( 0, components.size());

		components = this.resource.listComponents( this.app.getName());
		Assert.assertEquals( ComponentHelpers.findAllComponents( this.app ).size(), components.size());
	}


	@Test
	public void testComponentChildren() throws Exception {

		List<Component> components = this.resource.findComponentChildren( "inexisting", "" );
		Assert.assertEquals( 0, components.size());

		components = this.resource.findComponentChildren( this.app.getName(), "inexisting-component" );
		Assert.assertEquals( 0, components.size());

		components = this.resource.findComponentChildren( this.app.getName(), null );
		Assert.assertEquals( 1, components.size());
		Assert.assertTrue( components.contains( this.app.getMySqlVm().getComponent()));

		components = this.resource.findComponentChildren( this.app.getName(), this.app.getMySqlVm().getComponent().getName());
		Assert.assertEquals( 2, components.size());
		Assert.assertTrue( components.contains( this.app.getMySql().getComponent()));
		Assert.assertTrue( components.contains( this.app.getTomcat().getComponent()));
	}


	@Test
	public void testComponentAncestors() throws Exception {

		List<Component> components = this.resource.findComponentAncestors( "inexisting", "my-comp" );
		Assert.assertEquals( 0, components.size());

		components = this.resource.findComponentAncestors( this.app.getName(), "my-comp" );
		Assert.assertEquals( 0, components.size());

		components = this.resource.findComponentAncestors( this.app.getName(), this.app.getTomcat().getComponent().getName());
		Assert.assertEquals( 1, components.size());
		Assert.assertTrue( components.contains( this.app.getMySqlVm().getComponent()));
	}


	@Test
	public void testAddInstance_root_success() throws Exception {

		Assert.assertEquals( 2, this.app.getRootInstances().size());
		Instance newInstance = new Instance( "vm-mail" ).component( this.app.getMySqlVm().getComponent());
		Response resp = this.resource.addInstance( this.app.getName(), null, newInstance );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 3, this.app.getRootInstances().size());
	}


	@Test
	public void testAddInstance_IOException() throws Exception {

		this.msgClient.connected.set( false );

		Assert.assertEquals( 2, this.app.getRootInstances().size());
		Instance newInstance = new Instance( "vm-mail" ).component( this.app.getMySqlVm().getComponent());
		Response resp = this.resource.addInstance( this.app.getName(), null, newInstance );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 2, this.app.getRootInstances().size());
	}


	@Test
	public void testAddInstance_root_nullComponent() throws Exception {

		Assert.assertEquals( 2, this.app.getRootInstances().size());
		Instance existingInstance = new Instance( this.app.getMySqlVm().getName());
		Response resp = this.resource.addInstance( this.app.getName(), null, existingInstance );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testAddInstance_root_invalidComponent() throws Exception {

		Assert.assertEquals( 2, this.app.getRootInstances().size());
		Instance existingInstance = new Instance( this.app.getMySqlVm().getName()).component( new Component( "invalid" ));
		Response resp = this.resource.addInstance( this.app.getName(), null, existingInstance );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testAddInstance_root_duplicate() throws Exception {

		Assert.assertEquals( 2, this.app.getRootInstances().size());
		Instance existingInstance = new Instance( this.app.getMySqlVm().getName()).component( this.app.getMySqlVm().getComponent());
		Response resp = this.resource.addInstance( this.app.getName(), null, existingInstance );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testAddInstance_child_success() throws Exception {

		Instance newMysql = new Instance( "mysql-2" ).component( this.app.getMySql().getComponent());

		Assert.assertEquals( 1, this.app.getTomcatVm().getChildren().size());
		Assert.assertFalse( this.app.getTomcatVm().getChildren().contains( newMysql ));

		Response resp = this.resource.addInstance( this.app.getName(), InstanceHelpers.computeInstancePath( this.app.getTomcatVm()), newMysql );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 2, this.app.getTomcatVm().getChildren().size());

		List<String> paths = new ArrayList<> ();
		for( Instance inst : this.app.getTomcatVm().getChildren())
			paths.add( InstanceHelpers.computeInstancePath( inst ));

		String rootPath = InstanceHelpers.computeInstancePath( this.app.getTomcatVm());
		Assert.assertTrue( paths.contains( rootPath + "/" + newMysql.getName()));
		Assert.assertTrue( paths.contains( rootPath + "/" + this.app.getTomcat().getName()));
	}


	@Test
	public void testAddInstance_child_incompleteComponent() throws Exception {

		// Pass an incomplete component object to the REST API
		String mySqlComponentName = this.app.getMySql().getComponent().getName();
		Instance newMysql = new Instance( "mysql-2" ).component( new Component( mySqlComponentName ));

		Assert.assertEquals( 1, this.app.getTomcatVm().getChildren().size());
		Assert.assertFalse( this.app.getTomcatVm().getChildren().contains( newMysql ));

		Response resp = this.resource.addInstance( this.app.getName(), InstanceHelpers.computeInstancePath( this.app.getTomcatVm()), newMysql );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());
		Assert.assertEquals( 2, this.app.getTomcatVm().getChildren().size());

		List<String> paths = new ArrayList<> ();
		for( Instance inst : this.app.getTomcatVm().getChildren())
			paths.add( InstanceHelpers.computeInstancePath( inst ));

		String rootPath = InstanceHelpers.computeInstancePath( this.app.getTomcatVm());
		Assert.assertTrue( paths.contains( rootPath + "/" + newMysql.getName()));
		Assert.assertTrue( paths.contains( rootPath + "/" + this.app.getTomcat().getName()));
	}


	@Test
	public void testAddInstance_child_failure() throws Exception {

		// We cannot deploy a WAR directly on a VM!
		// At least, this what the graph says.
		Instance newWar = new Instance( "war-2" ).component( this.app.getWar().getComponent());

		Assert.assertEquals( 1, this.app.getTomcatVm().getChildren().size());
		Assert.assertFalse( this.app.getTomcatVm().getChildren().contains( newWar ));
		Response resp = this.resource.addInstance( this.app.getName(), InstanceHelpers.computeInstancePath( this.app.getTomcatVm()), newWar );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testAddInstance_inexstingApplication() throws Exception {

		Instance newMysql = new Instance( "mysql-2" ).component( this.app.getMySql().getComponent());
		Response resp = this.resource.addInstance( "inexisting", InstanceHelpers.computeInstancePath( this.app.getTomcatVm()), newMysql );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testAddInstance_inexstingParentInstance() throws Exception {

		Instance newMysql = new Instance( "mysql-2" ).component( this.app.getMySql().getComponent());
		Response resp = this.resource.addInstance( "inexisting", "/bip/bip", newMysql );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testRemoveInstance_success() {

		// Check the Tomcat instance is here.
		final String tomcatPath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Assert.assertNotNull( InstanceHelpers.findInstanceByPath( this.app, tomcatPath ));

		// Delete the Tomcat instance.
		Response resp = this.resource.removeInstance( this.app.getName(), tomcatPath );
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());

		// Check it is gone.
		Assert.assertNull( InstanceHelpers.findInstanceByPath( this.app, tomcatPath ));
	}


	@Test
	public void testRemoveInstance_stillRunning() {

		this.app.getTomcat().setStatus( InstanceStatus.DEPLOYED_STARTED );

		// Check the Tomcat instance is here.
		final String tomcatPath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Assert.assertNotNull( InstanceHelpers.findInstanceByPath( this.app, tomcatPath ));

		// Delete the Tomcat instance.
		Response resp = this.resource.removeInstance( this.app.getName(), tomcatPath );
		Assert.assertEquals( Status.FORBIDDEN.getStatusCode(), resp.getStatus());

		// Check it is NOT gone.
		Assert.assertNotNull( InstanceHelpers.findInstanceByPath( this.app, tomcatPath ));
	}


	@Test
	public void testRemoveInstance_IOException() {

		this.msgClient.connected.set( false );

		// Check the Tomcat instance is here.
		final String tomcatPath = InstanceHelpers.computeInstancePath( this.app.getTomcat());
		Assert.assertNotNull( InstanceHelpers.findInstanceByPath( this.app, tomcatPath ));

		// Delete the Tomcat instance.
		Response resp = this.resource.removeInstance( this.app.getName(), tomcatPath );
		Assert.assertEquals( Status.NOT_ACCEPTABLE.getStatusCode(), resp.getStatus());

		// Check it is NOT gone.
		Assert.assertNotNull( InstanceHelpers.findInstanceByPath( this.app, tomcatPath ));
	}


	@Test
	public void testRemoveInstance_nonExistingInstance() {
		Response resp = this.resource.removeInstance( this.app.getName(), "/I-do-not-exist" );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testRemoveInstance_nonExistingApplication() {
		Response resp = this.resource.removeInstance( "I-am-not-an-app", InstanceHelpers.computeInstancePath( this.app.getTomcat()));
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testResynchronize_success() throws Exception {
		final Collection<Instance> rootInstances = this.app.getRootInstances();

		// Deploy & start everything.
		for(Instance i : rootInstances)
			i.setStatus( InstanceStatus.DEPLOYED_STARTED );

		// Request an application resynchronization.
		Response resp = this.resource.resynchronize( this.app.getName());
		Assert.assertEquals( Status.OK.getStatusCode(), resp.getStatus());

		// Check a MsgCmdResynchronize has been sent to each agent.
		final List<Message> sentMessages = this.msgClient.sentMessages;
		Assert.assertEquals( rootInstances.size(), sentMessages.size());
		for( Message message : sentMessages )
			Assert.assertTrue( message instanceof MsgCmdResynchronize );
	}


	@Test
	public void testResynchronize_IOException() throws Exception {

		this.msgClient.connected.set( false );
		Response resp = this.resource.resynchronize( this.app.getName());
		Assert.assertEquals( Status.NOT_ACCEPTABLE.getStatusCode(), resp.getStatus());
	}


	@Test
	public void testResynchronize_nonExistingApplication() {
		Response resp = this.resource.resynchronize( "I-am-not-an-app" );
		Assert.assertEquals( Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}


	private TestClientDm getInternalClient() throws IllegalAccessException {
		return TestUtils.getInternalField( this.manager.getMessagingClient(), "messagingClient", TestClientDm.class );
	}
}