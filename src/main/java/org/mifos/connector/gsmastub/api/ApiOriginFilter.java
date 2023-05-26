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
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "X-Forwarded-Host",
            "X-Forwarded-Port",
            "X-Forwarded-Proto",
            "X-Forwarded-Scheme",
            "X-Original-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };
    private void getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String val = request.getHeader(header);
            logger.info("Header Name : {}",header);
            logger.info("Header Value : {}",val);
        }
        logger.info("Request remote address: {}",request.getRemoteAddr());
        logger.info("Remote Host: {}",request.getRemoteHost());
        logger.info("Header names: {}",request.getHeaderNames().toString());
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String corId= req.getHeader("X-CorrelationID");
        logger.info("X-CorrelationID:{}",corId);
        getClientIpAddress(req);

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
