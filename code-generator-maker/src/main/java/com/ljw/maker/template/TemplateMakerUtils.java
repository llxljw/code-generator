package com.ljw.maker.template;

import cn.hutool.core.util.StrUtil;
import com.ljw.maker.meta.Meta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ljw
 * @Date: 2024/02/27/11:09
 * @Description: 模板制作工具类
 */
public class TemplateMakerUtils {
    /**
     *  从未分组文件中移除跟组内相同的文件
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList){
        // 1.获取所有的分组信息
        List<Meta.FileConfig.FileInfo> groupFileList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        // 2.展开分组集合,获取集合内的所有文件
        List<Meta.FileConfig.FileInfo> groupInnerFileList = groupFileList.stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());
        // 3.获取文件内的inputPath
        List<String> groupFileInputPath = groupInnerFileList.stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toList());
        // 4.移除未分组文件中相同的文件
        List<Meta.FileConfig.FileInfo> distinctFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> !groupFileInputPath.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
        return distinctFileInfoList;
    }
}
