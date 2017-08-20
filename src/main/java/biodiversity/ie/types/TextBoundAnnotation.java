package biodiversity.ie.types;

public class TextBoundAnnotation implements Annotation {
	public static final int CHAR_OFFSET = 1;
	public static final int BYTE_OFFSET = 2;
	public static final int DEBUG = 3;
	
	public static final int A1 = 1 ;
	public static final int A2 = 2 ;
	
	private String id;
	private String filename;
	private int beginCharOffset;
	private int endCharOffset;
	private int beginByteOffset;
	private int endByteOffset;
	String label;
	String text;
	int aType;
	String note;
	boolean isChanged;
	boolean isEOS;
	
	public TextBoundAnnotation () {
		
	}
	
	public TextBoundAnnotation(String id) {
		this.id = id;
	}
	
	
	public TextBoundAnnotation (String id, String filename, int beginCharOffset, int endCharOffset, String label) {
		this.id = id;
		this.filename = filename;
		this.beginCharOffset = beginCharOffset;
		this.endCharOffset = endCharOffset;
		this.label = label;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getBeginCharOffset() {
		return beginCharOffset;
	}
	public void setBeginCharOffset(int beginCharOffset) {
		this.beginCharOffset = beginCharOffset;
	}
	public int getEndCharOffset() {
		return endCharOffset;
	}
	public void setEndCharOffset(int endCharOffset) {
		this.endCharOffset = endCharOffset;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String toString() {
		return (id + "\t" + label + " " + beginCharOffset + " " + endCharOffset + "\t" + text /*+  (note!=null?"\n" + note:"")*/);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	/*public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof Annotation) {
			Annotation s = (Annotation) o;
			
			if (s.getId().equals(this.id)) {
				result = true;
			}
		}
		return result;
	}*/

	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof TextBoundAnnotation) {
			TextBoundAnnotation s = (TextBoundAnnotation) o;
			if (s.getLabel().equals(this.getLabel()) && s.getBeginCharOffset()==this.getBeginCharOffset() && s.getEndCharOffset()==this.getEndCharOffset()) {
				result = true;
			}
		}
		
		return result;
	}
	
	
	public int getBeginByteOffset() {
		return beginByteOffset;
	}

	public void setBeginByteOffset(int beginByteOffset) {
		this.beginByteOffset = beginByteOffset;
	}

	public int getEndByteOffset() {
		return endByteOffset;
	}

	public void setEndByteOffset(int endByteOffset) {
		this.endByteOffset = endByteOffset;
	}
	
	public int compareTo(Object o) {
		int result = 0;
		if (o instanceof Annotation) {
			TextBoundAnnotation a = (TextBoundAnnotation) o;
			if (a.getBeginCharOffset()>this.getBeginCharOffset()) {
				result = -1;
			}
			else if (a.getBeginCharOffset()<this.getBeginCharOffset()) {
				result = 1;
			}
			else if (a.getBeginCharOffset()==this.getBeginCharOffset()) {
				if (a.getEndCharOffset()==this.getEndCharOffset()) {
					result = 0;
				}
				else if (a.getEndCharOffset()<this.getEndCharOffset()) {
					result = 1;
				}
				else if (a.getEndCharOffset()>this.getEndCharOffset()) {
					result = -1;
				}
			}
		}
		return result;
	}
	
	public String getStringRepresentation(int flag) {
		String result = "";
		if (flag == CHAR_OFFSET) {
			result = id + "\t" + label + " " + beginCharOffset + " " + endCharOffset + "\t" + text;
		}
		else if (flag == BYTE_OFFSET) {
			result = id + "\t" + label + " " + beginByteOffset + " " + endByteOffset + "\t" + text;
		}
		else if (flag == DEBUG) {
			result = id + "\t" + label +  " " + beginCharOffset + " " + endCharOffset + " " + beginByteOffset + " " + endByteOffset + "\t" + text;
		}
		return result;
	}

	public int getaType() {
		return aType;
	}

	public void setaType(int aType) {
		this.aType = aType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public boolean isEOS() {
		return isEOS;
	}

	public void setEOS(boolean isEOS) {
		this.isEOS = isEOS;
	}
}
