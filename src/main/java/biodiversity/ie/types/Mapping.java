package biodiversity.ie.types;

public class Mapping {
	private String autoTerm;
	private String ontoTerm;
	private String ontoID;
	private double score;
	private int frequency;
	
	public String getAutoTerm() {
		return autoTerm;
	}
	public void setAutoTerm(String autoTerm) {
		this.autoTerm = autoTerm;
	}
	public String getOntoTerm() {
		return ontoTerm;
	}
	public void setOntoTerm(String ontoTerm) {
		this.ontoTerm = ontoTerm;
	}
	public String getOntoID() {
		return ontoID;
	}
	public void setOntoID(String ontoID) {
		this.ontoID = ontoID;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public String toString() {
		return (this.autoTerm + "\t" + this.ontoID + "\t" + this.ontoTerm + "\t" + this.score + "\t" + this.frequency);
	}
}
