package biodiversity.ie.preprocessing;

import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jmcejuela.bio.jenia.JeniaTagger;

import biodiversity.ie.types.PreprocessedDocument;
import biodiversity.ie.types.Sentence;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;


public class TextPreprocessor {
	private String text;
	private PreprocessedDocument preprocessedText;
	private String geniaModelsPath;

	public TextPreprocessor(String text, String geniaModelsPath) {
		this.text = text;
		this.preprocessedText = new PreprocessedDocument(text);
		this.geniaModelsPath = geniaModelsPath;
	}

	public static void main(String[] args) {
		TextPreprocessor preprocessor = new TextPreprocessor("The common dipterocarp species present in this formation include Shorea negrosensis (red lauan), Shorea astylosa (yakal), Shorea almon (almon), Shorea falciferoides ssp. falciferoides (yakal-yamban), Shorea guiso (guijo), Shorea palosapis (mayapis), Shorea polysperma (tanguile), Hopea malibato (yakal-kaliot), Hopea philippinensis (guisok-guisok), Parashorea malaanonan (bagtikan), Dipterocarpus validus (hagakhak), and Vatica mangachapoi ssp. mangachapoi (narig).", "/home/riza/Dropbox (The University of Manchester)/DellWorkspace/Habitector/src/main/resources/models");
		PreprocessedDocument result = preprocessor.preprocess();
		System.out.println(result.toString());
	}

	public PreprocessedDocument preprocess() {
		splitSentences();
		tokenise();
		posAndChunkTag();
		return preprocessedText;
	}

	public void splitSentences() {
		SentenceSplitter splitter = new SentenceSplitter(text);
		preprocessedText.setSentences(splitter.splitDocumentText());
	}	

	/*public void tokenise() {
		for (Sentence sentence : preprocessedText.getSentences()) {
			String geniaInputText = "";
			TokenSequence tokenSequence = tokeniser.tokenise(sentence.getText());
			for (uk.ac.cam.ch.wwmm.oscar.document.Token chemToken : tokenSequence.getTokens()) {
				geniaInputText = geniaInputText + chemToken.getSurface() + " ";
				biodiversity.ie.types.Token token = new biodiversity.ie.types.Token();
				token.setBegin(chemToken.getStart());
				token.setEnd(chemToken.getEnd());
				token.setSurfaceForm(chemToken.getSurface());
				sentence.addToken(token);
				//System.out.println("Added " + token.getSurfaceForm());
			}
			sentence.setGeniaInputText(geniaInputText);
		}
	}*/

	public void tokenise() {
		for (Sentence sentence : preprocessedText.getSentences()) {
			String geniaInputText = "";
			PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(sentence.getText()), new CoreLabelTokenFactory(), "normalizeSpace=true,normalizeFractions=false,unicodeEllipsis=true,splitHyphenated=true,ptb3Escaping=false,untokenizable=noneKeep");

			while (ptbt.hasNext()) {
				CoreLabel label = ptbt.next();
				boolean isProblematic = isProblematicColon(label.originalText());
				if (!isProblematic) {
					biodiversity.ie.types.Token token = new biodiversity.ie.types.Token();
					token.setBegin(label.beginPosition());
					token.setEnd(label.endPosition());
					token.setSurfaceForm(label.originalText());
					sentence.addToken(token);
					geniaInputText = geniaInputText + token.getSurfaceForm() + " ";
					//System.out.println(label.beginPosition() + "\t" + label.endPosition() + "\t" + label.originalText());
				}
				else {
					StringTokenizer tokenizer = new StringTokenizer(label.originalText(), ":", true);
					int adjustedBegin = label.beginPosition();
					
					while (tokenizer.hasMoreTokens()) {
						String nextToken = tokenizer.nextToken();
						int adjustedEnd = adjustedBegin + nextToken.length();
						biodiversity.ie.types.Token token = new biodiversity.ie.types.Token();
						token.setBegin(adjustedBegin);
						token.setEnd(adjustedEnd);
						token.setSurfaceForm(nextToken);
						sentence.addToken(token);
						geniaInputText = geniaInputText + token.getSurfaceForm() + " ";
						//System.out.println(adjustedBegin + "\t" + adjustedEnd + "\t" + nextToken);
						adjustedBegin = adjustedEnd;
					}
				}
				
			}
			
			sentence.setGeniaInputText(geniaInputText);
		}
	}

	public void posAndChunkTag() {
		JeniaTagger.setModelsPath(this.geniaModelsPath);
		for (Sentence sentence : preprocessedText.getSentences()) {
			//System.out.println(sentence.getGeniaInputText());
			com.jmcejuela.bio.jenia.common.Sentence geniaSentence = JeniaTagger.analyzeAll(sentence.getGeniaInputText(), true);
			if (geniaSentence.size()!=sentence.getTokens().size()) {
				continue;
			}

			for (int i =0;i<geniaSentence.size();i++) {
				com.jmcejuela.bio.jenia.common.Token geniaToken = geniaSentence.get(i);

				biodiversity.ie.types.Token token = sentence.getTokens().get(i);
				token.setSurfaceForm(geniaToken.text);
				token.setBaseForm(geniaToken.baseForm);
				token.setBegin(sentence.getGlobalStartIndex() + token.getBegin());
				token.setEnd(sentence.getGlobalStartIndex() + token.getEnd());
				token.setPosTag(geniaToken.pos);
				token.setChunkTag(geniaToken.chunk);

			}

		}

	}
	
	public static boolean isProblematicColon(String tokenText) {
		boolean isProblematic = false;
		String regex =   ":[0-9]+";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(tokenText);
		if (matcher.find()) {
			isProblematic = true;
		}
		return isProblematic;
	}
}
