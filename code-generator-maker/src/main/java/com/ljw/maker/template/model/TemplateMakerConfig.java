package com.ljw.maker.template.model;

import com.ljw.maker.meta.Meta;
import lombok.Data;

/**
 * @Author: ljw
 * @Date: 2024/02/26/15:35
 * @Description: 封装制作模板文件所需参数
 */
@Data
public class TemplateMakerConfig {
    private Long id;
    private Meta meta = new Meta();
    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
