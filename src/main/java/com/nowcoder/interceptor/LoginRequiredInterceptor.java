package com.nowcoder.interceptor;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nowcoder.dao.LoginTicketDao;
import com.nowcoder.dao.UserDao;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor{

    // @Autowired
    // private LoginTicketDao loginTicketDao;

    // @Autowired
    // private UserDao userDao;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception{
            if (hostHolder.getUser() == null){
                // 如果用户请求request.getRequestURI()该url时未登录，则返回带参数的reglogin页面
                response.sendRedirect("/reglogin?callback=" + request.getRequestURI());
            }
            return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
        throws Exception{
            // 响应之后渲染之前
            if(modelAndView != null && hostHolder.getUser() != null){
                // 这里的addObject方法实际上是将键值埋到返回的url中，而不是模板中，user是对象，埋不进去:错!!!确实是加View中进去了          
                modelAndView.addObject("user",hostHolder.getUser());
                // modelAndView.addObject("fixString","能显示");
                System.out.println("postHandle已将user加入modelandview");     
             }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception{
            // 结束渲染之后：清理数据
            hostHolder.clear();
    }

    // 下面要写拦截器的注册
}
