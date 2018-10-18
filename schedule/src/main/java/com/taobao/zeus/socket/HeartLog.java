package com.taobao.zeus.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartLog {

	private static Logger log=LoggerFactory.getLogger(HeartLog.class);
	
	public static void error(String msg){
		log.error(msg);
	}
	public static void error(String msg,Throwable t){
		log.error(msg, t);
	}
	public static void info(String msg){
		log.info(msg);
	}

}
