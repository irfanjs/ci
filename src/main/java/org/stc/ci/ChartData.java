package org.stc.ci;

import java.util.ArrayList;
import java.util.List;

public class ChartData {
	final List<String> categories;
	final List<Object> data;
	
	public ChartData(){
		
		categories = new ArrayList<String>();
		data = new ArrayList<Object>();
	}
	
	public List<String> getCategories() {
		return categories;
	}
	public List<Object> getData() {
		return data;
	}
}
