package com.ljw.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/17/10:08
 * @Description: 遍历所有原始文件命令
 */

@Command(name = "list",mixinStandardHelpOptions = true,description = "遍历所有原始文件")
public class ListCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        // 获取当前项目的根目录
        String projectPath = System.getProperty("user.dir");
        File parentPath = new File(projectPath).getParentFile();
        // 模版路径
        String inputPath = new File(parentPath,"code-generator-demo-projects/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files){
            System.out.println(file);
        }
        return 0;
    }
}
