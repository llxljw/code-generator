package org.ljw.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.ljw.model.MainTemplateConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/16/11:18
 * @Description: 动态文件生成
 */
public class DynamicGenerator {
    public static void doGenerator(String inputPath,String outputPath,MainTemplateConfig model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        // 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        Writer out = new FileWriter(outputPath);
        template.process(model, out);
        out.close();
    }
}
