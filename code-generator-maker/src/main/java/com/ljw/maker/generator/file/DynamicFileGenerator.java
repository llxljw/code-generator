package com.ljw.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import com.ljw.maker.model.DataModel;

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
public class DynamicFileGenerator {
    public static void doGenerate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        // 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName,"utf-8");
        if (!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }
        Writer out = new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath + File.separator +"DataModel.java")), StandardCharsets.UTF_8);
//        Writer out = new FileWriter(outputPath);
        template.process(model, out);
        out.close();
    }
}
