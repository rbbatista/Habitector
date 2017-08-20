package biodiversity.ie.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import biodiversity.ie.types.Document;
import biodiversity.ie.types.TextBoundAnnotation;

public class BioNLPSTCorpusLoader {
	public static final String ANN_FILE_EXT = ".a1";
	public static final String TXT_FILE_EXT = ".txt";
	
	private String corpusPath;
	private List<Document> corpus;
	
	public BioNLPSTCorpusLoader(String corpusPath) {
		this.corpusPath = corpusPath;
		this.corpus = new ArrayList<Document>();
	}
	
	public static void main(String args[]) throws IOException {
		BioNLPSTCorpusLoader loader = new BioNLPSTCorpusLoader(args[0]);
		loader.loadDocuments();
	}
	
	public void loadDocuments() {
		File directory = new File(corpusPath);
		File[] files = directory.listFiles();
		
		for (File file : files) {
			if (file.getName().endsWith(TXT_FILE_EXT)) {
				String base = file.getName().split(TXT_FILE_EXT)[0];
				Document document = loadDocument(base);
/*//				for (TextBoundAnnotation annotation : document.getTextBoundAnnotations()) {
//					System.out.println("GOLD:" + annotation.getBeginCharOffset() + " " + annotation.getEndCharOffset() + "\t" + annotation.getText());
//				}
*/				corpus.add(document);
			}
		}
	}
	
	public Document loadDocument(String base) {
		Document document = new Document();
		document.setFilename(base);
		String documentText = "";
		
		try {
			Scanner txtScanner = new Scanner(new FileInputStream(corpusPath + File.separatorChar + base + TXT_FILE_EXT),"UTF-8");
			while (txtScanner.hasNextLine()) {
				documentText = documentText + txtScanner.nextLine() + "\n";
			}
			txtScanner.close();
			document.setText(documentText);
			
			File annotationFile = new File(corpusPath + File.separatorChar + base + ANN_FILE_EXT);
			if (annotationFile.exists()) {
				Scanner annScanner = new Scanner(new FileInputStream(annotationFile),"UTF-8");
				while (annScanner.hasNextLine()) {
					String line = annScanner.nextLine();
					String[] data = line.split("\t");
					if (data[0].startsWith("T")) {
						String[] annotationDetails = data[1].split(" ");
						TextBoundAnnotation annotation = new TextBoundAnnotation(data[0], base, Integer.parseInt(annotationDetails[1]), Integer.parseInt(annotationDetails[2]), annotationDetails[0]);
						annotation.setBeginByteOffset(annotation.getBeginCharOffset());
						annotation.setEndByteOffset(annotation.getEndCharOffset());
						annotation.setText(documentText.substring(annotation.getBeginCharOffset(), annotation.getEndCharOffset()));
						document.getTextBoundAnnotations().add(annotation);
					}
				}
				annScanner.close();
			}
		}
		catch (IOException io) {
			document = null;
		}
		return document;
	}

	public List<Document> getCorpus() {
		return corpus;
	}

	public void setCorpus(List<Document> corpus) {
		this.corpus = corpus;
	}
	
}
