package com.starterkit.comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.starterkit.data.Task;

public class MyViewerComparator extends ViewerComparator {
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction;

	public MyViewerComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			direction = 1 - direction;
		} else {
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Task p1 = (Task) e1;
		Task p2 = (Task) e2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = p2.getId().compareTo(p1.getId());
			break;
		case 1:
			rc = p2.getName().compareTo(p1.getName());
			break;
		case 2:
			rc = p2.getDescription().compareTo(p1.getDescription());
			break;
		case 3:
			rc = p2.getPriority().compareTo(p1.getPriority());
			break;
		default:
			rc = 0;
		}
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

}