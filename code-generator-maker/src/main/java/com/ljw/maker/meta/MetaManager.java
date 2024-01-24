package com.ljw.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: ljw
 * @Date: 2024/01/20/22:16
 * @Description: 双检索生成单例的元信息实体
 */
public class MetaManager {
    private static volatile Meta meta;
    private MetaManager(){}
     public static Meta getMeta(){
        if (meta == null){
            synchronized(Meta.class){
                if (meta == null){
                    meta = initMeta();
                }
            }
        }
        return meta;
     }

     public static Meta initMeta(){
         String metaJson = ResourceUtil.readUtf8Str("meta.json");
         Meta meta = JSONUtil.toBean(metaJson, Meta.class);
         Meta.FileConfig fileConfig = meta.getFileConfig();
         return meta;
     }
}
