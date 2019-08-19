package de.l3s.extra;

public class AIDAYAGOAnnotation {
	private String mention;
	private String offset; 
	private String entity;
	private String entityId;
	private String WikiLink;
	private String FreeBase;

	
	
	public AIDAYAGOAnnotation(String mention, String offset, String entity,
			String entityId, String wikiLink, String freeBase) {
		super();
		this.mention = mention;
		this.offset = offset;
		this.entity = entity;
		this.entityId = entityId;
		WikiLink = wikiLink;
		FreeBase = freeBase;
	}


	public AIDAYAGOAnnotation(String mention, String entity, String entityId,String wikiLink, String freeBase) {
		super();
		this.mention = mention;
		this.entity = entity;
		this.entityId = entityId;
		WikiLink = wikiLink;
		FreeBase = freeBase;
	}
	

	public AIDAYAGOAnnotation(String entity) {
		super();
		this.entity = entity;
	}

	

	public String getOffset() {
		return offset;
	}


	public void setOffset(String offset) {
		this.offset = offset;
	}


	public AIDAYAGOAnnotation(String entity, String entityId, String wikiLink) {
		super();
		this.entity = entity;
		this.entityId = entityId;
		WikiLink = wikiLink;
	}


	public AIDAYAGOAnnotation(String entity, String entityId, String wikiLink,
			String freeBase) {
		super();
		this.entity = entity;
		this.entityId = entityId;
		WikiLink = wikiLink;
		FreeBase = freeBase;
	}

	public AIDAYAGOAnnotation() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}


	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}


	/**
	 * @return the freeBase
	 */
	public String getFreeBase() {
		return FreeBase;
	}


	/**
	 * @param freeBase the freeBase to set
	 */
	public void setFreeBase(String freeBase) {
		FreeBase = freeBase;
	}

	/**
	 * @return the mention
	 */
	public String getMention() {
		return mention;
	}

	/**
	 * @param mention
	 *            the mention to set
	 */
	public void setMention(String mention) {
		this.mention = mention;
	}

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return the wikiLink
	 */
	public String getWikiLink() {
		return WikiLink;
	}

	/**
	 * @param wikiLink
	 *            the wikiLink to set
	 */
	public void setWikiLink(String wikiLink) {
		WikiLink = wikiLink;
	}

	
}
