package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.service.WendaService;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

// @Controller
// public class IndexController {
//     private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

//     @Autowired
//     WendaService wendaService;

//     @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
//     @ResponseBody
//     public String index(HttpSession httpSession) {
//         logger.info("VISIT HOME");
//         return wendaService.getMessage(2) + "Hello NowCoder" + httpSession.getAttribute("msg");
//     }

//     @RequestMapping(path = {"/profile/{groupId}/{userId}"})
//     @ResponseBody
//     public String profile(@PathVariable("userId") int userId,
//                           @PathVariable("groupId") String groupId,
//                           @RequestParam(value = "type", defaultValue = "1") int type,
//                           @RequestParam(value = "key", required = false) String key) {
//         return String.format("Profile Page of %s / %d, t:%d k: %s", groupId, userId, type, key);
//     }

//     @RequestMapping(path = {"/vm"}, method = {RequestMethod.GET})
//     public String template(Model model) {
//         model.addAttribute("value1", "vvvvv1");
//         List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
//         model.addAttribute("colors", colors);

//         Map<String, String> map = new HashMap<>();
//         for (int i = 0; i < 4; ++i) {
//             map.put(String.valueOf(i), String.valueOf(i * i));
//         }
//         model.addAttribute("map", map);
//         model.addAttribute("user", new User("LEE"));
//         return "home";
//     }

//     @RequestMapping(path = {"/request"}, method = {RequestMethod.GET})
//     @ResponseBody
//     public String request(Model model, HttpServletResponse response,
//                            HttpServletRequest request,
//                            HttpSession httpSession,
//                           @CookieValue("JSESSIONID") String sessionId) {
//         StringBuilder sb = new StringBuilder();
//         sb.append("COOKIEVALUE:" + sessionId);
//         Enumeration<String> headerNames = request.getHeaderNames();
//         while (headerNames.hasMoreElements()) {
//             String name = headerNames.nextElement();
//             sb.append(name + ":" + request.getHeader(name) + "<br>");
//         }
//         if (request.getCookies() != null) {
//             for (Cookie cookie : request.getCookies()) {
//                 sb.append("Cookie:" + cookie.getName() + " value:" + cookie.getValue());
//             }
//         }
//         sb.append(request.getMethod() + "<br>");
//         sb.append(request.getQueryString() + "<br>");
//         sb.append(request.getPathInfo() + "<br>");
//         sb.append(request.getRequestURI() + "<br>");

//         response.addHeader("nowcoderId", "hello");
//         response.addCookie(new Cookie("username", "nowcoder"));

//         return sb.toString();
//     }

//     @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
//     public RedirectView redirect(@PathVariable("code") int code,
//                                  HttpSession httpSession) {
//         httpSession.setAttribute("msg", "jump from redirect");
//         RedirectView red = new RedirectView("/", true);
//         if (code == 301) {
//             red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//         }
//         return  red;
//     }

//     @RequestMapping(path = {"/admin"}, method = {RequestMethod.GET})
//     @ResponseBody
//     public String admin(@RequestParam("key") String key) {
//         if ("admin".equals(key)) {
//             return "hello admin";
//         }
//         throw  new IllegalArgumentException("参数不对");
//     }

//     @ExceptionHandler()
//     @ResponseBody
//     public String error(Exception e) {
//         return "error:" + e.getMessage();
//     }
// }

@Controller
public class IndexController{
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/","index"},method={RequestMethod.GET,RequestMethod.POST})
    public String index(Model model){
        // List<Question> questionList = questionService.getQuestions(0,0,20);
        // List<ViewObject> vos = new ArrayList<>();
        // for (Question question:questionList){
        //     // System.out.println(question.getTitle());
        //     // System.out.println(question.getCreatedDate());
        //     ViewObject vo = new ViewObject();
        //     vo.set("question",question);
        //     vo.set("user", userService.getUser(question.getUserId())); 
        //     // System.out.println(question.getTitle() + ":" +  userService.getUser(question.getUserId()).getName());             
        //     vos.add(vo);
        // }
        // // System.out.println(vos); 
        // model.addAttribute("vos", vos);
        // model.addAttribute("Str", "怎么回事小老弟？");
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    } 


    // 下面写个人主页:实际上就是多了一个从问题筛选（UserId来筛选）
    @RequestMapping(path="/user/{userId}",method={RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model,@PathVariable("userId") int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }

    private List<ViewObject> getQuestions(int userId,int offset,int limit){
        List<Question> questionList = questionService.getQuestions(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("user", userService.getUser(question.getUserId())); 
            // System.out.println(question.getTitle() + ":" +  userService.getUser(question.getUserId()).getName());             
            vos.add(vo);
        }
        return vos;
    }


    // // 重定向与异常处理
    // @RequestMapping(path={"/redirect/{code}"},method={RequestMethod.GET,RequestMethod.POST})
    // public RedirectView redirect(@PathVariable("code") int code,HttpSession httpSession){
    //         // 重新定位到首页
    //         httpSession.setAttribute("redirect", "跳转到首页");
    //         // 制定跳转路径,301永久跳转，302临时跳转
    //         RedirectView redirectView = new RedirectView("/",true);
    //         if (code == 301){
    //             redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
    //         }
    //         return redirectView;
    //         // return "redirect:/";
    // }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error" + e.getMessage();
    }



}
