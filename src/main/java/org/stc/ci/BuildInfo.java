package org.stc.ci;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildInfo {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfo.class);
	
	
	public boolean insert(int buildNumber,int nightlyBuild, String moduleName,String result, String reason, int loc,int projectid) throws SQLException, ClassNotFoundException{
		LOGGER.debug("Build Info data inserting");
		PreparedStatement prepStatement = CIHelper.getInstance()
				.createInsertStatement("insert into buildinfo(nightlybuild_id,buildnumber,modulename,result,reason,datetime,loc,project_id) values(?,?,?,?,?,?,?,?);");
		
		if(nightlyBuild > 0){
			prepStatement.setInt(1, nightlyBuild);
		}
		else{
			prepStatement.setNull(1,Types.INTEGER);
		}
		prepStatement.setInt(2, buildNumber);
		prepStatement.setString(3, moduleName);
		prepStatement.setString(4, result);
		prepStatement.setString(5, reason);
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		prepStatement.setTimestamp(6, new java.sql.Timestamp(cal.getTimeInMillis()));
		prepStatement.setInt(7, loc );
		prepStatement.setInt(8, projectid );
		
	//	Record rd = new Record();
	//	Date dt = rd.getDate();
		//prepStatement.setTimestamp(6, dt);
		
	//	prepStatement.setDate(6, (java.sql.Date) dt);
	//	prepStatement.setTimestamp(6, new java.sql.Timestamp(rd.getDate());
		prepStatement.executeUpdate();
		LOGGER.debug("Build Info insert complete");
		return true;
	}
	
	public int getRecordIdForBuildNumber(int buildNumber) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select id from buildinfo where buildnumber =" + buildNumber);
		
		if(resultSet.next()){
			return resultSet.getInt("id");			
		}
		
		return -1;
	}
	
	public int getBuildInfoForBuildNumber(int buildNumber, String mdlname) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select id,nightlybuild_id,result,reason,datetime from buildinfo where buildnumber="+ buildNumber + " and modulename='" + mdlname + "'" );
		
		if(resultSet.next()){
			return resultSet.getInt("id");			
		}
		return -1;
	}
	
	public int getnightlyBuildIdForBuildNumber(int buildNumber, String mdlname) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select id,nightlybuild_id,result,reason,datetime from buildinfo where buildnumber = "+ buildNumber + " and modulename='" + mdlname + "'" + ";" );
		
		if(resultSet.next()){
			return resultSet.getInt("nightlybuild_id");			
		}
		return -1;
	}
	
	public int gettotalut(int projid) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select sum(total) total from (select subbi.modulename,bi.datetime,subbi.id,subut.total,subut.pass,subut.fail, subut.skip from (select modulename, max(id) id from  buildinfo  subbi  where project_id = " + projid + " and nightlybuild_id is NULL and result='SUCCESS' group by modulename) subbi INNER JOIN buildinfo bi ON subbi.id = bi.id LEFT JOIN unittest subut ON subbi.id = subut.buildinfo_id)s;");
		
		if(resultSet.next()){
			return resultSet.getInt("total");			
		}
		return -1;
		
	}
	
	
	public int getNightlyBuildId(int buildNumber) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet =  statement.executeQuery("select id from (select id, status from nightlybuild order by datetime desc limit 1) subNB where subNB.status = 0");
		
		if(resultSet.next()){
			return resultSet.getInt("id");			
		}
		return -1;
	}
}

