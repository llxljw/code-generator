package com.ljw.maker.meta;

/**
 *
 * @Author: ljw
 * @Date: 2024/02/20/17:18
 * @Description: 自定义异常
 */
public class MetaException extends RuntimeException{
    public MetaException(String msg){
        super(msg);
    }

    public MetaException(String msg,Throwable cause){
        super(msg,cause);
    }
}
