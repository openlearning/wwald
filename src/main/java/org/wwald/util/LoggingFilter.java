package org.wwald.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class LoggingFilter implements Filter {
	
	public static final Logger cLogger = Logger.getLogger(LoggingFilter.class);
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest req, 
						 ServletResponse res,
						 FilterChain chain) 
		throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)req;
		String log = httpReq.getRemoteHost() + " " +
					 httpReq.getRequestURL() + " " + 
					 httpReq.getRequestedSessionId() + " " + 
					 httpReq.getHeader("Referer");
		cLogger.info(log);		
		chain.doFilter(req, res);
	}

	public void init(FilterConfig arg0) throws ServletException {
		
	}
}
