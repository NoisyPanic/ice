/*******************************************************************************
 * Copyright (c) 2015 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jordan Deyton - Initial API and implementation and/or initial documentation
 *   
 *******************************************************************************/
package org.eclipse.ice.viz.service.connections.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ice.client.common.properties.CellColumnLabelProvider;
import org.eclipse.ice.client.common.properties.ICellContentProvider;
import org.eclipse.ice.datastructures.ICEObject.IUpdateable;
import org.eclipse.ice.datastructures.ICEObject.IUpdateableListener;
import org.eclipse.ice.datastructures.form.Entry;
import org.eclipse.ice.datastructures.form.TableComponent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class provides a basic JFace {@link IStructuredContentProvider} for ICE
 * {@link TableComponent}s. It automatically registers for updates from the
 * input {@code TableComponent} and refreshes the associated JFace
 * {@link Viewer} when the {@code TableComponent} changes.
 * 
 * @author Jordan Deyton
 *
 */
public class TableComponentContentProvider implements
		IStructuredContentProvider, IUpdateableListener {

	/**
	 * The data model for the {@link #viewer}.
	 */
	private TableComponent tableComponent;

	/**
	 * The JFace {@code TableViewer} that shows the contents of the
	 * {@link #tableComponent}.
	 */
	private TableViewer viewer;

	/**
	 * A list to keep track of the current row template. If the row template
	 * changes, then the viewer's columns will need to be recreated.
	 */
	private List<Entry> rowTemplate;

	/**
	 * The current {@link TableViewerColumn}s inside the {@link #viewer}.
	 */
	private final List<TableViewerColumn> columns = new ArrayList<TableViewerColumn>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// Nothing to do yet.
	}

	/**
	 * For the root element (the {@link #tableComponent}), this method returns
	 * the rows in the table. For each row, the {@link Entry} instances in the
	 * row are returned.
	 * <p>
	 * All other input elements are ignored.
	 * </p>
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		Object[] elements;

		// Handle the TableComponent.
		if (inputElement == tableComponent) {
			List<Integer> ids = tableComponent.getRowIds();
			int size = ids.size();
			elements = new Object[size];
			for (int i = 0; i < size; i++) {
				elements[i] = tableComponent.getRow(i);
			}
		}
		// Return an empty array for anything else.
		else {
			elements = new Object[] {};
		}

		return elements;
	}

	/**
	 * This method expects a new {@link #tableComponent} as input. This content
	 * provider will register for updates from the {@code TableComponent} and
	 * update the {@link #viewer} when the data model changes.
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer != null && viewer instanceof TableViewer && newInput != null) {

			// Update the reference to the JFace Viewer.
			this.viewer = (TableViewer) viewer;
			// Unregister from the old input if necessary.
			if (tableComponent != null) {
				tableComponent.unregister(this);
			}
			// Set the new input and register for model updates so that the
			// viewer will be automatically updated later.
			if (newInput instanceof TableComponent) {
				tableComponent = (TableComponent) newInput;
				// TableComponents sent to this content provider should have a
				// template set!
				if (tableComponent.getRowTemplate() == null) {
					throw new NullPointerException(
							"TableComponentContentProvider error: "
									+ "Cannot render TableComponents with no row template.");
				}
				// Before registering, trigger an update to the viewer.
				// Normally, you would not update the viewer. However, we would
				// like the content provider to manage the structure of the
				// table (which is handled in the refresh method).
				if (!this.viewer.isBusy()) {
					refreshViewer();
				}
				tableComponent.register(this);
			}
		}

		return;
	}

	/**
	 * Refreshes the contents of the viewer.
	 * <p>
	 * <b>Note:</b> This method should be called on the UI thread!
	 * </p>
	 */
	private void refreshViewer() {
		// How the viewer updates will depend on whether the row template
		// changed.

		// Get the TableComponent's new row template and see if it changed.
		boolean columnsChanged = false;
		List<Entry> newRowTemplate = tableComponent.getRowTemplate();
		if (!newRowTemplate.equals(rowTemplate)) {
			columnsChanged = true;
			rowTemplate = newRowTemplate;
		}

		// If the columns changed, we need to rebuild the columns from scratch.
		if (columnsChanged) {
			// Remove all previous columns from the viewer.
			for (TableViewerColumn column : columns) {
				column.getColumn().dispose();
			}
			columns.clear();

			EntryCellContentProvider basicContentProvider = new EntryCellContentProvider();
			EntryCellEditingSupport basicEditingSupport = new EntryCellEditingSupport(
					viewer, basicContentProvider);

			// Add a new column for each Entry.
			for (int i = 0; i < rowTemplate.size(); i++) {
				Entry entry = rowTemplate.get(i);

				// Create the column for the TableViewer.
				TableViewerColumn column = new TableViewerColumn(viewer,
						SWT.LEFT);
				columns.add(column);

				// Customize the underlying Column widget.
				TableColumn columnWidget = column.getColumn();
				columnWidget.setText(entry.getName());
				columnWidget.setToolTipText(entry.getDescription());
				columnWidget.setResizable(true);
				// Since we are replacing all the columns, pack it here based on
				// the column header text. Note: auto packing based on the
				// table's data might not be reasonable if a cell has a long
				// text string.
				columnWidget.pack();

				// Add the ColumnLabelProvider and the EditingSupport.
				ICellContentProvider contentProvider = new TableComponentCellContentProvider(
						basicContentProvider, i);
				EditingSupport editingSupport = new TableComponentCellEditingSupport(
						viewer, basicEditingSupport, i);
				column.setLabelProvider(new CellColumnLabelProvider(
						contentProvider));
				column.setEditingSupport(editingSupport);
			}
		}

		// Refresh the viewer contents.
		viewer.refresh();

		return;
	}

	/**
	 * This listens for changes to the {@link #tableComponent} and updates the
	 * {@link #viewer} as necessary.
	 */
	@Override
	public void update(IUpdateable component) {
		// Update the viewer since the underlying data has changed

		if (component != null && component == tableComponent) {

			// We must use the UI Thread
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					refreshViewer();
				}
			});
		}

		return;
	}
}
