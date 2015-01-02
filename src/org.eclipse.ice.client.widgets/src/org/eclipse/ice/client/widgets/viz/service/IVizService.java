/*******************************************************************************
 * Copyright (c) 2012, 2014 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - 
 *   Jay Jay Billings
 *******************************************************************************/
package org.eclipse.ice.client.widgets.viz.service;

import java.net.URI;
import java.util.Map;

/**
 * This is a pluggable service interface whereby visualization engines can
 * publish their services to the platform. It is designed to be implemented as a
 * pluggable, declarative OSGi service that is provided dynamically to an
 * implementation of the IVizServiceFactory.
 * 
 * IVizServices should be considered handles to the services that a particular
 * visualization engine provides. Its primary purpose a means to configure a
 * valid connection to the visualization service, if required, and to act as a
 * factory for creating IPlots.
 * 
 * @author Jay Jay Billings
 */
public interface IVizService {

	/**
	 * This operation returns the name of the service. The name should be
	 * something simple and human-readable.
	 * 
	 * @return The name of the IVizService
	 */
	public String getName();

	/**
	 * This operation returns a version number for the service. It should be
	 * more or less human readable and contain a major and a minor version
	 * (Version 2.1 instead of just 2, for example).
	 * 
	 * @return The version of the IVizService
	 */
	public String getVersion();

	/**
	 * This operation returns the current set of connection properties for the
	 * IVizService. The Connection Properties are any properties that are
	 * required to connect to additional pieces of the IVizService, such as a
	 * remote server or local socket.
	 * 
	 * The contents of the properties are up to the IVizService implementation
	 * 
	 * @return The properties or an empty (but not null) map if the IVizService
	 *         does not require making a connection.
	 */
	public Map<String, String> getConnectionProperties();

	/**
	 * This operation updates the connection properties based on updates from a
	 * client. These properties should be persisted through service and platform
	 * restarts.
	 * 
	 * If the IVizService does not require a connection to other components, it
	 * may ignore this operation.
	 * 
	 * @param props
	 *            The new property values
	 */
	public void setConnectionProperties(Map<String, String> props);

	/**
	 * This operation directs the IVizService to "connect" to any pieces of its
	 * service that may not already be running or require additional
	 * configuration at run time. For example, although the VisItVizService may
	 * be running, it may not be connected to a VisIt server, either locally or
	 * remotely. Some services, such as those implemented completely in Java
	 * and/or as OSGi services, may not need to perform a connection; developers
	 * should read the documentation for each IVizService carefully.
	 * 
	 * Each IVizService that requires a connection should provide a "best guess"
	 * at initial connection properties and if the properties are not updated a
	 * call to connect() should attempt to connect using the default properties.
	 * 
	 * @return True if the connection was successfully made, false otherwise.
	 *         Also true if the implementation did not need to make a connection
	 *         (connected by default).
	 */
	public boolean connect();

	/**
	 * This operation directs the IVizService to create a new plot using the
	 * specified file and to return a handle to that plot to the caller so that
	 * it may modify the plot.
	 * 
	 * @param file
	 *            The file from which the plot should be created
	 * @return The IPlot that will render a plot from the file
	 * @throws e
	 *             An exception indicating that the IVizService could not create
	 *             a plot with the given file and giving the reason why.
	 */
	public IPlot createPlot(URI file) throws Exception;
}
