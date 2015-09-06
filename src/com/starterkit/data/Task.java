package com.starterkit.data;


public class Task extends ModelObject{
	private Long id;
	private String name;
	private String description;
	private Long priority;
	
	public Task(Long id, String name, String description, Long priority) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priority = priority;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		firePropertyChange("id", this.id, this.id = id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		firePropertyChange("name", this.name, this.name = name);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		firePropertyChange("description", this.description, this.description = description);
	}
	
	public void setDescription2(String description) {
		this.description = description;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		firePropertyChange("priority", this.priority, this.priority = priority);
	}
}
