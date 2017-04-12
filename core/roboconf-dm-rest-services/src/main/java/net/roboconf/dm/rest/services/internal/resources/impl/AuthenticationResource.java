/**
 * Copyright 2017 Linagora, Université Joseph Fourier, Floralis
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

import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.roboconf.dm.rest.commons.UrlConstants;
import net.roboconf.dm.rest.commons.security.AuthenticationManager;
import net.roboconf.dm.rest.services.internal.resources.IAuthenticationResource;

/**
 * @author Vincent Zurczak - Linagora
 */
@Path( IAuthenticationResource.PATH )
public class AuthenticationResource implements IAuthenticationResource {

	private final Logger logger = Logger.getLogger( getClass().getName());
	private AuthenticationManager authenticationManager;


	@Override
	public Response login( String username, String password ) {
		this.logger.fine( "Authenticating user " + username + "..." );

		String sessionId;
		Response response;
		if( this.authenticationManager == null ) {
			response = Response.status( Status.INTERNAL_SERVER_ERROR ).entity( "No authentication manager was available." ).build();
			this.logger.fine( "No authentication manager was available. User was " + username );

		} else if(( sessionId = this.authenticationManager.login( username, password )) == null ) {
			response = Response.status( Status.FORBIDDEN ).entity( "Authentication failed." ).build();
			this.logger.fine( "Authentication failed. User was " + username );

		} else {
			response = Response.ok().cookie( new NewCookie( UrlConstants.SESSION_ID, sessionId )).build();
			this.logger.fine( "Authentication succeeded. User was " + username );
		}

		// NewCookie's implementation uses NewCookie.DEFAULT_MAX_AGE as the default
		// validity for a cookie, which means it is valid until the browser is closed.
		// That's fine for us. In addition, we maintain a validity period on the server, in memory.
		// This last one is managed by the authentication manager, itself bound to a REALM.

		return response;
	}


	@Override
	public Response logout( String sessionId ) {

		this.logger.fine( "Terminating session " + sessionId + "..." );
		if( this.authenticationManager != null )
			this.authenticationManager.logout( sessionId );

		return Response.ok().build();
	}


	/**
	 * @param authenticationManager the authenticationManager to set
	 */
	public void setAuthenticationManager( AuthenticationManager authenticationManager ) {
		this.authenticationManager = authenticationManager;
	}
}