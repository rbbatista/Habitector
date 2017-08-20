package biodiversity.ie.habitats;

import java.util.ArrayList;
import java.util.List;

public class HabitatMention {
	private String mention;
	private String id;
	private int frequency;
	private List<HabitatMentionSource> mentionSources;
	private List<HabitatMention> relatedMentions;
	
	public HabitatMention(String mention) {
		this.mention = mention;
		this.mentionSources = new ArrayList<HabitatMentionSource>();
	}
	
	public String getMention() {
		return mention;
	}
	public void setMention(String mention) {
		this.mention = mention;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public List<HabitatMentionSource> getMentionSources() {
		return mentionSources;
	}
	public void setMentionSources(List<HabitatMentionSource> mentionSources) {
		this.mentionSources = mentionSources;
	}
	
	public void incrementFrequency() {
		this.frequency++;
	}
	
	public String toJsonString(){
		//System.out.println("MENTION:" + mention.replaceAll("'", "").replaceAll("\"", "").replaceAll("/", "\\/"));
		//System.out.println("SIZE:" + mentionSources.size());
		String result = "{" + "\"mention\":\"" + mention.replaceAll("'", "").replaceAll("\"", "")  + "\", \"frequency\":" + frequency + ",";
		result = result + "\"" + "sources" + "\": [";
		for (int i = 0; i < mentionSources.size();i++) {
			HabitatMentionSource source = mentionSources.get(i);
			result = result + source.toJsonString() + (i<mentionSources.size()-1?", " : "");
		}
		result = result + "]}";
		
		//System.out.println("RESULT:" + result);
		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<HabitatMention> getRelatedMentions() {
		return relatedMentions;
	}

	public void setRelatedMentions(List<HabitatMention> relatedMentions) {
		this.relatedMentions = relatedMentions;
	}
}
