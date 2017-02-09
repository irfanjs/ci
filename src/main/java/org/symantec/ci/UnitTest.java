package org.symantec.ci;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnitTest {
	
	

	private static final Logger LOGGER = LoggerFactory.getLogger(UnitTest.class);
	public boolean insert(int buildintoId,int total,int passed,int failed, int skiped) throws SQLException, ClassNotFoundException{
		LOGGER.debug("unit test data inserting");
		PreparedStatement prepStatement = CIHelper.getInstance()
				.createInsertStatement("insert into unittest(buildinfo_id,total,pass,fail,skip) values(?,?,?,?,?);");
		
		prepStatement.setInt(1, buildintoId);
		prepStatement.setInt(2, total);
		prepStatement.setInt(3, passed);
		prepStatement.setInt(4, failed);
		prepStatement.setInt(5, skiped);
		
		
		prepStatement.executeUpdate();
		LOGGER.debug("unit test insert complete");
		
		return true;
	}
	
	public List<Map<String, Object>> getUnitTestDataForLatestBuildId() throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select ut.total,"
		          		+ "ut.pass,"
		          		+ "ut.fail,"
		          		+ "ut.skip,"
		          		+ "bi.id,"
		          		+ "bi.buildid "
		          		+ "from unittest ut, buildinfo bi "
		          		+ "where bi.id = ut.buildinfo_id "
		          		+ "order by datetime desc "
		          		+ "limit 1;");
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
	
	public List<Map<String, Object>> getUnitTestForBuildId(int buildId) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select ut.total,"
		          		+ "ut.pass,"
		          		+ "ut.fail,"
		          		+ "ut.skip,"
		          		+ "bi.id,"
		          		+ "bi.buildid "
		          		+ "from unittest ut, buildinfo bi "
			          	+ "where bi.id = ut.buildinfo_id "
			          	+ "and bi.id = "+ buildId );
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}	
	
	public List<Map<String, Object>> getUnitTestDataForNightlyBuildId(int nightlybuildId) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select ut.total,"
		          		+ "ut.pass,"
		          		+ "ut.fail,"
		          		+ "ut.skip,"
		          		+ "bi.id,"
		          		+ "bi.buildid "
		          		+ "from unittest ut, buildinfo bi, nightlybuild nb "
		          		+ "where nb.id = bi.nightlybuild_id "
		          		+ "and bi.id = ut.buildinfo_id "
		          		+ "and nb.id = "+ nightlybuildId);
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
	
	public List<Map<String, Object>> getAggregatedUnitTestDataForNightlyBuildId(int nightlybuildId) throws SQLException, ClassNotFoundException{
		
		LOGGER.debug("called getAggregatedUnitTestDataForNightlyBuildId");
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select nb.id,"
		          		+ "sum(ut.total) total,"
		          		+ "sum(ut.pass) pass,"
		          		+ "sum(ut.fail) fail "
		          		+ "sum(ut.skip) skip "
		          		+ "from unittest ut, buildinfo bi, nightlybuild nb "
		          		+ "where nb.id = bi.nightlybuild_id "
		          		+ "and bi.id = ut.buildinfo_id "
		          		+ "and nb.id = "+ nightlybuildId
		          		+ " group by nb.id;");
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
	
	
}
