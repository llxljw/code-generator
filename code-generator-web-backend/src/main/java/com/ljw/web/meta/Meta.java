package com.ljw.web.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther: ljw
 * @Date: 2024/01/20/19:14
 * @Description:
 */
@NoArgsConstructor
@Data
public class Meta {
    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfig {
        private String inputRootPath;
        private String sourceRootPath;
        private String outputRootPath;
        private String type;
        private List<FileInfo> fileInfo;

        @NoArgsConstructor
        @Data
        public static class FileInfo {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
            private String condition;
            private String groupKey;
            private String groupName;
            private List<FileInfo> files;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfig {
        private List<ModelInfo> modelInfo;

        @NoArgsConstructor
        @Data
        public static class ModelInfo {
            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;
            private String abbr;
            private String needGit;
            private String groupKey;
            private String groupName;
            private List<ModelInfo> models;
            private String condition;
            //分组下的参数拼接
            private String allArgsStr;
        }
    }
}
