package com.ljw.maker;

import com.ljw.maker.generator.main.MainGenerator;
import com.ljw.maker.meta.enums.FileTypeEnum;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 *
 * @Author: ljw
 * @Date: 2024/01/15/16:19
 * @Description:
 */
public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}
