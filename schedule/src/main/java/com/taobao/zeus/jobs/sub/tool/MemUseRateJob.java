package com.taobao.zeus.jobs.sub.tool;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.ShellJob;
import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.util.Environment;
import com.taobao.zeus.util.RunningJobKeys;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemUseRateJob extends ShellJob{

	private double rate;
	public MemUseRateJob(JobContext jobContext,double rate) {
		super(jobContext,"free -m | grep Mem:");
		/**
		 * 添加一个表示是否为心跳的标示符
		 */
		jobContext.getProperties().getAllProperties().put(RunningJobKeys.JOB_RUN_TYPE, "MemUseRateJob");
		this.rate=rate;
	}
	static Pattern pattern=Pattern.compile("\\d+");
	private static Date date=new Date();
	@Override
	public Integer run() throws Exception {
		//window mac 系统直接返回成功
		String os=System.getProperties().getProperty("os.name");
		if(os!=null && (os.startsWith("win") || os.startsWith("Win") || os.startsWith("Mac"))){
			//放一个假的数字，方便开发
			jobContext.putData("mem", Environment.getMaxMemRate().doubleValue());
			return 0;
		}
		Integer exitCode=super.run();
		if(exitCode==0){
			String[] content=getJobContext().getJobHistory().getLog().getContent().split("\n");
			for(String s:content){
				if(s.contains("Mem:")){
					String line=s.substring(s.indexOf("Mem:"));
					Matcher matcher=pattern.matcher(line);
					double total=0d;
					double used=0d;
					double free=0d;
					int num=0;
					while(matcher.find()){
						num++;
						if(num==1){
							total=Double.valueOf(matcher.group());
							continue;
						}
						if(num==2){
							used=Double.valueOf(matcher.group());
							continue;
						}
						if(num==6){
							free=Double.valueOf(matcher.group());
							break;
						}
					}
					if((new Date().getTime()-date.getTime())>3*60*1000){
						ScheduleInfoLog.info("mem use rate used:"+used+" free:"+free+" rate:"+(used/(total)));
						date=new Date();
					}
					// 可能迭代不到，迭代到的话这里就会return，所以最后面应该return -1
					jobContext.putData("mem", (used/(total)));
					if(used/(total)>rate){
						return -1;
					}else{
						return 0;
					}
				}
			}
		}
		return -1;
	}
}
