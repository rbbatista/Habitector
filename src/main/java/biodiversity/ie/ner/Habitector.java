package biodiversity.ie.ner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Habitector {
	public static void main(String[] args) throws IOException {
		String arg = args[0];
		Map<String,String> argsMap = new HashMap<String,String>();
		
		if (arg.equals("generateTrainingData")) {
			for (int j=1;j<4;j++) {
				String[] tokens = args[j].split("=");
				argsMap.put(tokens[0], tokens[1]);
			}
			BIOAnnotationGenerator generator = new BIOAnnotationGenerator(true, false, argsMap.get("--geniaModelsPath"));
			generator.getCorpus(argsMap.get("--corpusPath"), argsMap.get("--outputPath"));
		}
		else if (arg.equals("generateTestData")) {
			for (int j=1;j<5;j++) {
				String[] tokens = args[j].split("=");
				argsMap.put(tokens[0], tokens[1]);
			}
			BIOAnnotationGenerator generator = new BIOAnnotationGenerator(false, Boolean.parseBoolean(argsMap.get("--writeSeparateFiles")), argsMap.get("--geniaModelsPath"));
			generator.getCorpus(argsMap.get("--corpusPath"), argsMap.get("--outputPath"));
		}
		else if (arg.equals("interpretResults")) {
			for (int j=1;j<3;j++) {
				String[] tokens = args[j].split("=");
				argsMap.put(tokens[0], tokens[1]);
			}
			StandoffAnnotationGenerator generator = new StandoffAnnotationGenerator();
			generator.getCorpus(argsMap.get("--resultsPath"), argsMap.get("--outputPath"));
		}

	}
}
