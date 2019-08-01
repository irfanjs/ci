package org.stc.ci;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ViewDescription {

@Expose
private Object description;
@Expose
private List<Job> jobs = new ArrayList<Job>();
@Expose
private String name;
@Expose
private List<Object> property = new ArrayList<Object>();
@Expose
private String url;

public Object getDescription() {
return description;
}

public void setDescription(Object description) {
this.description = description;
}

public List<Job> getJobs() {
return jobs;
}

public void setJobs(List<Job> jobs) {
this.jobs = jobs;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public List<Object> getProperty() {
return property;
}

public void setProperty(List<Object> property) {
this.property = property;
}

public String getUrl() {
return url;
}

public void setUrl(String url) {
this.url = url;
}

}