package com.nnu.chatrobot.filter;

import com.alibaba.fastjson.JSON;
//import com.nnu.chatrobot.common.BaseContext;
import com.nnu.chatrobot.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
//@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter{
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/user/login",
                "/user/logout",
                "/user/register",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        String requestURI=request.getRequestURI();
        log.info("拦截到" + requestURI);
        //判断是否需要拦截
        boolean chech=check(urls,requestURI);
        //直接放行
        if(chech==true)
        {
            log.info("不需要处理直接放行" + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //判断是否已登录，登录了就直接放行
        boolean islogin=(request.getSession().getAttribute("user")!=null);
        if(islogin==true)
        {
            int userid= (int) request.getSession().getAttribute("user");
//            BaseContext.setCurrentId(userid);

            log.info("已登录直接放行" + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //否则拦截，返回一个error
        log.info("未登录拦截" + requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String urls[],String requestURI)
    {
        for (String url : urls) {
            boolean match=PATH_MATCHER.match(url,requestURI);
            if(match==true)
            {
                return true;
            }
        }
        return false;
    }
}
