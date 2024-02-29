package com.ljw.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljw.web.model.entity.Generator;
import java.util.Date;
import java.util.List;

/**
 * 生成器数据库操作
 *
 */
public interface GeneratorMapper extends BaseMapper<Generator> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Generator> listGeneratorWithDelete(Date minUpdateTime);

}




