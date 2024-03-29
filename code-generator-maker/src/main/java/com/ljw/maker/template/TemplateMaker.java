package com.ljw.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ljw.maker.meta.Meta;
import com.ljw.maker.meta.enums.FileGenerateTypeEnum;
import com.ljw.maker.meta.enums.FileTypeEnum;
import com.ljw.maker.template.enums.FileFilterRangeEnum;
import com.ljw.maker.template.enums.FileFilterRuleEnum;
import com.ljw.maker.template.model.*;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ljw
 * @Date: 2024/02/24/0:08
 * @Description: 模版文件，配置文件生成
 */
public class TemplateMaker {

    public static Long makeTemplate(TemplateMakerConfig templateMakerConfig){
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();
        Long id = templateMakerConfig.getId();

        return makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, outputConfig,id);

    }
    public static Long makeTemplate(Meta newMeta, String originSourceRootPath,
                                     TemplateMakerFileConfig templateMakerFileConfig,
                                     TemplateMakerModelConfig templateMakerModelConfig,
                                    TemplateMakerOutputConfig templateMakerOutputConfig,
                                    Long id) {
        // 没有id则生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        // 复制原始模版文件到临时工作空间中
        String projectPath = System.getProperty("user.dir");
        String tempOutPath = projectPath + File.separator + ".temp";
        String templateOutPath = tempOutPath + File.separator + id;
        // 首次制作时要复制
        if (!FileUtil.exist(templateOutPath)) {
            FileUtil.mkdir(templateOutPath);
            FileUtil.copy(originSourceRootPath, templateOutPath, false);
        }

        // 复制后输入文件根目录
//        String sourceRootPath = templateOutPath + File.separator + FileUtil.getLastPathEle(Paths.get(originSourceRootPath)).toString();
//        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        String sourceRootPath = FileUtil.loopFiles(new File(templateOutPath),1,null).stream()
                .filter(FileUtil::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new).getAbsolutePath();

        // 注意 win 系统需要对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        // 生成模版文件
        List<Meta.FileConfig.FileInfo> fileInfoList = getFileInfoList(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);

        // 生成配置文件
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);

        String outputMetaPath = templateOutPath + File.separator + "meta.json";
        if (FileUtil.exist(outputMetaPath)) {
            // 读取久配置文件，然后进行追加
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(outputMetaPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            //追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfos = newMeta.getFileConfig().getFileInfo();
            fileInfos.addAll(fileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfos = newMeta.getModelConfig().getModelInfo();
            modelInfos.addAll(newModelInfoList);
            // 去重
            newMeta.getFileConfig().setFileInfo(distinctFile(fileInfos));
            newMeta.getModelConfig().setModelInfo(distinctModel(modelInfos));

        } else {
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            fileConfig.setSourceRootPath(sourceRootPath);
            newMeta.setFileConfig(fileConfig);
            List<Meta.FileConfig.FileInfo> fileInfos = new ArrayList<>();
            fileInfos.addAll(fileInfoList);
            fileConfig.setFileInfo(fileInfos);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfos = new ArrayList<>();
            modelInfos.addAll(newModelInfoList);
            modelConfig.setModelInfo(modelInfos);
        }
        if (templateMakerOutputConfig != null){
            if (templateMakerOutputConfig.isRemoveGroupFilesFromRoot()){
                List<Meta.FileConfig.FileInfo> fileInfos = TemplateMakerUtils.removeGroupFilesFromRoot(newMeta.getFileConfig().getFileInfo());
                newMeta.getFileConfig().setFileInfo(fileInfos);
            }
        }
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), outputMetaPath);
        return id;
    }

    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        // - 本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        // 处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModelInfoConfigList();
        if (CollUtil.isEmpty(models)){
            return newModelInfoList;
        }
        // - 转换为配置接受的 ModelInfo 对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            modelInfo.setFieldName(modelInfoConfig.getFieldName());
            modelInfo.setType(modelInfoConfig.getType());
            modelInfo.setDefaultValue(modelInfoConfig.getDefaultValue());
            modelInfo.setDescription(modelInfoConfig.getDescription());
            return modelInfo;
        }).collect(Collectors.toList());

        // - 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();

            BeanUtil.copyProperties(modelGroupConfig,groupModelInfo);
            // 模型全放到一个分组内
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    private static List<Meta.FileConfig.FileInfo> getFileInfoList(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFileInfoConfigList();
        if (CollUtil.isEmpty(fileInfoConfigList)){
            return fileInfoList;
        }
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {
            // 模版文件绝对路径
            String inputFileAbsolutPath = sourceRootPath + File.separator + fileInfoConfig.getPath();
            // 过滤条件
            List<FileFilterConfig> fileFilterConfigList = fileInfoConfig.getFileFilterConfigList();
            // 进行过滤，返回过滤后满足过滤条件的文件
            List<File> files = FileFilter.doFilter(inputFileAbsolutPath, fileFilterConfigList);

            // 排除掉.ftl文件
            files = files.stream()
                    .filter(file -> !file.getName().endsWith(".ftl"))
                    .collect(Collectors.toList());
            for (File inputFile : files) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(inputFile, templateMakerModelConfig,fileInfoConfig, sourceRootPath);
                fileInfoList.add(fileInfo);
            }
        }


        // 构建配置文件信息
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        // 分组信息不为空
        if (fileGroupConfig != null) {
            Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
            fileInfo.setGroupKey(fileGroupConfig.getGroupKey());
            fileInfo.setGroupName(fileGroupConfig.getGroupName());
            fileInfo.setCondition(fileGroupConfig.getCondition());
            fileInfo.setType(FileTypeEnum.GROUP.getValue());
            // 过滤后的文件放入一个组中
            fileInfo.setFiles(fileInfoList);
            fileInfoList = new ArrayList<>();
            fileInfoList.add(fileInfo);
        }
        return fileInfoList;
    }

    /**
     * 生成单个模版文件
     *
     * @param inputFile
     * @param sourceRootPath
     */
    public static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, TemplateMakerModelConfig templateMakerModelConfig,
                                                            TemplateMakerFileConfig.FileInfoConfig fileInfoConfig,
                                                            String sourceRootPath) {
        // 要挖坑的文件绝对路径（用于制作模板）
        // 注意 win 系统需要对路径进行转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入输出的相对路径（用于生成配置文件）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";
        String templateContent;
        // 如果已有模板文件，说明不是第一次制作，则在模板基础上再次挖坑
        boolean hasTemplate = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplate) {
            templateContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            templateContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = templateMakerModelConfig.getModelInfoConfigList();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String replacement;
        String newTemplateContent = templateContent;
        // 遍历所有的数据模型
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : modelInfoConfigList) {
            // 不是分组
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                // 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                // 注意挖坑要多一个层级
                replacement = String.format("${%s.%s}", groupKey, modelInfoConfig.getFieldName());
            }
            // 原文件替换
            newTemplateContent = StrUtil.replace(newTemplateContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        // 在制作工具中是根据元信息中的模板文件路径生成需要的java文件
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setCondition(fileInfoConfig.getCondition());
        // 默认生成类型为动态
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        boolean contentsEquals = newTemplateContent.equals(templateContent);
        // 原先没有模板文件
        if (!hasTemplate) {
//            没有要替换的内容时，为静态生成
            if (contentsEquals){
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                // 输出路径 = 输入路径
                fileInfo.setInputPath(fileInputPath);
            }
//            有要替换的内容时，为动态生成
            else {
                FileUtil.writeUtf8String(newTemplateContent, fileOutputAbsolutePath);
            }
        } else if(!contentsEquals){
            // 有模板文件，且增加了新坑，生成模板文件
            FileUtil.writeUtf8String(newTemplateContent, fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    /**
     * 文件去重
     */
    private static List<Meta.FileConfig.FileInfo> distinctFile(List<Meta.FileConfig.FileInfo> fileInfos) {
        // 1.将所有文件配置（fileInfo）分为有分组的和无分组的
        // 有分组的
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfos.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        // 2.同组内的文件合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            // 对合并后的分组文件去重
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(tempInfo -> tempInfo.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r))
                    .values());
            // 使用最新的group
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }
        // 3.将文件分组添加到结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        // 4. 将未分组的文件添加到结果列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfos.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                ).values()));
        return resultList;
    }

    /**
     * 数据模型去重
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModel(List<Meta.ModelConfig.ModelInfo> modelInfos) {
        // 1.将所有文件配置（fileInfo）分为有分组的和无分组的
        // 有分组的
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyFileInfoListMap = modelInfos.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        // 2.同组内的文件合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempFileInfoList = entry.getValue();
            // 对合并后的分组文件去重
            List<Meta.ModelConfig.ModelInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(tempInfo -> tempInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r))
                    .values());
            // 使用最新的group
            Meta.ModelConfig.ModelInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setModels(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }
        // 3.将文件分组添加到结果列表
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        // 4. 将未分组的文件添加到结果列表
        List<Meta.ModelConfig.ModelInfo> noGroupFileInfoList = modelInfos.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                ).values()));
        return resultList;
    }

}
