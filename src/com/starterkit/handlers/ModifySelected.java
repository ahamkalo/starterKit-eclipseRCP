package com.starterkit.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.starterkit.data.Task;
import com.starterkit.dialogs.ModifyTaskAreaDialog;
import com.starterkit.views.TodosView;

public class ModifySelected extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			@SuppressWarnings("unchecked")
			Iterator<Object> iterator = strucSelection.iterator();
			while(iterator.hasNext()){
				Object element = iterator.next();
				if (element instanceof Task) {
					Task selectedTask = (Task) element;
					if (selectedTask != null) {
						TitleAreaDialog dialog = new ModifyTaskAreaDialog(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), selectedTask);
						dialog.open();
						TodosView.refreshTableViewer();
					}
				}
			}
		}
		return null;
	}
}
