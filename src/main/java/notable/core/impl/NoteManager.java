package notable.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import notable.core.interfaces.iNote;

public class NoteManager {

	private static final Logger LOG = LogManager.getLogger(NoteManager.class);

	private List<iNote> notes;

	private static NoteManager instance;

	private NoteManager() {
	}

	public void addNote(iNote note) {
		LOG.trace(">> addNote()");
		if (notes == null) {
			notes = new ArrayList<iNote>();
		}
		notes.add(note);
		LOG.info("Note added!");
	}

	public void removeNote(iNote note) {
		LOG.trace(">> removeNote()");
		notes.remove(note);
		LOG.info("Note removed!");
	}

	public List<iNote> getNotes() {
		return notes;
	}

	public boolean isEmpty() {
		if (notes == null || notes.isEmpty()) {
			return true;
		} else {
			return false;
		}

	}

	public static NoteManager getInstance() {
		if (NoteManager.instance == null) {
			NoteManager.instance = new NoteManager();
		}

		return NoteManager.instance;
	}
	/* parallelStream() verlangsamt die Abfrage beim ersten Aufruf deutlich, da die Threads zuerst erstellt werden müssen */
	public List<iNote> searchNotes(String sequence) {
		return notes.stream()
				.filter(n -> n.getNote().toLowerCase().contains(sequence.toLowerCase()))
				.collect(Collectors.toList());
	}

}