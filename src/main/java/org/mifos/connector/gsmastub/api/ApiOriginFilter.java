package org.mifos.connector.gsmastub.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-09-27T11:46:46.417Z[GMT]")
public class ApiOriginFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String corId= req.getHeader("X-CorrelationID");
        logger.info("X-CorrelationID:{}",corId);
        String remoteAddress = req.getHeader("X-Forwarded-For");

        if (remoteAddress == null || remoteAddress.isEmpty()) {
            remoteAddress = req.getRemoteAddr();
        } else {
            remoteAddress = remoteAddress.split(",")[0].trim();
        }
        logger.info("Remote IP address: {}", remoteAddress);

        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.addHeader("Access-Control-Allow-Headers", "Content-Type");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
