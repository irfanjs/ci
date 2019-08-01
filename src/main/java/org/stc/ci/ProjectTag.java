package org.stc.ci;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class ProjectTag {
	
	
	public ProjectTag() {
		// TODO Auto-generated constructor stub
	}

	public List<Map<String, Object>> getProjectId(String view) throws SQLException, ClassNotFoundException{
		Statement statement = CIHelper.getInstance().createExecuteStatement();
		ResultSet resultSet = statement
		          .executeQuery("select id from projects where tag = '" + view + "'");
		
		return CIHelper.getInstance().getEntitiesFromResultSet(resultSet);
	}
}
