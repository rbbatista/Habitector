package biodiversity.ie.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import biodiversity.ie.types.PreprocessedDocument;
import biodiversity.ie.types.Sentence;
import biodiversity.ie.types.Token;

public class BIOCorpusLoader {
	public static final int MAX_OFFSET = 100000;
	public static final String BIO_EXTENSION = ".bio";
	
	private int ctr = 1;
	private String corpusPath;
	private List<PreprocessedDocument> corpus;
	
	public BIOCorpusLoader(String corpusPath) {
		this.corpusPath = corpusPath;
		this.corpus = new ArrayList<PreprocessedDocument>();
	}
	
	public static void main(String[] args) {
		BIOCorpusLoader loader = new BIOCorpusLoader(args[0]);
		loader.loadDocuments();
	}
	
	public void loadDocument(File filePath, boolean isDirectory) {
		try {
			Scanner scanner = new Scanner(new FileInputStream(filePath), "UTF-8");
			String[] pathTokens = filePath.getName().split(File.separatorChar+"");
			String basename = pathTokens[pathTokens.length-1].split("\\.")[0];
			System.out.println(filePath);
			String currentLine = "";
			int currentOffset = MAX_OFFSET;
			PreprocessedDocument currentDocument = null;
			Sentence currentSentence = null;
			StringBuffer buffer = new StringBuffer();
			
			while (scanner.hasNextLine()) {
				currentLine = scanner.nextLine();
				
				if (!currentLine.isEmpty()) {
					String[] tokens = currentLine.split("\t");
					int startOffset = Integer.parseInt(tokens[0]);
					int endOffset = Integer.parseInt(tokens[1]);
					
					String surfaceForm = tokens[2];
					String baseForm = tokens[3];
					String posTag = tokens[4];
					String chunkTag = tokens[5];
					String neTag = tokens[6];
					
					if (startOffset < currentOffset && currentDocument!=null) {
						currentDocument.setDocumentText(buffer.toString());
						currentDocument.setFilename(isDirectory?basename:"Doc" + ctr++);
						corpus.add(currentDocument);
						currentDocument = new PreprocessedDocument("");
						currentSentence = new Sentence();
						buffer = new StringBuffer();
						
					}
					else if (startOffset < currentOffset) {
						currentDocument = new PreprocessedDocument("");
						currentSentence = new Sentence();
						buffer = new StringBuffer();
					}
					
					//pad
					int bufferLength = 0;
					while ((bufferLength = buffer.length()) < startOffset) {
						buffer.append(" ");
					}
					
					Token token = new Token();
					token.setBegin(startOffset);
					token.setEnd(endOffset);
					token.setSurfaceForm(surfaceForm);
					token.setBaseForm(baseForm);
					token.setPosTag(posTag);
					token.setChunkTag(chunkTag);
					token.setResponseLabel(neTag);
					currentSentence.addToken(token);
					
					buffer.append(surfaceForm);
					
					currentOffset = endOffset;
				}
				else {
					currentDocument.addSentence(currentSentence);
					currentSentence = new Sentence();
				}
			}
			
			if (currentDocument!=null) {
				currentDocument.setDocumentText(buffer.toString());
				
				currentDocument.setFilename(isDirectory?basename:"Doc" + ctr++);
				corpus.add(currentDocument);
			}
			scanner.close();
		}
		catch (IOException io) {
			System.out.println("Failed to load BIO-formatted corpus.");
			return;
		}
		
		/*//checking
		for (PreprocessedDocument currentDocument : corpus) {
			System.out.println(currentDocument.getDocumentText());
			for (Sentence sentence : currentDocument.getSentences()) {
				for (Token token : sentence.getTokens()) {
					System.out.println(token.toCompleteString());
				}
			}
		}*/
	}
	
	public void loadDocuments () {
		File corpusFile = new File(corpusPath);
		if (corpusFile.isDirectory()) {
			File[] files = corpusFile.listFiles();
			for (File file : files) {
				loadDocument(file, corpusFile.isDirectory());
			}
		}
		else {
			loadDocument(corpusFile, corpusFile.isDirectory());
		}
	}
	
	public List<PreprocessedDocument> getCorpus() {
		return this.corpus;
	}
}
