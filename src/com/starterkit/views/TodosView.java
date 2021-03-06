package com.starterkit.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import com.starterkit.comparator.MyViewerComparator;
import com.starterkit.data.Task;
import com.starterkit.data.TasksRepository;
import com.starterkit.dialogs.AddTaskAreaDialog;
import com.starterkit.dialogs.ModifyTaskAreaDialog;
import com.starterkit.handlers.SearchTextModifyListener;

public class TodosView extends ViewPart {
	private static TableViewer tableViewer;
	private TasksRepository model = TasksRepository.getInstance();
	private MyViewerComparator comparator;
	private List<String> proposals;
	private static WritableList displayedTasks;

	public TodosView() {
		proposals = new ArrayList<String>();
		displayedTasks = new WritableList(new ArrayList<Task>(), Task.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, false));

		createSearchPanel(parent);

		createAddTaskButton(parent);
		createModifyButton(parent);
		createAddToArchiveButton(parent);

		createTableViewer(parent);
	}

	private void createSearchPanel(Composite parent) {
		Label searchByNameLabel = new Label(parent, SWT.NONE);
		searchByNameLabel.setText("Search by name:");

		Text searchText = new Text(parent, SWT.BORDER);
		GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1);
		searchText.setLayoutData(textGridData);

		createSearchTextDecoration(searchText);
		SimpleContentProposalProvider proposalProvider = createSimpleContentProposalProvider();
		createContentProposalAdapter(searchText, proposalProvider);

		ModifyListener modifyListener = new SearchTextModifyListener(
				proposalProvider, model.getToDoTasks(), displayedTasks);

		searchText.addModifyListener(modifyListener);

		@SuppressWarnings("unused")
		Label emptyLabel = new Label(parent, SWT.NONE);
	}

	private void createContentProposalAdapter(Text searchText,
			SimpleContentProposalProvider proposalProvider) {
		String lcl = "abcdefghijklmnopqrstuvwxyz";
		String ucl = lcl.toUpperCase();
		String nums = "0123456789";
		String allChars = lcl + ucl + nums;
		char[] autoActivationCharacters = allChars.toCharArray();
		
		KeyStroke keyStroke = null;
		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		ContentProposalAdapter adapter = new ContentProposalAdapter(searchText,
				new TextContentAdapter(), proposalProvider, keyStroke,
				autoActivationCharacters);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	}

	private SimpleContentProposalProvider createSimpleContentProposalProvider() {
		SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(
				null);

		@SuppressWarnings("unchecked")
		Iterator<Task> taskIterator = model.getToDoTasks().iterator();
		while (taskIterator.hasNext()) {
			proposals.add(taskIterator.next().getName());
		}

		String[] wordProposals = proposals
				.toArray(new String[proposals.size()]);
		
		proposalProvider.setProposals(wordProposals);
		return proposalProvider;
	}

	private void createSearchTextDecoration(Text searchText) {
		final ControlDecoration searchTextDecoration = new ControlDecoration(
				searchText, SWT.TOP | SWT.LEFT);

		Image image = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage();

		searchTextDecoration
				.setDescriptionText("Use CTRL + SPACE to see proposals.");
		searchTextDecoration.setImage(image);
		searchTextDecoration.setShowOnlyOnFocus(true);
	}

	private void createAddToArchiveButton(Composite parent) {
		Button addToArchiveButton = new Button(parent, SWT.PUSH);

		GridData buttonGrid = new GridData();
		buttonGrid.widthHint = 120;
		buttonGrid.heightHint = 45;
		addToArchiveButton.setLayoutData(buttonGrid);

		addToArchiveButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				Iterator<Object> iterator = selection.iterator();
				while (iterator.hasNext()) {
					Object element = iterator.next();
					if (element instanceof Task) {
						Task selectedTask = (Task) element;
						if (selectedTask != null) {
							model.getToDoTasks().remove(selectedTask);
							model.getArchivedTasks().add(selectedTask);
							displayedTasks.remove(selectedTask);
							ArchiveView.updateDisplayedTasks();
						}
					}
				}
			}
		});
		addToArchiveButton.setText("Add to archive");
	}

	private void createAddTaskButton(Composite parent) {
		Button addTaskButton = new Button(parent, SWT.PUSH);

		GridData buttonGrid = new GridData();
		buttonGrid.widthHint = 120;
		buttonGrid.heightHint = 45;
		addTaskButton.setLayoutData(buttonGrid);

		addTaskButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddTaskAreaDialog dialog = new AddTaskAreaDialog(getSite()
						.getShell(), model.getToDoTasks(), model
						.getArchivedTasks().size());
				dialog.create();
				if (dialog.open() == Window.OK) {
					displayedTasks.add(dialog.getTask());
				} 
			}
		});
		addTaskButton.setText("Add new task");
	}

	private void createModifyButton(Composite parent) {
		Button modifyTaskButton = new Button(parent, SWT.PUSH);

		GridData buttonGrid = new GridData();
		buttonGrid.widthHint = 120;
		buttonGrid.heightHint = 45;
		modifyTaskButton.setLayoutData(buttonGrid);

		modifyTaskButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				Iterator<Object> iterator = selection.iterator();
				while (iterator.hasNext()) {
					Object element = iterator.next();
					if (element instanceof Task) {
						Task selectedTask = (Task) element;
						if (selectedTask != null) {
							TitleAreaDialog dialog = new ModifyTaskAreaDialog(
									getSite().getShell(), selectedTask);
							dialog.open();
							tableViewer.refresh();
						}
					}
				}
			}
		});
		modifyTaskButton.setText("Modify");
	}

	private void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tableViewer.getTable();

		GridData tableGridData = new GridData(SWT.LEFT, SWT.FILL, true, true,
				4, 1);
		tableGridData.heightHint = 430;
		tableGridData.widthHint = 480;
		table.setLayoutData(tableGridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		createColumns();

		tableViewer.setContentProvider(new ObservableListContentProvider());
		@SuppressWarnings("unchecked")
		Iterator<Task> taskIterator = model.getToDoTasks().iterator();
		while (taskIterator.hasNext()) {
			displayedTasks.add(taskIterator.next());
		}
		tableViewer.setInput(displayedTasks);
		comparator = new MyViewerComparator();
		tableViewer.setComparator(comparator);

		MenuManager menuManager = new MenuManager();

		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);

		getSite().registerContextMenu(menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);
	}
	
	public static void refreshTableViewer(){
		tableViewer.refresh();
	}
	
	public static void removeTaskFromDisplayedTasks(Task task){
		displayedTasks.remove(task);
	}

	private void createColumns() {
		String[] headerWords = { "Id", "Name", "Description", "Priority" };
		int[] widths = { 40, 120, 270, 70 };

		TableViewerColumn idColumn = createColumn(headerWords[0], widths[0], 0);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Task task = (Task) element;
				return task.getId().toString();
			}

		});

		TableViewerColumn nameColumn = createColumn(headerWords[1], widths[1],
				1);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Task task = (Task) element;
				return task.getName();
			}

		});

		TableViewerColumn descriptionColumn = createColumn(headerWords[2],
				widths[2], 2);
		descriptionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Task task = (Task) element;
				return task.getDescription();
			}
		});

		TableViewerColumn priorityColumn = createColumn(headerWords[3],
				widths[3], 3);
		priorityColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Task task = (Task) element;
				return task.getPriority().toString();
			}
		});
	}

	private TableViewerColumn createColumn(String headerWord, int width,
			int columnNumber) {
		TableViewerColumn column = new TableViewerColumn(tableViewer,
				SWT.CENTER);
		column.getColumn().setText(headerWord);
		column.getColumn().setWidth(width);
		TableColumn tableColumn = column.getColumn();
		tableColumn.addSelectionListener(getSelectionAdapter(tableColumn,
				columnNumber));

		return column;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		};
		return selectionAdapter;
	}

	@Override
	public void setFocus() {

	}
}
