package com.ljw.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
        List<String> inputFilePathList = Arrays.asList(inputFilePath1, inputFilePath2);
        // 第二次挖坑填充的数据
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("className");
//        modelInfo.setType("String");
//        String searchStr = "MainTemplate";

//        Long id = makeTemplate(meta, originSourceRootPath, inputFilePath, modelInfo, searchStr, null);
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
        Long id = makeTemplate(meta, originSourceRootPath, templateMakerFileConfig, modelInfo, searchStr, null);
        System.out.println(id);

    }
    private static Long makeTemplate(Meta newMeta, String originSourceRootPath, TemplateMakerFileConfig templateMakerFileConfig, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, Long id){
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
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(inputFile, modelInfo, searchStr, sourceRootPath);
                fileInfoList.add(fileInfo);
            }
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
            modelInfos.add(modelInfo);
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
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelInfoList.add(modelInfo);
            modelConfig.setModelInfo(modelInfoList);
        }

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta),outputMetaPath);
        return id;
    }

    /**
     *  生成单个模版文件
     * @param inputFile
     * @param modelInfo
     * @param searchStr
     * @param sourceRootPath
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath) {
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
        // 替换串
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        // 原文件替换
        String newTemplateContent = StrUtil.replace(templateContent, searchStr, replacement);

        // 生成配置文件
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
            // 生成文件
            FileUtil.writeUtf8String(newTemplateContent,fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    /**
     *文件去重
     */
    public static List<Meta.FileConfig.FileInfo> distinctFile(List<Meta.FileConfig.FileInfo> fileInfos){
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>( fileInfos.stream()
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath,o ->o,(e,r) -> r))
                .values());
       return newFileInfoList;
    }

    /**
     *数据模型去重
     */
    public static List<Meta.ModelConfig.ModelInfo> distinctModel(List<Meta.ModelConfig.ModelInfo>  modelInfos){
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(modelInfos.stream()
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,o ->o,(e,r) -> r))
                .values());
        return newModelInfoList;
    }

}
