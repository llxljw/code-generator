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
import com.ljw.maker.template.model.FileFilterConfig;
import com.ljw.maker.template.model.TemplateMakerFileConfig;
import com.ljw.maker.template.model.TemplateMakerModelConfig;
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

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
//        String originSourceRootPath = new File(projectPath).getParent() + File.separator +"code-generator-demo-projects/acm-template-pro";
//        String inputFilePath = "src/com/ljw/acm/MainTemplate.java";
        String originSourceRootPath = new File(projectPath).getParent() + File.separator +"code-generator-demo-projects/springboot-init/springboot-init";
        String inputFilePath = "src/main/java/com/yupi/springbootinit";
        // 1.输入基本信息
//        String name = "acm-template-pro-generator";
//        String description = "ACM 示例模板生成器";
        String name = "springboot-init";
        String description = "springboot万用模版";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        // 1.第一次挖坑填充的数据
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("BaseResponse");
        modelInfo.setType("String");

//        // 模版要替换的字段
        String searchStr = "BaseResponse";

        // 指定文件
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/resources/application.yml";

// 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

// - 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

// - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModelInfoConfigList(modelInfoConfigList);

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigList1 = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();

        fileFilterConfigList1.add(fileFilterConfig);
        fileInfoConfig1.setFileFilterConfigList(fileFilterConfigList1);
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);
        templateMakerFileConfig.setFileInfoConfigList(Arrays.asList(fileInfoConfig1, fileInfoConfig2));

        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText11");
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组11");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);


        Long id = makeTemplate(meta, originSourceRootPath, templateMakerFileConfig, templateMakerModelConfig, null);
        System.out.println(id);

    }
    private static Long makeTemplate(Meta newMeta, String originSourceRootPath,
                                     TemplateMakerFileConfig templateMakerFileConfig,
                                     TemplateMakerModelConfig templateMakerModelConfig, Long id){
        // 没有id则生成
        if (id == null){
            id = IdUtil.getSnowflakeNextId();
        }
        // 复制原始模版文件到临时工作空间中
        String projectPath = System.getProperty("user.dir");
        String tempOutPath = projectPath + File.separator + ".temp";
        String templateOutPath = tempOutPath + File.separator + id;
        // 首次制作时要复制
        if (!FileUtil.exist(templateOutPath)){
            FileUtil.mkdir(templateOutPath);
            FileUtil.copy(originSourceRootPath,templateOutPath,false);
        }

        // 复制后输入文件根目录
        String sourceRootPath = templateOutPath + File.separator + FileUtil.getLastPathEle(Paths.get(originSourceRootPath)).toString();
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");

        // 生成模版文件
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFileInfoConfigList();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList){
            // 模版文件绝对路径
            String inputFileAbsolutPath = sourceRootPath + File.separator + fileInfoConfig.getPath();
            // 过滤条件
            List<FileFilterConfig> fileFilterConfigList = fileInfoConfig.getFileFilterConfigList();
            // 进行过滤，返回过滤后满足过滤条件的文件
            List<File> files = FileFilter.doFilter(inputFileAbsolutPath, fileFilterConfigList);
            for (File inputFile : files){
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(inputFile, templateMakerModelConfig, sourceRootPath);
                fileInfoList.add(fileInfo);
            }
        }

        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        // 分组信息不为空
        if (fileGroupConfig != null){
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

        // 处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModelInfoConfigList();
// - 转换为配置接受的 ModelInfo 对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());

// - 本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

// - 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            groupModelInfo.setGroupKey(groupKey);
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setCondition(condition);

            // 模型全放到一个分组内
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }

        String outputMetaPath = sourceRootPath + File.separator +"meta.json";
        if (FileUtil.exist(outputMetaPath)){
            // 读取久配置文件，然后进行追加
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(outputMetaPath), Meta.class);
            BeanUtil.copyProperties(newMeta,oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            //追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfos = newMeta.getFileConfig().getFileInfo();
            fileInfos.addAll(fileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfos = newMeta.getModelConfig().getModelInfo();
            modelInfos.addAll(newModelInfoList);
            // 去重
            newMeta.getFileConfig().setFileInfo(distinctFile(fileInfos));
            newMeta.getModelConfig().setModelInfo(distinctModel(modelInfos));

        }
        else {
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

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta),outputMetaPath);
        return id;
    }

    /**
     *  生成单个模版文件
     * @param inputFile
     * @param sourceRootPath
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        // 要挖坑的文件绝对路径（用于制作模板）
        // 注意 win 系统需要对路径进行转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入输出的相对路径（用于生成配置文件）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/","");
        String fileOutputPath = fileInputPath + ".ftl";
        String templateContent;
        // 如果已有模板文件，说明不是第一次制作，则在模板基础上再次挖坑
        if (FileUtil.exist(fileOutputAbsolutePath)){
            templateContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }
        else {
            templateContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = templateMakerModelConfig.getModelInfoConfigList();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String replacement;
        String newTemplateContent = templateContent;
        // 遍历所有的数据模型
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : modelInfoConfigList){
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
             newTemplateContent = StrUtil.replace(newTemplateContent,modelInfoConfig.getReplaceText(),replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        // 文件没有需要挖坑的地方时，为静态生成
        if (newTemplateContent.equals(templateContent)){
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            // 输出路径 = 输入路径
            fileInfo.setOutputPath(fileInputPath);
        }
        else {
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            // 生成模版文件
            FileUtil.writeUtf8String(newTemplateContent,fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    /**
     *文件去重
     */
    public static List<Meta.FileConfig.FileInfo> distinctFile(List<Meta.FileConfig.FileInfo> fileInfos){
        // 1.将所有文件配置（fileInfo）分为有分组的和无分组的
        // 有分组的
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfos.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        // 2.同组内的文件合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String,List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()){
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
            groupKeyMergedFileInfoMap.put(groupKey,newFileInfo);
        }
        // 3.将文件分组添加到结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        // 4. 将未分组的文件添加到结果列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfos.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)
                ).values()));
       return resultList;
    }

    /**
     *数据模型去重
     */
    public static List<Meta.ModelConfig.ModelInfo> distinctModel(List<Meta.ModelConfig.ModelInfo>  modelInfos){
        // 1.将所有文件配置（fileInfo）分为有分组的和无分组的
        // 有分组的
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyFileInfoListMap = modelInfos.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        // 2.同组内的文件合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String,List<Meta.ModelConfig.ModelInfo>> entry : groupKeyFileInfoListMap.entrySet()){
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
            groupKeyMergedFileInfoMap.put(groupKey,newFileInfo);
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
