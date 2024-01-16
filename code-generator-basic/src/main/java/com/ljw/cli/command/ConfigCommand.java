package com.ljw.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.ljw.model.MainTemplateConfig;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/17/10:09
 * @Description: 查看允许用户传入的动态参数信息命令
 */
@Command(name = "config",mixinStandardHelpOptions = true,description = "查看参数信息")
public class ConfigCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("查看参数信息");
        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);
        for (Field field : fields){
            System.out.println("字段名称:" + field.getName());
            System.out.println("字段类型:" + field.getType());
            System.out.println("---");
        }
        return 0;
    }
}
