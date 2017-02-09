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

public class CodeComplexity {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(CodeComplexity.class);
	public boolean insert(int buildintoId, float function) throws SQLException, ClassNotFoundException{
		LOGGER.debug("code complexity data inserting");
		PreparedStatement prepStatement = CIHelper.getInstance()
				.createInsertStatement("insert into codecomplexity(buildinfo_id,function) values(?,?);");
		
		prepStatement.setInt(1, buildintoId);
		prepStatement.setFloat(2, function);
		
		prepStatement.executeUpdate();
		LOGGER.debug("code complexity insert complete");
		
		return true;
	}
	
}
