package com.taobao.zeus.web.listener;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.Log4jConfigListener;

import javax.servlet.ServletContextEvent;

/**
 * Created by yxl on 2017/9/14.
 */
public class Log4jListener  extends Log4jConfigListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String loggingRoot = event.getServletContext().getInitParameter("zeus.loggingRoot");
        if(StringUtils.isNotEmpty(loggingRoot)){
            System.setProperty("zeus.loggingRoot",loggingRoot) ;
        }
        super.contextInitialized(event);
    }
}
