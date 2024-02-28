package com.ljw.maker.template.model;

import com.ljw.maker.meta.Meta;
import lombok.Data;

import java.util.List;

/**
 * @Author: ljw
 * @Date: 2024/02/24/22:57
 * @Description: 封装所有文件的所有配置
 */
@Data
public class TemplateMakerFileConfig {
    List<FileInfoConfig> fileInfoConfigList;

    /**
     * 文件分组（一个TemplateMakerFileConfig下的所有文件为一组）
     */
    FileGroupConfig fileGroupConfig;
    @Data
    public static class FileGroupConfig{
        private String condition;
        private String groupKey;
        private String groupName;
    }
    @Data
    public static class FileInfoConfig{
        /**
         * 文件路径
         */
        private String path;

        /**
         * 控制单个文件是否生成
         */
        private String condition;

        /**
         * 文件过滤规则集合
         */
        private List<FileFilterConfig> fileFilterConfigList;
    }
}
