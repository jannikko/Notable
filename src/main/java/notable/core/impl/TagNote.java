package notable.core.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import notable.core.interfaces.Taggable;

public class TagNote extends Note implements Taggable {

	private static final Logger LOG = LogManager.getLogger(TagNote.class);

	private String emptyTitle;

	private Set<String> tags;
	private boolean tagged;

	public TagNote() {

		LOG.trace("** TagNote()");
		emptyTitle = "Empty Note";
		title = emptyTitle;
		tagged = false;
		LOG.debug("Created: " + TagNote.class);
	}

	public void addTag(String tag) {
		LOG.trace(">> addTag()");
		if (tags == null) {
			tags = new LinkedHashSet<String>();
		}
		tags.add(tag);
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public Set<String> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		if (note == null || note.isEmpty()) {
			setTitle(emptyTitle);
		} else if (getNote().length() > 40) {
			String tempTitle = getNote().substring(0, 40);
			tempTitle = tempTitle.replaceAll("\n", " ");
			setTitle(tempTitle);
		} else {
			setTitle(note.replaceAll("\n", " "));
		}
		return getTitle();
	}
	
	@Override
	public String getNote() {
		if (note == null) {
			return "";
		} else {
			return note;
		}
	}

	@Override
	public void removeTag(String tag) {
		LOG.trace(">> removeTag()");
		tags.remove(tag);
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean value) {
		tagged = value;

	}

}
