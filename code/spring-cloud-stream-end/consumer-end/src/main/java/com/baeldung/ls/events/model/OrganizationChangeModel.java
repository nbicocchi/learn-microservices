package com.baeldung.ls.events.model;

public class OrganizationChangeModel {
	private String type;
	private String organizationId;

	public OrganizationChangeModel(String type, String organizationId) {
		this.type = type;
		this.organizationId = organizationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@Override
	public String toString() {
		return "OrganizationChangeModel{" + "type='" + type + '\'' + ", organizationId='" + organizationId + '\'' + '}';
	}
}


