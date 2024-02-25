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
    @Data
    public static class FileInfoConfig{
        /**
         * 文件路径
         */
        private String path;

        /**
         * 文件过滤规则集合
         */
        private List<FileFilterConfig> fileFilterConfigList;
    }
}
