/**
 * 
 */
/*******************************************************************************
 * Copyright (c) 2020- UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - 
 *   Jay Jay Billings
 *******************************************************************************/
package gov.ornl.rse.renderer.client.test;

import java.io.Serializable;

import org.eclipse.ice.dev.annotations.IDataElement;
import org.eclipse.ice.renderer.DataElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

import elemental.json.JsonObject;

/**
 * This is a base class for renderer clients tailored to Vaadin. It provides
 * the basic accessors for the data member.
 * 
 * @author Jay Jay Billings
 */
@Tag("renderer-template")
@JsModule("./src/renderer.ts")
public class VaadinRendererClient<T extends IDataElement> extends Component implements IRendererClient<T> {

	/**
	 * version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The local reference to the data element
	 */
	T data;

	/**
	 * Constructor
	 */
	public VaadinRendererClient() {
		// This function updates the data every time the client posts and update.
		getElement().addPropertyChangeListener("dataElementJSON", "data-changed", e -> {
			String value = getElement().getProperty("dataElementJSON");
			data.fromJson(value);
		});
	}

	/**
	 * This operation sets the data that should be rendered.
	 * 
	 * @param otherData The data element that should be rendered. This function
	 *                  overwrites the existing data on the client and server.
	 */
	@Override
	public void setData(T otherData) {
		data = otherData;
		getElement().setProperty("dataElementJSON", data.toJson());
	}

	/**
	 * This function returns the present version of the DataElement
	 * 
	 * @return the data element. Note that it is as up to date as possible, but
	 *         there is a chance that the most recent updates from the client have
	 *         not been committed due to latency.
	 */
	@Override
	public T getData() {
		return data;
	}

}
