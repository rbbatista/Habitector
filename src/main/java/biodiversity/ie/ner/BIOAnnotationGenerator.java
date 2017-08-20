package biodiversity.ie.ner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.util.List;

import biodiversity.ie.corpus.BIOCorpusLoader;
import biodiversity.ie.corpus.BioNLPSTCorpusLoader;
import biodiversity.ie.corpus.StandOffToBIOConverter;
import biodiversity.ie.preprocessing.TextPreprocessor;
import biodiversity.ie.types.Document;
import biodiversity.ie.types.PreprocessedDocument;
import biodiversity.ie.types.Sentence;
import biodiversity.ie.types.TextBoundAnnotation;
import biodiversity.ie.types.Token;

public class BIOAnnotationGenerator {
	public static final String LABELS_EXTENSION = ".labels";
	
	private boolean includeGoldLabels;
	private boolean writeSeparateFiles;
	private String geniaModelsPath;
	
	public BIOAnnotationGenerator(boolean includeGoldLabels, boolean writeSeparateFiles, String geniaModelsPath) throws IOException {
		this.includeGoldLabels = includeGoldLabels;
		this.writeSeparateFiles = writeSeparateFiles;
		this.geniaModelsPath = geniaModelsPath;
	}

	public static void main(String[] args) throws IOException {
		BIOAnnotationGenerator generator = new BIOAnnotationGenerator(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1]), "/home/riza/Dropbox (The University of Manchester)/DellWorkspace/Habitector/src/main/resources/models");
		generator.getCorpus(args[2], args[3]);
	}

	public void getCorpus(String corpusPath, String outputFilePath) throws IOException {
		Writer labelWriter = null;
		Writer bioWriter = null;
		File outputFile = new File(outputFilePath);
		
		
		if (!writeSeparateFiles) {
			if (outputFile.isDirectory()) {
				System.out.println("outputFilePath points to a directory that already exists (not allowed if writeSeparateFiles=false).");
				System.exit(1);
			}
			bioWriter = new OutputStreamWriter(new FileOutputStream(new File(outputFilePath)), "UTF-8");
			
			if (includeGoldLabels) {
				labelWriter = new OutputStreamWriter(new FileOutputStream(new File(outputFilePath + LABELS_EXTENSION)), "UTF-8");
			}
		}
		
		BioNLPSTCorpusLoader loader = new BioNLPSTCorpusLoader(corpusPath);
		loader.loadDocuments();
		List<Document> corpus = loader.getCorpus();
		
		for(Document doc : corpus) {
			generateInstances(doc, bioWriter, labelWriter, outputFilePath);
		}
		
		if (!writeSeparateFiles) {
			bioWriter.close();
			
			if (includeGoldLabels) {
				labelWriter.close();
			}
		}
	}

	public static Sentence getSentenceWithAnnotation(PreprocessedDocument preprocessedText, TextBoundAnnotation annotation) {
		Sentence matchingSentence = null;
		for (Sentence sentence : preprocessedText.getSentences()) {
			if (annotation.getBeginCharOffset()>=sentence.getGlobalStartIndex() && annotation.getEndCharOffset()<=sentence.getGlobalEndIndex()) {
				matchingSentence = sentence;
				break;
			}
		}
		return matchingSentence;
	}
	
	public void generateInstances(Document document, Writer bioWriter, Writer labelWriter, String outputFilePath) throws IOException {
		if (writeSeparateFiles) {
			File outputFile = new File(outputFilePath);
			if (!outputFile.isDirectory()) {
				System.out.println("outputFilePath should point to a directory if writeSeparateFiles=true");
				System.exit(1);
			}
			bioWriter = new OutputStreamWriter(new FileOutputStream(new File(outputFile, document.getFilename() + BIOCorpusLoader.BIO_EXTENSION)), "UTF-8");
			
			if (includeGoldLabels) {
				labelWriter = new OutputStreamWriter(new FileOutputStream(new File(outputFile, document.getFilename() + LABELS_EXTENSION)), "UTF-8");
			}
		}
		TextPreprocessor preprocessor = new TextPreprocessor(document.getText(), this.geniaModelsPath);
		PreprocessedDocument preprocessedText = preprocessor.preprocess();
		
		if (includeGoldLabels) {
			//map annotations to tokens
			for (TextBoundAnnotation annotation : document.getTextBoundAnnotations()) {
				Sentence containingSentence = getSentenceWithAnnotation(preprocessedText, annotation);
				if (containingSentence!=null) {
					containingSentence.addAnnotation(annotation);
				}

			}
		}
		

		StandOffToBIOConverter converter = new StandOffToBIOConverter(StandOffToBIOConverter.REFERENCE);
		
		for (Sentence sentence : preprocessedText.getSentences()) {
			//System.out.println(sentence.getText());
			if (!sentence.getText().startsWith("—")) {
				if (includeGoldLabels) {
					converter.alignAnnotationsWithTokens(sentence);
				}
				
				for (int k=0;k<sentence.getTokens().size();k++) {
					Token token = sentence.getTokens().get(k);
					if (includeGoldLabels) {
						labelWriter.write((includeGoldLabels && token.getReferenceLabel()!=null?token.getReferenceLabel():"") + "\n");
					}
					bioWriter.write(token.getBegin() + "\t" + token.getEnd() + "\t" + token.getSurfaceForm() + "\t" + token.getBaseForm() + "\t" + token.getPosTag() + "\t" + token.getChunkTag() + "\n");
				}
				
				if (includeGoldLabels) {
					labelWriter.write("\n");
				}
				bioWriter.write("\n");
			}
		}
		
		if (writeSeparateFiles) {
			bioWriter.close();
			
			if (includeGoldLabels) {
				labelWriter.close();
			}
		}
	}

	public boolean isIncludeGoldLabels() {
		return includeGoldLabels;
	}

	public void setIncludeGoldLabels(boolean includeGoldLabels) {
		this.includeGoldLabels = includeGoldLabels;
	}

	public boolean isWriteSeparateFiles() {
		return writeSeparateFiles;
	}

	public void setWriteSeparateFiles(boolean writeSeparateFiles) {
		this.writeSeparateFiles = writeSeparateFiles;
	}
	
	

}
