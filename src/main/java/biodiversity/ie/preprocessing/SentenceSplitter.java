package biodiversity.ie.preprocessing;

import java.util.ArrayList;
import java.util.List;

import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import biodiversity.ie.types.Sentence;

public class SentenceSplitter {
	static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
	static final SentenceModel SENTENCE_MODEL  = new MedlineSentenceModel();
	//static final SentenceModel SENTENCE_MODEL  = new IndoEuropeanSentenceModel();
	
    private String documentText;
	
	public SentenceSplitter(String documentText) {
		this.documentText = documentText;
	}
	
	public static void main (String[] args) {
		SentenceSplitter splitter = new SentenceSplitter(" Native to deciduous forests in Mexico (Chiapas, Guerrero, Oaxaca), Guatemala (Chiquimula, Zacapa, Jalapa), and Honduras (Comayagua, La Paz); 0–1100 m.");
		List<Sentence> sentences = splitter.splitDocumentText();
		for (Sentence sentence : sentences) {
			System.out.println("SENTENCE:" + sentence.getText());
		}
	}
	
	public ArrayList<Sentence> splitDocumentText() {
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		ArrayList<String> tokenList = new ArrayList<String>();
		ArrayList<String> whiteList = new ArrayList<String>();
		
		Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(documentText.toCharArray(),0,documentText.length());
		tokenizer.tokenize(tokenList,whiteList);
		String[] tokens = new String[tokenList.size()];
		String[] whites = new String[whiteList.size()];
		tokenList.toArray(tokens);
		whiteList.toArray(whites);
		int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);			
		if (sentenceBoundaries.length < 1) {
		    //System.out.println("No sentence boundaries found.");
		    //System.out.println(documentText);
		    //return;
		}
		int sentStartTok = 0;
		int sentEndTok = 0;
		int index = 0;
		
		for (int k = 0; k < sentenceBoundaries.length; ++k) {
			String sentence = "";
			Sentence s = new Sentence();
			
		    sentEndTok = sentenceBoundaries[k];
		   
		    for (int j=sentStartTok; j<=sentEndTok; j++) {
		    	if (j<sentEndTok) {
		    		sentence = sentence + tokens[j]+whites[j+1];
		    	}
		    	else {
		    		sentence = sentence + tokens[j];
		    	}
		    	
		    }
		    s.setText(sentence);
		    s.setGlobalStartIndex(documentText.indexOf(sentence, index));
		    s.setGlobalEndIndex(s.getGlobalStartIndex()+sentence.length());
		    sentences.add(s);
		    index = s.getGlobalEndIndex();
		    sentStartTok = sentEndTok+1;
		    
		}
		return sentences;
	}
}
