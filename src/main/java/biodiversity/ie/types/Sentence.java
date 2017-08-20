package biodiversity.ie.types;

import java.util.ArrayList;
import java.util.List;


public class Sentence {
	private int globalStartIndex;
	private int globalEndIndex;
	private String text;
	private String geniaInputText;
	private List<Token> tokens;
	private List<TextBoundAnnotation> annotations;
	
	public Sentence() {
		this.tokens = new ArrayList<Token>();
		this.annotations = new ArrayList<TextBoundAnnotation>();
		this.geniaInputText = "";
	}
	
	public int getGlobalStartIndex() {
		return globalStartIndex;
	}
	public void setGlobalStartIndex(int globalStartIndex) {
		this.globalStartIndex = globalStartIndex;
	}
	public int getGlobalEndIndex() {
		return globalEndIndex;
	}
	public void setGlobalEndIndex(int globalEndIndex) {
		this.globalEndIndex = globalEndIndex;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String toCompleteString () {
		String result = "";
		for (Token token : tokens) {
			result = result + token.toCompleteString() + "\n";
		}
		return result;
	}
	
	public String toString () {
		String result = "";
		for (Token token : tokens) {
			//result = result + token.toString() + "\n";
			result = result + token.getSurfaceForm() + " ";
		}
		return result;
	}
	
	public String getGeniaInputText() {
		return geniaInputText;
	}
	public void setGeniaInputText(String geniaInputSentence) {
		this.geniaInputText = geniaInputSentence;
	}
	public List<Token> getTokens() {
		return tokens;
	}
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(Token token) {
		this.tokens.add(token);
	}
	
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof Sentence) {
			Sentence s = (Sentence) o;
			if (this.getGlobalStartIndex()==s.getGlobalStartIndex() && this.globalEndIndex==s.getGlobalEndIndex() && this.text.equals(s.getText())) {
				result = true;
			}
		}
		return result;
	}

	public List<TextBoundAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<TextBoundAnnotation> annotations) {
		this.annotations = annotations;
	}
	
	public void addAnnotation(TextBoundAnnotation annotation) {
		this.annotations.add(annotation);
	}
	
}
