package biodiversity.ie.habitats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import biodiversity.ie.types.Mapping;
import biodiversity.ie.utils.LevenshteinDistance;

public class MappingFilter {
	public static final int FREQUENCY_THRESHOLD = 3;
	public static final double SCORE_THRESHOLD = 0.84;
	public static final int EDIT_DISTANCE_THRESHOLD = 2;
	
	private String mappingsFile;
	private String newMappingsFile;
	private Map<String, Mapping> mappings;
	
	public MappingFilter(String mappingsFile, String newMappingsFile) {
		this.mappingsFile = mappingsFile;
		this.newMappingsFile = newMappingsFile;
		this.mappings = new HashMap<String,Mapping>();
	}
	
	public static void main(String[] args) throws IOException {
		MappingFilter filter = new MappingFilter(args[0], args[1]);
		filter.clean();
	}
	
	public void clean() throws IOException {
		Scanner scanner = new Scanner(new FileInputStream(new File(mappingsFile)), "UTF-8");
		while (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			String[] lineTokens = currentLine.split("\t");
			
			Mapping mapping = new Mapping();
			mapping.setAutoTerm(lineTokens[0]);
			mapping.setOntoTerm(lineTokens[2]);
			mapping.setOntoID(lineTokens[1]);
			mapping.setScore(Double.parseDouble(lineTokens[3]));
			mapping.setFrequency(Integer.parseInt(lineTokens[4]));
			
			mappings.put(lineTokens[0], mapping);
			
		}
		scanner.close();
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(new File(newMappingsFile)), "UTF-8");
		
		for (Mapping mapping : mappings.values()) {
			if (mapping.getScore()<SCORE_THRESHOLD && mapping.getFrequency()>3) {
				int editDistance = LevenshteinDistance.getLevenshteinDistance(mapping.getAutoTerm().toLowerCase(),mapping.getOntoTerm().toLowerCase());
				if (editDistance > EDIT_DISTANCE_THRESHOLD) {
					writer.write(mapping.toString() + "\n");
				}
			}
		}
		writer.close();
	}
	
}
