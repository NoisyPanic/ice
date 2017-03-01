/*******************************************************************************
 * Copyright (c) 2015 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Alex McCaskey - Initial API and implementation and/or initial documentation
 *
 *******************************************************************************/
package org.eclipse.ice.developer.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apps.AppsFactory;
import apps.EnvironmentManager;
import apps.IEnvironment;
import apps.eclipse.EclipseFactory;

/**
 * The GitCloneHandler is an AbstractHandler that provides an execute
 * implemenation that clones the repository with the given repoURLID found in
 * the ExecutionEvent IParameter map.
 * 
 * @author Alex McCaskey
 *
 */
public class ICEAppStoreHandler extends AbstractHandler {

	/**
	 * Logger for handling event messages and other information.
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ICEAppStoreHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		EnvironmentManager manager = AppsFactory.eINSTANCE.createEnvironmentManager();

		manager.setEnvironmentStorage(EclipseFactory.eINSTANCE.createEclipseEnvironmentStorage());
		
		// Show view to get Json string
		String jsonStr = "{\n" + 
				"   \"General\": {\n" + 
				"       \"name\": \"mccaskey/test_env\",\n" + 
				"       \"type\": \"Docker\"\n" + 
				"    },\n" + 
				"    \"Application\": {\n" + 
				"       \"type\": \"Source\",\n" + 
				"       \"name\": \"xacc\",\n" + 
				"       \"repoURL\": \"https://github.com/ORNL-QCI/xacc\",\n" + 
				"       \"buildCommand\": \"cd xacc && mkdir build && cd build && cmake .. && make\"\n" + 
				"     },\n" + 
				"     \"Dependencies\": [\n" + 
				"         {\n" + 
				"           \"type\": \"OS\",\n" + 
				"           \"name\": \"cmake\",\n" + 
				"         },\n" + 
				"         {\n" + 
				"           \"type\": \"OS\",\n" + 
				"           \"name\": \"https://github.com/ORNL-QCI/ScaffCC/releases/download/v2.0/scaffold-2.0-1.fc25.x86_64.rpm\"\n" + 
				"         },\n" + 
				"         {\n" + 
				"           \"type\": \"OS\",\n" + 
				"           \"name\": \"boost-mpich-devel\"\n" + 
				"         },\n" + 
				"         {\n" + 
				"           \"type\": \"OS\",\n" + 
				"           \"name\": \"mpich-devel\"\n" + 
				"         }\n" + 
				"      ],\n" + 
				"      \"ContainerConfig\": {\n" + 
				"         \"name\": \"xaccdev\",\n" + 
				"         \"ephemeral\": true\n" + 
				"      }\n" + 
				"}";
		
		IEnvironment environment = manager.create(jsonStr);
		
		environment.setProjectlauncher(EclipseFactory.eINSTANCE.createDockerPTPSyncProjectLauncher());
		
		if (!environment.build() || !environment.connect()) {
			throw new ExecutionException("Could not build or connect to the environment.");
		}
		
		return null;
	}

}
