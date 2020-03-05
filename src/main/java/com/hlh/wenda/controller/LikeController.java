package com.hlh.wenda.controller;

import com.hlh.wenda.Util.WendaUtil;
import com.hlh.wenda.model.EntityType;
import com.hlh.wenda.model.HostHolder;
import com.hlh.wenda.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path ={"/like"},method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId")int commentId ){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);
        return  WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path ={"/dislike"},method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId")int commentId ){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);
        return  WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

}
