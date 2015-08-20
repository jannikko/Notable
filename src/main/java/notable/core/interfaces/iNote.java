package notable.core.interfaces;

import java.time.LocalDateTime;

public interface iNote {

	void setNote(String note);

	String getNote();

	void setDate(LocalDateTime date);

	LocalDateTime getDate();

	void setTitle(String title);

	String getTitle();

}
