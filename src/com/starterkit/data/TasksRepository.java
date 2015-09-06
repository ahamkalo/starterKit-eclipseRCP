package com.starterkit.data;

import java.util.ArrayList;

import org.eclipse.core.databinding.observable.list.WritableList;

public class TasksRepository {
	
	private static TasksRepository instance = null;
	
	private WritableList toDoTasks = new WritableList(new ArrayList<Task>(),
			Task.class);
	
	private WritableList archivedTasks = new WritableList(new ArrayList<Task>(),
			Task.class);

	private TasksRepository() {
		toDoTasks.add(new Task(1L, "task1", "description1", 2L));
		toDoTasks.add(new Task(2L, "task2", "description2", 4L));
		toDoTasks.add(new Task(3L, "task3", "description3", 3L));
		toDoTasks.add(new Task(4L, "task4", "description4", 1L));
		toDoTasks.add(new Task(5L, "task5", "description5", 5L));
		toDoTasks.add(new Task(6L, "task6", "description6", 4L));
	}

	public static TasksRepository getInstance() {
		if (instance == null) {
			instance = new TasksRepository();
		}
		return instance;
	}

	public WritableList getToDoTasks() {
		return toDoTasks;
	}
	
	public WritableList getArchivedTasks() {
		return archivedTasks;
	}
}
