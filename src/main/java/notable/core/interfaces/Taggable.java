package notable.core.interfaces;

import java.util.Set;

public interface Taggable {
	
	Set<String> getTags();
	
	void addTag(String tag);
	
	void removeTag(String tag);
	
	void setTags(Set<String> tags);
	
	boolean isTagged();
	
	void setTagged(boolean value);
	
}
