package org.stc.ci;

import java.sql.SQLException;

public class Check {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Check c = new Check();
		c.checking();
	
	}
	
	public String checking()
	{
		BuildInfoNightly bin = new BuildInfoNightly();
		
		
		try {
			int projectid = 1;
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
		return null;
		
	}

}
