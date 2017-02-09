package org.symantec.ci;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;


public class Job {

	@Expose
	private String name;
	@Expose
	private String url;
	@Expose
	private String color;
	public String getName() {
	return name;
	}

	public void setName(String name) {
	this.name = name;
	}

	public String getUrl() {
	return url;
	}

	public void setUrl(String url) {
	this.url = url;
	}

	public String getColor() {
	return color;
	}

	public void setColor(String color) {
	this.color = color;
	}

	
}
