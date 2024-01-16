package com.ljw.model;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/16/11:08
 * @Description: 数据模型实体
 */
@Data
public class MainTemplateConfig {
    /**
     * 是否循环
     */
    private boolean loop;
    /**
     * 作者
     */
    private String anther = "ljw";
    /**
     * 输出信息
     */
    private String outputText = "Hello, World!";
}
