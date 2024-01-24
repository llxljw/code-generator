package com.ljw.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import com.ljw.maker.generator.file.FileGenerator;
import com.ljw.maker.model.DataModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/17/10:10
 * @Description: 生成文件命令
 */
@Command(name = "generate",mixinStandardHelpOptions = true,description = "生成代码")
@Data
public class GenerateCommand implements Callable<Integer> {

    @Option(names = {"-l","--loop"},interactive = true,arity = "0..1",echo = true,description = "是否循环")
    boolean loop;
    @Option(names = {"-a","--anther"},interactive = true,arity = "0..1",echo = true,description = "作者")
    String anther = "ljw";
    @Option(names = {"-o","--outputText"},interactive = true,arity = "0..1",echo = true,description = "输出内容")
    String outputText = "Hello world";
    @Override
    public Integer call() throws Exception {
        DataModel mainTemplateConfig = new DataModel();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        System.out.println("数据模型为:"+mainTemplateConfig);
        FileGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
