package com.ljw.generator;

import freemarker.template.TemplateException;
import com.ljw.model.MainTemplateConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/16/11:27
 * @Description: 动态，静态文件生成
 */
public class MainGenerator {
    public static void doGenerate(Object model) throws TemplateException, IOException {
//        String inputRootPath ="code-generator/code-generator-demo-projects/acm-template-pro";
//        String outputRootPath = "code-generator/acm-template";
//
//        String inputPath;
//        String outputPath;
//
//        inputPath = new File(inputRootPath,"src/com/ljw/acm/MainTemplate.java.ftl").getAbsolutePath();
//        outputPath = new File(outputRootPath,"src/com/ljw/acm/MainTemplate.java").getAbsolutePath();
//        DynamicGenerator.doGenerate(inputPath, outputPath, model);
        String inputRootPath = "D:\\code-generator\\code-generator-demo-projects\\acm-template-pro";
        String outputRootPath = "D:\\code-generator\\code-generator-demo-projects\\acm-template-pro";

        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath, "src/com/ljw/acm/MainTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath, "src/com/ljw/acm/MainTemplate.java").getAbsolutePath();
        DynamicGenerator.doGenerate(inputPath, outputPath, model);

        inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);

        inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
        outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
    }
}
