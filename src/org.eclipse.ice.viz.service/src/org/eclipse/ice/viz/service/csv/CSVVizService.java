/*******************************************************************************
 * Copyright (c) 2014 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - 
 *   Jay Jay Billings
 *******************************************************************************/
package org.eclipse.ice.viz.service.csv;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.ice.viz.service.AbstractVizService;
import org.eclipse.ice.viz.service.IPlot;

/**
 * This class implements the IVizService interface to provide CSV plotting tools
 * to the platform via the IVizServiceFactory. It used the classes already
 * available in org.eclipse.ice.viz and provides a rudimentary plotting
 * capability.
 * 
 * It is the default "ice-plot" implementation provided by the platform.
 * 
 * @author Jay Jay Billings
 * 
 */
public class CSVVizService extends AbstractVizService {

	/**
	 * The default constructor.
	 * <p>
	 * <b>Note:</b> Only OSGi should call this method!
	 * </p>
	 */
	public CSVVizService() {
		// Nothing to do.
	}

	/*
	 * Overrides a super class method. Tries to create a CSVPlot.
	 */
	@Override
	public IPlot createPlot(URI file) throws Exception {
		// Call the super method to validate the URI's extension.
		super.createPlot(file);

		// Create the plot and load it
		CSVPlot plot = new CSVPlot(file);
		plot.load();

		return plot;
	}

	/*
	 * Implements an abstract method from AbstractVizService.
	 */
	@Override
	protected Set<String> findSupportedExtensions() {
		Set<String> extensions = new HashSet<String>();
		extensions.add("csv");
		return extensions;
	}

	/*
	 * Implements a method from IVizService.
	 */
	@Override
	public String getName() {
		return "ice-plot";
	}

	/*
	 * Implements a method from IVizService.
	 */
	@Override
	public String getVersion() {
		return "2.0";
	}

}
