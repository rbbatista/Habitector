package biodiversity.ie.habitats;

public class HabitatMentionSource {
	private String filename;
	private String context;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
	public String toJsonString() {
		return ("{" + "\"sourceFile\":\"" + this.filename + "\", \"context\":\"" + context.trim().replaceAll("'", "").replaceAll("\"", "") + "\"}");
		//return ("{" + "\"sourceFile\":\"" + this.filename + "\"}");
	}
	
}
