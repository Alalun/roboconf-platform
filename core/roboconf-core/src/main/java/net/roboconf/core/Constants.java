/**
 * Copyright 2013-2014 Linagora, Université Joseph Fourier, Floralis
 *
 * The present code is developed in the scope of their joint LINAGORA -
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

package net.roboconf.core;

/**
 * A set of constants.
 * @author Vincent Zurczak - Linagora
 */
public interface Constants {

	/**
	 * The name of the target installer (which is somehow specific).
	 */
	String TARGET_INSTALLER = "target";

	/**
	 * The name of the file that contain the target properties.
	 */
	String TARGET_PROPERTIES_FILE_NAME = "target.properties";

	/**
	 * The heart beat period (in milliseconds).
	 */
	long HEARTBEAT_PERIOD = 60000;

	/**
	 * Keyword for facet property.
	 */
	String PROPERTY_FACET_EXTENDS = "extends";


	/**
	 * Keyword for component property.
	 */
	String PROPERTY_COMPONENT_ALIAS = "alias";

	/**
	 * Keyword for component property.
	 */
	String PROPERTY_COMPONENT_FACETS = "facets";

	/**
	 * Keyword for component property.
	 */
	String PROPERTY_COMPONENT_IMPORTS = "imports";

	/**
	 * Keyword for component property.
	 */
	String PROPERTY_COMPONENT_OPTIONAL_IMPORT = "(optional)";


	/**
	 * Keyword for a facet or a component property.
	 */
	String PROPERTY_GRAPH_EXPORTS = "exports";

	/**
	 * Keyword for a facet or a component property.
	 */
	String PROPERTY_GRAPH_CHILDREN = "children";

	/**
	 * Keyword for a facet or a component property.
	 */
	String PROPERTY_GRAPH_ICON_LOCATION = "icon";

	/**
	 * Keyword for a facet or a component property.
	 */
	String PROPERTY_GRAPH_INSTALLER = "installer";


	/**
	 * Keyword for an instance property.
	 */
	String PROPERTY_INSTANCE_NAME = "name";

	/**
	 * Keyword for an instance property (runtime, not meant to be set manually).
	 */
	String PROPERTY_INSTANCE_DATA = "instance-data";

	/**
	 * Keyword for an instance property (runtime, not meant to be set manually).
	 */
	String PROPERTY_INSTANCE_STATE = "instance-state";

	/**
	 * Keyword for an instance property.
	 */
	String PROPERTY_INSTANCE_CHANNEL = "channel";

	/**
	 * Keyword to create several instances at once.
	 */
	String PROPERTY_INSTANCE_COUNT = "count";


	/**
	 * The <strong>graph</strong> directory.
	 */
	String PROJECT_DIR_GRAPH = "graph";

	/**
	 * The <strong>descriptor</strong> directory.
	 */
	String PROJECT_DIR_DESC = "descriptor";

	/**
	 * The <strong>instances</strong> directory.
	 */
	String PROJECT_DIR_INSTANCES = "instances";

	/**
	 * The <strong>application.properties</strong> file name.
	 */
	String PROJECT_FILE_DESCRIPTOR = "application.properties";
}
