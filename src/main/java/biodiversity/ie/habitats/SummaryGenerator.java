package biodiversity.ie.habitats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import biodiversity.ie.corpus.BioNLPSTCorpusLoader;
import biodiversity.ie.types.Document;
import biodiversity.ie.types.TextBoundAnnotation;

public class SummaryGenerator {
	public static final String HABITAT_LABEL = "Habitat";
	public static final int WINDOW_SIZE = 100;
	
	private String label;
	private String annotatedCorpusDirectoryPath;
	private String jsonOutputPath;
	
	private Map<String,HabitatMention> habitatMentions;
	
	public SummaryGenerator(String label, String annotatedCorpusDirectoryPath, String jsonOutputPath) {
		this.label = label;
		this.annotatedCorpusDirectoryPath = annotatedCorpusDirectoryPath;
		this.jsonOutputPath = jsonOutputPath;
		this.habitatMentions = new HashMap<String,HabitatMention>();
	}
	
	public static void main(String args[]) throws IOException {
		SummaryGenerator generator = new SummaryGenerator(args[0], args[1], args[2]);
		generator.generate();
	}
	
	public void generate() throws IOException {
		BioNLPSTCorpusLoader loader = new BioNLPSTCorpusLoader(annotatedCorpusDirectoryPath);
		loader.loadDocuments();
		List<Document> corpus = loader.getCorpus();
		
		for (Document document : corpus) {
			List<TextBoundAnnotation> annotations = document.getTextBoundAnnotations();
			for (TextBoundAnnotation annotation : annotations) {
				if (label.equals(HABITAT_LABEL) && annotation.getLabel().equals(label)) {
					String stringMention = annotation.getText().trim().toLowerCase();
					HabitatMention mention = habitatMentions.get(stringMention);
					if (mention == null) {
						mention = new HabitatMention(stringMention);
						habitatMentions.put(stringMention, mention);
					}
					
					mention.incrementFrequency();
					
					HabitatMentionSource mentionSource = new HabitatMentionSource();
					mentionSource.setFilename(document.getFilename());
					
					int sourceStartOffset = Math.max(annotation.getBeginCharOffset() - WINDOW_SIZE, 0);
					int sourceEndOffset = Math.min(annotation.getEndCharOffset() + WINDOW_SIZE, document.getText().length());
					mentionSource.setContext(document.getText().substring(sourceStartOffset, sourceEndOffset));
					mention.getMentionSources().add(mentionSource);
				}
			}
			
		}
		
		String outputString = "{\"habitatMentions\" : [";
		Set<String> keys = habitatMentions.keySet();
		int ctr = 1;
		
		for (String key : keys) {
			HabitatMention habitatMention = habitatMentions.get(key);
			outputString = outputString + habitatMention.toJsonString() + (ctr<keys.size()?", ":"");
			ctr++;
		}
		outputString = outputString + "]}";
		
		//System.out.println(outputString);
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(new File(jsonOutputPath)), "UTF-8");
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(outputString);
		String formattedString = gson.toJson(je);
		writer.write(formattedString + "\n");
		//writer.write(outputString + "\n");
		writer.close();
	}
}
