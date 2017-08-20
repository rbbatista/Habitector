package biodiversity.ie.types;

import java.util.ArrayList;
import java.util.List;


public class PreprocessedDocument {
	private String filename;
	private String documentText;
	private List<Sentence> sentences;
	
	public PreprocessedDocument(String documentText) {
		this.documentText = documentText;
		this.sentences = new ArrayList<Sentence>();
	}
	
	public String getDocumentText() {
		return documentText;
	}
	public void setDocumentText(String documentText) {
		this.documentText = documentText;
	}
	public List<Sentence> getSentences() {
		return sentences;
	}
	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}

	public void addSentence(Sentence sentence) {
		this.sentences.add(sentence);
	}
	
	public String toString () {
		String result = "";
		for (Sentence sentence : sentences) {
			result = result + sentence.toCompleteString() + "\n";
		}
		return result;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
