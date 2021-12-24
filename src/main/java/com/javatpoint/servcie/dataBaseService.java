package com.javatpoint.servcie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class dataBaseService {
	static String database_url;
	static String database_username;
	static String database_password;
	
	@Value("${spring.datasource.url}")
	public void setDatabaseUrl(String url) {
		database_url = url;
    }
	
	@Value("${spring.datasource.username}")
	public void setDatabaseUsername(String username) {
		database_username = username;
    }
	
	@Value("${spring.datasource.password}")
	public void setDatabasePassword(String password) {
		database_password = password;
    }
	
	private static Connection connection = null;
	

public static void dataBaseConnection() throws ClassNotFoundException, SQLException {
   
    
   
    if(connection==null) {
		synchronized (dataBaseService.class) 
	      { 
	        if(connection==null) 
	        { 
	        	Class.forName("org.postgresql.Driver"); 
	            Connection con=DriverManager.getConnection(database_url, database_username, database_password); 
	            
	            connection = con;

	        } 
	        
	      } 
    }
    //Statement statement = con.createStatement();
	//muneesh 
	//return statement;   
}
public void executeBatchInsertion(List<String> st) throws ClassNotFoundException, SQLException{
    
	if(connection==null) {
		dataBaseConnection();
	}
		
    Statement stm = connection.createStatement();
    for(String query:st) {
        stm.addBatch(query);
    }
    try {
        stm.executeBatch();
    }catch(SQLException e) {
        throw e;
    }finally {
        stm.close();
    }  
}
public JSONArray getDataList(String qurey) throws ClassNotFoundException, SQLException, JSONException{
    
	if(connection==null) {
		dataBaseConnection();
	}
	
    Statement selectStmt = null;
    //muneesh 
   // Statement stm = connection.createStatement();
    selectStmt = connection.createStatement();
    ResultSet rs = selectStmt.executeQuery(qurey);
    ResultSetMetaData rm = rs.getMetaData();
	int totalColumns = rm.getColumnCount();
	ArrayList<String> columns = new ArrayList<String>();
	
	for(int i=1;i<=totalColumns;i++ )
	{
		columns.add(rm.getColumnName(i));
	}
    JSONArray arr = new JSONArray();
    while(rs.next())
	{				
		
		
		
		JSONObject jb = new JSONObject();
		for(String cname:columns)
		{
			if(rs.getString(cname) == null)
			{
				jb.put(cname, "");
			}
			else
			{
				jb.put(cname, rs.getString(cname));
			}
			
		}
		arr.put(jb);
	}	
    try {
    	selectStmt.executeBatch();
    }catch(SQLException e) {
        throw e;
    }finally {
    	selectStmt.close();
    }
    return arr;
}
}
