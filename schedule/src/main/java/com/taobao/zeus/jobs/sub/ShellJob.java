package com.taobao.zeus.jobs.sub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.ProcessJob;
import com.taobao.zeus.store.Super;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.Environment;
import com.taobao.zeus.util.PropertyKeys;


/**
 * 采用Shell脚本的任务
 * @author zhoufang
 *
 */
public class ShellJob extends ProcessJob{

	private static Logger log=LoggerFactory.getLogger(ShellJob.class);
	protected String shell;

	public ShellJob(JobContext jobContext) {
		super(jobContext);
	}
	public ShellJob(JobContext jobContext,String shell){
		this(jobContext);
		this.shell=shell;
	}
	
	@Override
	public Integer run() throws Exception {
		return super.run();
	}

	@Override
	public List<String> getCommandList() {
		String script=null;
		if(shell!=null){
			script=shell;
		}else{
			script=getProperties().getLocalProperty(PropertyKeys.JOB_SCRIPT);
		}
		
		OutputStreamWriter writer=null;
		try {
			File f=new File(jobContext.getWorkDir()+File.separator+(new Date().getTime())+".sh");
			if(!f.exists()){
				f.createNewFile();
			}
			writer=new OutputStreamWriter(new FileOutputStream(f),Charset.forName(jobContext.getProperties().getProperty("zeus.fs.encode", "utf-8")));
			writer.write(script);
			getProperties().setProperty(PropertyKeys.RUN_SHELLPATH, f.getAbsolutePath());
		} catch (Exception e) {
			jobContext.getJobHistory().getLog().appendZeusException(e);
		} finally{
			IOUtils.closeQuietly(writer);
		}
		
		String shellFilePath=getProperty(PropertyKeys.RUN_SHELLPATH, "");
		
		List<String> list=new ArrayList<String>();
		
		
		//修改权限
		String shellPrefix = processScriptPrefix();

		//格式转换
		String[] excludeFiles = Environment.getExcludeFile().split(";");
		boolean isDos2unix = true;
		if(excludeFiles!=null && excludeFiles.length>0){
			for(String excludeFile : excludeFiles){
				if(shellFilePath.toLowerCase().endsWith("."+excludeFile.toLowerCase())){
					isDos2unix = false;
					break;
				}
			}
//			System.out.println(Environment.getExcludeFile());
		}
		if(isDos2unix){
			list.add("dos2unix " + shellFilePath);
//			System.out.println("dos2unix file: " + shellFilePath);
			log("dos2unix file: " + shellFilePath);
		}

		//执行shell
		// run shell as current user
        String envFilePath = this.getClass().getClassLoader().getResource("/").getPath()+"env.sh";
        String tmpFilePath = jobContext.getWorkDir()+File.separator+"tmp.sh";
        String localEnvFilePath = jobContext.getWorkDir()+File.separator+"env.sh";
        File f=new File(envFilePath);
        if(f.exists()){
            list.add("cp " + envFilePath + " " + jobContext.getWorkDir());
            File tmpFile = new File(tmpFilePath);
            OutputStreamWriter tmpWriter=null;
            try {
                if(!tmpFile.exists()){
                    tmpFile.createNewFile();
                }
                tmpWriter=new OutputStreamWriter(new FileOutputStream(tmpFile),Charset.forName(jobContext.getProperties().getProperty("zeus.fs.encode", "utf-8")));
                tmpWriter.write("source " + localEnvFilePath + "; sh " + shellFilePath);
            } catch (Exception e) {
                jobContext.getJobHistory().getLog().appendZeusException(e);
            } finally{
                IOUtils.closeQuietly(tmpWriter);
            }
            list.add("chmod -R 777 " + jobContext.getWorkDir());
            list.add(shellPrefix + " sh " + tmpFilePath);
        }else{
            list.add("chmod -R 777 " + jobContext.getWorkDir());
            list.add(shellPrefix + " sh " + shellFilePath);
        }
		return list;
	}

}
