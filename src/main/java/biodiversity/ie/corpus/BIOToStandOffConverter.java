package biodiversity.ie.corpus;

import java.util.ArrayList;
import java.util.List;

import biodiversity.ie.types.PreprocessedDocument;
import biodiversity.ie.types.Sentence;
import biodiversity.ie.types.TextBoundAnnotation;
import biodiversity.ie.types.Token;

public class BIOToStandOffConverter {
	public static final int REFERENCE = 1;
	public static final int RESPONSE = 2;
	private int mode;
	
	public BIOToStandOffConverter(int mode) {
		this.mode = mode;
	}
	
	public List<TextBoundAnnotation> getAnnotations(PreprocessedDocument preprocessedDoc) {
		List<TextBoundAnnotation> annotations = new ArrayList<TextBoundAnnotation>();
		TextBoundAnnotation tbAnnotation = null;
		int start = 0, end = 0;
		int ctr = 1;
		for (Sentence sentence : preprocessedDoc.getSentences()) {
			for (Token token : sentence.getTokens()) {
				String bioLabel = "O";
				String entityType = "";
				if (mode==REFERENCE) {
					bioLabel = token.getReferenceLabel();
					entityType = token.getReferenceLabel().split("-").length>1?token.getReferenceLabel().split("-")[1]:"";
				}
				else if (mode==RESPONSE) {
					bioLabel = token.getResponseLabel();
					entityType = token.getResponseLabel().split("-").length>1?token.getResponseLabel().split("-")[1]:"";
				}
				if (bioLabel.startsWith("B-")) {
					start = token.getBegin();
					end = token.getEnd();
					
					if (tbAnnotation!=null && !annotations.contains(tbAnnotation)) {
						annotations.add(tbAnnotation);
					}
					
					tbAnnotation = new TextBoundAnnotation();
					tbAnnotation.setId("T" + ctr++);
					tbAnnotation.setBeginCharOffset(start);
					tbAnnotation.setEndCharOffset(end);
					tbAnnotation.setLabel(entityType);
					
					
				}
				else if (bioLabel.startsWith("I-")) {
					if (tbAnnotation==null) {
						start = token.getBegin();
						end = token.getEnd();
						
						tbAnnotation = new TextBoundAnnotation();
						tbAnnotation.setId("T" + ctr++);
						tbAnnotation.setBeginCharOffset(start);
						tbAnnotation.setEndCharOffset(end);
						tbAnnotation.setLabel(entityType);
					}
					else {
						end = token.getEnd();
						tbAnnotation.setEndCharOffset(end);
					}
					
				}
				else if (bioLabel.startsWith("O")) {
					if (tbAnnotation!=null && !annotations.contains(tbAnnotation)) {
						annotations.add(tbAnnotation);
					}
	
					tbAnnotation = null;
				}
			}
			if (tbAnnotation!=null && !annotations.contains(tbAnnotation)) {
				annotations.add(tbAnnotation);
				tbAnnotation = null;
			}
		}
		return annotations;
	}
	
}
