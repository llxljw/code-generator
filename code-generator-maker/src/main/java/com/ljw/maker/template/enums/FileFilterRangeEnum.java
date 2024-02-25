package com.ljw.maker.template.enums;

/**
 * @Author: ljw
 * @Date: 2024/02/21/10:01
 * @Description: 文件过滤范围枚举
 */
public enum FileFilterRangeEnum {
    FILE_NAME("文件名","fileName"),
    FILE_CONTENT("文件内容","fileContent");

    private String text;
    private String value;
    private String group;
    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    public static FileFilterRangeEnum getFileFilterRangeByValue(String value){
        if (value == null){
            return null;
        }
        for (FileFilterRangeEnum fileFilterRangeEnum : FileFilterRangeEnum.values()){
            if (fileFilterRangeEnum.getValue().equals(value)){
                return fileFilterRangeEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
