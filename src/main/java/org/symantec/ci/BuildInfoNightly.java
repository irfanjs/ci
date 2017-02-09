package org.symantec.ci;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class BuildInfoNightly {
	
	int cnt;
	public boolean insert(int buildNumber) throws SQLException, ClassNotFoundException{
		//LOGGER.debug("Build Info nightly data inserting");
		PreparedStatement prepStatement = CIHelper.getInstance()
				.createInsertStatement("insert into nightlybuild(buildnumber,datetime) values(?,?);");
		
		prepStatement.setInt(1, buildNumber);
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		prepStatement.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));
		prepStatement.executeUpdate();
		return true;
	}
	
	public void UpdateNightlyBuildId() throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
//		ResultSet resultSet =  statement.executeQuery("select id,nightlybuild_id, modulename, result, reason, datetime from buildinfo where buildnumber =" + buildNumber);
		statement.execute("update nightlybuild set status = 1 order by datetime desc limit 1;");
		
		/*if(resultSet.next()){
			return resultSet.getInt("id");			
		}*/
		
	}
	
	public void UpdatereviewIdcnt(int a) throws SQLException, IOException, ClassNotFoundException {
		
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		//int cntfinal = bn.getCount();
		
//		ResultSet resultSet =  statement.executeQuery("select id,nightlybuild_id, modulename, result, reason, datetime from buildinfo where buildnumber =" + buildNumber);
		statement.execute("update nightlybuild set reviewidcount = " + a + " order by datetime desc limit 1;");
		
		/*if(resultSet.next()){
			return resultSet.getInt("id");			
		}*/
	
		
	}
	
public void UpdatereviewTotalBugCnt(int b) throws SQLException, IOException, ClassNotFoundException {
		
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		//int cntfinal = bn.getCount();
		
//		ResultSet resultSet =  statement.executeQuery("select id,nightlybuild_id, modulename, result, reason, datetime from buildinfo where buildnumber =" + buildNumber);
		statement.execute("update nightlybuild set reviewbugcount = " + b + " order by datetime desc limit 1;");
		
		/*if(resultSet.next()){
			return resultSet.getInt("id");			
		}*/
	
		
	}
	
	
	
public List<Map<String, Object>> getAggregatedCodeCollabDataForLatestNightlyBuild(int proj) throws SQLException, ClassNotFoundException{
		
		
	   String sql = "select count(*) reviewidcount from (select distinct reviewid from buildinfo bi INNER JOIN " 
				+ "codecollaborator cc on bi.id = cc.buildinfo_id where datetime > (select bi.datetime from buildinfo bi inner join nightlybuild " 
				+ "nt on nt.id = bi.nightlybuild_id where bi.datetime < (select datetime from nightlybuild order by datetime "
				+ "desc limit 1) and project_id = " + proj + " order by datetime desc limit 1) and datetime <= ( select bi.datetime from buildinfo bi inner join nightlybuild "
				+ "on nightlybuild.id = bi.nightlybuild_id order by bi.datetime desc limit 1) and project_id = " + proj + ") s;";
		
		return executeQuery(sql);
		}


public List<Map<String, Object>> executeQuery(String sql)
		throws SQLException {
	Connection conn = null;
	Statement statement = null;
	ResultSet resultSet = null;

	try {
		conn = CIHelper.getInstance().getConnection();
		statement = conn.createStatement();
		resultSet = statement.executeQuery(sql);

		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}

	finally {
		CIHelper.close(conn, statement, resultSet);
	}

}
	 
	public Integer getCount(List<Map<String, Object>> b) throws IOException, ClassNotFoundException, SQLException{
		
		//data = bn.getAggregatedCodeCollabDataForLatestNightlyBuild();
		 List<Integer> arrayList = new ArrayList<Integer>();
		 
		
		for (Map<String, Object> data1 : b) {
		    for (Map.Entry<String, Object> entry : data1.entrySet()) {
		       System.out.println(entry.getKey() + ": " + entry.getValue());
		       
		       if (entry.getKey().equals("reviewidcount"))
		       {
		    	   arrayList.add(Integer.parseInt(entry.getValue().toString()));
		    	   cnt = Integer.parseInt(entry.getValue().toString());
		    	   
		       }
		    }
		}
	return cnt;	
	}
	
	public Integer getMaxnbid(int projid) throws ClassNotFoundException, SQLException
	{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select max(nightlybuild_id) nightlybuildid from buildinfo where project_id = " + projid + ";" );
		
		if(resultSet.next()){
			return resultSet.getInt("nightlybuildid");			
		}
		return -1;
	}
	
	public Integer getaggregateLOCforLatestNightlyBuild(int projid) throws ClassNotFoundException, SQLException
	{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select  sum(LOC) LOC  from buildinfo bi inner join (select max(datetime) datetime from buildinfo where project_id = " + projid + " and result = 'SUCCESS' and nightlybuild_id is NULL group by modulename order by datetime) tempmodmaxdate on bi.datetime = tempmodmaxdate.datetime;");
		
		if(resultSet.next()){
			return resultSet.getInt("LOC");		
		}
		return -1;
	}
	
	public Integer getLastRecordFromBuildinfo(int bldno, String mdlname) throws ClassNotFoundException, SQLException
	{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select id from buildinfo where buildnumber = " + bldno + " and modulename = '" + mdlname + "';");
		
		if(resultSet.next()){
			return resultSet.getInt("id");		
		}
		return -1;
	}
	
	public Integer UpdateLocForLatestNightlyBuild(int projid,int nightlybuildid,int loc) throws SQLException, ClassNotFoundException
	{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		int resultSet =  statement.executeUpdate("update buildinfo set LOC = " + loc + " where nightlybuild_id = " + nightlybuildid + " and project_id = " + projid + ";");
		return resultSet;
		
		/*if(resultSet.next()){
			return resultSet.getInt("LOC");			
		}
		return -1;*/
		
		
	}
	
	public int UpdateReasonForLastRecord(String reason, int id) throws SQLException, ClassNotFoundException
	{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		int resultSet =  statement.executeUpdate("update buildinfo set reason = '" + reason + "'" + " where id = " + id + ";");
		return resultSet;
		
		/*if(resultSet.next()){
			return resultSet.getInt("LOC");			
		}
		return -1;*/
		
		
	}
	
}


