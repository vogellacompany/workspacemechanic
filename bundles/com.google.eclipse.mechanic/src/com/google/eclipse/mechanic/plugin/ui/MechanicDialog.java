/*******************************************************************************
 * Copyright (C) 2007, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.plugin.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.google.eclipse.mechanic.RepairDecisionProvider.Decision;
import com.google.eclipse.mechanic.Task;

/**
 * Dialog box that displays a list of available Tasks. Users use this
 * dialog box to select Tasks to execute.
 *
 * @author smckay@google.com (Steve McKay)
 */
public class MechanicDialog extends TitleAreaDialog {

  // action choice display names
  private static final String YES = "Fix Now";
  private static final String NO = "Fix Later";
  private static final String NEVER = "Never Fix"; 

  private final List<Task> items;
  private final Map<Task, Decision> userTaskChoices;

  public MechanicDialog(Shell parent, List<Task> items) {
    super(parent);
    this.items = items;
    this.userTaskChoices = new HashMap<Task, Decision>(items.size());

    // sets the default value for each choice the user must make
    for (Task item : items) {
      userTaskChoices.put(item, Decision.YES);
    }

    // make the dialog resizeable
    setShellStyle(getShellStyle() | SWT.RESIZE);
  }

  /**
   * @return true if user clicked OK button.
   */
  public boolean isOkay() {
    return Window.OK == getReturnCode();
  }

  /**
   * Returns map of {@link Task} to the user's choice for that Task.
   */
  public Map<Task, Decision> getUserChoices() {
    return Collections.unmodifiableMap(userTaskChoices);
  }

  @Override 
  public void create() {
    super.create();
    setTitle("Workspace Mechanic");
    setMessage("Changes suggested for your Eclipse workspace:");
  }

  /**
   * Here we fill the center area of the dialog
   */
  @Override 
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    createForm(area);
    return area;
  }

  /**
   * Initilize the buttons on the bottom dialog
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID,
        IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID,
        IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected Point getInitialSize() {
    return new Point(650, 500);
  }

  /**
   * Add a form to the supplied Composite.
   */
  private void createForm(Composite parent) {

    // Without a FormToolkit the theme styles the form like the rest of the dialog.
    ScrolledForm form = new ScrolledForm(parent, SWT.V_SCROLL);
    form.setExpandHorizontal(true);
    form.setExpandVertical(true);

    // Size hints make the form scroll and wrap inside the dialog instead of asking for its full content size.
    GridData formData = new GridData(GridData.FILL_BOTH);
    formData.widthHint = 600;
    formData.heightHint = 350;
    form.setLayoutData(formData);

    TableWrapLayout layout = new TableWrapLayout();
    layout.numColumns = 2;
    layout.horizontalSpacing = 15;
    layout.verticalSpacing = 4;
    layout.leftMargin = 10;
    layout.rightMargin = 10;

    form.getBody().setLayout(layout);
    form.getBody().setLayoutData(new TableWrapData(
        TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 3));

    for (Task item : items) {
      if (item != items.get(0)) {
        Label spacer = new Label(form.getBody(), SWT.NONE);
        TableWrapData spacerData = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP, 1, 2);
        spacerData.heightHint = 8;
        spacer.setLayoutData(spacerData);
      }

      Label title = new Label(form.getBody(), SWT.NONE);
      title.setText(item.getTitle());
      title.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
      title.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

      Combo combo = createDecisionCombo(form.getBody(), item);
      combo.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE, 2, 1));

      Label description = new Label(form.getBody(), SWT.WRAP);
      description.setText(item.getDescription());
      description.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    }
  }

  /**
   * Creates a new combo box, initilizes the enty values, and configures
   * it with a listener capable of updating the right entry in our
   * map of item->decision.
   */
  private Combo createDecisionCombo(Composite parent, Task item) {
    Combo combo = new Combo(parent, SWT.READ_ONLY);
    combo.add(YES);
    combo.add(NO);
    combo.add(NEVER);
    combo.select(0);

    combo.addSelectionListener(new ComboListener(item));
    return combo;
  }

  /**
   * Listens to combo box "selection" events. Updates the entry in the
   * outer classes map of item->decision.
   */
  private class ComboListener extends SelectionAdapter {
    
    private final Task item;

    public ComboListener(Task item) {
      this.item = item;
    }

    @Override 
    public void widgetSelected(SelectionEvent e) {
      Combo combo = (Combo) e.getSource();
      int index = combo.getSelectionIndex();
      Decision choice = Decision.valueOf(index);
      userTaskChoices.put(item, choice);
    }
  }
}
