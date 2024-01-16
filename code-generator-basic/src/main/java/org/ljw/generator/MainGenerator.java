package org.ljw.generator;

import freemarker.template.TemplateException;
import org.ljw.model.MainTemplateConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/16/11:27
 * @Description:
 */
public class MainGenerator {
    public static void doGenerator(MainTemplateConfig model) throws TemplateException, IOException {
        // 获取当前项目的根目录
        String projectPath = System.getProperty("user.dir");
        File parentPath = new File(projectPath).getParentFile();
        // 模版路径
        String inputPath = new File(parentPath,"code-generator-demo-projects/acm-template").getAbsolutePath();

        String outputPath = projectPath;
        // 静态文件复制
        StaticGenerator.copyFilesByHutool(inputPath,projectPath);
        String inputDynamicFilePath = projectPath + File.separator + "src/main/resources/templates/mainTemplate.java.ftl";
        String outputDynamicFilePath = outputPath + File.separator + "mainTemplate.java";
        // 动态文件生成
        DynamicGenerator.doGenerator(inputDynamicFilePath,outputDynamicFilePath,model);
    }
}
