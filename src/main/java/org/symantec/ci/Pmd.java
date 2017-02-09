package org.symantec.ci;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class Pmd {
	

	public boolean insert(int buildintoId,int highwarnings,int normalwarnings,int lowwarnings, int newwarnings, int totalwarnings ) throws SQLException, ClassNotFoundException{
		PreparedStatement prepStatement = CIHelper.getInstance()
				.createInsertStatement("insert into pmd(buildinfo_id,highwarnings,normalwarnings,lowwarnings,newwarnings,totalwarnings) values(?,?,?,?,?,?);");
		
		prepStatement.setInt(1, buildintoId);
		prepStatement.setInt(2, highwarnings);
		prepStatement.setInt(3, normalwarnings);
		prepStatement.setInt(4, lowwarnings);
		prepStatement.setInt(5, newwarnings);
		prepStatement.setInt(6, totalwarnings);
		
		prepStatement.executeUpdate();
		
		return true;
	}
	
	public List<Map<String, Object>> getPMDDataForLatestBuildId() throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select p.highwarnings,"
		          		+ "p.normalwarnings,"
		          		+ "p.lowwarnings,"
		          		+ "p.newwarnings,"
		          		+ "p.totalwarnings,"
		          		+ "bi.id,"
		          		+ "bi.buildid "
		          		+ "from pmd p, buildinfo bi "
		          		+ "where bi.id = p.buildinfo_id "
		          		+ "order by datetime desc "
		          		+ "limit 1;");
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
	
	public List<Map<String, Object>> getPMDForBuildId(int buildId) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		
		ResultSet resultSet = statement
		          .executeQuery("select p.highwarnings,"
			          		+ "p.normalwarnings,"
			          		+ "p.lowwarnings,"
			          		+ "p.newwarnings,"
			          		+ "p.totalwarnings,"
			          		+ "bi.id,"
			          		+ "bi.buildid "
			          		+ "from pmd p, buildinfo bi "
			          		+ "where bi.id = p.buildinfo_id "
			          		+ "and bi.id = "+ buildId );
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
	
	public List<Map<String, Object>> getPMDForNightlyBuildId(int nightlybuildId) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select p.highwarnings,"
			          		+ "p.normalwarnings,"
			          		+ "p.lowwarnings,"
			          		+ "p.newwarnings,"
			          		+ "p.totalwarnings,"
			          		+ "bi.id,"
			          		+ "bi.buildid "
			          		+ "from pmd p, buildinfo bi, nightlybuild nb "
			          		+ "where nb.id = bi.nightlybuild_id "
			          		+ "and bi.id = p.buildinfo_id "
			          		+ "and nb.id = "+ nightlybuildId );
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
}
