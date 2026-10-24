package com.hope.xueling.common.util;
/**
 * 线程本地变量工具类
 * 提供线程本地变量的存取和移除功能
 * @author 谢光湘
 * @since 2026/1/22
 */
public class ThreadLocalUtils {
    /**
     * 请求级线程上下文，存放当前请求解析出的 JWT 载荷
     */
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取当前线程绑定的值
     * @return 当前线程绑定的值，未设置时为 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    /**
     * 向当前线程绑定值
     * @param value 待绑定的值，通常是 JWT 载荷
     */
    public static  void set(Object value){
        THREAD_LOCAL.set(value);
    }

    /**
     * 清除当前线程绑定的值，必须在请求结束时调用以避免线程池复用导致的数据串用
     */
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
