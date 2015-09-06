package com.starterkit.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.starterkit.data.Task;

public class SearchTextModifyListener implements ModifyListener {
	SimpleContentProposalProvider proposalProvider;
	WritableList tasks;
	WritableList displayedTasks;
	List<String> proposals;

	public SearchTextModifyListener(
			SimpleContentProposalProvider proposalProvider, WritableList tasks,
			WritableList displayedTasks) {
		this.proposalProvider = proposalProvider;
		this.tasks = tasks;
		this.displayedTasks = displayedTasks;
		proposals = new ArrayList<String>();
	}

	@Override
	public void modifyText(ModifyEvent e) {
		Text text = (Text) e.getSource();
		if (!text.getText().isEmpty()) {
			updateDisplayedTasks(text);
			updateProposals(text);
		} else {
			setDisplayedAllTasks();
			setDefaultProposals();
		}

	}

	private void setDisplayedAllTasks() {
		@SuppressWarnings("unchecked")
		Iterator<Task> taskIterator = tasks.iterator();
		displayedTasks.clear();
		while (taskIterator.hasNext()) {
			displayedTasks.add(taskIterator.next());
		}
	}

	private void updateDisplayedTasks(Text text) {
		@SuppressWarnings("unchecked")
		Iterator<Task> taskIterator = tasks.iterator();
		displayedTasks.clear();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (task.getName().startsWith(text.getText())) {
				displayedTasks.add(task);
			}
		}

	}

	private void updateProposals(Text text) {
		@SuppressWarnings("unchecked")
		Iterator<Task> taskIterator = tasks.iterator();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (task.getName().startsWith(text.getText())) {
				proposals.add(task.getName());
			}
		}

		String[] wordProposals = proposals
				.toArray(new String[proposals.size()]);
		proposals.clear();
		proposalProvider.setProposals(wordProposals);
	}

	private void setDefaultProposals() {
		@SuppressWarnings("unchecked")
		Iterator<Task> taskIterator = tasks.iterator();
		while (taskIterator.hasNext()) {
			proposals.add(taskIterator.next().getName());
		}

		String[] wordProposals = proposals
				.toArray(new String[proposals.size()]);
		proposals.clear();
		proposalProvider.setProposals(wordProposals);
	}
}
