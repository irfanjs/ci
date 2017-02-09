package org.symantec.ci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CalculateCodeComplexity {
	
	public CalculateCodeComplexity()
	{
	}

	public static String readURL(String url) throws MalformedURLException, IOException
	{
		String jsonText;
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			jsonText = readAll(rd);
			System.out.println(jsonText);

		}finally {
			is.close();
		}
		return jsonText;
	}

	private static String readAll(BufferedReader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
		
	}

	public Map<String, Float> getData(PrintStream logger) throws MalformedURLException, IOException
	{
		String jsonText;
		jsonText = readURL("http://10.211.64.231:9000/api/resources?metrics=function_complexity&format=json");
		System.out.println(jsonText);
		System.out.println(jsonText.length());
		Gson gson = new Gson();        
		JsonParser parser = new JsonParser();
		JsonArray jArray = parser.parse(jsonText).getAsJsonArray();
		 ArrayList<ComplexityCategories> lcs = new ArrayList<ComplexityCategories>();
		Map <String, Float> hm = new HashMap<String, Float>();
		for(JsonElement obj : jArray )
		{
			
			ComplexityCategories cse = gson.fromJson( obj , ComplexityCategories.class);
			lcs.add(cse);
			//hm.put(cse.getKey(), cse.getComplexityData().get(0).getVal());
		}
		
		 Iterator<ComplexityCategories> it = lcs.iterator();
		 while(it.hasNext())
		    {
			 ComplexityCategories obj = it.next();
				        
		        System.out.println(obj.getMsr().get(0).getVal());
		        
		        hm.put(obj.getName(), obj.getMsr().get(0).getVal());
		     
		    }
		 
		 Iterator it23 = hm.entrySet().iterator();
		    while (it23.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it23.next();
		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
		   
		    }
		 

		return hm;

	}

}



