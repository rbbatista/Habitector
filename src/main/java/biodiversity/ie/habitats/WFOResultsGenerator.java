package biodiversity.ie.habitats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import biodiversity.ie.types.Mapping;

public class WFOResultsGenerator {
	public static final int FREQUENCY_THRESHOLD = 3;
	public static final double SCORE_THRESHOLD = 0.84;
	public static final String MEASUREMENT_TYPE = "http://kew.org/wcs/terms/habitat";
	
	private String descriptionIDMappingFile = "/home/riza/Dropbox (The University of Manchester)/Projects/Biodiversity/ClearEarth/WFO_William/mappings/taxon_description_ids.out.tsv";
	private String nameMappingFile = "/home/riza/Dropbox (The University of Manchester)/Projects/Biodiversity/ClearEarth/WFO_William/mappings/name_taxon_ids.out.tsv";
	private String envoToWFOMappingsFile = "/home/riza/Dropbox (The University of Manchester)/Projects/Biodiversity/ClearEarth/WFO_William/mappings/WFOMappings.tsv";
	private String nerResultsFile;
	private String groundingResultsFile;
	private String wfoFile;
	
	private Map<String,HabitatMention> allHabitatMentions;
	private Map<String, Mapping> allGroundingMappings;
	private Map<String, String> descIDToWFOIdentiferMappings;
	private Map<String, String> nameToWFOIdentifierMappings;
	private Map<String, String> envoToWFOMappings;
	
	
	public WFOResultsGenerator(String nerResultsFile, String groundingResultsFile, String wfoFile) {
		this.nerResultsFile = nerResultsFile;
		this.groundingResultsFile = groundingResultsFile;
		this.allHabitatMentions = new HashMap<String,HabitatMention>();
		this.allGroundingMappings = new HashMap<String,Mapping>();
		this.descIDToWFOIdentiferMappings = new HashMap<String,String>();
		this.nameToWFOIdentifierMappings = new HashMap<String,String>();
		this.envoToWFOMappings = new HashMap<String,String>();
		this.wfoFile = wfoFile;
	}
	
	public static void main(String[] args) throws IOException {
		WFOResultsGenerator generator = new WFOResultsGenerator(args[0], args[1], args[2]);
		generator.generate();
	}
	
	public void generate() throws IOException {
		this.loadSimpleMappings(descriptionIDMappingFile, descIDToWFOIdentiferMappings);
		this.loadSimpleMappings(nameMappingFile, nameToWFOIdentifierMappings);
		this.loadSimpleMappings(envoToWFOMappingsFile, envoToWFOMappings);
		this.loadNERResults();
		this.loadGroundingResults();
		
		this.generateWFOMappings();
		
	}
	
	public void generateWFOMappings() throws IOException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(new File(wfoFile)), "UTF-8");
		Set<String> keys = allGroundingMappings.keySet();
		for (String key : keys) {
			Mapping groundingMapping = allGroundingMappings.get(key);
			if (groundingMapping.getScore() >= SCORE_THRESHOLD) {
				List<HabitatMentionSource> sources = allHabitatMentions.get(groundingMapping.getAutoTerm()).getMentionSources();
				for (HabitatMentionSource source : sources) {
					String wfoCode = envoToWFOMappings.get(groundingMapping.getOntoID());
					String sourceID = source.getFilename();
					String wfoID = descIDToWFOIdentiferMappings.get(sourceID);
					if (wfoID!=null) {
						writer.write(groundingMapping.getAutoTerm() + "\t" + wfoID + "\t" + MEASUREMENT_TYPE + "\t" + (wfoCode!=null?wfoCode:groundingMapping.getOntoID()) + "\n");
					}
					
					wfoID = nameToWFOIdentifierMappings.get(source.getFilename());
					if (wfoID!=null) {
						writer.write(groundingMapping.getAutoTerm() + "\t" + wfoID + "\t" + MEASUREMENT_TYPE + "\t" + (wfoCode!=null?wfoCode:groundingMapping.getOntoID()) + "\n");
					}
					
				}
			}
		}
		writer.close();
	}

	public void loadSimpleMappings(String mappingsPath, Map<String,String> mappings) throws IOException {
		File file = new File(mappingsPath);
		Scanner scanner = new Scanner(new FileInputStream(file), "UTF-8");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] tokens = line.split("\t");
			mappings.put(tokens[0], tokens[1]);
		}
		scanner.close();
	}
	
	public void loadNERResults() throws IOException {
		String jsonString = "";
		Scanner scanner = new Scanner(new FileInputStream(new File(nerResultsFile)), "UTF-8");
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
			
			allHabitatMentions.put(mention.getMention(), mention);
		}
		
	}
	
	public void loadGroundingResults() throws IOException {
		Scanner scanner = new Scanner(new FileInputStream(new File(groundingResultsFile)), "UTF-8");
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
}
