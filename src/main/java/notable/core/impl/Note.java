package notable.core.impl;

import java.time.LocalDateTime;
import notable.core.interfaces.iNote;

public class Note implements iNote {

	protected String note;
	protected String title;
	protected LocalDateTime date;
	
	@Override
	public void setNote(String note) {
		this.note = note;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	public LocalDateTime getDate() {
		return date;
	}

	@Override
	public String getNote() {
		return note;
	}
	@Override
	public String getTitle() {
		return title;
	}

}
