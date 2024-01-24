package com.ljw.maker.meta;

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
        private String outputRootPath;
        private String type;
        private List<Files> fileInfo;

        @NoArgsConstructor
        @Data
        public static class Files {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfig {
        private List<Models> modelInfo;

        @NoArgsConstructor
        @Data
        public static class Models {
            private String fieldName;
            private String type;
            private String description;
            private Boolean defaultValue;
            private String abbr;
        }
    }
}
