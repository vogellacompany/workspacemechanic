/*******************************************************************************
 * Copyright (C) 2014, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.samples;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.eclipse.mechanic.CompositeTask;
import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.IResourceTaskReference;
import com.google.eclipse.mechanic.ListCollector;
import com.google.eclipse.mechanic.ResourceTaskScanner;
import com.google.eclipse.mechanic.TaskCollector;

/**
 * Processes files with the extension '.showview'. Each file contains
 * view ids which will be opened when run.
 *
 * When the task is executed, the view will either appear, or hide.
 */
public class ShowViewScanner extends ResourceTaskScanner {
  private static final Logger LOG = Logger.getLogger(ShowViewScanner.class.getName());

  @Override
  protected void scan(IResourceTaskProvider source, TaskCollector collector) {
    /**
     * Scan our source. Add a new Task for each EPF found.
     */
    ListCollector<IResourceTaskReference> taskCollector = ListCollector.create();
    source.collectTaskReferences(".showview", taskCollector);
    for (IResourceTaskReference taskRef : taskCollector.get()) {
      processTaskRef(collector, taskRef);
    }
  }

  public void processTaskRef(
      TaskCollector collector,
      IResourceTaskReference taskRef) {
    try {
      String inputText;
      try (InputStream inputStream = taskRef.newInputStream()) {
        inputText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      }
      // This example doesn't deal with Windows newlines.
      List<String> list = new ArrayList<>();
      for (String line : inputText.split("\n")) {
        line = line.trim();
        if (line.startsWith("#") || line.length() == 0) {
          continue;
        }
        list.add(line);
      }
      collector.collect(new ShowViewTask(taskRef.getName(), list));
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Can't process task " + taskRef, e);
    } catch (RuntimeException e) {
      LOG.log(Level.SEVERE, "Can't process task " + taskRef, e);
    }
  }

  public class ShowViewTask extends CompositeTask {

    private final String name;
    private final List<String> list;

    public ShowViewTask(String name, List<String> list) {
      this.name = name;
      this.list = List.copyOf(list);
    }

    public String getId() {
      return ShowViewTask.class.getName() + ":" + name;
    }

    public String getTitle() {
      return "Show views: " + name;
    }

    public String getDescription() {
      return "Enables and disables views. More details.";
    }

    public boolean evaluate() {
      if (!PlatformUI.isWorkbenchRunning()) {
        return true;
      }
      final IWorkbench workbench = PlatformUI.getWorkbench();
      if (workbench == null) {
        return true;
      }

      final Set<String> remaining = new HashSet<>(list);
      Display.getDefault().syncExec(new Runnable() {
        public void run() {
          for (IWorkbenchWindow workbenchWindow : workbench.getWorkbenchWindows()) {
            for (IWorkbenchPage workbenchPage : workbenchWindow.getPages()) {
              for (IViewReference viewReference : workbenchPage.getViewReferences()) {
                remaining.remove(viewReference.getId());
              }
            }
          }
        }
      });
      return remaining.isEmpty();
    }

    public void run() {
      Display.getDefault().syncExec(new Runnable() {
        public void run() {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
          IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
          for (String viewId : list) {
            try {
              activePage.showView(viewId);
            } catch (PartInitException e) {
              LOG.log(Level.SEVERE, "Can't open view " + viewId, e);
            }
          }
        }
      });
    }
  }
}
