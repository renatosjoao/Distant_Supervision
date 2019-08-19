package de.l3s.extra;

public class WPAnnotation {
	private String mention;
	private String offset; 
	private String entity;
	
	public WPAnnotation(String mention, String offset, String entity) {
		super();
		this.mention = mention;
		this.offset = offset;
		this.entity = entity;
	}

	public WPAnnotation() {
		super();
	}

	public String getMention() {
		return mention;
	}

	public void setMention(String mention) {
		this.mention = mention;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}
}
