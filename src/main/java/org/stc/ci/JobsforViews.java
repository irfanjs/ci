package org.stc.ci;

import java.awt.List;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class JobsforViews {
	
	JobsforViews()
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

		public ArrayList<String> getData(String url) throws MalformedURLException, IOException
		{
			
			
			String jsonText;
	//		jsonText = readURL("http://10.211.64.231:9000/api/resources?metrics=function_complexity&format=json");
			jsonText = readURL(url);
			System.out.println(jsonText);
			System.out.println(jsonText.length());
			Gson gson = new Gson();        
			JsonParser parser = new JsonParser();
			
			//JsonArray jArray = parser.parse(jsonText).getAsJsonArray();
			JsonArray jArray = (JsonArray)parser.parse(jsonText).getAsJsonObject().get("jobs");
			 ArrayList<Job> lcs = new ArrayList<Job>();
			Map <String, String> hm = new HashMap<String, String>();
			for(JsonElement obj : jArray )
			{
				
				
				Job cse = gson.fromJson( obj , Job.class);
				lcs.add(cse);
				//hm.put(cse.getKey(), cse.getComplexityData().get(0).getVal());
			}
			
			 Iterator<Job> it = lcs.iterator();
			 ArrayList<String> jobNames = new ArrayList<String>();
			 while(it.hasNext())
			    {
				 Job obj = it.next();
				 
				 jobNames.add(obj.getName());
				 
			    }
			 
			return jobNames;

		}

	}
