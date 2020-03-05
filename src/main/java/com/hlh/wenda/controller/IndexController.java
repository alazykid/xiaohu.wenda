package com.hlh.wenda.controller;

/*
首页
 */
import com.hlh.wenda.aspect.LogAspect;
import com.hlh.wenda.model.User;
import com.hlh.wenda.service.WendaService;
import com.sun.javafx.collections.MappingChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.*;

//@Controller  //入口层
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    WendaService wendaService;

//    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET})
//    @ResponseBody           //返回的是直接的字符串而不是模板
//    public String index(HttpSession httpSession){
//        logger.info("Visit home");
//        return "王馨同学真可爱" + wendaService.getMessage(1);
//    }

    @RequestMapping(path = {"/profile/{groupId}/{userId}"})  //返回路径
    @ResponseBody           //返回的是直接的字符串而不是模板
    public String profile(@PathVariable("userId")int userId,
                          @PathVariable("groupId")String groupId,
                          @RequestParam (value = "type",defaultValue = "1",required = false)int type,
                          @RequestParam(value = "key",required = false)String key){
        return String.format("Profile Page of %s / %d , t:%d k: %s",
                groupId,userId,type,key);
        /*http://localhost:8080/profile/user/112?type=2&key=z
        得到的结果是 Profile Page of user / 112 , t:2 k:z
         */
    }


    @RequestMapping(path = {"/vm"},method = {RequestMethod.GET})
    public String template(Model model){
        model.addAttribute("value1","vvvvv1");
        List<String> colors = Arrays.asList(new String[]{"RED","GREEN","BLUE"});
        model.addAttribute("colors",colors);

        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("map",map);
        model.addAttribute("user",new User("LEE"));

        return "home";
    }

}
