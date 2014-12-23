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

package net.roboconf.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.roboconf.core.Constants;
import net.roboconf.core.model.beans.Component;
import net.roboconf.core.model.beans.Instance;

/**
 * @author Vincent Zurczak - Linagora
 */
public final class ResourceUtils {

	/**
	 * Private empty constructor.
	 */
	private ResourceUtils() {
		// nothing
	}


	/**
	 * Stores the instance resources into a map.
	 * @param applicationFilesDirectory the application's directory
	 * @param instance an instance (not null)
	 * @return a non-null map (key = the file location, relative to the instance's directory, value = file content)
	 * @throws IOException if something went wrong while reading a file
	 */
	public static Map<String,byte[]> storeInstanceResources( File applicationFilesDirectory, Instance instance ) throws IOException {

		// Recipes
		Map<String,byte[]> result = new HashMap<String,byte[]> ();
		File instanceResourcesDirectory = findInstanceResourcesDirectory( applicationFilesDirectory, instance );
		if( instanceResourcesDirectory.exists()
				&& instanceResourcesDirectory.isDirectory())
			result.putAll( Utils.storeDirectoryResourcesAsBytes( instanceResourcesDirectory ));

		// Measure files (are not located with recipes, so no trouble with component inheritance)
		String fileName = instance.getComponent().getName() + Constants.FILE_EXT_MEASURES;
		File autonomicMesureFile = new File( applicationFilesDirectory, Constants.PROJECT_DIR_AUTONOMIC + "/" + fileName );
		if( autonomicMesureFile.exists()) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Utils.copyStream( autonomicMesureFile, os );
			result.put( autonomicMesureFile.getName(), os.toByteArray());
		}

		return result;
	}


	/**
	 * Finds the resource directory for an instance.
	 * @param applicationFilesDirectory the application's directory
	 * @param instance an instance
	 * @return a non-null file (that may not exist)
	 */
	public static File findInstanceResourcesDirectory( File applicationFilesDirectory, Instance instance ) {
		return findInstanceResourcesDirectory( applicationFilesDirectory, instance.getComponent());
	}


	/**
	 * Finds the resource directory for an instance.
	 * <p>
	 * The resource directory may be the one of another component.
	 * This is the case when a component extends another component.
	 * </p>
	 * <p>
	 * An extending component can override the resource directory.
	 * </p>
	 *
	 * @param applicationFilesDirectory the application's directory
	 * @param componentName the component name (may be null)
	 * @return a non-null file (that may not exist)
	 */
	public static File findInstanceResourcesDirectory( File applicationFilesDirectory, Component component ) {

		File root = new File( applicationFilesDirectory, Constants.PROJECT_DIR_GRAPH );
		File result = new File( "No recipe directory." );
		Set<Component> alreadyChecked = new HashSet<Component> ();
		for( Component c = component; c != null; c = c.getExtendedComponent()) {
			// Prevent infinite loops for exotic cases
			if( alreadyChecked.contains( c ))
				break;

			alreadyChecked.add( c );
			if(( result = new File( root, c.getName())).exists())
				break;
		}

		return result;
	}
}