package com.ljw.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import com.ljw.model.MainTemplateConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/16/11:18
 * @Description: 动态文件生成
 */
public class DynamicGenerator {
    public static void doGenerate(String inputPath,String outputPath,Object model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        // 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName,"utf-8");

        System.out.println(outputPath);
        Writer out = new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8);
        template.process(model, out);
        out.close();
    }
}
