package com.itheima.reggie.web.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.utils.BaseContextUtil;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Author Vsunks.v
 * @Date 2022/6/8 15:58
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description: 登录校验
 */
@WebFilter("/*") // 表示该类是一个filter，拦截所有请求
@Slf4j
public class LoginCheckFilter implements Filter {


    // 2.2 创建路径匹配器对象
    AntPathMatcher apm = new AntPathMatcher();

    @Value("${urlStr}")
    String urlStr;

    /**
     * 判断是否登录，并确定是否放行
     *
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        //1. 获取本次请求的URI
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();

        //2. 判断本次请求, 是否需要登录, 才可以访问

        //2.1 定义要放行的所有请求
        /*
            String[] urls = {
                    "/backend/**",
                    "/front/**",
                    "/employee/login",
                    "/favicon.ico"
            };
        */

        // 2.1 解析要放行的所有请求
        String[] urls = urlStr.split(",");

        // 2.3 检查是否在放行范围
        if (checkUrl(urls, requestURI)) {
            //3. 如果不需要，则直接放行
            filterChain.doFilter(request, response);
            return; // 后面代码不需要执行，直接结束
        }


        //4. 判断登录状态，如果已登录，则直接放行
        // 4.1 后台校验
        HttpSession session = req.getSession();
        Long employeeId = (Long) session.getAttribute("employee");

        if (employeeId != null) {
            // 验证是否在同一个线程中
            /*Thread thread = Thread.currentThread();
            String name = thread.getName();
            System.out.println("**********************************");
            System.out.println("**********Filter-check:  " + requestURI + "----" + name);
            */

            //通过ThreadLocal对象，为当前线程绑定一个值.
            // 因为所有请求都会过Filter，在这里设置好之后，
            // 之后在dao/service/Controller等位置所有同一个线程中都可以获取并使用
            BaseContextUtil.setCurrentId(employeeId);

            // 放行
            filterChain.doFilter(request, response);
            return; // 后面代码不需要执行，直接结束
        }


        // 4.3 前台校验
        Long userId = (Long) session.getAttribute("user");

        if (userId != null) {

            BaseContextUtil.setCurrentId(userId);

            // 放行
            filterChain.doFilter(request, response);
            return; // 后面代码不需要执行，直接结束
        }


        //5. 如果未登录, 则返回未登录结果
        // 前端页面写死的，只识别NOTLOGIN；接受到该符号后，跳转到登录页面
        response.getWriter().write(JSON.toJSONString(R.fail("NOTLOGIN")));


    }

    /**
     * 判断某个请求是否在不登录的时候就可以放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    private boolean checkUrl(String[] urls, String requestURI) {

        boolean matchResult = false;
        for (String url : urls) {

            // 匹配 本次请求的requestURI  是否符合 url
            matchResult = apm.match(url, requestURI);
            if (matchResult) {
                return true;
            }
        }
        log.info("本次请求url为：{}，是否需要放行{}", requestURI, matchResult);


        return false;
    }
}
