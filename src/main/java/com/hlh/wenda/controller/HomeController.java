package com.hlh.wenda.controller;

import com.hlh.wenda.aspect.LogAspect;
import com.hlh.wenda.model.HostHolder;
import com.hlh.wenda.model.Question;
import com.hlh.wenda.model.ViewObject;
import com.hlh.wenda.service.QuestionService;
import com.hlh.wenda.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET})
    public String UserIndex(Model model, @PathVariable("userId")int userId){
        model.addAttribute("vos",getQuestions(userId,0,10));
        return "index";
    }


    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET})
    public String index(Model model){

        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    private List<ViewObject>  getQuestions(int userId,int offset,int limit){
        List<Question> questionList =  questionService.getLatestQuestion(0,0,10);
        List<ViewObject> vos = new ArrayList<ViewObject>();
        for(Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }
}
