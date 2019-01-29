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
public class PassportInterceptor implements HandlerInterceptor{

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception{
            // 判断request是否带有token
            String token = null;
            if(request.getCookies() != null){
                for(Cookie cookie:request.getCookies()){
                    if(cookie.getName().equals("token")){
                        // System.out.println("passportInterceptor:拿到了cookie中的token");
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            if (token != null){
                LoginTicket loginTicket = loginTicketDao.selectByTicket(token);
                // 无论怎样返回True才会继续拦截器的进行，否则的话 DispatcherServlet认为该handle已经处理了响应
                if (loginTicket ==null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                    return true;
                }
                User user = userDao.selectById(loginTicket.getUserId());
                hostHolder.setUser(user);
                // 这里拿到的居然是上一个用户信息！！！！！，这是什么神仙bug....
                // System.out.println(String.format("拿到了User:  %s", user.getName()));
                // 直接在controller之前就把user加入view试试：不行，报错
                // modelAndView.addObject("user",hostHolder.getUser());
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