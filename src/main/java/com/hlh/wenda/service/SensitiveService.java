package com.hlh.wenda.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null){
                addWord(lineTxt.trim());
            }
            read.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+ e.getMessage());
        }
    }

    //构造字典树，增加关键词
    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < lineTxt.length();i++){
            Character c = lineTxt.charAt(i);
            if(isSymbol(c)){
                continue;
            }

            TrieNode node = tempNode.getSubNode(c);

            if(node == null){
                node = new TrieNode();
                tempNode.addSubNode(c,node);
            }

            tempNode = node;

            if(i == lineTxt.length() -1 ){
                tempNode.setKeyWordEnd(true);
            }

        }
    }

    private class TrieNode{
        //是不是关键词的结尾
        private boolean end = false;

        //当前节点下所有的子节点
        private Map<Character,TrieNode> subNodes = new HashMap<Character,TrieNode>();


        private void addSubNode(Character key, TrieNode node){
            subNodes.put(key,node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeyWordEnd(){
            return end;}

        void setKeyWordEnd(boolean end){
            this.end = end;
        }

    }

    private TrieNode rootNode = new TrieNode();

    //所有不是东亚文字的都算做符号进行过滤掉
    private boolean isSymbol(char c){
        int ic =(int) c;
        //东亚文字  0x2E80~0x9FFF
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);

    }


    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return  text;
        }

        StringBuilder result = new StringBuilder();

        String replacement = "***";//发现敏感词打码，如果要直接删除就注释掉这行
        TrieNode tempNode = rootNode;//1
        int begin = 0; //2
        int position = 0; //3

        while(position < text.length()){
            char c = text.charAt(position);

            if(isSymbol(c)){
                if(tempNode == rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if(tempNode == null){
                //不是敏感词，2，3直接后移下一位进行判断，1回到字典树根结点
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                //发现敏感词
                result.append(replacement);
                position = position  +1;
                begin = position;
                tempNode = rootNode;
            }else{
                position++;
            }
        }

        result.append(text.substring(begin));
        return result.toString();

    }


    public static void main(String[] args) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        s.addWord("色狼");
        System.out.println(s.filter("i色情，hello，去赌博吗？"));
    }
}
