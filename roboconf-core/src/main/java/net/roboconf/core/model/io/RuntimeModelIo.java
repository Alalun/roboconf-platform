/**
 * Copyright 2013-2014 Linagora, Université Joseph Fourier
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

package net.roboconf.core.model.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.roboconf.core.Constants;
import net.roboconf.core.ErrorCode;
import net.roboconf.core.RoboconfError;
import net.roboconf.core.model.ApplicationDescriptor;
import net.roboconf.core.model.ModelError;
import net.roboconf.core.model.converters.FromGraphDefinition;
import net.roboconf.core.model.converters.FromInstanceDefinition;
import net.roboconf.core.model.parsing.FileDefinition;
import net.roboconf.core.model.runtime.Application;
import net.roboconf.core.model.runtime.Graphs;
import net.roboconf.core.model.runtime.Instance;
import net.roboconf.core.model.validators.ParsingModelValidator;
import net.roboconf.core.model.validators.RuntimeModelValidator;
import net.roboconf.core.utils.Utils;

/**
 * @author Vincent Zurczak - Linagora
 */
public class RuntimeModelIo {

	/**
	 * Loads an application from a directory.
	 * <p>
	 * The directory structure must be the following one:
	 * </p>
	 * <ul>
	 * 		<li>descriptor</li>
	 * 		<li>graph</li>
	 * 		<li>instances (optional)</li>
	 * </ul>
	 *
	 * @param projectDirectory the project directory
	 * @return a load result (never null)
	 */
	public static LoadResult loadApplication( File projectDirectory ) {
		LoadResult result = new LoadResult();
		Application app = new Application();

		ApplicationDescriptor appDescriptor = null;
		File descDirectory = new File( projectDirectory, Constants.PROJECT_DIR_DESC );
		File instDirectory = new File( projectDirectory, Constants.PROJECT_DIR_INSTANCES );
		File graphDirectory = new File( projectDirectory, Constants.PROJECT_DIR_GRAPH );


		// Read the application descriptor
		DESC: if( ! descDirectory.exists()) {
			RoboconfError error = new RoboconfError( ErrorCode.PROJ_NO_DESC_DIR );
			error.setDetails( "Directory path: " + projectDirectory.getAbsolutePath());
			result.loadErrors.add( error );

		} else {
			File descriptorFile = new File( descDirectory, Constants.PROJECT_FILE_DESCRIPTOR );
			if( ! descriptorFile.exists()) {
				result.loadErrors.add( new RoboconfError( ErrorCode.PROJ_NO_DESC_FILE ));
				break DESC;
			}

			try {
				appDescriptor = ApplicationDescriptor.load( descriptorFile );
				app.setName( appDescriptor.getName());
				app.setDescription( appDescriptor.getDescription());
				app.setQualifier( appDescriptor.getQualifier());

				Collection<RoboconfError> errors = RuntimeModelValidator.validate( appDescriptor );
				result.loadErrors.addAll( errors );

			} catch( IOException e ) {
				RoboconfError error = new RoboconfError( ErrorCode.PROJ_READ_DESC_FILE );
				StringBuilder sb = new StringBuilder( "IO exception." );
				if( e.getMessage() != null ) {
					sb.append( " " );
					sb.append( e.getMessage());
				}

				error.setDetails( sb.toString());
				result.loadErrors.add( error );
			}
		}


		// Load the graph
		GRAPH: if( ! graphDirectory.exists()) {
			RoboconfError error = new RoboconfError( ErrorCode.PROJ_NO_GRAPH_DIR );
			error.setDetails( "Directory path: " + projectDirectory.getAbsolutePath());
			result.loadErrors.add( error );

		} else if( appDescriptor != null ) {
			File mainGraphFile = new File( graphDirectory, appDescriptor.getGraphEntryPoint());
			if( ! mainGraphFile.exists()) {
				RoboconfError error = new RoboconfError( ErrorCode.PROJ_MISSING_GRAPH_EP );
				error.setDetails( "Expected path: " + mainGraphFile.getAbsolutePath());
				result.loadErrors.add( error );
				break GRAPH;
			}

			FileDefinition def = ParsingModelIo.readConfigurationFile( mainGraphFile, true );
			if( ! def.getParsingErrors().isEmpty()) {
				result.loadErrors.addAll( def.getParsingErrors());
				break GRAPH;
			}

			if( def.getFileType() != FileDefinition.GRAPH
					&& def.getFileType() != FileDefinition.AGGREGATOR ) {
				result.loadErrors.add( new ModelError( ErrorCode.PROJ_NOT_A_GRAPH, 1 ));
				break GRAPH;
			}

			Collection<ModelError> validationErrors = ParsingModelValidator.validate( def );
			if( ! validationErrors.isEmpty()) {
				result.loadErrors.addAll( validationErrors );
				break GRAPH;
			}

			FromGraphDefinition fromDef = new FromGraphDefinition( def );
			Graphs graph = fromDef.buildGraphs();
			if( ! fromDef.getErrors().isEmpty()) {
				result.loadErrors.addAll( fromDef.getErrors());
				break GRAPH;
			}

			Collection<RoboconfError> errors = RuntimeModelValidator.validate( graph );
			result.loadErrors.addAll( errors );

			app.setGraphs( graph );
		}


		// Load the instances
		INST: if( appDescriptor != null && instDirectory.exists()) {
			if( Utils.isEmptyOrWhitespaces( appDescriptor.getInstanceEntryPoint()))
				break INST;

			File mainInstFile = new File( instDirectory, appDescriptor.getInstanceEntryPoint());
			if( ! mainInstFile.exists()) {
				RoboconfError error = new RoboconfError( ErrorCode.PROJ_MISSING_INSTANCE_EP );
				error.setDetails( "Expected path: " + mainInstFile.getAbsolutePath());
				result.loadErrors.add( error );
				break INST;
			}

			FileDefinition def = ParsingModelIo.readConfigurationFile( mainInstFile, true );
			if( ! def.getParsingErrors().isEmpty()) {
				result.loadErrors.addAll( def.getParsingErrors());
				break INST;
			}

			if( def.getFileType() != FileDefinition.INSTANCE
					&& def.getFileType() != FileDefinition.AGGREGATOR ) {
				result.loadErrors.add( new ModelError( ErrorCode.PROJ_NOT_AN_INSTANCE, 1 ));
				break INST;
			}

			Collection<ModelError> validationErrors = ParsingModelValidator.validate( def );
			if( ! validationErrors.isEmpty()) {
				result.loadErrors.addAll( validationErrors );
				break INST;
			}

			FromInstanceDefinition fromDef = new FromInstanceDefinition( def );
			Collection<Instance> instances = fromDef.buildInstances( app.getGraphs());
			if( ! fromDef.getErrors().isEmpty()) {
				result.loadErrors.addAll( fromDef.getErrors());
				break INST;
			}

			Collection<RoboconfError> errors = RuntimeModelValidator.validate( instances );
			result.loadErrors.addAll( errors );

			app.getRootInstances().addAll( instances );
		}

		result.application = app;
		return result;
	}


	/**
	 * A bean that stores both the application and loading errors.
	 */
	public static class LoadResult {
		Application application;
		final Collection<RoboconfError> loadErrors = new ArrayList<RoboconfError> ();

		/**
		 * @return the application (can be null)
		 */
		public Application getApplication() {
			return this.application;
		}

		/**
		 * @return the load errors (never null)
		 */
		public Collection<RoboconfError> getLoadErrors() {
			return this.loadErrors;
		}
	}
}
