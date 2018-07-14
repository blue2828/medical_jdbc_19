package com.sample.util;

import java.util.regex.Pattern;

public class StringUtil {
    public static boolean isEmpty(String str){
        if(null == str || "".equals(str)) return true;
        else return false;
    }
    public static int countCertainChar(String str, String findChar)throws Exception{
        int count = 0;
        while(str.indexOf(findChar) != -1){
            count++;
            str = str.substring(str.indexOf(findChar) + findChar.length());
        }
        return count;
    }
    public static String formatRole (String[] str) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < str.length; i ++) {
            String temp = "";
            switch (str[i]) {
                case "1" : temp = "超级管理员"; break;
                case "2" : temp = "县合管办领导"; break;
                case "3" : temp = "县合管经办人"; break;
                case "4" : temp = "乡镇农合经办人"; break;
            }
            if(i == (str.length - 1))
                sb.append(temp);
            else
                sb.append(temp + ",");
        }
        return  sb.toString();
    }

    public static String formatMenu (int arg) {
        String str = "";
        switch (arg) {
            case 101 : str = "角色管理"; break;
            case 102 : str = "用户管理"; break;
            case 103 : str = "菜单管理"; break;
            case 104 : str = "农合经办点管理"; break;
            case 201 : str = "县管理"; break;
            case 202 : str = "镇管理"; break;
            case 203 : str = "村管理"; break;
            case 204 : str = "组管理"; break;
            case 301 : str = "家庭档案管理"; break;
            case 302 : str = "参合登记"; break;
            case 303 : str = "慢性病管理"; break;
            case 304 : str = "慢性病政策设置"; break;
            case 305 : str = "慢性病报销"; break;
            case 306 : str = "慢病报销情况统计查询"; break;
        }
        return str;
    }

    public static String formatArrToStr (String str[]) {
        StringBuffer arg = new StringBuffer();
        for (int i = 0; i < str.length; i ++) {
            if (!StringUtil.isEmpty(str[i])){
                if (i == str.length - 1)
                    arg.append(str[i]);
                else {
                    if (i == str.length - 2 || i == str.length - 3 || i == 0) {
                        if (StringUtil.isEmpty(str[i + 1]))
                            arg.append(str[i]);
                        else
                            arg.append(str[i] + ",");
                    } else {
                        arg.append(str[i] + ",");
                    }
                }
            }
        }
        return arg.toString();
    }
    public static String formatRsAge (String age) {
        return String.valueOf(Integer.parseInt(DateUtil.getCurrentTime().substring(0,4))  - Integer.parseInt(age));
    }
}
