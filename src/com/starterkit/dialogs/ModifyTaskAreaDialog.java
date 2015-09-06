package com.starterkit.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.starterkit.data.Task;

public class ModifyTaskAreaDialog extends TitleAreaDialog {
	private Text nameText;
	private Text descriptionText;
	private ComboViewer priorityComboViewer;
	Task task;

	ControlDecoration nameTextDecorator;
	ControlDecoration descriptionTextDecorator;

	public ModifyTaskAreaDialog(Shell parentShell, Task task) {
		super(parentShell);
		this.task = task;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add new task");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createNameTextField(container);
		createDescriptionTextField(container);

		createPriorityComboViewer(container);

		return area;
	}

	private void createNameTextField(Composite container) {
		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Name: ");

		GridData nameGridData = new GridData();
		nameGridData.grabExcessHorizontalSpace = true;
		nameGridData.horizontalAlignment = GridData.FILL;

		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(nameGridData);
		nameText.setText(task.getName());

		nameTextDecorator = createErrorTextDecorator(nameText);

		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (validateTextField(nameText)) {
					nameTextDecorator.hide();
					if (validateTextField(descriptionText)) {
						setMessage("Press OK button to add new task.",
								IMessageProvider.INFORMATION);
					}
				} else {
					nameTextDecorator.show();
				}
			}

		});
	}

	private void createDescriptionTextField(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Description: ");

		GridData descriptionGridData = new GridData();
		descriptionGridData.grabExcessHorizontalSpace = true;
		descriptionGridData.horizontalAlignment = GridData.FILL;

		descriptionText = new Text(container, SWT.BORDER);
		descriptionText.setLayoutData(descriptionGridData);
		descriptionText.setText(task.getDescription());

		descriptionTextDecorator = createErrorTextDecorator(descriptionText);

		descriptionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (validateTextField(descriptionText)) {
					descriptionTextDecorator.hide();
					if (validateTextField(nameText)) {
						setMessage("Press OK button to add new task.",
								IMessageProvider.INFORMATION);
					}
				} else {
					descriptionTextDecorator.show();
				}
			}

		});
	}

	private ControlDecoration createErrorTextDecorator(Text txtFirstName) {
		ControlDecoration controlDecorator = new ControlDecoration(
				txtFirstName, SWT.TOP | SWT.LEFT);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		Image image = fieldDecoration.getImage();
		controlDecorator.setImage(image);
		controlDecorator.setDescriptionText("This field should not be empty!");
		controlDecorator.hide();

		return controlDecorator;
	}

	private boolean validateTextField(Text txtFirstName) {
		if (txtFirstName.getText().isEmpty()) {
			setMessage("Both text fields are required!", IMessageProvider.ERROR);
			return false;
		} else {
			return true;
		}
	}
	
	private void createPriorityComboViewer(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Select priority: ");
		priorityComboViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo = priorityComboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));

		priorityComboViewer.setContentProvider(ArrayContentProvider
				.getInstance());
		priorityComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					String priority = (String) element;
					return priority;
				}
				return super.getText(element);
			}
		});

		String[] priorities = { "1", "2", "3", "4", "5" };
		priorityComboViewer.setInput(priorities);
		
		priorityComboViewer.setSelection(new StructuredSelection(task.getPriority().toString()));
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void modifyTask() {
		String name = nameText.getText().toString();
		String description = descriptionText.getText().toString();

		String priorityText = priorityComboViewer.getSelection().toString();
		Long priority = Long.parseLong(priorityText.substring(1, 2));

		task.setName(name);
		task.setDescription(description);
		task.setPriority(priority);
	}

	@Override
	protected void okPressed() {
		if (validateTextField(nameText) && validateTextField(descriptionText)) {
			modifyTask();
			super.okPressed();
		} else {
			setMessage("Both text fields are required!", IMessageProvider.ERROR);
			if(!validateTextField(nameText)){
				nameTextDecorator.show();
			}
			if(!validateTextField(descriptionText)){
				descriptionTextDecorator.show();
			}
		}
	}
}
