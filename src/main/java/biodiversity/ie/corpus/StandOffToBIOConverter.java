package biodiversity.ie.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biodiversity.ie.types.Sentence;
import biodiversity.ie.types.TextBoundAnnotation;
import biodiversity.ie.types.Token;


public class StandOffToBIOConverter {
	public static final int RESPONSE = 1;
	public static final int REFERENCE = 2;
	private int mode;

	public StandOffToBIOConverter(int mode) {
		this.mode = mode;
	}

	public void alignAnnotationsWithTokens(Sentence sentence) {
		List<TextBoundAnnotation> annotations = sentence.getAnnotations();
		List<Token> tokens = sentence.getTokens();
		for (int i=0;i<annotations.size();i++) {
			TextBoundAnnotation currentAnnotation = annotations.get(i);
			//System.out.println("CURRENT:" + currentAnnotation.toString());
			//find the token which contains this term
			int startIndexInTokens = findAnnotationByStartOffset(currentAnnotation, tokens);
			//System.out.println(startIndexInTokens);
			Token matchingToken = null;
			if (startIndexInTokens!=-1) {
				matchingToken = tokens.get(startIndexInTokens);

				if (matchingToken.getBegin()!=currentAnnotation.getBeginCharOffset()) {
					//System.out.println("case 0: " + currentAnnotation.getText() + " at "  + currentAnnotation.getBeginCharOffset());
					//split the token first and start again
					int currentBeginning = matchingToken.getBegin();
					splitToken(tokens, startIndexInTokens, currentBeginning, currentAnnotation.getText().split("\\s+")[0], "O");
					i--;
					continue;
				}

				if (currentAnnotation.getEndCharOffset()==matchingToken.getEnd()) {
					//System.out.println("case 1");
					//case 1: exact match
					//do nothing but set the semantic type
					if (mode==REFERENCE) {
						matchingToken.setReferenceLabel("B-" + currentAnnotation.getLabel());
					}
					else {
						matchingToken.setResponseLabel("B-" + currentAnnotation.getLabel());
					}


				}
				else if (currentAnnotation.getEndCharOffset()>matchingToken.getEnd()) {
					//System.out.println("case 2");
					//case 2: term spans several tokens
					//set the first token as B

					if (mode==REFERENCE) {				
						matchingToken.setReferenceLabel("B-" + currentAnnotation.getLabel());
					}
					else {
						matchingToken.setResponseLabel("B-" + currentAnnotation.getLabel());
					}

					int endIndexInTokens = findAnnotationByEndOffset(currentAnnotation, tokens);
					//for each token between 
					int currentBeginning = matchingToken.getBegin();

					int insertionPoint = -1;
					for (int x=startIndexInTokens+1;x<= endIndexInTokens;x++) {

						Token token = (Token) tokens.get(x);
						currentBeginning = token.getBegin();
						if (token.getEnd()<=currentAnnotation.getEndCharOffset()) {
							if (mode==REFERENCE) {
								token.setReferenceLabel("I-" + currentAnnotation.getLabel());
							}
							else {
								token.setResponseLabel("I-" + currentAnnotation.getLabel());
							}
						}
						else {
							//this token needs splitting before its semantic type can be set
							insertionPoint = x;
							break;
						}
					}
					if (insertionPoint!=-1) {
						String label = "I-" + currentAnnotation.getLabel();
						splitToken(tokens, insertionPoint, currentBeginning, currentAnnotation.getText(), label);
						i--;
						continue;
					}


				}
				else if (currentAnnotation.getEndCharOffset() < matchingToken.getEnd()) {
					//System.out.println("case 3");
					//case 3: term is shorter than the token
					//token needs splitting

					String label = "B-" + currentAnnotation.getLabel();
					splitToken(tokens, startIndexInTokens, currentAnnotation.getBeginCharOffset(), currentAnnotation.getText(), label);
					i--;
					continue;
				}
			}
		}



	}
	/*if (tokens.size()!=previousNoTokens) {
			//System.out.println("aligned!");
			for (Token token : tokens) {
				//System.out.println(token.toCompleteString());
			}
		}*/

	public void splitToken(List<Token> tokens, int index, int startIndexInToken, String annotationText, String label) {
		Token token = (Token) tokens.get(index);
		////System.out.println("BASE TOKEN:" + token.toCompleteString());
		////System.out.println("ct value:" + ct.getValue());		
		/*int y = term.getEnd() - startIndexInToken; // e.g., 1356 - 1349 = 7
		int z = term.getText().length() - y; // e.g., 24 - 7 = 17

		String remainingTextInTerm = term.getText().substring(z);
		String remainingTextInToken = ct.getValue().substring(remainingTextInTerm.length());

		ct.setEndOffset(term.getEnd());
		ct.setValue(remainingTextInTerm);
		ct.setType(label);*/

		List<Token> additionalTokens = new ArrayList<Token>();



		String regex =   "(" + Pattern.quote(annotationText) + "|-|'|`|,|\\(|\\)|\\*|\\.|\\[|\\])?";
		//String regex =   "(" + Pattern.quote(term.getText()) + "|'|,|\\(|\\)|\\*|\\.|\\[|\\])?";
		Pattern p = Pattern.compile(regex);
		//System.out.println(regex);
		Matcher matcher = p.matcher(token.getSurfaceForm());

		int startIndex = -1;
		int endIndex = -1;
		while (matcher.find() /*&& token.getSurfaceForm().length()>1*/) {
			//System.out.println("inside " + token.getSurfaceForm());
			if (matcher.group().length()>0) {
				////System.out.println(matcher.group().length());
				startIndex = matcher.start();
				endIndex = matcher.end();
				//System.out.println("Starting & ending index of " + matcher.group() + ":=" + "start=" + matcher.start() + " end = " + matcher.end());
				break;
				
			}
			
		}

		String text = token.getSurfaceForm();

		String newString = "";
		try {
			newString = text.substring(0, startIndex) + "\t" + text.substring(startIndex, endIndex) + "\t" + text.substring(endIndex, text.length());
			//System.out.println("new string:" + newString);
		}
		catch (StringIndexOutOfBoundsException se) {
			//System.out.println("ERRONEOUS:\t" + text + " vs " + annotationText);
			//System.exit(0);
		}
		String parts[] = newString.split("\t");
		int tempBegin = token.getBegin();
		for (int i=0;i<parts.length;i++) {
			if (!parts[i].equals("")) {
				//System.out.println("PART:" + parts[i]);
				Token additionalToken = new Token();
				String[] labelTokens = label.split("-");
				if (labelTokens.length==1) {
					if (mode==REFERENCE) {
						additionalToken.setReferenceLabel(labelTokens[0]);
					}
					else {
						additionalToken.setResponseLabel(labelTokens[0]);
					}
				}
				else {
					////System.out.println("chunkTagPrefix:" + chunkTagPrefix);
					////System.out.println("chunkTagSuffix:" + chunkTagSuffix);
					if (mode==REFERENCE) {
						if (label.startsWith("B")) {
							if (additionalTokens.size()==0) {
								additionalToken.setReferenceLabel("B-"+labelTokens[1]);
							}
							/*else {
								additionalToken.setReferenceLabel("I-"+labelTokens[1]);
							}*/
						}
						else {
							additionalToken.setReferenceLabel("I-"+labelTokens[1]);
						}
						
						//additionalToken.setReferenceLabel(label.startsWith("B")?(additionalTokens.size()==0?"B-"+labelTokens[1]:"I-"+labelTokens[1]):"I-"+labelTokens[1]);
					}
					else {
						if (label.startsWith("B")) {
							if (additionalTokens.size()==0) {
								additionalToken.setResponseLabel("B-"+labelTokens[1]);
							}
							/*else {
								additionalToken.setReferenceLabel("I-"+labelTokens[1]);
							}*/
						}
						else {
							additionalToken.setResponseLabel("I-"+labelTokens[1]);
						}
						//additionalToken.setResponseLabel(label.startsWith("B")?(additionalTokens.size()==0?"B-"+labelTokens[1]:"I-"+labelTokens[1]):"I-"+labelTokens[1]);
					}
				}

				additionalToken.setPosTag(token.getPosTag());
				if (token.getChunkTag().equals("O")) {
					additionalToken.setChunkTag(token.getChunkTag());
				}
				else {
					String chunkTagPrefix = token.getChunkTag().split("-")[0];
					String chunkTagSuffix = token.getChunkTag().split("-")[1];

					////System.out.println("chunkTagPrefix:" + chunkTagPrefix);
					////System.out.println("chunkTagSuffix:" + chunkTagSuffix);
					additionalToken.setChunkTag(chunkTagPrefix.startsWith("B")?(additionalTokens.size()==0?"B-"+chunkTagSuffix:"I-"+chunkTagSuffix):"I-"+chunkTagSuffix);
				}



				additionalToken.setSurfaceForm(parts[i]);
				additionalToken.setBaseForm(parts[i]);

				additionalToken.setBegin(tempBegin);
				additionalToken.setEnd(tempBegin+additionalToken.getSurfaceForm().length());
				tempBegin = tempBegin + additionalToken.getSurfaceForm().length();
				additionalTokens.add(additionalToken);
				//System.out.println("part:" + additionalToken.getSurfaceForm() + "(" + additionalToken.getBegin()+ "," + additionalToken.getEnd() + ")");
			}
		}
		//additionalTokens.get(0).setChunkTag("B-" + token.getChunkTag().split("-")[1]);
		/*while (strtok.hasNext()) {
				String tok = (String) strtok.next();
				if (!tok.equals("")) {
					MyChemToken additionalToken = new MyChemToken();
					additionalToken.setType(AnnotationConstants.O);
					additionalToken.setValue(tok);
					additionalToken.setStartOffset(tempBegin);
					additionalToken.setEndOffset(tempBegin+additionalToken.getValue().length());
					tempBegin = tempBegin + additionalToken.getValue().length();
					additionalTokens.add(additionalToken);
				}
			}*/
		tokens.remove(index);
		/*for (Token addlToken : additionalTokens) {
			//System.out.println("ADDL:" + addlToken.toCompleteString());
		}*/
		tokens.addAll(index, additionalTokens);

	}

	public int findAnnotationByStartOffset(TextBoundAnnotation annotation, List<Token> tokens) {
		int index = -1;
		int ctr = 0;

		for (Token token : tokens) {

			if (annotation.getBeginCharOffset() >= token.getBegin() && annotation.getBeginCharOffset() < token.getEnd()) {
				index = ctr;
				break;
			}
			ctr++;
		}
		return index;
	}

	public int findAnnotationByEndOffset(TextBoundAnnotation annotation, List<Token> tokens) {
		int index = -1;
		int ctr = 0;

		for (Token token : tokens) {

			if (annotation.getEndCharOffset() > token.getBegin() && annotation.getEndCharOffset() <= token.getEnd()) {
				index = ctr;
				break;
			}
			ctr++;
		}
		return index;
	}
}
