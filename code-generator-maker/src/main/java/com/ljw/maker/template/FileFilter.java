package com.ljw.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.ljw.maker.template.enums.FileFilterRangeEnum;
import com.ljw.maker.template.enums.FileFilterRuleEnum;
import com.ljw.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ljw
 * @Date: 2024/02/24/23:11
 * @Description: 文件过滤
 */
public class FileFilter {

    /**
     * 对某个文件或目录进行过滤，返回文件列表
     *
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath,List<FileFilterConfig> fileFilterConfigList){
        // 获取所有文件
        List<File> files = FileUtil.loopFiles(filePath);
        return files.stream().filter(file ->doSingleFileFilter(file,fileFilterConfigList))
                .collect(Collectors.toList());
    }
    /**
     * 单个文件过滤
     *
     * @param fileFilterConfigList 过滤规则
     * @param file 单个文件
     * @return 是否保留
     */
    public static boolean doSingleFileFilter(File file, List<FileFilterConfig> fileFilterConfigList){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);
        boolean result = true;
        if (CollUtil.isEmpty(fileFilterConfigList)) {
            return true;
        }
        for (FileFilterConfig fileFilterConfig : fileFilterConfigList){
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();
            FileFilterRangeEnum fileFilterRangeByValue = FileFilterRangeEnum.getFileFilterRangeByValue(range);
            if (fileFilterRangeByValue == null){
                continue;
            }
            String content = fileName;
            switch(fileFilterRangeByValue){
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }

            FileFilterRuleEnum fileFilterRuleByValue = FileFilterRuleEnum.getFileFilterRuleByValue(rule);
            if (fileFilterRuleByValue == null){
                continue;
            }
            switch (fileFilterRuleByValue){
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                default:
            }
            // 一个不满足就返回false
            if (!result){
                return false;
            }
        }
        // 都满足返回true
        return true;
    }
}
