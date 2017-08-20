package biodiversity.ie.types;

public class Token {
	private int begin;
	private int end;
	private String surfaceForm;
	private String baseForm;
	private String posTag;
	private String chunkTag;
	private String referenceLabel;
	private String responseLabel;
	private double marginalProbability;
	
	public double getMarginalProbability() {
		return marginalProbability;
	}

	public void setMarginalProbability(double marginalProbability) {
		this.marginalProbability = marginalProbability;
	}

	public Token() {
		this.referenceLabel = "O";
		this.responseLabel = "O";
	}
	
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public String getSurfaceForm() {
		return surfaceForm;
	}
	public void setSurfaceForm(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}
	public String getBaseForm() {
		return baseForm;
	}
	public void setBaseForm(String baseForm) {
		this.baseForm = baseForm;
	}
	public String getPosTag() {
		return posTag;
	}
	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}
	public String getChunkTag() {
		return chunkTag;
	}
	public void setChunkTag(String chunkTag) {
		this.chunkTag = chunkTag;
	}
	
	public String toCompleteString() {
		return (begin + "\t" + end + "\t" + referenceLabel + "\t" + responseLabel + "\t" + surfaceForm + "\t" + baseForm + "\t" + posTag + "\t" + chunkTag);
	}
	
	public String toNerSuiteString() {
		return (begin + "\t" + end + "\t" + surfaceForm + "\t" + baseForm + "\t" + posTag + "\t" + chunkTag + "\t" + referenceLabel);
	}

	public String toString() {
		return (/*begin + "\t" + end + "\t" + */ referenceLabel + "\t" + surfaceForm + /*"\t" + baseForm +*/ "\t" + posTag + "\t" + chunkTag);
	}
	public String getReferenceLabel() {
		return referenceLabel;
	}
	public void setReferenceLabel(String referenceLabel) {
		this.referenceLabel = referenceLabel;
	}
	public String getResponseLabel() {
		return responseLabel;
	}
	public void setResponseLabel(String responseLabel) {
		this.responseLabel = responseLabel;
	}
	
}
