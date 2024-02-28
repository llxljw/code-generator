package com.ljw.maker.template.model;

import lombok.Data;

/**
 * @Author: ljw
 * @Date: 2024/02/27/11:04
 * @Description: 输出文件规则（比如是否对组内外相同的内容去重）
 */
@Data
public class TemplateMakerOutputConfig {
    private boolean removeGroupFilesFromRoot = true;
}
