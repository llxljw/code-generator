package com.ljw.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.ljw.maker.generator.file.DynamicFileGenerator;
import com.ljw.maker.meta.Meta;
import com.ljw.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/20/23:36
 * @Description:
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMeta();
        System.out.println(meta);

        // 输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // Java 包基础路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model";
        if (!FileUtil.exist(outputFilePath)){
            FileUtil.mkdir(outputFilePath);
        }
        DynamicFileGenerator.doGenerate(inputFilePath , outputFilePath + File.separator +"DataModel.java", meta);

        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/";
        if (!FileUtil.exist(outputFilePath)){
            FileUtil.mkdir(outputFilePath);
        }
        DynamicFileGenerator.doGenerate(inputFilePath , outputFilePath+"ConfigCommand.java", meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/";
        if (!FileUtil.exist(outputFilePath)){
            FileUtil.mkdir(outputFilePath);
        }
        DynamicFileGenerator.doGenerate(inputFilePath , outputFilePath+"GenerateCommand.java", meta);

        // cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/";
        if (!FileUtil.exist(outputFilePath)){
            FileUtil.mkdir(outputFilePath);
        }
        DynamicFileGenerator.doGenerate(inputFilePath , outputFilePath+"ListCommand.java", meta);

        // cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/";
        if (!FileUtil.exist(outputFilePath)){
            FileUtil.mkdir(outputFilePath);
        }
        DynamicFileGenerator.doGenerate(inputFilePath , outputFilePath+"CommandExecutor.java", meta);

        // Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath;
        if (!FileUtil.exist(outputFilePath)){
            FileUtil.mkdir(outputFilePath);
        }
        DynamicFileGenerator.doGenerate(inputFilePath , outputFilePath+"/Main.java", meta);
    }
}
