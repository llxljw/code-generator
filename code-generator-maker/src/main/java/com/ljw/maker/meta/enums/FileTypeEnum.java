package com.ljw.maker.meta.enums;

/**
 * @Author: ljw
 * @Date: 2024/02/21/10:01
 * @Description: 文件枚举
 */
public enum FileTypeEnum {
    FILE("文件","file"),
    DIR("目录","dir");

    private String text;
    private String value;
    FileTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
