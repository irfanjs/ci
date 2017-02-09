package org.symantec.ci;

import java.util.List;

public class ComplexityCategories{
   	private String creationDate;
   	private String date;
   	private String description;
   	private String id;
   	private String key;
   	private String lname;
   	private List<Msr> msr;
   	private String name;
   	private String qualifier;
   	private String scope;
   	private String version;
   	
 	public String getCreationDate(){
 		
		return this.creationDate;
	}
	public void setCreationDate(String creationDate){
		this.creationDate = creationDate;
	}
 	public String getDate(){
		return this.date;
	}
	public void setDate(String date){
		this.date = date;
	}
 	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description = description;
	}
 	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
 	public String getKey(){
		return this.key;
	}
	public void setKey(String key){
		this.key = key;
	}
 	public String getLname(){
		return this.lname;
	}
	public void setLname(String lname){
		this.lname = lname;
		
	}
 	public List<Msr> getMsr(){
		return this.msr;
	}
	public void setMsr(List<Msr> msr){
		this.msr = msr;
	}
 	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
 	public String getQualifier(){
		return this.qualifier;
	}
	public void setQualifier(String qualifier){
		this.qualifier = qualifier;
	}
 	public String getScope(){
		return this.scope;
	}
	public void setScope(String scope){
		this.scope = scope;
	}
 	public String getVersion(){
		return this.version;
	}
	public void setVersion(String version){
		this.version = version;
	}
}
