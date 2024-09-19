package com.baeldung.lsd.persistence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class Project {
	@Id
	private String id;
	@Indexed(unique = true)
	private String code;
	private String name;
	private String description;

	public Project(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		if (!super.equals(object)) return false;
		Project project = (Project) object;
		return Objects.equals(id, project.id) && Objects.equals(code, project.code) && Objects.equals(name, project.name) && Objects.equals(description, project.description);
	}

	public int hashCode() {
		return Objects.hash(super.hashCode(), id, code, name, description);
	}

	@Override
	public String toString() {
		return "Project{" +
				"id=" + id +
				", code='" + code + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
