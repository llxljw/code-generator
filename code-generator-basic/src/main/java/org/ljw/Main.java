package org.ljw;

import freemarker.template.TemplateException;
import org.ljw.generator.MainGenerator;
import org.ljw.generator.StaticGenerator;
import org.ljw.model.MainTemplateConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/15/16:19
 * @Description:
 */
public class Main {
    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig model = new MainTemplateConfig();
        model.setLoop(false);
        model.setAnther("ljw");
        model.setOutputText("今天又是摸鱼的一天");
        MainGenerator.doGenerator(model);
    }
}
