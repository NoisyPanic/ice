/*******************************************************************************
 * Copyright (c) 2012, 2014 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - Jay Jay Billings,
 *   Jordan H. Deyton, Dasha Gorin, Alexander J. McCaskey, Taylor Patterson,
 *   Claire Saunders, Matthew Wang, Anna Wojtowicz
 *******************************************************************************/
package org.eclipse.ice.tablecomponenttester;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ice.datastructures.form.AllowedValueType;
import org.eclipse.ice.datastructures.form.DataComponent;
import org.eclipse.ice.datastructures.form.Entry;
import org.eclipse.ice.datastructures.form.Form;
import org.eclipse.ice.datastructures.form.MasterDetailsComponent;
import org.eclipse.ice.datastructures.form.MasterDetailsPair;
import org.eclipse.ice.datastructures.form.ResourceComponent;
import org.eclipse.ice.datastructures.form.TableComponent;
import org.eclipse.ice.datastructures.form.TreeComposite;
import org.eclipse.ice.viz.service.geometry.GeometryComponent;
import org.eclipse.ice.datastructures.resource.ICEResource;
import org.eclipse.ice.item.Item;
import org.eclipse.ice.reactor.LWRComponent;
import org.eclipse.ice.reactor.LWRComponentReader;
import org.osgi.framework.Bundle;

@XmlRootElement(name = "TableComponentTester")
public class TableComponentTester extends Item {

	public TableComponentTester() {
		this(null);
	}

	public TableComponentTester(IProject project) {
		super(project);
	}

	@Override
	public void setupForm() {

		// Local Declarations
		MasterDetailsComponent mDetailsComp = new MasterDetailsComponent();
		DataComponent detailsComp1, detailsComp2, detailsComp3;
		MasterDetailsPair mdpair1, mdpair2, mdpair3;
		ArrayList<MasterDetailsPair> template;
		ArrayList<DataComponent> dataCompTemplate;
		ArrayList<String> masterTypeTemplate;
		Entry entry1, entry2, entry3, entry4;
		ResourceComponent resourceComp = new ResourceComponent();
		TreeComposite parent = null, child1 = null, child2 = null, child3 = null;

		// Create the Form
		form = new Form();

		// Set the details of this Item
		setName("ICE Demonstration Component");
		setDescription("This component demonstrates the different types of "
				+ "data structures available in ICE and their visual "
				+ "representations.");

		// create a new instance of MasterDetailsComponent
		mDetailsComp = new MasterDetailsComponent();

		// Setup list for templates
		// Create Entries

		entry1 = new Entry() {
			@Override
			protected void setup() {
				this.allowedValues = new ArrayList<String>();
				this.allowedValues.add("0");
				this.allowedValues.add("50");
				this.defaultValue = "1";
				this.allowedValueType = AllowedValueType.Continuous;
			}
		};
		entry1.setName("Generic 1");

		entry2 = new Entry() {
			@Override
			protected void setup() {
				this.allowedValues = new ArrayList<String>();
				this.allowedValues.add("Check");
				this.allowedValues.add("Not Checked");
				// this.defaultValue = "Yes";
				this.allowedValueType = AllowedValueType.Discrete;
			}
		};
		entry2.setName("Generic 2");

		entry3 = new Entry() {
			@Override
			protected void setup() {
				this.allowedValues = new ArrayList<String>();
				this.defaultValue = "Text";
				this.allowedValueType = AllowedValueType.Undefined;
			}
		};
		entry3.setName("Generic 3");

		entry4 = new Entry() {
			@Override
			protected void setup() {
				this.allowedValues = new ArrayList<String>();
				this.allowedValues.add("0");
				this.allowedValues.add("10000");
				this.defaultValue = "9001";
				this.allowedValueType = AllowedValueType.Continuous;
			}
		};

		// Create DataComponents
		detailsComp1 = new DataComponent();
		detailsComp2 = new DataComponent();
		detailsComp3 = new DataComponent();

		// Add entries
		detailsComp1.addEntry(entry1);
		detailsComp2.addEntry(entry2);
		detailsComp3.addEntry(entry3);
		// detailsComp3.addEntry(entry4);

		// Set names
		detailsComp1.setName("Generic #1");
		detailsComp2.setName("Generic #2");
		detailsComp3.setName("Generic #3");
		detailsComp1.setDescription("Generic #1 Description");
		detailsComp2.setDescription("Generic #2 Description");
		detailsComp3.setDescription("Generic #3 Description");

		// Create pairs
		mdpair1 = new MasterDetailsPair("Generic 1", detailsComp1);
		mdpair2 = new MasterDetailsPair("Generic 2", detailsComp2);
		mdpair3 = new MasterDetailsPair("Generic 3", detailsComp3);
		mdpair1.setMasterDetailsPairId(0);
		mdpair2.setMasterDetailsPairId(1);
		mdpair3.setMasterDetailsPairId(2);

		// Add pairs to template
		template = new ArrayList<MasterDetailsPair>();
		template.add(mdpair1);
		template.add(mdpair2);
		template.add(mdpair3);

		// create a page, set template
		mDetailsComp = new MasterDetailsComponent();
		mDetailsComp.setName("MultiLauncher");
		mDetailsComp.setDescription("A Lion");

		// check that the template needs to have contents
		mDetailsComp.setTemplates(template);
		form.addComponent(mDetailsComp);

		// ================================== NEXT PAGE: TableComponent
		// =============================================================================

		TableComponent table = new TableComponent();

		Entry column1 = new Entry() {
			@Override
			public void setup() {
				defaultValue = "hello1";
				allowedValueType = AllowedValueType.Undefined;
			}
		};
		Entry column2 = new Entry() {
			@Override
			public void setup() {
				allowedValueType = AllowedValueType.Discrete;
				allowedValues.add("Hello");
				allowedValues.add("World");
				defaultValue = allowedValues.get(0);
			}
		};
		Entry column3 = new Entry() {
			@Override
			public void setup() {
				allowedValueType = AllowedValueType.Continuous;
				allowedValues.add("0.0");
				allowedValues.add("2.0");
				defaultValue = allowedValues.get(0);
			}
		};

		column1.setName("Column 1 - Undefined");
		column2.setName("Column 2 - Discrete");
		column3.setName("Column 3 - Continues");

		column1.setDescription("desc 1");
		column2.setDescription("desc 2");
		column3.setDescription("desc 3");

		ArrayList<Entry> template1 = new ArrayList<Entry>();
		template1.add(column1);
		template1.add(column2);
		template1.add(column3);

		table.setRowTemplate(template1);

		table.addRow();
		table.addRow();
		table.addRow();

		table.setName("TableComponent");
		table.setDescription("Table Component description");
		table.setId(1);
		form.addComponent(table);

		// Setup and add a data component
		DataComponent component = new DataComponent();
		component.setDescription("This will describe the DataComponent.");
		component.setId(20120516);
		component.setName("DataComponent");
		component.addEntry(entry4);
		form.addComponent(component);

		// ===========================================================================Try
		// making geometry page
		GeometryComponent geometryComponent = new GeometryComponent();
		geometryComponent.setId(108);
		geometryComponent.setName("ICE Geometry Editor");
		geometryComponent.setDescription("Create or edit a geometry in 3D.");
		// geometryComponent.addShape(shape);
		// geometryComponent.setShapes(shapes);
		form.addComponent(geometryComponent);

		// ===== Resource Component Page ===== //
		resourceComp.setName("Resource component");
		resourceComp.setId(109);
		resourceComp
				.setDescription("This component contains the files, resources "
						+ "and data available to this plug-in.");
		// Add files from the project space to the Resource Component
		if (project != null) {
			try {
				IResource[] resources = project.members();
				// Search the project members
				for (int i = 0; i < resources.length; i++)
					// Look for files
					if (resources[i].getType() == IResource.FILE) {
						IFile file = (IFile) resources[i];
						// Create resource
						ICEResource iceResource = new ICEResource(new File(
								file.getRawLocationURI()));
						iceResource.setId(i);
						iceResource.setName("File " + i);
						iceResource.setPath(file.getLocationURI());
						// Setup some entries for testing properties
						Entry entryClone = (Entry) entry1.clone();
						ArrayList<Entry> properties = new ArrayList<Entry>();
						properties.add(entryClone);
						properties.add(entry4);
						properties.add(entry3);
						properties.add(entry2);
						properties.add(entry1);
						// Give the clone a different name so that we can check
						// changes
						entryClone.setName("File " + i + " Property");
						// Add the properties
						iceResource.setProperties(properties);
						// Add it to the component
						resourceComp.addResource(iceResource);
					}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Add the component to the Form
		form.addComponent(resourceComp);

		// ===== TreeComposite =====//

		// Setup the parent
		parent = new TreeComposite();
		parent.setName("Parent Node");
		parent.setId(1);
		parent.setDescription("The parent");

		// Create the children
		child1 = new TreeComposite();
		child1.setName("Child Node");
		child1.setId(1);
		child1.setDescription("The first child");
		child2 = new TreeComposite();
		child2.setName("Child Node");
		child2.setId(2);
		child2.setDescription("The second child");
		child3 = new TreeComposite();
		child3.setName("Child Node");
		child3.setId(3);
		child3.setDescription("The second child");

		// Add the children to the parent
		parent.setNextChild(child1);
		parent.setNextChild(child2);

		// Add the third child to the second
		child2.setNextChild(child3);

		// Add data nodes to the children
		child1.addComponent(detailsComp1);
		child2.addComponent(detailsComp2);
		child2.addComponent(detailsComp3);
		child3.addComponent(mDetailsComp);

		// Add the TreeComposite to the Form
		form.addComponent(parent);

		// Find the ReactorEditor test file - get the bundle first
		Bundle bundle = Platform
				.getBundle("org.eclipse.ice.tablecomponenttester");
		String separator = System.getProperty("file.separator");
		Path testDataPath = new Path("data" + separator + "test_new.h5");
		URL testDataURL = FileLocator.find(bundle, testDataPath, null);
		LWRComponentReader reader = new LWRComponentReader();
		LWRComponent lwrComponent;
		try {
			URI dataFileURI = FileLocator.toFileURL(testDataURL).toURI();
			System.out.println(dataFileURI);
			lwrComponent = (LWRComponent) reader.read(dataFileURI);
			lwrComponent.setId(999);
			// Add the LWRComponent to the form
			form.addComponent(lwrComponent);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
}
