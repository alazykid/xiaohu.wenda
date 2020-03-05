package com.hlh.wenda.controller;

import com.hlh.wenda.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class SettingController {

    @Autowired
    WendaService wendaService;

    @RequestMapping(path = {"/setting"})
    @ResponseBody           //返回的是直接的字符串而不是模板
    public String setting(HttpSession httpSession){
        return "Setting ok!" + wendaService.getMessage(1);
    }

}
