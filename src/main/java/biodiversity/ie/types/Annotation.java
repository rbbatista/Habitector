package biodiversity.ie.types;

public interface Annotation extends Comparable {
	
	public String getId();
	public void setId(String id);
	
	public String getFilename();
	public void setFilename(String filename);
	
	public String getLabel();
	public void setLabel(String label);
	
	public String getNote();
	public void setNote(String note);
	
	public String toString();
	public boolean equals(Object o);
	public int compareTo(Object o);
		
}
