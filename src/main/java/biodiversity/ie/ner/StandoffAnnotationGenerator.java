package biodiversity.ie.ner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import biodiversity.ie.corpus.BIOCorpusLoader;
import biodiversity.ie.corpus.BIOToStandOffConverter;
import biodiversity.ie.corpus.BioNLPSTCorpusLoader;
import biodiversity.ie.types.PreprocessedDocument;
import biodiversity.ie.types.TextBoundAnnotation;

public class StandoffAnnotationGenerator {
	
	public static void main(String args[]) throws IOException {
		StandoffAnnotationGenerator generator = new StandoffAnnotationGenerator();
		generator.getCorpus(args[0], args[1]);
	}
	
	public void getCorpus(String corpusPath, String outputDirectoryPath) throws IOException {
		BIOCorpusLoader loader = new BIOCorpusLoader(corpusPath);
		loader.loadDocuments();
		List<PreprocessedDocument> documents = loader.getCorpus();
		
		
		BIOToStandOffConverter converter = new BIOToStandOffConverter(BIOToStandOffConverter.RESPONSE);
		for (PreprocessedDocument document : documents) {
			List<TextBoundAnnotation> annotations = converter.getAnnotations(document);
			File outputDirectory = new File(outputDirectoryPath);
			Writer txtWriter = new OutputStreamWriter(new FileOutputStream(new File(outputDirectory, document.getFilename() + BioNLPSTCorpusLoader.TXT_FILE_EXT)), "UTF-8");
			txtWriter.write(document.getDocumentText() + "\n");
			txtWriter.close();
			
			Writer annWriter = new OutputStreamWriter(new FileOutputStream(new File(outputDirectory, document.getFilename() + BioNLPSTCorpusLoader.ANN_FILE_EXT)), "UTF-8");
			for (TextBoundAnnotation tbAnnotation : annotations) {
				annWriter.write(tbAnnotation.getId() + "\t" + tbAnnotation.getLabel() + " " + tbAnnotation.getBeginCharOffset() + " " + tbAnnotation.getEndCharOffset() + "\t" + document.getDocumentText().substring(tbAnnotation.getBeginCharOffset(),  tbAnnotation.getEndCharOffset()) + "\n");
			}
			annWriter.close();
		}
	}
}
