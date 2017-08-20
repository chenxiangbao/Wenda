package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean{

//    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ming.txt")));
//    String data = null;
//    while((data = br.readLine())!=null)
//    {
//        System.out.println(data);
//    }
    //读取敏感词
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTixt;
            while((lineTixt = bufferedReader.readLine())!=null){
                addWord(lineTixt);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private TrieNode rootNode = new TrieNode();

    //判断是否是非法词
    public boolean isSymbol(char c){
        int ic =(int) c;
        //东亚文字 0x2E80-Ox9FFF
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF );
    }


    //增加关键词
    private void addWord(String lineText){
        TrieNode tempNode = rootNode;
        for(int i=0;i<lineText.length();i++){
            Character c = lineText.charAt(i);

            TrieNode node = tempNode.getSubNode(c);
            if(node==null){
                node = new TrieNode();
                tempNode.addSunNode(c,node);
            }

            tempNode=node;

            if(i==lineText.length() - 1){
                tempNode.setkeywordEnd(true);
            }

        }
    }
    //进行敏感词过滤。三个指针
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        StringBuilder sb = new StringBuilder();

        String replacement = "***";
        TrieNode tempNode = rootNode;
        //用来进行在字符串上进行位移的位置指针
        int begin = 0;
        //用来和字典树进行对比的位置指针
        int position = 0;
        while(position<text.length()){
            char c = text.charAt(position);
            //对非法词进行过滤
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    sb.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode=tempNode.getSubNode(c);
            //如果在字典树中没有找到敏感词
            if(tempNode==null){
                sb.append(text.charAt(begin));
                position=begin+1;
                begin=position;
                tempNode=rootNode;
            }else if (tempNode.isKeyWordEnd()){
                //发现敏感词
                sb.append(replacement);
                position=position+1;
                begin=position;
                tempNode=rootNode;
            }else {
                //发现敏感词，但是没到底
                position=position+1;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private class TrieNode{
        //是不是关键词的结尾
        private boolean end=false;

        //当前节点下所有的子节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public void addSunNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeyWordEnd(){
            return end;
        }
        void setkeywordEnd(boolean end){
            this.end = end;
        }
    }



}
