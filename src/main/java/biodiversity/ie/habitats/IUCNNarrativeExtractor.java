package biodiversity.ie.habitats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class IUCNNarrativeExtractor {
	private String jsonCorpusPath;
	private String txtCorpusPath;
	
	public IUCNNarrativeExtractor(String jsonCorpusPath,String txtCorpusPath) {
		this.jsonCorpusPath = jsonCorpusPath;
		this.txtCorpusPath = txtCorpusPath;
	}
	
	public static void main (String[] args) throws IOException {
		IUCNNarrativeExtractor extractor = new IUCNNarrativeExtractor(args[0], args[1]);
		extractor.extract();
	}
	
	public void extract() throws IOException {
		File jsonDirectory = new File(jsonCorpusPath);
		File[] jsonFiles = jsonDirectory.listFiles();
		for (File jsonFile : jsonFiles) {
			String jsonString = "";
			Scanner scanner = new Scanner(new FileInputStream(jsonFile), "UTF-8");
			while (scanner.hasNextLine()) {
				jsonString = jsonString + scanner.nextLine() + "\n";
			}
			scanner.close();
			
			String text = "";
			JsonElement jsonElement = new JsonParser().parse(jsonString);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String speciesName = jsonObject.getAsJsonPrimitive("name").getAsString();
			text = text + speciesName + "\n";
			JsonArray resultArray = jsonObject.getAsJsonArray("result");
			for (int i = 0;i<resultArray.size();i++) {
				JsonObject arrayElementObject = resultArray.get(i).getAsJsonObject();
				Set<Map.Entry<String,JsonElement>> members = arrayElementObject.entrySet();
				for (Map.Entry<String, JsonElement> member : members) {
					if (!(member.getValue() instanceof JsonNull)) {
						String value = member.getValue().getAsString();
						if (value.split(" ").length>1) {
							text = text + Jsoup.parse(value).text()+ "\n";
						}
						
					}
				}
			}
			
			File outputDirectory = new File(txtCorpusPath);
			Writer writer = new OutputStreamWriter(new FileOutputStream(new File(outputDirectory, jsonFile.getName() + ".txt")), "UTF-8");
			writer.write(text + "\n");
			writer.close();
		}
	}
}
