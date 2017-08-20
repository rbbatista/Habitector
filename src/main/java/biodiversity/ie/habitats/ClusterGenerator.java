package biodiversity.ie.habitats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import biodiversity.ie.types.Mapping;

public class ClusterGenerator {
	private String newTermsFile;
	private String relatedTermsFile;
	
	private Map<String, Mapping> allGroundingMappings;
	private Map<String,HabitatMention> allSemanticRelatedTerms;
	
	public ClusterGenerator(String newTermsFile, String relatedTermsFile) {
		this.newTermsFile = newTermsFile;
		this.relatedTermsFile = relatedTermsFile;
	}
	
	public static void main(String[] args) throws IOException {
		ClusterGenerator generator = new ClusterGenerator(args[0], args[1]);
		generator.generate();
	}
	
	public void generate() throws IOException {
		this.loadGroundingResults();
		this.loadSemanticRelatednessResults();
	}
	
	public void loadGroundingResults() throws IOException {
		Scanner scanner = new Scanner(new FileInputStream(new File(newTermsFile)), "UTF-8");
		while (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			String[] lineTokens = currentLine.split("\t");
			
			Mapping mapping = new Mapping();
			mapping.setAutoTerm(lineTokens[0]);
			mapping.setOntoTerm(lineTokens[2]);
			mapping.setOntoID(lineTokens[1]);
			mapping.setScore(Double.parseDouble(lineTokens[3]));
			mapping.setFrequency(Integer.parseInt(lineTokens[4]));
			
			allGroundingMappings.put(lineTokens[0], mapping);
			
		}
		scanner.close();
	}
	
	public void loadSemanticRelatednessResults() throws IOException {
		String jsonString = "";
		Scanner scanner = new Scanner(new FileInputStream(new File(relatedTermsFile)), "UTF-8");
		while (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			jsonString = jsonString + currentLine;
		}
		scanner.close();
		
		JsonElement jsonElement = new JsonParser().parse(jsonString);
		JsonObject jsonResult = jsonElement.getAsJsonObject();
		JsonArray habitatMentions = jsonResult.getAsJsonArray("habitatMentions");
		for (int i=0;i< habitatMentions.size(); i++ ) {
			JsonObject habitatMentionObject = habitatMentions.get(i).getAsJsonObject();
			HabitatMention mention = new HabitatMention(habitatMentionObject.get("mention").getAsString());
			mention.setFrequency(habitatMentionObject.get("frequency").getAsInt());
			
			List<HabitatMentionSource> mentionSources = new ArrayList<HabitatMentionSource>();
			JsonArray sources = habitatMentionObject.get("sources").getAsJsonArray();
			for (int j=0;j<sources.size();j++) {
				JsonObject sourceObject = sources.get(j).getAsJsonObject();
				HabitatMentionSource source = new HabitatMentionSource();
				source.setFilename(sourceObject.get("sourceFile").getAsString());
				source.setContext(sourceObject.get("context").getAsString());
				mentionSources.add(source);
			}
			
			mention.setMentionSources(mentionSources);
			
			List<HabitatMention> relatedMentions = new ArrayList<HabitatMention>();
			JsonArray relatedTerms = habitatMentionObject.get("relatedCategories").getAsJsonArray();
			for (int j=0;j<relatedTerms.size();j++) {
				JsonObject termObject = relatedTerms.get(j).getAsJsonObject();
				HabitatMention term = new HabitatMention(termObject.get("val").getAsString());
				term.setId(termObject.get("id").getAsString());
				relatedMentions.add(term);
			}
			
			mention.setRelatedMentions(relatedMentions);
			
			allSemanticRelatedTerms.put(mention.getMention(), mention);
		}
		
	}
}
