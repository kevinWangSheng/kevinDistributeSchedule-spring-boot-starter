package cn.kevinwang.schedule.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author wang
 * @create 2024-01-16-16:46
 */
public class StringUtil {
    public static String join(String ... strs){
        if(strs == null ||strs.length == 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(String str:strs){
            if(StringUtils.isNotBlank(str)) {
                sb.append(str);
            }
        }
        return sb.toString();
    }
}
