package com.nowcoder.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController1{
    private static final Logger logger = LoggerFactory.getLogger(LoginController1.class);

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    // 登录注册页
     @RequestMapping(path={"/reglogin"},method={RequestMethod.GET})
     public String regloginPage(@RequestParam(value = "callback",required = false) String callback,Model model){
        //  如果带有参数，则将callback变量埋入/reglogin对应的login.html的要提交的表格当中，这样callback变量可以在/login 、、reg/请求中可以解析到
         model.addAttribute("callback", callback);
         return "login";
     }

     @RequestMapping(path={"/login/"},method={RequestMethod.POST})
     public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value="callback", required = false) String callback,
                        @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse httpServletResponse){
        //  判断账户密码合法性，需要用到User服务,在userService中提供登录判断操作
        try {
            Map<String,Object> map = userService.login(username, password);
            if(map.containsKey("token")){
                Cookie cookie = new Cookie("token", map.get("token").toString());

                /*********************************
                两天的错误原因：线将cookie加入了response,才设置作用的path:导致这个cookie无作用域，所以产生了首页未显示登录信息的错误
                *******************************/

                cookie.setPath("/");
                if (rememberme == true){
                    cookie.setMaxAge(3600*24*7);
                }
                httpServletResponse.addCookie(cookie);
                // System.out.println("controller响应完毕，准备返回redirect");
                // 现在我在controller里面加attribute,不在interceptor里面加了,但是还是不行，现在有两种情况：
                /*
                1.在controller里面加user属性且return "redirect:/",登录信息无效不显示；但在加user属性的基础上return "index",返回账户信息，但无最新动态中的问题显示
                2.在passportInterceptor中加user属性并且在controller中return 'redirect'，登录信息无效；但若在interceptor中setviewname("header"),显示账户信息，但也无最新动态问题显示
                */
                // model.addAttribute("user",hostHolder.getUser());
                eventProducer.fireEvent(new EventModel().setType(EventType.LOGIN).
                setExts("username", username).setExts("email", "zjuyxy@qq.com")
                .setActorId((int)map.get("userId")));
                if (StringUtils.isNotBlank(callback)){
                    return "redirect:" + callback;
                }
                return "redirect:/";
            }
            model.addAttribute("msg", map.get("msg"));
            return "login";
        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
     }


     @RequestMapping(path={"/reg/"},method={RequestMethod.POST})
     public String register(Model model,@RequestParam("username") String username,@RequestParam("password") String password,HttpServletResponse httpServletResponse,
                            @RequestParam(value = "callback", required = false) String callback,@RequestParam(value="rememberme", defaultValue = "false") boolean rememberme){
         try {
            //  判断账户密码合法性，需要用到User服务,在userService中提供登录判断操作
            Map<String,Object> map = userService.register(username, password);
            if(map.containsKey("token")){
                Cookie cookie = new Cookie("token",map.get("token").toString());
                cookie.setPath("/");
                if (rememberme == true){
                    cookie.setMaxAge(3600*24*7);
                }
                httpServletResponse.addCookie(cookie);
                System.out.println("controller响应完毕，准备返回redirect");
                if (StringUtils.isNotBlank(callback)){
                    return "redirect:" + callback;
                }
                return "redirect:/";
                }
            model.addAttribute("msg", map.get("msg"));
            return "login";
         } catch (Exception e) {
             logger.error("注册异常", e.getMessage());
             model.addAttribute("msg", "服务器错误");
             return "login";
         }

     }

    //  下面要解决的是登录、注册之后的带token的浏览（全页面的带token）:从interceptor和hostholder讲起
    @RequestMapping(path = {"/logout"},method={RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("token") String token){
        userService.logout(token);
        System.out.println("执行了登出服务");
        return "redirect:/";
    }
 }
