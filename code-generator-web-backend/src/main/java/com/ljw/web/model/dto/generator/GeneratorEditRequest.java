package com.ljw.web.model.dto.generator;

import com.ljw.web.meta.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求
 *
 */
@Data
public class GeneratorEditRequest implements Serializable {

    private Long id;
    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;


    private static final long serialVersionUID = 1L;
}
