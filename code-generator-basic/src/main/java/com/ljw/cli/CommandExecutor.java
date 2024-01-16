package com.ljw.cli;

import com.ljw.cli.command.ConfigCommand;
import com.ljw.cli.command.GenerateCommand;
import com.ljw.cli.command.ListCommand;
import picocli.CommandLine;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/17/10:13
 * @Description: 命令调用者
 */
@CommandLine.Command(name = "execute",mixinStandardHelpOptions = true,description = "调用命令（调用者）")
public class CommandExecutor implements Runnable {
    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }


    @Override
    public void run() {
        // 不输入子命令时，给出友好提示
        System.out.println("请输入具体命令，或者输入 --help 查看命令提示");
    }

    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }
}
