package com.taobao.zeus.broadcast.alarm;

import com.google.gson.Gson;
import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.MysqlLogManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.Environment;
import com.taobao.zeus.util.JsonUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.ivy.util.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SMSAlarm extends AbstractZeusAlarm{
	private static Logger log=LogManager.getLogger(SMSAlarm.class);
	@Autowired
	private UserManager userManager;
	@Autowired
	private MysqlLogManager zeusLogManager;

    private static String smsUrl = Environment.getNotifyUrl();//Noc服务器
	private static String accessToken = Environment.getAccessToken();//Noc access_token
	private static String company = Environment.getCompany();//Noc access_token
	private static String sn = Environment.getSn();
	private static String snPassword = Environment.getSnPassword();


	@Override
	public void alarm(String jobId, List<String> uids, String title, String content)
			throws Exception {
		String srcId = "调度系统";
		List<String> userPhoneList = new ArrayList<String>();
		List<ZeusUser> userList = userManager.findListByUidByOrder(uids);
		if(userList != null && userList.size()>0){
			for(ZeusUser user : userList){
                userPhoneList.add(user.getPhone()) ;
			}
		}
		String userPhones = StringUtils.join(userPhoneList.toArray(),",") ;
		if(company.equals("yunniao"))
		{
			sendYunNiaoAlarm(jobId ,userPhones,content);
		}else if(company.equals("wuhao"))
		{
			sendWuHaoAlarm(jobId ,userPhones,content);
		}

	}

	@SuppressWarnings("deprecation")
	public void sendYunNiaoAlarm(String jobId ,String userPhones,String content) {
	    log.info("发送短信:" + userPhones + " content:" + content);
        HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(smsUrl);
		Gson gson = new Gson();
		method.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
		try {
			Map<String, Object> bodyMap = new HashMap<String,Object >();
			bodyMap.put("template","super_template") ;
			bodyMap.put("mobile", userPhones) ;
			Map<String,String> contentMap =  new HashMap<String, String>() ;
			contentMap.put("content", content) ;
			bodyMap.put("data",contentMap) ;
			method.setRequestBody(JsonUtils.objectToJson(bodyMap));
			int code = client.executeMethod(method);
			log.info("sms the return code is : " + code);
			String responseBodyAsString = method.getResponseBodyAsString(2000);
			log.info("sms the response body is " + responseBodyAsString);
			ResponseJson rJ = null;
			if (responseBodyAsString != null) {
				rJ = gson.fromJson(responseBodyAsString, ResponseJson.class);
			}
			if (code !=  HttpStatus.SC_OK || rJ == null || !rJ.isSuccess()) {
				log.error("jobId : " + jobId  +"sms send noc failed, code: " + code);
				return;
			}
			log.info("jobId : " + jobId  + "sms send noc successfully!");
		} catch(HttpException  e) {
			log.error("jobId: " + jobId + " send noc fail,", e);
		} catch (IOException e) {
			log.error("jobId: " + jobId + " send noc fail,", e);
		} catch (Exception e) {
			log.error("jobId: " + jobId + " send noc fail,", e);
		}
   }
   
	class ResponseJson{

		private String message;
		private String data;
		private boolean success;
		private int error;
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public int getError() {
			return error;
		}
		public void setError(int error) {
			this.error = error;
		}

		@Override
		public String toString() {
			return "ReturnJson [message=" + message + ", data=" + data
					+ ", success=" + success + ", error=" + error + "]";
		}
	}


	public void sendWuHaoAlarm(String jobId ,String userPhones,String content) {
		try{
			EntinfoSMSClinet client=new EntinfoSMSClinet(sn,snPassword);
			//个性短信发送
			String result_gxmt = client.mdsmssend(userPhones, content, "", "", "", "");
		}catch (Exception e)
		{
			log.error("jobId: " + jobId + " send sms fail,", e);
		}


	}
	
	public static void main(String[] args) {
		String returnString = "{\"message\": \"\", \"data\": \"enqueue\", \"success\": true, \"error\": 0}";
//		String returnString = null;
		Gson gson = new Gson();
		ResponseJson rJson = gson.fromJson(returnString, ResponseJson.class);
		System.out.println("the message is " + rJson.getMessage());
		System.out.println("the data is " + rJson.getData());
		System.out.println("the error is " + rJson.getError());
		System.out.println("the success is " + rJson.isSuccess());

	}
	
	

}