package com.ljw.maker.generator.file;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/15/16:50
 * @Description: 静态文件生成
 */
public class StaticFileGenerator {
    /**
     * 拷贝文件（Hutool 实现，会将输入目录完整拷贝到输出目录下）
     *
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByHutool(String inputPath,String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }

}
