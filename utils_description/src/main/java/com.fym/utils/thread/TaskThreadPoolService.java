package com.fym.utils.thread;

import com.fym.utils.interfaces.FutureTaskMethodInterface;
import com.fym.utils.interfaces.VoidMethodInterface;
import com.fym.utils.interfaces.VoidTaskMethodInterface;
import com.fym.utils.model.TaskResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by fengyiming on 2018/1/24.
 */

public class TaskThreadPoolService {

    @Inject
    private Logger logger;

    /**
     * 默认线程大小
     */
    private final static Integer DEFAULT_POOL_SIZE = 20;

    /**
     * 默认最大线程大小
     */
    private final static Integer DEFAULT_MAX_POOL_SIZE = 100;

    /**
     * 公共线程池
     */
    private final static ThreadPoolExecutor commonExecutor = new ThreadPoolExecutor(DEFAULT_MAX_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    /**
     * 将公共线程池的核心线程也设置空闲可回收
     */
    static {
        commonExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * 异步通过公共线程池执行方法
     * 适用于异步执行的方法
     *
     * @param voidMethodInterface
     * @throws Exception
     */
    public void asyncVoidTaskByCommonExecutor(VoidMethodInterface voidMethodInterface) throws Exception {
        try {
            commonExecutor.submit(() -> {
                try {

                    voidMethodInterface.exec();
                } catch (Throwable e) {
                    logger.error("asyncVoidTask" + e.getMessage(), e);
                } finally {

                }
            });
        } catch (Exception e) {
            logger.error("异步通过公共线程池执行方法出现异常");
            throw new Exception("asyncVoidTask error");
        }
    }

    /**
     * 多线程执行无返回值方法
     * 线程池大小为20，存活时间为0的无边界队列线程池
     * 不会同步等待所有任务执行结束
     *
     * @param voidTaskMethodInteface
     * @param list
     * @param <T>
     */
    public <T> void splitVoidTask(VoidTaskMethodInterface<T> voidTaskMethodInteface, List<T> list) throws Exception {
        splitVoidTask(voidTaskMethodInteface, list, DEFAULT_POOL_SIZE, false);
    }

    /**
     * 多线程执行无返回值方法
     * 线程池大小为20，存活时间为0的无边界队列线程池
     * 同步等待所有任务执行结束
     *
     * @param voidTaskMethodInteface
     * @param list
     * @param <T>
     */
    public <T> void splitVoidTask(VoidTaskMethodInterface<T> voidTaskMethodInteface, List<T> list, boolean wait) throws Exception {
        splitVoidTask(voidTaskMethodInteface, list, DEFAULT_POOL_SIZE, wait);
    }

    /**
     * 多线程执行无返回值方法
     * 线程池大小为20，存活时间为0的无边界队列线程池
     * 不会同步等待所有任务执行结束
     *
     * @param voidTaskMethodInteface
     * @param list
     * @param <T>
     */
    public <T> void splitVoidTask(VoidTaskMethodInterface<T> voidTaskMethodInteface, List<T> list, int poolSize) throws Exception {
        splitVoidTask(voidTaskMethodInteface, list, poolSize, false);
    }

    /**
     * 多线程执行无返回值方法(可指定部分线程池参数)
     *
     * @param voidTaskMethodInteface
     * @param list
     * @param <T>
     */
    public <T> void splitVoidTask(VoidTaskMethodInterface<T> voidTaskMethodInteface, List<T> list, int poolSize, boolean wait) throws Exception {
        if (poolSize < 0 || poolSize > DEFAULT_MAX_POOL_SIZE) {
            poolSize = DEFAULT_MAX_POOL_SIZE;
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        try {
            CountDownLatch countDownLatch = new CountDownLatch(list.size());
            for (T t : list) {
                executor.submit(() -> {
                    try {
                        voidTaskMethodInteface.exec(t);
                    } catch (Throwable e) {
                        logger.error("splitVoidTask" + e.getMessage(), e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            //如果需要等待所有任务执行结束
            if (wait) {
                try {
                    //等待线程全部执行完成
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            logger.error("多线程执行无返回值方法出现异常");
            throw new Exception("splitVoidTask error");
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 带返回值的多线程异步等待所有结果返回的
     * 线程池大小为20，存活时间为0的无边界队列线程池
     * 单个返回的结果不能返回null，影响结果判定
     * 如果发生异常会中断线程
     *
     * @param futureTaskMethodInteface
     * @param list
     * @param <T>
     * @param <F>
     * @return
     * @throws Exception
     */
    public <T, F> Map<T, F> splitFutureTask(FutureTaskMethodInterface<T, F> futureTaskMethodInteface, List<T> list) throws Exception {
        return splitFutureTask(futureTaskMethodInteface, list, DEFAULT_POOL_SIZE);
    }

    /**
     * 带返回值的多线程异步等待所有结果返回的(可指定部分线程池参数)
     * 单个返回的结果不能返回null，影响结果判定
     * future的get方法属于阻塞方法，无需使用计数器等待所有任务执行完毕
     * 如果发生异常会中断线程
     *
     * @param futureTaskMethodInteface
     * @param list
     * @param <T>
     * @param <F>
     * @return
     * @throws Exception
     */
    public <T, F> Map<T, F> splitFutureTask(FutureTaskMethodInterface<T, F> futureTaskMethodInteface, List<T> list, int poolSize) throws Exception {
        if (poolSize < 0 || poolSize > DEFAULT_MAX_POOL_SIZE) {
            poolSize = DEFAULT_MAX_POOL_SIZE;
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        try {
            Map<T, Future<F>> futureMap = new HashMap<>(list.size());
            for (T t : list) {
                Future<F> future = executor.submit(() -> {
                    try {
                        F f = futureTaskMethodInteface.exec(t);
                        return f;
                    } catch (Throwable e) {
                        logger.error("splitFutureTask" + e.getMessage(), e);
                        return null;
                    } finally {

                    }
                });
                futureMap.put(t, future);
            }
            Map<T, F> resultMap = new HashMap<>(futureMap.size());
            //检测每个线程的执行结果，如果有future的返回结果为null，则认为执行失败
            for (Map.Entry<T, Future<F>> entry : futureMap.entrySet()) {
                try {
                    if (entry.getValue().get() == null) {
                        throw new Exception("splitFutureTask error");
                    }
                    resultMap.put(entry.getKey(), entry.getValue().get());
                } catch (Exception e) {
                    logger.error("splitFutureTask error params:%s,error msg:%s", VJson.writeAsString(entry.getKey()), e.getMessage());
                    throw new Exception("splitFutureTask error");
                }
            }
            return resultMap;
        } catch (Exception e) {
            logger.error("多线程执行有返回值方法出现异常");
            throw new Exception("splitFutureTask error");
        } finally {
            executor.shutdown();
        }
    }


    /**
     * 带返回值的多线程异步等待所有结果返回的
     * 线程池大小为20，存活时间为0的无边界队列线程池
     * 如果发生异常不会中断线程
     *
     * @param futureTaskMethodInteface
     * @param list
     * @param <T>
     * @param <F>
     * @return
     * @throws Exception
     */
    public <T, F> Map<T, TaskResponse<T, F>> splitFutureTaskForErrorMsg(FutureTaskMethodInterface<T, F> futureTaskMethodInteface, List<T> list) throws Exception {
        return splitFutureTaskForErrorMsg(futureTaskMethodInteface, list, DEFAULT_POOL_SIZE);
    }

    /**
     * 带返回值的多线程异步等待所有结果返回的(可指定部分线程池参数)
     * future的get方法属于阻塞方法，无需使用计数器等待所有任务执行完毕
     * 如果发生异常不会中断线程，每个任务的执行结果放在TaskResponse里，每个单独的任务执行结果需要使用者手动判断。
     *
     * @param futureTaskMethodInteface
     * @param list
     * @param <T>
     * @param <F>
     * @return
     * @throws Exception
     */
    public <T, F> Map<T, TaskResponse<T, F>> splitFutureTaskForErrorMsg(FutureTaskMethodInterface<T, F> futureTaskMethodInteface, List<T> list, int poolSize) throws Exception {
        if (poolSize < 0 || poolSize > DEFAULT_MAX_POOL_SIZE) {
            poolSize = DEFAULT_MAX_POOL_SIZE;
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        try {
            Map<T, Future<TaskResponse<T, F>>> futureMap = new HashMap<>(list.size());
            for (T t : list) {
                Future<TaskResponse<T, F>> future = executor.submit(() -> {
                        try {
                            try {
                                F f = futureTaskMethodInteface.exec(t);
                                return TaskResponse.success(t, f);
                            } catch (Throwable e) {
                                logger.error("splitFutureTaskForErrorMsg" + e.getMessage(), e);
                                return TaskResponse.failed(t, e.getMessage());
                            } finally {

                            }
                        });
                        futureMap.put(t, future);
                    }
                    Map < T, TaskResponse < T, F >> resultMap = new HashMap<>(futureMap.size());
                for (Map.Entry<T, Future<TaskResponse<T, F>>> entry : futureMap.entrySet()) {
                    resultMap.put(entry.getKey(), entry.getValue().get());
                }
                return resultMap;
            } catch(Exception e){
                logger.error("多线程执行有返回值方法出现异常");
                throw new Exception("splitFutureTaskForErrorMsg error");
            } finally{
                executor.shutdown();
            }
        }
    }
