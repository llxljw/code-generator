package com.ljw.web.mapper;

import com.ljw.web.model.entity.Generator;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子数据库操作测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class GeneratorMapperTest {

    @Resource
    private GeneratorMapper generatorMapper;

    @Test
    void listGeneratorWithDelete() {
        List<Generator> generatorList = generatorMapper.listGeneratorWithDelete(new Date());
        Assertions.assertNotNull(generatorList);
    }
}
