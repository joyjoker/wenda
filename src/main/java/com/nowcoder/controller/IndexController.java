package com.nowcoder.controller;

import com.nowcoder.model.User;
import com.nowcoder.service.WendaService;
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

//@Controller
public class IndexController{
    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    //根本不需要初始化,在用的地方依赖注入
    @Autowired
    WendaService wendaService;//远程定义了一个Service,在这里可以直接使用
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    @ResponseBody
    public String index(HttpSession httpSession) {
        logger.info("visit home");
        return wendaService.getMessage(2) + "Hello nowcoder" + httpSession.getAttribute("msg");

    }

    //@RequestParam输入在参数部分@PathVariable输入在路径部分
    @RequestMapping(path = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,
                          @PathVariable("groupId") String groupId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", required = false) String key) {
        return String.format("Profile Pages of %s/ %d, t %d k %s", groupId, userId, type, key);
    }

    @RequestMapping(path = {"/vm"}, method = {RequestMethod.GET})
    public String template(Model model) {
        model.addAttribute("value1", "vvvvv1");
        List<String> colors = Arrays.asList(new String[] {"RED", "GREEN", "BLUE"});
        model.addAttribute("colors", colors);
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }
        model.addAttribute("map", map);
        model.addAttribute("user", new User("LEI"));
        return "home";
    }

    @RequestMapping(path = {"/request"}, method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session,
                          @CookieValue("JSESSIONID") String sessionID) {
        StringBuilder sb = new StringBuilder();
        sb.append("COOKIDVALUE" + sessionID);
        Enumeration<String> headerName = request.getHeaderNames();
        while (headerName.hasMoreElements()) {
            String name = headerName.nextElement();
            sb.append(name + "    :    " + request.getHeader(name) + "<br/>");
        }
        if(request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                sb.append("KOOKIE      " + cookie.getName()+ "      Value      " + cookie.getValue());
            }
        }
        sb.append("1         "+request.getMethod() + "<br/>");
        sb.append("2         "+request.getQueryString() + "<br/>");
        sb.append("3         "+request.getContextPath() + "<br/>");
        sb.append("4         "+request.getHeaderNames() + "<br/>");
        sb.append("5         "+request.getAuthType() + "<br/>");
        sb.append("6         "+request.getPathInfo() + "<br/>");
        sb.append("7         "+request.getRequestURI() + "<br/>");
        sb.append("8         "+request.getServletPath() + "<br/>");

        return sb.toString();
    }

    @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
    public RedirectView redirect(@PathVariable("code")int code,
                                 HttpSession session) {
        session.setAttribute("msg", "jump from redirect");
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping(path = {"/admin"}, method = {RequestMethod.GET})
    @ResponseBody
    public String admin(@RequestParam("key") String key) {
        if ("admin".equals(key)) {
            return "hello admin";
        } else {
            throw new IllegalArgumentException("参数不对");
        }
    }

    //统一的异常处理
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }
    
}

