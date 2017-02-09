package org.symantec.ci;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class CodeCoverage {
	
	public boolean insert(int buildintoId,
			float packages,
			float files,
			float classes,
			float methods,
			float linesofcode,
			float conditions) throws SQLException, ClassNotFoundException{
		PreparedStatement prepStatement = CIHelper.getInstance()
				.createInsertStatement("insert into codecoverage(buildinfo_id,packages,files,classes,methods,linesofcode,conditions) values(?,?,?,?,?,?,?);");
		
		prepStatement.setInt(1, buildintoId);
		prepStatement.setFloat(2, packages);
		prepStatement.setFloat(3, files);
		prepStatement.setFloat(4, classes);
		prepStatement.setFloat(5, methods);
		prepStatement.setFloat(6, linesofcode);
		prepStatement.setFloat(7, conditions);
		
	//	prepStatement.setInt(2, packages);
		/*prepStatement.setInt(3, files);
		prepStatement.setInt(4, classes);
		prepStatement.setInt(5, methods);
		prepStatement.setInt(6, linesofcode);
		prepStatement.setInt(7, conditions);*/
		
		prepStatement.executeUpdate();
		
		return true;
	}
	
	
	public List<Map<String, Object>> getCodeCoverageDataForLatestBuildId() throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select cc.packages,"
		          		+ "cc.files,"
		          		+ "cc.classes,"
		          		+ "cc.methods,"
		          		+ "cc.linesofcode,"
		          		+ "cc.conditions,"
		          		+ "bi.id,"
		          		+ "bi.buildid "
		          		+ "from codecoverage cc, buildinfo bi "
		          		+ "where bi.id = cc.buildinfo_id "
		          		+ "order by datetime desc "
		          		+ "limit 1;");
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
	
	public List<Map<String, Object>> getCodeCoverageForBuildId(int buildId) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select cc.packages,"
		          		+ "cc.files,"
		          		+ "cc.classes,"
		          		+ "cc.methods,"
		          		+ "cc.linesofcode,"
		          		+ "cc.conditions,"
		          		+ "bi.id,"
		          		+ "bi.buildid "
		          		+ "from codecoverage cc, buildinfo bi "
			          	+ "where bi.id = cc.buildinfo_id "
			          	+ "and bi.id = "+ buildId );
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}	
	
	public List<Map<String, Object>> getCodeCoverageDataForNightlyBuildId(int nightlybuildId) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select cc.packages,"
		          		+ "cc.files,"
		          		+ "cc.classes,"
		          		+ "cc.methods,"
		          		+ "cc.linesofcode,"
		          		+ "cc.conditions,"
		          		+ "bi.id,"
		          		+ "bi.buildid "
		          		+ "from codecoverage cc, buildinfo bi, nightlybuild nb "
		          		+ "where nb.id = bi.nightlybuild_id "
		          		+ "and bi.id = cc.buildinfo_id "
		          		+ "and nb.id = "+ nightlybuildId );
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
}
