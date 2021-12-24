package com.javatpoint.scheduler;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.javatpoint.servcie.ServiceDB;

public class schedulerConfig {
	
@Autowired
private ServiceDB sdb;
	
	
	
	@Scheduled(fixedDelay =1000)
	  public void run() throws NumberFormatException, ClassNotFoundException, IOException, SQLException, JSONException{
		if(sdb.mobile_sanitizer) {
			sdb.createAndUpdateMobileData();
		}
		//System.out.println("");
	}
	}


