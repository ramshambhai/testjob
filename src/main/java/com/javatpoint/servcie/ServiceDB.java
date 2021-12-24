package com.javatpoint.servcie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceDB {
	
@Autowired
private dataBaseService db;
public static boolean mobile_sanitizer=false;
public static String nib_ip="";
private static 	final Logger logger = LoggerFactory.getLogger(ServiceDB.class);
	public String readFile(String fname) throws Exception
    {
		StringBuilder resultStringBuilder = new StringBuilder();
        File file = new File(fname);
        //File file = new File("D:\\a.txt");
        logger.debug("URL HIT! readFile");
		
        BufferedReader br= new BufferedReader(new FileReader(file));
        String st;
        String imsi="123344567777";
        String imei="123456789";
        String simsi="";
        String simei="";
        String dimsi="";
        String dimei="";
        boolean sfound =false;
        boolean dfound =false;
        String query="";
        String query1="";
        String apiip="";
    	JSONArray ja = new JSONArray();
    	JSONArray ja1 = new JSONArray();
    	JSONArray ja3 = new JSONArray();
    
    	
    	
    	
    	JSONArray api = new JSONArray();
    	JSONArray temp = new JSONArray();
    	 JSONArray apiData=new JSONArray();
    	 String result="";
    
    	HashMap<String , String >hs =new HashMap<String, String>();
    	int count=0;
    	String nibquery="select id from nib where nib_ip='"+nib_ip+"'";
    	JSONArray nibId= db.getDataList(nibquery);
        while ((st = br.readLine()) != null) {
        	resultStringBuilder.append(st).append("\n");
        	
        	
        	JSONObject jo =new JSONObject();
        	long endtime=0;
        	long duration=0;
        	String Time=st.split("\\s+")[0];
        	long time =Long.parseLong(Time.substring(0, 10));
        	String sourceIp=st.split("\\s+")[1];
        	String destinationIp=st.split("\\s+")[2];
        	String sourceDestinationPort=st.split("\\s+")[3];
        	String sourcePort="";
        	String destinationPort="";
        	String protocol=st.split("\\s+")[4];
        	String url="";
        	if(st.split("\\s+").length>5)
        		url=st.split("\\s+")[5];
        	
        	if(sourceDestinationPort.contains(",")) {
	        	sourcePort=sourceDestinationPort.split(",")[0];
	        	destinationPort=sourceDestinationPort.split(",")[1];
        	}
        	String strtocheck=sourceIp+"@"+destinationIp+"@"+sourcePort+"@"+destinationPort ;
        	String revstrtocheck=destinationIp+"@"+sourceIp+"@"+destinationPort+"@"+sourcePort;
            String value=null;
        	//
            if(!hs.isEmpty()) {
	        	String checkexists=hs.get(strtocheck);
	        	String checkexists1=hs.get(revstrtocheck);
	        	if (checkexists!=null)
	        	{
	        		String [] chk = checkexists.split("@");
	        		endtime=Long.parseLong(hs.get(strtocheck).split("@")[4]);
	        		duration=time-endtime;
	        		value=chk[0]+"@"+chk[1]+"@"+chk[2]+"@"+chk[3]+"@"+chk[4]+"@"+time+"@"+duration+"@"+protocol+"@"+url;
	        		hs.put(strtocheck, value);
	        		
	        	}
	        	else if (checkexists1!=null)
	        	{
	        		String [] chk = checkexists1.split("@");
	        		endtime=Long.parseLong(hs.get(revstrtocheck).split("@")[4]);
        			duration=time-endtime;
        			value=chk[0]+"@"+chk[1]+"@"+chk[2]+"@"+chk[3]+"@"+chk[4]+"@"+time+"@"+duration+"@"+protocol+"@"+url;
        			hs.put(revstrtocheck, value);
	        	}
	        	else {
	        		hs.put(strtocheck, strtocheck+"@"+time+"@"+endtime+"@"+duration+"@"+protocol+"@"+url);
	        	}
            }
            else
        	{
        		hs.put(strtocheck, strtocheck+"@"+time+"@"+endtime+"@"+duration+"@"+protocol+"@"+url);
        	}
        	//hs.add(sourceIp+"@"+destinationIp+"@"+sourcePort+"@"+destinationPort+"@"+time);
        	
        }
       //for(int i=0;i<hs.size();i++) {
    	    //List<String> list = new ArrayList<String>(hs);
        for(Map.Entry m : hs.entrySet()){  
        	String list=m.getValue().toString();
    	    String s_ip=list.split("@")[0];
    	    String d_ip=list.split("@")[1];
    	    String mobile_ip="";
    	    String s_port=list.split("@")[2];
    	    String d_port=list.split("@")[3];
    		JSONObject insertJson=new JSONObject();
    		JSONObject insertJson1=new JSONObject();
    		JSONObject insertJson2=new JSONObject();
    		JSONArray verifiedja = new JSONArray();
    		JSONArray sja = new JSONArray();
        	JSONArray dja = new JSONArray();
        	JSONArray ja2 = new JSONArray();
        	
    	    long dateStr = Long.parseLong(list.split("@")[4]);
    	    long dateend = Long.parseLong(list.split("@")[5]);
    	    int duration = Integer.parseInt(list.split("@")[6]);
    	    String protocol=list.split("@")[7];
    	    String url_qry_name="";
    	    if(list.split("@").length>8)
    	    	url_qry_name =list.split("@")[8];
    	    
    	    sfound=false;
    	    dfound=false;
    	    final DateTimeFormatter formatter = 
        		    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        		
        		final String date1 = Instant.ofEpochSecond(dateStr)
        		        .atZone(ZoneId.of("Asia/Kolkata"))
        		        .format(formatter);
        		final String enddate = Instant.ofEpochSecond(dateend)
        		        .atZone(ZoneId.of("Asia/Kolkata"))
        		        .format(formatter);
        		 logger.debug("URL HIT! date"+date1);
    	    if(s_ip.contains(",")) {
    	    	s_ip=s_ip.split(",")[0];
    	    }
    	    if(d_ip.contains(",")) {
    	    	d_ip=d_ip.split(",")[0];
    	    }
    	    
	    	query="select distinct imsi,imei from mobile_registered where assigned_ip = '"+s_ip+"'";
	    	ja = db.getDataList(query);
	    	if(ja.length()>0) {
	    		 simsi=ja.getJSONObject(0).getString("imsi");
	    		 simei=ja.getJSONObject(0).getString("imei");
	    		 mobile_ip=s_ip;
	    		 sfound=true;
	    		 insertJson.put("status", "known");
	    		 insertJson.put("color", "green");
				 insertJson.put("country", "India");
				 insertJson.put("sourceIp", s_ip);
				 insertJson.put("destinationIp", d_ip);
				 insertJson.put("sourcePort",s_port );
				 insertJson.put("destinationPort",d_port );
				 insertJson.put("timestamp",date1 );
				
				 insertJson.put("endTime",enddate );
				 insertJson.put("duration",duration );
				 insertJson.put("mobile_ip",mobile_ip );
				 if(protocol.equalsIgnoreCase("17"))
					 insertJson.put("protocol","UDP" );
				 else
					 insertJson.put("protocol","TCP" );
				 insertJson.put("url",url_qry_name );
				 apiip=s_ip;
				 sja.put(insertJson);
				 
	 	    }
	    	//else {
	    		query="select distinct imsi,imei from mobile_registered where assigned_ip = '"+d_ip+"'";
	    		ja1 = db.getDataList(query);
	    		//if(ja3.length()>0) {
		    		if(ja1.length()>0) {
			    		dimsi=ja1.getJSONObject(0).getString("imsi");
			    		dimei=ja1.getJSONObject(0).getString("imei");
			    		mobile_ip=d_ip;
			    		dfound=true;
			    		insertJson2.put("status", "known");
						insertJson2.put("country", "India");
						insertJson2.put("color", "green");
						insertJson2.put("sourceIp", s_ip);
						insertJson2.put("destinationIp", d_ip);
						insertJson2.put("sourcePort",s_port );
						insertJson2.put("destinationPort",d_port );
						insertJson2.put("timestamp",date1 );
						
						insertJson2.put("duration",duration );
						insertJson2.put("mobile_ip",mobile_ip );
						if(protocol.equalsIgnoreCase("17"))
							 insertJson2.put("protocol","UDP" );
						 else
							 insertJson2.put("protocol","TCP" );
						 insertJson2.put("url",url_qry_name );
						apiip=d_ip;
						dja.put(insertJson2);
		 	    }
	    		//}
	    	//}
		    
	    	if(!sfound && !dfound) {
	    		continue;
	    	}
	    	//get data from ip_info 
	    	if(!sfound) {
	    		query="select * from ip_info where ip_address = '"+s_ip+"'";
		    	 ja2 = db.getDataList(query);
		    	 apiip=s_ip;
		    	
	    	}
	    	else if(!dfound) {
	    		query="select * from ip_info where ip_address = '"+d_ip+"'";
		    	 ja2 = db.getDataList(query);
		    	 apiip=d_ip;
	    	}
	    	
	    	if(ja2.length()>0) {
	    		String data =ja2.getJSONObject(0).getString("ip_info");
	    		JSONObject jo=new JSONObject(data);
				String apiid = jo.getString("apiid");
	    		query="select * from api_table where id ="+apiid;
		    	api = db.getDataList(query);
		    	if( api.length()>0)
		    	{
		    	//for(int k=0;k<api.length();k++) {
			    	String country=api.getJSONObject(0).getString("field_name").split(",")[0];
					String org=api.getJSONObject(0).getString("field_name").split(",")[1];
					String isp=api.getJSONObject(0).getString("field_name").split(",")[2];
					
					insertJson1.put("status", "Unknown");
					 insertJson1.put("color", "Blue");
					 insertJson1.put("country", jo.get(country));
					 insertJson1.put("isp", jo.get(isp));
					 insertJson1.put("org", jo.get(org));
					 insertJson1.put("sourceIp", s_ip);
					 insertJson1.put("destinationIp", d_ip);
					 insertJson1.put("sourcePort",s_port );
					 insertJson1.put("destinationPort",d_port );
					 insertJson1.put("timestamp",date1 );
					 
					 insertJson1.put("endTime",enddate );
					 insertJson1.put("duration",duration );
					 insertJson1.put("mobile_ip",mobile_ip );
					 if(protocol.equalsIgnoreCase("17"))
						 insertJson1.put("protocol","UDP" );
					 else
						 insertJson1.put("protocol","TCP" );
					 insertJson1.put("url",url_qry_name );
					 verifiedja.put(insertJson1);
	    	}
		    //	}
	    		//need to add apid here and only add the data 
	    		
	    		//ja3.put(data);
	    	}
	    	
	    	//when ip_info is null
	    	if(ja2.isNull(0)) {
	    		query="select * from api_table order by id";
		    	api = db.getDataList(query);
		    	boolean datafound=false;
		    	for(int k=0;k<api.length();k++) {
		    	String url =api.getJSONObject(0).getString("api");
		    	String apiid =api.getJSONObject(0).getString("id");
		    	String ip=apiip;
		    	String country=api.getJSONObject(0).getString("field_name").split(",")[0];
				String org=api.getJSONObject(0).getString("field_name").split(",")[1];
				String isp=api.getJSONObject(0).getString("field_name").split(",")[2];
		    	JSONArray j22=new JSONArray();
		    	JSONArray j33=new JSONArray();
		    	
		    	try {
		    	//	if(apiData.isNull(0)) {
					RestTemplate restTemplate = new RestTemplate();
					//if(result.equalsIgnoreCase("")) {
						result  = restTemplate.getForObject(url.concat(ip), String.class);
						if (result.contains(country))
						{
							JSONObject jo=new JSONObject(result);
							jo.put("apiid", apiid);
						
					//
					//	}
						 logger.debug("Api result"+result);
						 System.out.println(result);
//						 if(result.length()>0) {
//							 JSONObject jo=new JSONObject(result);
//							 apiData.put(jo);
//						 }
		    	//	}
							 query="insert into ip_info (ip_address,ip_info)values('"+ip+"','"+jo+"')";
							 ArrayList<String> al = new ArrayList<String>();
							 al.add(query);
							 db.executeBatchInsertion(al);
							 query="select * from ip_info where ip_address='"+ip+"'";
							 j22 = db.getDataList(query);
							 JSONObject jo1=new JSONObject(j22.getJSONObject(0).getString("ip_info"));
							 insertJson1.put("status", "Unknown");
							 insertJson1.put("color", "Blue");
							 insertJson1.put("country", jo1.get(country));
							 insertJson1.put("isp", jo1.get(isp));
							 insertJson1.put("org", jo1.get(org));
							 insertJson1.put("sourceIp", s_ip);
							 insertJson1.put("destinationIp", d_ip);
							 insertJson1.put("sourcePort",s_port );
							 insertJson1.put("destinationPort",d_port );
							 insertJson1.put("timestamp",date1 );
							
							 insertJson1.put("endTime",enddate );
							 insertJson1.put("duration",duration );
							 insertJson1.put("mobile_ip",mobile_ip );
							 if(protocol.equalsIgnoreCase("17"))
								 insertJson1.put("protocol","UDP" );
							 else
								 insertJson1.put("protocol","TCP" );
							 insertJson1.put("url",url_qry_name );
							 verifiedja.put(insertJson1);
							 datafound=true;
							 //System.out.println(result);
							 break;
		    	}
						 
					} catch (Exception e) {
						 logger.debug("Exception while making request : MSG : " + e.getMessage());
						System.out.println("Exception while making request : MSG : " + e.getMessage());
					}
		    	}
		    	if (!datafound)
		    	{
		    		 insertJson1.put("status", "Unknown");
		    		 if(url_qry_name!=null)
		    			 insertJson1.put("color", "Blue");
		    		 else
		    			 insertJson1.put("color", "Red");
					 insertJson1.put("country", "");
					 insertJson1.put("isp", "");
					 insertJson1.put("org", "");
					 insertJson1.put("sourceIp", s_ip);
					 insertJson1.put("destinationIp", d_ip);
					 insertJson1.put("sourcePort",s_port );
					 insertJson1.put("destinationPort",d_port );
					 insertJson1.put("timestamp",date1 );
					 insertJson1.put("endTime",enddate );
					 insertJson1.put("mobile_ip",mobile_ip );
					 
					 insertJson1.put("duration",duration );
					 insertJson1.put("mobile_ip",mobile_ip );
					 if(protocol.equalsIgnoreCase("17"))
						 insertJson1.put("protocol","UDP" );
					 else
						 insertJson1.put("protocol","TCP" );
					 insertJson1.put("url",url_qry_name );
					 verifiedja.put(insertJson1);
		    	}
	    	}
	    	query1="";
	    	if (sfound && dfound)
	    	{
	    		query="insert into report (imei,imsi,user_report,nib_id,transaction_id)values('"+simei+"','"+simsi+"','"+sja+"',"+nibId.getJSONObject(0).getInt("id")+",'1')";
	    		query1="insert into report (imei,imsi,user_report,nib_id,transaction_id)values('"+dimei+"','"+dimsi+"','"+dja+"',"+nibId.getJSONObject(0).getInt("id")+",'1')";
	    		
	    	}
	    	else 
	    	{
	    		if (sfound)
	    			query="insert into report (imei,imsi,user_report,nib_id,transaction_id)values('"+simei+"','"+simsi+"','"+verifiedja+"',"+nibId.getJSONObject(0).getInt("id")+",'1')";
	    		else
	    			query="insert into report (imei,imsi,user_report,nib_id,transaction_id)values('"+dimei+"','"+dimsi+"','"+verifiedja+"',"+nibId.getJSONObject(0).getInt("id")+",'1')";
	    	}	
	    		
	    	ArrayList<String> al = new ArrayList<String>();
			al.add(query);
			if (query1.length()>0)
				al.add(query1);
			db.executeBatchInsertion(al); 
			logger.debug("query"+query1);
    		
        	
    }
        logger.debug("Exit Function: readFile");
		return resultStringBuilder.toString();
    }
	
	public static String runTCPDUmpComand(String crunchifyCmd) throws IOException {
		System.out.println("inside runTCPDUmpComand()");
		logger.debug("inside runTCPDUmpComand()");
		String tcpdumpCmdResponse = "";
		ProcessBuilder crunchifyProcessBuilder = null;
 
		// Find OS running on VM
		String operatingSystem = System.getProperty("os.name");
 
		if (operatingSystem.toLowerCase().contains("window")) {
			// In case of windows run command using "crunchifyCmd"
			crunchifyProcessBuilder = new ProcessBuilder("cmd", "/c", crunchifyCmd);
		} else {
			// In case of Linux/Ubuntu run command using /bin/bash
			crunchifyProcessBuilder = new ProcessBuilder("/bin/bash", "-c", crunchifyCmd);
		}
 
		crunchifyProcessBuilder.redirectErrorStream(true);
 
		 crunchifyProcessBuilder.start();
			
		 logger.debug("Exit function runTCPDUmpComand()");
		return tcpdumpCmdResponse;
	}
	public String runTCPDUmp(String crunchifyCmd, boolean waitForResult) {
		System.out.println("inside runTCPDUmp()");
		logger.debug("inside runTCPDUmp()");
		String tcpdumpCmdResponse = "";
		ProcessBuilder crunchifyProcessBuilder = null;
 
		// Find OS running on VM
		String operatingSystem = System.getProperty("os.name");
 
		if (operatingSystem.toLowerCase().contains("window")) {
			// In case of windows run command using "crunchifyCmd"
			crunchifyProcessBuilder = new ProcessBuilder("cmd", "/c", crunchifyCmd);
		} else {
			// In case of Linux/Ubuntu run command using /bin/bash
			crunchifyProcessBuilder = new ProcessBuilder("/bin/bash", "-c", crunchifyCmd);
		}
 
		crunchifyProcessBuilder.redirectErrorStream(true);
 
		try {
			Process process = crunchifyProcessBuilder.start();
			if (waitForResult) {
				InputStream crunchifyStream = process.getInputStream();
				tcpdumpCmdResponse = getStringFromStream(crunchifyStream);
				crunchifyStream.close();
			}
 
		} catch (Exception e) {
			logger.debug("Error Executing tcpdump command" + e);
			System.out.println("Error Executing tcpdump command" + e);
		}
		logger.debug("Exit runTCPDUmp()");
		return tcpdumpCmdResponse;
	}
 
	private static String getStringFromStream(InputStream crunchifyStream) throws IOException {
		System.out.println("inside getStringFromStream()");
		logger.debug("inside getStringFromStream()");

		if (crunchifyStream != null) {
			Writer crunchifyWriter = new StringWriter();
 
			char[] crunchifyBuffer = new char[3072];
			try {
				Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
				int count;
				while ((count = crunchifyReader.read(crunchifyBuffer)) != -1) {
					crunchifyWriter.write(crunchifyBuffer, 0, count);
				}
			} finally {
				crunchifyStream.close();
			}
			return crunchifyWriter.toString();
		} else {
			return "";
		}
		
	}
	public String setLockUnlock(String ip, String flag) {
		logger.debug("inside setLockUnlock()");
		String myURL = "http://" + ip + "/cgi-bin/processData_CLI.sh";
		String mdr = "";
		String CMD_TYPE = "SET_SYSLOCK";
		//String CMD_CODE = "SET_CELL_LOCK";
		if (flag.equalsIgnoreCase("2")) {
			CMD_TYPE = "SET_SYSUNLOCK";
			
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD_TYPE", CMD_TYPE));
		params.add(new BasicNameValuePair("TAGS00", "undefined"));
		params.add(new BasicNameValuePair("TAGS01", "undefined"));
		params.add(new BasicNameValuePair("TAGS02", "undefined"));
		params.add(new BasicNameValuePair("TAGS03", "undefined"));
		params.add(new BasicNameValuePair("TAGS04", "undefined"));
		try {
			mdr = callPostDataUrl(myURL, params);
		} catch (Exception E) {
			logger.debug("Exception setLockUnlock()"+E);
			System.out.println(E);
		}
		logger.debug("Exit setLockUnlock()");
		return mdr;
	}
	public String callPostDataUrl(String myURL,List<NameValuePair> params)
	{
		logger.debug("Inside callPostDataUrl()");
		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(myURL);		
		try 
		{
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			logger.debug("Exception UnsupportedEncodingException()"+e);
		    // writing error to Log;
		}
		try {
		    
			HttpResponse response = httpClient.execute(httpPost);
		    HttpEntity respEntity = response.getEntity();

		    if (respEntity != null) 
		    {
		        //EntityUtils to get the response content
		        content =  EntityUtils.toString(respEntity);
		       
		    }
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			logger.debug("ClientProtocolException"+e);
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		    logger.debug("IOException"+e);
		}
		logger.debug("Exit callPostDataUrl()");
		return content;
	}
	public String createAndUpdateMobileData() throws NumberFormatException, IOException, ClassNotFoundException, SQLException, JSONException
	{
		logger.debug("Inside createAndUpdateMobileData()");
		StringBuilder resultStringBuilder = new StringBuilder();
		String createFileCmd=" /home/GET-IMSI-IP-IMEI-MAPPING >/home/tshark/pdp.txt";
		runTCPDUmpComand(createFileCmd);
        //File file = new File("D:\\pdp.txt");
        File file = new File("/home/tshark/pdp.txt");
        BufferedReader br= new BufferedReader(new FileReader(file));
        HashMap<String , String >ms =new HashMap<String, String>();
        String st;
        int count=0;
        int totalcs=0;
        int totalps=0;
        String query="";
        boolean csfound=false;
        boolean psfound=false;
        JSONObject jo=new JSONObject();
        while ((st = br.readLine()) != null) {
    	resultStringBuilder.append(st).append("\n");
    	if (st.contains("Total IMSI attached"))
    	{
    		csfound=false;
    		totalcs = Integer.parseInt(st.split(" = ")[1]);
    	   continue;
    	}
    	
    	//if (totalcs>0 )
    	//{
    		if(st.contains("IMSI/IMEI :"))
    		{
    			csfound=true;
    			continue;
    		}
    	//}

    	//if (totalcs>0 && csfound && count <totalcs)
    	if (csfound && !st.contains("*"))
    	{
    		ms.put(st.split("/")[0], "");
    		jo.put(st.split("/")[0], st.split("/")[1]);
    		count=count+1;
    		continue;
    	}
    	

    	if (st.contains("Total Active PDP"))
    	{
    	   totalps = Integer.parseInt(st.split(" = ")[1]);
    	   count=0;
    	   totalcs=0;
    	   csfound=false;
    	   continue;
    	}
    	
    	if (totalps>0 )
    	{
    		if(st.contains("IMSI"))
    		{
    			psfound=true;
    			continue;
    		}
    	}
    	
    	if (totalps>0 && psfound && count <totalps)
    	{
    		if (st.trim().length()>0)
    		{
    			String st1[]=st.split(" ");
    			ms.put(st1[0], st1[3]);
    			count=count+1;
    			continue;
    		}
    	}
    	
    	
    	logger.debug(st);

    
	}
        for(Map.Entry m : ms.entrySet()) {
        	String list=m.getValue().toString();
        	String imsi =m.getKey().toString();
        	String imei=jo.getString(imsi);
        	query="select * from mobile_registered where imsi='"+imsi+"'";
        	JSONArray ja= db.getDataList(query);
        	query="";
        	String nibquery="select id from nib where nib_ip='"+nib_ip+"'";
        	JSONArray nibId= db.getDataList(nibquery);
        	if (ja.length()>0 )
        	{
        		if (ja.getJSONObject(0).getString("registered").contains("t")) {
        			if (!ja.getJSONObject(0).getString("ps_status").contains("t") && list.length()>0) {
        				query="update mobile_registered set assigned_ip='"+list+"',ps_status=true,imei='"+imei+"' where imsi='"+imsi+"'";
        			}
        		
        		}else {
        			if (list.length()>0)
        				query="update mobile_registered set assigned_ip='"+list+"',ps_status=true,imei='"+imei+"',registered=true,timestamp =now() where imsi='"+imsi+"'";
        			else
        				query="update mobile_registered set assigned_ip=null,ps_status=false,registered=true,imei='"+imei+"',timestamp =now() where imsi='"+imsi+"'";
        		}
        	}else {
        		if (list.length()>0)
        			query="insert into mobile_registered ( assigned_ip ,created_at,cs_status,imsi,ps_status,registered, timestamp,updated_at, nib_id,imei) values ('"+list+"',now(),true,'"+imsi+"',true,true,now(),now(),"+nibId.getJSONObject(0).getInt("id")+",'"+imei+"')";
        		else
        			query="insert into mobile_registered (created_at,cs_status,imsi,registered, timestamp,updated_at, nib_id,imei) values (now(),true,'"+imsi+"',true,now(),now(),"+nibId.getJSONObject(0).getInt("id")+",'"+imei+"')";	
        	}
        		
        	if (query.length()>0)
        	{
        		logger.debug("Query"+query);
        		ArrayList<String> al = new ArrayList<String>();
        		al.add(query);
        		db.executeBatchInsertion(al);
        	}
        }
        logger.debug("Exit createAndUpdateMobileData()");
		return st;
	}
}
