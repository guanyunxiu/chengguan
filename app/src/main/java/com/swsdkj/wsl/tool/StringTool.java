package com.swsdkj.wsl.tool;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class StringTool {
    //截取后两个字符
    public static String getString2Front(String str){
        if(str.length()>=2){// 判断是否长度大于等于4
            String strsub1=str.substring(str.length()- 2,str.length());//截取两个数字之间的部分
            return strsub1;
        }else {
            return "";
        }


    }
}
