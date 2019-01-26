package com.nowcoder.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    private static final String DEFAULT_REPLACEMENT = "***";

    @Override
    // 建立敏感词树
    public void afterPropertiesSet() throws Exception{
        //读取敏感词文件，会用到敏感词树添加方法（addword）
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine())!= null){
                // trim() 方法用于删除字符串的头尾空白符。
                lineTxt = lineTxt.trim();
                addword(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage() );
        }

    }

    // 建立前缀树
    private class TrieNode{
        private boolean end =false;
        private Map<Character,TrieNode> subNodes = new HashMap<Character,TrieNode>();

        // 添加子敏感字符
        void addSubNode(Character key,TrieNode trieNode){
            subNodes.put(key, trieNode);
        }

        //获取子敏感词
        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        } 

        // 判断当前节点是否是敏感词
        boolean isKeywordNode(){
            return end;
        }

        //设置当前节点字符是否敏感词
        void setKeywordEnd(boolean end){
            this.end = end;
        }

        //获取子节点数量（好像用不到）
        public int getSubNodesCount(){
            return subNodes.size();
        }
    }

    //然后应该先将敏感词树建立起来
    //建立根节点
    TrieNode rootNode = new TrieNode();
    private  boolean isSymbol(Character c){
        int ic = (int) c;
        //不是英文或者东亚文字就返回true,symbol:不是文字;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    // 建立敏感词树所要用到的add方法
    private  void addword(String lineTxt){
        TrieNode tempNode = rootNode;
        for (int i=0;i < lineTxt.length(); ++i){
            Character c = lineTxt.charAt(i);
            // 如果不是正常字符就跳过
            if (isSymbol(c)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            if(node == null){
                // ********又一个new重新付给了node
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            tempNode = node;
            if(i == lineTxt.length() -1 ){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    // 下面开始写前缀树算法
    public String filter(String text){
        if (text.isBlank()){
            return text;
        }
        // 一些初始化的工作
        StringBuilder result = new StringBuilder();
        int begin = 0;
        int position = 0;
        TrieNode tempNode = rootNode;

        while (position < text.length()){
            Character c = text.charAt(position);
            // 如果是非法字符，则跳过
            if (isSymbol(c)){
                //非法字符不在敏感树中
                if (tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            // 如果是有效字符，则深入节点
            tempNode = tempNode.getSubNode(c);
            // 如果当前字符不在敏感前缀树中,则退回到begin后一位，然后begin前进一位
            if (tempNode == null){
                // 关键的两句***********************
                position = begin + 1;
                begin = position;
                // *********************************************
                result.append(c);
                tempNode = rootNode;
            }else if(tempNode.isKeywordNode()){
                result.append(DEFAULT_REPLACEMENT);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else{
                // 如果当前position字符在敏感词前缀树中且并不是end位，则position自增知道tempNode为null或者为end位
                ++position;
            }
        }
        //************************************************** 
        // 最后特别容易忘的一点：将postion走到最后的begin位置及其之后的字符返回：因为有可能最后几位进行了敏感词前缀树的检查但是没有找到真正的end位（即不是真正的敏感词），导致最后几位没append
        result.append(text.substring(begin));
        return result.toString();
    }

    // public static void main(String[] args) {
    //     // Character ch = new Character('a');
    //     // System.out.println(isSymbol(ch));
    //     SensitiveService s = new SensitiveService();
    //     s.addword("色情");
    //     System.out.println(s.filter("你好色情"));
    // }

}