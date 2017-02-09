package org.symantec.ci;


import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.JobPropertyDescriptor;
import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.model.View;
import hudson.model.Result;
import hudson.plugins.cobertura.CoberturaBuildAction;
import hudson.plugins.cobertura.targets.CoverageMetric;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.junit.TestResultAction;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.plugins.cobertura.*;
import hudson.plugins.findbugs.FindBugsResultAction;
import hudson.plugins.perforce.PerforceTagAction;
import hudson.plugins.perforce.PerforceTagAction.PerforceTag;
import hudson.plugins.pmd.PmdResultAction;
import hudson.plugins.testng.TestNGTestResultBuildAction;
import hudson.model.*;


//import hudson.model.Hudson;

public class Record extends Publisher {


	String reportDate;
	Date datetime;
	int bldno;
	String mdlname;
	String result;
	int failCnt;
	int passCnt;
	int totCnt;
	int skipCnt;
	//FilePath wksp;
	float packages = 0;
	float files = 0;
	float classes = 0;
	float method = 0;
	float line = 0;
	float condition = 0;
	String reason;
	String s2;
	int finalloc;
	int projectid = 0;
	int totute2e = 0;
	int bugCount = 0;
	int hour = 0;


	int numfxwarn;
	int numhghwrn;
	int numlowarn;
	int numnmlwarn;
	int numnewwarn;
	int totwarn;
	int totmdl;
	int reviewidsize;
	int bugCntsize = 0;
	String finalmodulename;

	int pmdhghwarn;
	int pmdnmlwarn;
	int pmdlowwarn;
	int pmdnewwarn;
	int pmdtotwarn;
	int reviewID;
	int changelistno;
	String checkintype;
	int pmd = 0;

	String TotalTCCount;
	String FailTestcases;
	String PassTestcases;
	String batresult;
	String nightly;
	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;
	
	int ccut = 0;

	float [] myStringArray = new float[6];

	//DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	String str = null;


	@SuppressWarnings("deprecation")
	@DataBoundConstructor
	public Record()
	{
		//	rh = new RecordHelper();
		this.datetime = datetime;
		
	}
	
	public static String stringStackTrace(final Exception ex) {
		if (ex == null) {
			return "";
		}

		final StringBuilder sbTrace = new StringBuilder();
		sbTrace.append(getExceptionTrace(ex));

		Throwable cause =  ex.getCause();

		while (cause != null)
		{
			final String sb = "Cause : Exception : %1$s\nStackTrace : %2$s";
			sbTrace.append(String.format(sb,
					new Object[] {cause.getMessage(),
					getExceptionTrace(cause)}));

			cause = cause.getCause();
		}
		return sbTrace.toString();
	}
	private static String getExceptionTrace(final Exception ex)
	{
		if (ex == null) {
			return "";
		}
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}
	private static String getExceptionTrace(final Throwable ex)
	{
		if (ex == null) {
			return "";
		}
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}


	@Override
	public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		
		int prodbld = 0;

		Map <String,String> var;
		var = build.getBuildVariables();
		
		for (Map.Entry<String, String> entry : var.entrySet()) {
			listener.getLogger().println("key" + entry.getKey() +  ": value " + entry.getValue());
			
			if(entry.getKey().equals("Branch") && entry.getValue().equals("//depot/DCS/UA/trunk/scm/templates/odprod_template.txt"))
			{
				listener.getLogger().println("inside prod branch loop for OD ");
				prodbld = 1;
			}
			else if (entry.getKey().equals("Branch") && entry.getValue().equals("//depot/DCS/UA/trunk/scm/templates/umc_prod.txt"))
			{
				listener.getLogger().println("inside prod branch loop for umc ");
				prodbld = 1;
			}
		}
		
		if (prodbld == 1)
		{
			listener.getLogger().println("started rod branch loop ");
		for (Map.Entry<String, String> entry : var.entrySet()) {
			listener.getLogger().println("key" + entry.getKey() +  ": value " + entry.getValue());	
			if (entry.getKey().equals("Run_UnitTest_CodeCoverage") && entry.getValue().equals("true") )
			{
				ccut = 1;
			}
			
		}
		Object Testngobj = null;
		
		mdlname = build.getProject().getName();
		List<Map<String, Object>> myMap = null;
		
		JobsforViews jv = new JobsforViews();
		ProjectTag pt = new ProjectTag();
		ArrayList<String> jbNames = new ArrayList<String>();
		
		Collection<View> hudson = Hudson.getInstance().getViews();
		
		Iterator<View> iteratorv = hudson.iterator();

	      // while loop
	      while (iteratorv.hasNext()) {
	    	  String viewname = iteratorv.next().getViewName();
	    	  if (viewname.equals("All"))
	    	  {
	    	  
	    	  }
	    	  else
	    	  {
	      listener.getLogger().println("value= " + viewname);
	      
	      String url = "http://10.211.161.72:8080/view/" + viewname + "/api/json";
	      listener.getLogger().println("url is : " + url);
          jbNames=jv.getData(url);
          for (String curVal : jbNames){
        	  if (curVal.equals(mdlname)){
        	    listener.getLogger().println("project name " + mdlname + "is present in view " + viewname );
        	  try 
        	  {
        		    try {
						try {
							myMap = pt.getProjectId(viewname);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						listener.getLogger().println(stringStackTrace(e));
					}
        		    
        		    if (myMap.isEmpty())
        		    {
        		    	listener.getLogger().println("the data returned from projects table is empty ");
        		    	return true;
        		    }
        		            		    
            	    for (Map<String, Object> data1 : myMap) {
        			    for (Map.Entry<String, Object> entry : data1.entrySet()) {
        			       System.out.println(entry.getKey() + ": " + entry.getValue());
        			       projectid = Integer.parseInt(entry.getValue().toString());
        			       if (projectid == 0)
        			       {
        			    	   listener.getLogger().println(" project id not found in db");
        			       }
            	  }
            	}
        	  }
        	  finally
        	  {
        		  
        	  }
	      
	      }
        	  else
        	  {
        		  listener.getLogger().println("required modulename is not found");
        	  }
          }
	      }
	      } 	  
	      
	    
	     	
		User u = User.current();
		String usr = u.getFullName();
		listener.getLogger().println(usr);

		Set<Integer> numbers = new TreeSet<Integer>();
		Set<Integer> totalBugcnt = new TreeSet<Integer>();
		

		//wksp = build.getWorkspace();
		datetime = build.getTime();

		System.out.println(datetime);

		bldno = build.getNumber();
		
		// inserting build number to nightlybuild table
		BuildInfoNightly bn = new BuildInfoNightly();
		if (mdlname.equals("SO-E2E-Snapshot"))
		{
			
			try {
				bn.insert(bldno);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				listener.getLogger().println("some issue while insterting data in nightlybuild");
				e1.printStackTrace();
			}
		}
		
		if (mdlname.equals("UMC-E2E-Snapshot"))
		{
			
			try {
				bn.insert(bldno);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				listener.getLogger().println("some issue while insterting data in nightlybuild");
				e1.printStackTrace();
			}
		}
		
		result = build.getResult().toString();

		reason = build.getResult().toExportedObject();
		

		if (mdlname.equals("feature-ecosystemadapter"))
		{
			finalmodulename = "Ecosystem adapter";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("feature-srdmgmt"))
		{
			finalmodulename = "SRDManagement";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-common"))
		{
			finalmodulename = "common";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-EcosystemIntegration"))
		{
			finalmodulename = "EcosystemIntegration";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		else if (mdlname.equals("platform-shell"))
		{
			finalmodulename = "Shell";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		else if (mdlname.equals("platform-eventnormalizer"))
		{
			finalmodulename = "EventNormalizer";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-eventprocessing"))
		{
			finalmodulename = "EventProcessing";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-events"))
		{
			finalmodulename = "Events";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-messagebus"))
		{
			finalmodulename = "MessageBus";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-ruleengine"))
		{
			finalmodulename = "Rule Engine";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-soservice"))
		{
			finalmodulename = "so-service-platform";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-webportal"))
		{
			finalmodulename = "webportal";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-workflow"))
		{
			finalmodulename = "workflow for platform";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-reporting"))
		{
			finalmodulename = "reporting";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-provisioning"))
		{
			finalmodulename = "provisioning";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("platform-securityprovisioning"))
		{
			finalmodulename = "Security Provisioning";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		else if (mdlname.equals("feature-securityprovisioning"))
		{
			finalmodulename = "Security provisioning plugins";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		else if (mdlname.equals("feature-workloadmgmt"))
		{
			finalmodulename = "Workload Services";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("feature-securityprofilemgmt"))
		{
			finalmodulename = "SecurityProfileManagement";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (mdlname.equals("umc-common"))
		{
			finalmodulename = "UMCcommon";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (mdlname.equals("Umc-credentialMgmt"))
		{
			finalmodulename = "CredentialManagement";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (mdlname.equals("umc-HostingPlatformCommon"))
		{
			finalmodulename = "hosting-platform-common";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (mdlname.equals("umc-IdentityMgmt"))
		{
			finalmodulename = "IdentityManagement";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (mdlname.equals("umc-serviceplatform"))
		{
			finalmodulename = "umc-service-platform";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (mdlname.equals("umc-webportal"))
		{
			finalmodulename = "UMCwebportal";
			Statement statement;
			try {
				statement = CIHelperSonar.getInstance().createExecuteStatement();
				ResultSet resultSet = statement.executeQuery("SELECT pm.value value FROM projects p INNER JOIN snapshots "
						+ " s ON p.id = s.project_id "
						+ " INNER JOIN project_measures pm ON pm.snapshot_id = s.id INNER JOIN "
						+ "  metrics m ON m.id = pm.metric_id WHERE p.name='" + finalmodulename + "' AND "
						+ " m.name ='ncloc' and s.islast = '1' and p.language = 'java';"
						);  	

				while (resultSet.next()) {
					System.out.println(resultSet.getString("value"));
					float a = Float.parseFloat(resultSet.getString("value"));
					System.out.println(a);
					finalloc = (int)a;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();


			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else
		{
			listener.getLogger().println("specified module not found");
		}

		/*Moduleloc ml = new Moduleloc();
		int loc = ml.getModuleLocData();
		 */
		BuildInfo biInfo = new BuildInfo();
		listener.getLogger().println("before insert query:" +datetime);
		listener.getLogger().println("the value of project id :" +projectid);


		int buildInfoId = 0;
		int nightlyBuildId = 0;
	//	int projectid = 1;
		
		try {
			listener.getLogger().println("getting nightlybuild id ");
			int a = biInfo.getNightlyBuildId(bldno); 
			listener.getLogger().println("the value of a is  " + a);
			biInfo.insert(bldno, a, mdlname , result, reason, finalloc, projectid);
			buildInfoId = biInfo.getBuildInfoForBuildNumber(bldno,mdlname);
		
			
			
		} catch (SQLException e) {
			listener.getLogger().println(e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			listener.getLogger().println(sw.toString());
			listener.getLogger().println("DB insertion failed. marking build as failed");
			build.setResult(Result.FAILURE);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		listener.getLogger().println("after insert query:" +datetime);

		// code to add code collaborator data
		//creating object to get the bug count
		CodeCollaboratorBugCount bg = new CodeCollaboratorBugCount();

		ChangeLogSet<? extends Entry> a = build.getChangeSet();
		Object[] l;
		l = a.getItems();
		listener.getLogger().println("the length of change log entry is : " + l.length);

		for (int k=0; k< l.length; k++)
		{

			Object obj = l[k];
			try {
				String msg = ((hudson.plugins.perforce.PerforceChangeLogEntry)obj).getMsg();
				String changelist = ((hudson.plugins.perforce.PerforceChangeLogEntry)obj).getCommitId();
				String name = ((hudson.plugins.perforce.PerforceChangeLogEntry)obj).getUser();
				String rev = ((hudson.plugins.perforce.PerforceChangeLogEntry)obj).getRevision();
				changelistno = Integer.parseInt(rev);

				//System.out.println(name + "\n" + changelist + "\n" + rev);

				String[] msgarr = msg.split("\n");

				for(String tt:msgarr)
				{
					//reviewID = -1;
					//		listener.getLogger().println("in for loop : " + tt);
					if (tt.contains("review ID"))

					{
						tt = tt.trim();
						int startidx = tt.indexOf(">>") + 2;
						s2 = tt.substring(startidx);
						//int f = tt.indexOf("<<review ID:>>");
						//int a1 = "<<review ID:>>".length();
						//int a2 = tt.length();
						//		listener.getLogger().println(s2);
						//String ss =tt.substring(f+ a1, tt.length());
						if(s2.length() > 0){
							listener.getLogger().println("in bug count loop");
							reviewID = Integer.parseInt(s2);
							listener.getLogger().println("the review id is " + reviewID);
							bugCount = bg.getBugCount(reviewID);
							hour = bg.getTotaltimeSpentperReviewId(reviewID);
							listener.getLogger().println("the bug count is " + bugCount);
							if (bugCount != 0)
							{
							totalBugcnt.add(bugCount);
							}
							
							//System.out.println(ss);
							//	listener.getLogger().println("in review ID loop");
						}
					}

					else if (tt.contains("Check-in type"))
					{

						//	listener.getLogger().println("in check-in loop ");
						tt = tt.trim();
						//		System.out.println(tt);

						int startidx1 = tt.indexOf(">>") + 2;
						checkintype = tt.substring(startidx1);
						/*	System.out.println(checkintype);
						listener.getLogger().println("before loop");
						listener.getLogger().println(checkintype);*/
						if (checkintype.equals(""))
						{
							checkintype = "null";
							//		listener.getLogger().println("the value os checkintype is : " + checkintype);
							//		listener.getLogger().println("to check in null condition");
						}

						if (checkintype.equals("Doc") && s2.length() == 0)
						{
							listener.getLogger().println("setting review ID to -1");
							reviewID = -1;

						}
						//		listener.getLogger().println("in if loop");
						listener.getLogger().println("the value os checkintype is : " + checkintype);
					}
				}

				numbers.add(reviewID);
				reviewidsize = numbers.size();
				bugCntsize = totalBugcnt.size();
				
				listener.getLogger().println("the total count for review ID is " + reviewidsize);
				listener.getLogger().println("the total bug count is " + bugCntsize);
				
				Collaborator cc = new Collaborator();
				cc.insert(buildInfoId, reviewID, changelistno, name, checkintype,bugCount,hour);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				listener.getLogger().println("exception has occured");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
		}
		
		if (mdlname.equals("SO-E2E-Snapshot"))
		{
			List<Map<String, Object>> data;
			 try {
				data = bn.getAggregatedCodeCollabDataForLatestNightlyBuild(projectid);
				 bn.UpdateNightlyBuildId();
				 int cntfinal = bn.getCount(data);
				 bn.UpdatereviewIdcnt(cntfinal);
				 bn.UpdatereviewTotalBugCnt(bugCntsize);
				 nightlyBuildId = biInfo.getnightlyBuildIdForBuildNumber(bldno,mdlname);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (mdlname.equals("UMC-E2E-Snapshot"))
		{
			List<Map<String, Object>> data;
			 try {
				data = bn.getAggregatedCodeCollabDataForLatestNightlyBuild(projectid);
				 bn.UpdateNightlyBuildId();
				 int cntfinal = bn.getCount(data);
				 bn.UpdatereviewIdcnt(cntfinal);
				 bn.UpdatereviewTotalBugCnt(bugCntsize);
				 nightlyBuildId = biInfo.getnightlyBuildIdForBuildNumber(bldno,mdlname);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		for (Action buildAction : build.getActions()){
			System.out.println("build action" + buildAction);
			if (buildAction instanceof FindBugsResultAction) {
				
				listener.getLogger().println("in findbug loop");
				FindBugsResultAction fb = (FindBugsResultAction)buildAction;
				if ((ccut == 1 ) && (fb.isSuccessful()))
				{
				System.out.println(fb.getResult().getNumberOfFixedWarnings());
				listener.getLogger().println("in findbug loop");
				numfxwarn = fb.getResult().getNumberOfFixedWarnings();

				System.out.println(fb.getResult().getNumberOfHighPriorityWarnings());
				numhghwrn = fb.getResult().getNumberOfHighPriorityWarnings();

				System.out.println(fb.getResult().getNumberOfLowPriorityWarnings());
				numlowarn = fb.getResult().getNumberOfLowPriorityWarnings();

				System.out.println(fb.getResult().getNumberOfNormalPriorityWarnings());
				numnmlwarn = fb.getResult().getNumberOfNormalPriorityWarnings();

				System.out.println(fb.getResult().getNumberOfNewWarnings());
				numnewwarn = fb.getResult().getNumberOfNewWarnings();

				System.out.println(fb.getResult().getNumberOfWarnings());
				totwarn = fb.getResult().getNumberOfWarnings();
				System.out.println(fb.getResult().getNumberOfModules());
				totmdl = fb.getResult().getNumberOfModules();

				System.out.println("build action" + buildAction);
				listener.getLogger().println("in findbug");
				FindBug fbDB = new FindBug();
				BuildInfo bi = new BuildInfo();
				try {
					//int bldidfb = bi.getRecordIdForBuildNumber(buildNumber);
					fbDB.insert(buildInfoId, numhghwrn, numnmlwarn, numlowarn, numnewwarn, totwarn);
				}
				
				catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}else if (buildAction instanceof PmdResultAction && ccut == 1) {

				listener.getLogger().println("in pmd loop");
				PmdResultAction pmd = (PmdResultAction)buildAction;
				int pmdfixwarn = pmd.getResult().getNumberOfFixedWarnings();
				pmdhghwarn = pmd.getResult().getNumberOfHighPriorityWarnings();
				pmdlowwarn = pmd.getResult().getNumberOfLowPriorityWarnings();
				pmdnmlwarn = pmd.getResult().getNumberOfNormalPriorityWarnings();
				pmdnewwarn = pmd.getResult().getNumberOfNewWarnings();
				pmdtotwarn = pmd.getResult().getNumberOfWarnings();

				listener.getLogger().println("in pmd");

				Pmd p = new Pmd();
				BuildInfo bi = new BuildInfo();
				try {
					//int bldidpm = bi.getRecordIdForBuildNumber(buildNumber);
					p.insert(buildInfoId, pmdhghwarn, pmdnmlwarn, pmdlowwarn, pmdnewwarn, pmdtotwarn);
				}

				catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (buildAction instanceof CoberturaBuildAction && ccut == 1) {
				CoberturaBuildAction cba = (CoberturaBuildAction)buildAction;
				//	System.out.println(cba.getResults());
				//	hudson.plugins.cobertura.Ratio m = cba.getResults().get(cba);
				
				
				Map<CoverageMetric,Ratio> map = cba.getResults();
				Iterator<CoverageMetric> iterator = map.keySet().iterator();


				while(iterator.hasNext()){
					CoverageMetric coverageMetric = iterator.next();
					Ratio ratio = map.get(coverageMetric);
					float value = (ratio.numerator/ratio.denominator)*100;
					//	System.out.println(coverageMetric + " = " + value);
					if ("PACKAGES".equals(coverageMetric.toString()))
					{
						packages = value;
					}
					else if ("FILES".equals(coverageMetric.toString()))
					{
						files = value;
					}

					else if ("CLASSES".equals(coverageMetric.toString()))
					{
						classes = value;
					}
					else if ("METHOD".equals(coverageMetric.toString()))
					{
						method = value;
					}
					else if ("LINE".equals(coverageMetric.toString()))
					{
						line = value;
					}
					else if ("CONDITIONAL".equals(coverageMetric.toString()))
					{
						condition = value;
					}

				}
				CodeCoverage cc = new CodeCoverage();
				BuildInfo ccbi = new BuildInfo();
				try {
					//int bldidcc = ccbi.getRecordIdForBuildNumber(buildNumber);
					cc.insert(buildInfoId, packages, files, classes, method, line, condition);
				}

				catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}else if (buildAction instanceof TestNGTestResultBuildAction && ccut == 1) {
				TestNGTestResultBuildAction ta = (TestNGTestResultBuildAction)buildAction;
			//	totCnt = build.getTestResultAction().getTotalCount();
				
				totCnt = ta.getTotalCount();
				skipCnt = ta.getSkipCount();
				failCnt = ta.getFailCount();
				passCnt = (totCnt - skipCnt - failCnt);

				UnitTest ut = new UnitTest();
				BuildInfo utbi = new BuildInfo();
				if (failCnt > 0 && nightlyBuildId > 0)
				{
					
				    try {
						totute2e = utbi.gettotalut(projectid);
						totCnt = totute2e;
						passCnt = (totCnt - skipCnt - failCnt);
						failCnt = (totCnt - skipCnt - passCnt);
						skipCnt = (totCnt - failCnt - passCnt);
						ut.insert(buildInfoId,totCnt,passCnt, failCnt, skipCnt);
					} catch (ClassNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				    
				}
				else
				{
								
				try {
					//int bldid = utbi.getRecordIdForBuildNumber(buildNumber);
					ut.insert(buildInfoId,totCnt,passCnt, failCnt, skipCnt);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				}	

			}
			else if (buildAction instanceof PerforceTagAction)
			{
				PerforceTagAction pta = (PerforceTagAction)buildAction;
				List<PerforceTag> tag = pta.getTags();
				listener.getLogger().println("the perforce label is : " + tag.toString());
				Iterator<PerforceTag> itr = tag.iterator();
				int i = 0;
				while (itr.hasNext()) {
					listener.getLogger().println("the " + i++ + " position value is: " + itr.next().getName());
				}
			}

		}
		/*File input = new File(wksp + "\\" + "Result_Mail.html");
		 Document doc = Jsoup.parse(input, "UTF-8", "");

		 Element id = doc.getElementById("TotalTestcases");
	     Document doc1 = Jsoup.parse(id.html());
	     TotalTCCount = doc1.text();


	     Element id1 = doc.getElementById("PassTestcases");
	     Document doc2 = Jsoup.parse(id1.html());
	     PassTestcases = doc2.text();

	     Element id2 = doc.getElementById("FailTestcases");
	     Document doc3 = Jsoup.parse(id2.html());
	     FailTestcases = doc3.text();*/

		/*if (FailTestcases.equals("0"))
	     {
	    	 batresult = "Pass";
	     }

	     else
	     {
	    	 batresult = "Fail";
	     }*/

		//   getfinalDate();
		//	 getFileCreate(listener);

		//getfileFind();

		//	 String fpath = wksp + "\\" + getfinalDate() +".txt";

		//	 String fpath = "E:\\ci\\2013-11-241.txt";
		//	 System.out.println("fpath:" +fpath);

		//    FindFile f = new FindFile();

		//	 boolean fsrch = f.getFilesrch(fpath);
		// listener.getLogger().println(" :" +fsrch);

		listener.getLogger().println("date is :" +datetime);
		listener.getLogger().println("total count is :" + totCnt);

		listener.getLogger().println("total findbugs count is :" + totwarn);

		//	 listener.getLogger().println("file is" + ":" + wksp + "\\" + mdlname + ".txt");



		// f.getreadFile(fpath);

		// BuildResultParser bp = new BuildResultParser();
		// BuildResultBean b = bp.getreadFile(wksp + "\\" + mdlname + ".txt");

		CalculateCodeComplexity cc = new CalculateCodeComplexity();
		Map <String, Float> hm = new HashMap<String, Float>();
		hm = cc.getData(listener.getLogger());

		CodeComplexity ccx = new CodeComplexity();
		listener.getLogger().println("code conmplexity data is being inserted");

		for (Map.Entry<String, Float> entry : hm.entrySet()) {
			listener.getLogger().println(entry.getKey() + ": " + entry.getValue());
			if (entry.getKey().equals("Ecosystem adapter") && mdlname.equals("feature-ecosystemadapter"))
			{
				try {
					
					listener.getLogger().println("code conmplexity data is being inserted ");

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("Rule Engine") && mdlname.equals("platform-ruleengine"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("Security Provisioning") && mdlname.equals("feature-securityprovisioning"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("SRDManagement") && mdlname.equals("feature-srdmgmt"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("EventNormalizer") && mdlname.equals("platform-eventnormalizer"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (entry.getKey().equals("Shell") && mdlname.equals("platform-shell"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("EcosystemIntegration") && mdlname.equals("platform-EcosystemIntegration"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("EventProcessing") && mdlname.equals("platform-eventprocessing"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("Events") && mdlname.equals("platform-events"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("SecurityProfileManagement") && mdlname.equals("feature-securityprofilemgmt"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("reporting") && mdlname.equals("platform-reporting"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("Workload Services") && mdlname.equals("feature-workloadmgmt"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("provisioning") && mdlname.equals("platform-provisioning"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("MessageBus") && mdlname.equals("platform-messagebus"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			else if (entry.getKey().equals("common") && mdlname.equals("platform-common"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("webportal") && mdlname.equals("platform-webportal"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("so-service-platform") && mdlname.equals("platform-soservice"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("workflow for platform") && mdlname.equals("platform-workflow"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (entry.getKey().equals("Security Provisioning") && mdlname.equals("platform-securityprovisioning"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (entry.getKey().equals("UMCcommon") && mdlname.equals("umc-common"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (entry.getKey().equals("CredentialManagement") && mdlname.equals("Umc-credentialMgmt"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (entry.getKey().equals("hosting-platform-common") && mdlname.equals("umc-HostingPlatformCommon"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (entry.getKey().equals("IdentityManagement") && mdlname.equals("umc-IdentityMgmt"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (entry.getKey().equals("umc-service-platform") && mdlname.equals("umc-serviceplatform"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (entry.getKey().equals("UMCwebportal") && mdlname.equals("umc-webportal"))
			{
				try {

					ccx.insert(buildInfoId,entry.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		// update the LOC
		
		if (nightlyBuildId > 0)
		{
		
			listener.getLogger().println("nightlybuild is more than 0");
			
		BuildInfoNightly bin = new BuildInfoNightly();
		try {
			int nbid = bin.getMaxnbid(projectid);
			int loc = bin.getaggregateLOCforLatestNightlyBuild(projectid);
		    bin.UpdateLocForLatestNightlyBuild(projectid,nbid,loc);	
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		}	
		
		
		}
		
		
	      return true;
		
	}


	/*public String getfinalDate() throws FileNotFoundException, UnsupportedEncodingException
	 {
		 reportDate = df.format(datetime);
		 return reportDate;

	 }

	 public void getFileCreate(BuildListener listener) throws FileNotFoundException

	 {
		 PrintWriter writer = new PrintWriter(wksp + "\\" + reportDate.toString() +".txt");

		 writer.println(mdlname);
		 writer.println(bldno);
		 writer.println(result);
		 writer.println("[" + failCnt + "," + passCnt + "," + totCnt + "]");
		 writer.println(failCnt);
		 writer.println(passCnt);
		 writer.println(totCnt);

		 writer.println(packages);
		 writer.println(files);
		 writer.println(classes);
		 writer.println(method);
		 writer.println(line);
		 writer.println(condition);

		 writer.println(numfxwarn);
			writer.println(numhghwrn);
			writer.println(numlowarn);
			writer.println(numnmlwarn);
			writer.println(numnewwarn);
			writer.println(totwarn);
			writer.println(totmdl);

		 writer.close();

PrintWriter writer1 = new PrintWriter(wksp + "\\" + mdlname + ".txt");
listener.getLogger().println("file is" + ":" + wksp + "\\" + mdlname + ".txt");

		writer1.println(mdlname);
		writer1.println(bldno);
		writer1.println(result);
	//	writer1.println("[" + failCnt + "," + passCnt + "," + totCnt + "]");
		writer1.println(failCnt);
		writer1.println(passCnt);
		writer1.println(totCnt);

		writer1.println(packages);
		writer1.println(files);
		writer1.println(classes);
		writer1.println(method);
		writer1.println(line);
		writer1.println(condition);

		writer1.println(numfxwarn);
		writer1.println(numhghwrn);
		writer1.println(numlowarn);
		writer1.println(numnmlwarn);
		writer1.println(numnewwarn);
		writer1.println(totwarn);
		writer1.println(totmdl);

		writer1.println(TotalTCCount);
		writer1.println(PassTestcases);
		writer1.println(FailTestcases);
		writer1.println(batresult);

		writer1.close();

	 }
	 */

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		public DescriptorImpl() {

		}

		@Override
		public String getDisplayName() {
			return "Gather results";
		}
		@Override
		public Record newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			return req.bindJSON(Record.class,formData);
		}

		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

	}
}



