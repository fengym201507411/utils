package com.fym.utils.model;

/**
 * Created by fengyiming on 2018/4/25.
 */
@Data
public class TaskResponse<T, F> {

    /**
     * 任务成功标识
     */
    private boolean success;

    /**
     * 任务的key
     */
    private T t;

    /**
     * 任务成功返回体
     */
    private F f;

    /**
     * 任务异常信息
     */
    private String errorMsg;


    /**
     * 成功
     *
     * @param t
     * @param f
     * @param <T>
     * @param <F>
     * @return
     */
    public static <T, F> TaskResponse success(T t, F f) {
        TaskResponse response = new TaskResponse();
        response.setSuccess(true);
        response.setT(t);
        response.setF(f);
        return response;
    }

    /**
     * 失败
     *
     * @param t
     * @param <T>
     * @param <F>
     * @return
     */
    public static <T, F> TaskResponse failed(T t, String errorMsg) {
        TaskResponse response = new TaskResponse();
        response.setSuccess(false);
        response.setT(t);
        response.setErrorMsg(errorMsg);
        return response;
    }
}
