package notable.core.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import notable.core.exceptions.NoTagsEnteredException;
import notable.core.interfaces.Taggable;
import notable.core.interfaces.iNote;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagManager {

	private static final Logger LOG = LogManager.getLogger(TagManager.class);

	private static TagManager instance;

	private Map<String, AtomicInteger> tags = new HashMap<String, AtomicInteger>();

	private TagManager() {
	}

	public Set<String> getTags() {
		return tags.keySet();
	}

	public Set<String> searchTags(String sequence) {
		LOG.trace(">> searchTags()");
		return tags.keySet().stream()
				.filter(s -> s.startsWith(sequence.toLowerCase()))
				.collect(Collectors.toSet());
	}

	public void addTagToNote(String tag, Taggable note) {

		LOG.trace(">> addTagToNote()");

		long nanos = System.nanoTime();
		if (note.isTagged()) {
			deleteTagsToNote(note);
		}
		
		tag = tag.trim();
		if (!tag.isEmpty() ) {
			Set<String> splittedTags = new LinkedHashSet<String>(
					Arrays.asList(tag.split("[ ,]+")));
			(note).setTags(splittedTags);
			(note).setTagged(true);
			splittedTags.forEach(x -> {
				if (tags.containsKey(x)) {
					tags.get(x).incrementAndGet();
				} else {
					tags.put(x, new AtomicInteger(1));
				}
			});
			LOG.info("Tags added!");
			LOG.debug("It took " + ((System.nanoTime() - nanos)/1000) + "μs to add the tags");
		} else {
			note.setTagged(false);
			/* notagsenteredexception */
			throw new NoTagsEnteredException("-- addTagToNote() > NoTagsEnteredException: tag entered was empty or null");
		}
	}

	public void deleteTagsToNote(Taggable note) {

		LOG.trace(">> deleteTagsToNote()");
		
		long nanos = System.nanoTime();
		if ((note).isTagged()) {
			note.getTags().forEach(s -> {
				if (tags.get(s).decrementAndGet() == 0) {
					tags.remove(s);
				}
			});
			note.setTagged(false);
		}
		LOG.debug("It took " + ((System.nanoTime() - nanos)/1000) + "μs to delete the tags");
	}

	public String getFormattedTags(Taggable note) {
		LOG.trace(">> getFormattedTags()");
		if (note.isTagged()) {
			return note.getTags().stream().reduce(new String(), (s1, s2) -> {
				if (s1.isEmpty()) {
					return s2;
				} else {
					return s1 + ", " + s2;
				}
			});
		} else {
			return "";
		}
	}

	public Set<iNote> getNotesToTag(String tag) {
		LOG.trace(">> getNotesToTag()");
		return NoteManager
				.getInstance()
				.getNotes()
				.parallelStream()						//ohne parallel läuft es schneller					
				.filter(n -> n instanceof Taggable)
				.map((c -> (Taggable) c))
				.filter(n -> n.getTags() != null)
				.filter(n -> n.getTags().contains(tag.toLowerCase())
						|| n.getTags().contains(tag)).map((c -> (iNote) c))
				.collect(Collectors.toSet());
	}

	public static TagManager getInstance() {
		if (TagManager.instance == null) {
			TagManager.instance = new TagManager();
		}
		return TagManager.instance;
	}

}
