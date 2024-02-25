package com.ljw.maker.template.enums;

/**
 * @Author: ljw
 * @Date: 2024/02/21/10:01
 * @Description: 文件过滤范围枚举
 */
public enum FileFilterRuleEnum {
    CONTAINS("包含","fileName"),
    EQUALS("相等","fileContent"),
    STARTS_WITH("前缀匹配","fileContent"),
    ENDS_WITH("后缀匹配","fileContent"),
    REGEX("正则","fileContent");


    private String text;
    private String value;
    private String group;
    FileFilterRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    public static FileFilterRuleEnum getFileFilterRuleByValue(String value){
        if (value == null){
            return null;
        }
        for (FileFilterRuleEnum fileFilterRuleEnum : FileFilterRuleEnum.values()){
            if (fileFilterRuleEnum.getValue().equals(value)){
                return fileFilterRuleEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
