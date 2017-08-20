package biodiversity.ie.types;

import java.util.ArrayList;
import java.util.List;

public class Document {
	private String filename;
	private String text;
	private List<TextBoundAnnotation> textBoundAnnotations;
	
	public Document() {
		this.textBoundAnnotations = new ArrayList<TextBoundAnnotation>();
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<TextBoundAnnotation> getTextBoundAnnotations() {
		return textBoundAnnotations;
	}
	public void setTextBoundAnnotations(List<TextBoundAnnotation> textBoundAnnotations) {
		this.textBoundAnnotations = textBoundAnnotations;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
}
