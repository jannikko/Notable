package notable.core.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoteFactory {
	
	private static final Logger LOG = LogManager.getLogger(NoteFactory.class);
	
	public static Note createTagNote(){
		LOG.trace(">> createTagNote()");
		return new TagNote();
	}
}
