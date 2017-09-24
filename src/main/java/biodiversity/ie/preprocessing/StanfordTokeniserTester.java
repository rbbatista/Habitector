package biodiversity.ie.preprocessing;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class StanfordTokeniserTester {
	public static void main(String[] args) {
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader("28:886, 28885 21992 21999 VMamil, May, 1914, For."), new CoreLabelTokenFactory(), "normalizeSpace=true,normalizeFractions=false,unicodeEllipsis=true,splitHyphenated=true,ptb3Escaping=false,untokenizable=noneKeep");

		while (ptbt.hasNext()) {
			CoreLabel label = ptbt.next();
			boolean isProblematic = isProblematicColon(label.originalText());
			if (!isProblematic) {
				System.out.println(label.beginPosition() + "\t" + label.endPosition() + "\t" + label.originalText());
			}
			else {
				StringTokenizer tokenizer = new StringTokenizer(label.originalText(), ":", true);
				int adjustedBegin = label.beginPosition();
				
				while (tokenizer.hasMoreTokens()) {
					String nextToken = tokenizer.nextToken();
					int adjustedEnd = adjustedBegin + nextToken.length();
					System.out.println(adjustedBegin + "\t" + adjustedEnd + "\t" + nextToken);
					adjustedBegin = adjustedEnd;
				}
			}
			
		}
	}
	
	public static boolean isProblematicColon(String tokenText) {
		boolean isProblematic = false;
		String regex =   ":[0-9]+";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(tokenText);
		if (matcher.find()) {
			isProblematic = true;
		}
		return isProblematic;
	}
}
