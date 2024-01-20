package com.ljw;

import freemarker.template.TemplateException;
import com.ljw.cli.CommandExecutor;
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
//        args = new String[]{"generate","-l","-a","-o"};
//        args = new String[]{"list"};
//        args = new String[]{"config"};
        // 命令行
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}
