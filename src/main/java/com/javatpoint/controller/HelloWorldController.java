package com.javatpoint.controller;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javatpoint.servcie.ServiceDB;
import com.javatpoint.servcie.dataBaseService;


@RestController
@RequestMapping("/myapp")
public class HelloWorldController 
{
	private static 	final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

	@Autowired
	private ServiceDB sdb;
	
	@Autowired
	private dataBaseService db;
	
	@Value("${interface}")
	String intarface;
	
	@Value("${findIp}")
	String pcapIP;
   
	String filename="";
	
	@RequestMapping("/tcpdump")
    public String tcpdump(HttpServletRequest request,@RequestBody String data)throws Exception {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		 logger.debug("inside function tcpdump");
		//JSONArray js = new JSONArray(data);
		String ip=data;
//		String setEnv=". /home/armtest/TESTSETUP/cp/repos/testStubs/SetEnv.sh";
//		sdb.runTCPDUmpComand(setEnv);
		sdb.nib_ip=data;
		sdb.setLockUnlock(ip, "2");
		filename="pcap-"+formater.format(new Date())+".pcap";
		String tcpDumpCmd = "tcpdump -ni "+intarface+" -w "+filename;
		sdb.mobile_sanitizer=true;
		String tcpDumpResult = ServiceDB.runTCPDUmpComand(tcpDumpCmd);
		System.out.println(tcpDumpResult);
		logger.debug(tcpDumpResult);
		logger.debug("Exit function tcpdump");
		return "startdump";
	}
	@RequestMapping("/stoptcpdump")
    public String stoptcpdump(HttpServletRequest request,@RequestBody String data) throws Exception {
		logger.debug("Inside function stoptcpdump");
		String tcpDumpCmd = "killall tcpdump";
		//sdb.readFile("");
		//JSONArray js = new JSONArray(data);
		String ip=data;
	
		sdb.setLockUnlock(ip, "1");
		String tcpDumpResult = ServiceDB.runTCPDUmpComand(tcpDumpCmd);
		String fl = parsepcap(filename);
		logger.debug("parse pcap file lengrth"+fl.length());
		System.out.println("parse pcap file lengrth"+fl.length());
		if (fl.length()>5);
			sdb.readFile(fl);
		
		sdb.mobile_sanitizer=false;
		System.out.println(tcpDumpResult);
		logger.debug("Exit function stoptcpdump");
		return "stopdump";
	}
	public String parsepcap(String flname) throws Exception {
		logger.debug("Inside function parsepcap");
		 String tcpDumpCmd="";
		 String tcpDumpResult="";
		SimpleDateFormat formater = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String filename1="output-"+formater.format(new Date())+".json";
			String query="select * from mobile_registered where registered=true";
			JSONArray ja = db.getDataList(query);
			String ips="";
		for (int i=0;i<ja.length();i++) {
			if(ja.getJSONObject(i).getString("assigned_ip").length()>0) {
			if (i >0)
				ips=ips +"|| ip.addr=="+ ja.getJSONObject(i).getString("assigned_ip");
			else
				ips="ip.addr=="+ ja.getJSONObject(i).getString("assigned_ip");
			}	
		}
		if(ips.length()>0) {
			ips="\"" + ips + "\"";
			tcpDumpCmd = "tshark -2 -R "+ips+"  -r /home/tshark/"+flname+" -n -T fields -e frame.time_epoch  -e ip.src -e ip.dst -e tcp.port -e udp.port -e proto -e _ws.col.Info -e ip.proto -e dns.qry.name> "+filename1;
			tcpDumpResult = sdb.runTCPDUmp(tcpDumpCmd, true);
		}else
			filename1="";
	 	 
		 System.out.println(tcpDumpResult);
		 logger.debug("Exit function parsepcap");
		 return filename1;
			
		}
}
