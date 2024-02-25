package com.ljw.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: ljw
 * @Date: 2024/02/24/22:53
 * @Description: 文件过滤配置
 */
@Data
@Builder
public class FileFilterConfig {
    /**
     * 过滤范围（名字，内容）
     */
    private String range;

    /**
     * 过滤规则
     */
    private String rule;
    /**
     * 过滤值
     */
    private String value;

}
