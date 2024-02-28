package com.ljw.maker.template.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author: ljw
 * @Date: 2024/02/25/16:50
 * @Description:
 */
@Data
public class TemplateMakerModelConfig {
    private List<ModelInfoConfig> modelInfoConfigList;
    private ModelGroupConfig modelGroupConfig;
    @Data
    public static class ModelInfoConfig{
        private String fieldName;
        private String type;
        private String description;
        private String defaultValue;
        private String abbr;
        // 用于替换哪些文本
        private String replaceText;
    }
    @Data
    public static class ModelGroupConfig{
        private String condition;

        private String groupKey;

        private String groupName;

        private String type;

        private String description;
    }
}
