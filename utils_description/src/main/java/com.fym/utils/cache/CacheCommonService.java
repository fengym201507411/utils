package com.fym.utils.cache;

/**
 * Created by fengyiming on 2018/1/24.
 */
public class CacheCommonService {

    /**
     * 防止缓存穿透设置的值
     */
    private final static String PREVENT_PENETRATE_VALUE = "THIS_KEY_NO_VALUE";

    /**
     * 防止缓存穿透设置的值的过期时间
     */
    private final static int DEFAULT_EXPIRE_TIME_SECONDS = 10 * 60;

    /**
     * 防止缓存穿透设置的值的过期时间(单位/s)
     */
    private final static int PREVENT_PENETRATE_TIME_SECONDS = 60;

    /**
     * 最长重试时间(单位/s)
     */
    private final static int DEFAULT_MAX_RETRY_TIME_SECONDS = 60;

    /**
     * 随机数
     */
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * 随机等待时间最小值
     */
    private static final int WAIT_INTERVAL_MIN_MILLS = 10;

    /**
     * 随机等待时间最大值
     */
    private static final int WAIT_INTERVAL_MAX_MILLS = 100;

    /**
     * 获取缓存，有全局缓存开关判断，全走默认配置，默认防止缓存穿透
     *
     * @param f              继承于RedisCache
     * @param key            缓存key
     * @param getValueMethod 如果缓存没东西怎么获得值
     * @param typeToken      缓存value的数据类型
     * @return
     * @throws Exception
     */
    public <V, F extends RedisCache> V getCache(F f, String key, GetValueMethodInterface<V> getValueMethod, TypeToken<V> typeToken) throws Exception {
        return getCache(f, key, null, getValueMethod, true, DEFAULT_EXPIRE_TIME_SECONDS, typeToken);
    }


    /**
     * 获取缓存，有全局缓存开关判断，配置缓存过期时间，默认防止缓存穿透
     *
     * @param f              继承于RedisCache
     * @param key            缓存key
     * @param getValueMethod 如果缓存没东西怎么获得值
     * @param expireSeconds  过期时间
     * @param typeToken      缓存value的数据类型
     * @return
     * @throws Exception
     */
    public <V, F extends RedisCache> V getCache(F f, String key, GetValueMethodInterface<V> getValueMethod, int expireSeconds, TypeToken<V> typeToken) throws Exception {
        return getCache(f, key, null, getValueMethod, true, expireSeconds, typeToken);

    }

    /**
     * 获取缓存，有全局缓存开关判断，默认防止缓存穿透，配置缓存过期时间、会依据配置的缓存打点的key有是否缓存指定缓存开关判定，单独的打点
     *
     * @param f              继承于RedisCache
     * @param key            缓存key
     * @param metricKey      缓存key所属的打点类型key
     * @param getValueMethod 如果缓存没东西怎么获得值
     * @param typeToken      缓存value的数据类型
     * @return
     * @throws Exception
     */
    public <V, F extends RedisCache> V getCache(F f, String key, String metricKey, GetValueMethodInterface<V> getValueMethod, TypeToken<V> typeToken) throws Exception {
        return getCache(f, key, metricKey, getValueMethod, true, DEFAULT_EXPIRE_TIME_SECONDS, typeToken);
    }

    /**
     * 获取缓存，有全局缓存开关判断，默认防止缓存穿透，配置缓存过期时间、会依据配置的缓存打点的key有是否缓存指定缓存开关判定，单独的打点
     *
     * @param f              继承于RedisCache
     * @param key            缓存key
     * @param metricKey      缓存key所属的打点类型key
     * @param getValueMethod 如果缓存没东西怎么获得值
     * @param expireSeconds  过期时间
     * @param typeToken      缓存value的数据类型
     * @return
     * @throws Exception
     */
    public <V, F extends RedisCache> V getCache(F f, String key, String metricKey, GetValueMethodInterface<V> getValueMethod, int expireSeconds, TypeToken<V> typeToken) throws Exception {
        return getCache(f, key, metricKey, getValueMethod, true, expireSeconds, typeToken);
    }

    /**
     * 封装获取缓存值的方法
     * 注意：1、getValueMethod方法里尽量不要抛异常，因为可能会影响缓存穿透的功能
     * 2、preventPenetrateFlag设置成false的话有缓存穿透的风险
     * 3、内置redis缓存开关，有做全局缓存开关和指定缓存开关
     * <p>
     * 1、需要传递过来redisache
     * 2、根据key拿到对应的string类型的value值，判断value是否为null 走第3步，不为null走第4步
     * 3、根据从db拿值的方法函数取出对应的value，并对此次的value再次判空处理，为null第5步，不为null第6步
     * 4、判断是否与设置的缓存穿透的值相等，如果相等直接返回null，不相等则进行反序列化，处理成对应的返回值类型返回数据
     * 5、直接将缓存穿透的值设置到该key对应的value里，并返回null
     * 6、将值设置到该key对应的value里，并返回该对象
     *
     * @return
     */
    public <V, F extends RedisCache> V getCache(F f, String key, String metricKey, GetValueMethodInterface<V> getValueMethod, boolean preventPenetrateFlag, int expireSeconds, TypeToken<V> typeToken) throws Exception {
        //全局缓存开关配置
        if (HuskarSwitchCommonSettings.closeCache()) {
            TraceUtils.notHitCache();
            return getValueMethod.getValue();
        }

        //指定缓存key的配置
        if (!StringUtils.isEmpty(metricKey)) {
            //指定类型关闭缓存
            if (HuskarSwitchCommonSettings.closeDataCache(metricKey)) {
                TraceUtils.notHitCache(MetricEnum.MISS_CACHE.getType(), metricKey);
                return getValueMethod.getValue();
            }
        }

        //缓存的计算
        String value = f.get(key);
        if (value == null) {
            if (!StringUtils.isEmpty(metricKey)) {
                TraceUtils.notHitCache(MetricEnum.MISS_CACHE.getType(), metricKey);
            }
            V v = getValueMethod.getValue();
            if (v == null) {
                if (preventPenetrateFlag) {
                    f.set(key, PREVENT_PENETRATE_TIME_SECONDS, PREVENT_PENETRATE_VALUE);
                } else {
                    f.set(key, expireSeconds, null);
                }
                return null;
            }
            f.set(key, expireSeconds, VJson.writeAsString(v));
            return v;
        }

        //命中缓存
        if (!StringUtils.isEmpty(metricKey)) {
            TraceUtils.hitCache(MetricEnum.HIT_CACHE.getType(), metricKey);
        }
        if (PREVENT_PENETRATE_VALUE.equals(value)) {
            TraceUtils.incr(MetricEnum.PREVENT_PENETRATE);
            return null;
        }
        return VJson.read(value, typeToken);
    }


    /**
     * 分布式锁(60s的重试时间)
     *
     * @param r             继承于RedisLock
     * @param lockKey       分布式锁的key
     * @param voidMethod    获得到锁后需要执行的方法
     * @param expireSeconds 锁定时间 单位/s（需要评估方法执行时间）
     * @throws Exception
     */
    public <R extends RedisLock> void lock(R r, String lockKey, VoidMethodInterface voidMethod, int expireSeconds) throws Exception {
        lock(r, lockKey, voidMethod, expireSeconds, DEFAULT_MAX_RETRY_TIME_SECONDS);
    }

    /**
     * 分布式锁
     *
     * @param r             继承于RedisLock
     * @param lockKey       分布式锁的key
     * @param voidMethod    获得到锁后需要执行的方法
     * @param expireSeconds 锁定时间 单位/s（需要评估方法执行时间）
     * @param retrySeconds  重试间隔 单位/s
     * @throws Exception
     */
    public <R extends RedisLock> void lock(R r, String lockKey, VoidMethodInterface voidMethod, int expireSeconds, int retrySeconds) throws Exception {
        //获取锁
        LockHandle lockHandle = null;
        try {
            //内置重试机制
            lockHandle = r.lock(lockKey, expireSeconds * 1000, retrySeconds * 1000);
        } catch (LockFailedException e) {
            logger.error("锁定失败，错误信息：%s", e.getMessage());
            TraceUtils.incr(MetricEnum.REDIS_LOCK_FAILED);
            return;
        }
        try {
            voidMethod.exec();
        } catch (Exception e) {
            logger.error("voidMethod error msg:%s", e.getMessage());
            return;
        } finally {
            lockHandle.unlock();
        }
    }

    /**
     * 分布式锁(60s的重试时间)
     *
     * @param r             继承于Redis
     * @param lockKey       分布式锁的key
     * @param voidMethod    获得到锁后需要执行的方法
     * @param expireSeconds 锁定时间 单位/s（需要评估方法执行时间）
     * @throws Exception
     */
    public <R extends Redis> void lock(R r, String lockKey, VoidMethodInterface voidMethod, int expireSeconds) throws Exception {
        lock(r, lockKey, voidMethod, expireSeconds, DEFAULT_MAX_RETRY_TIME_SECONDS);
    }

    /**
     * 分布式锁
     *
     * @param r             继承于Redis
     * @param lockKey       分布式锁的key
     * @param voidMethod    获得到锁后需要执行的方法
     * @param expireSeconds 锁定时间 单位/s（需要评估方法执行时间）
     * @param retrySeconds  重试间隔 单位/s
     * @throws Exception
     */
    public <R extends Redis> void lock(R r, String lockKey, VoidMethodInterface voidMethod, int expireSeconds, int retrySeconds) throws Exception {
        long begin = 0L;
        long retryMills = retrySeconds * 1000;
        //获取随机字符串，避免lockValue相同
        String lockValue = UUID.randomUUID().toString();
        while (begin <= retryMills) {
            String response = r.set(lockKey, lockValue, "nx", "ex", expireSeconds);
            if (response != null) {
                logger.debug("lock method exec begin");
                try {
                    voidMethod.exec();
                } catch (Exception e) {
                    logger.error("method exec failed ,msg：%s", e.getMessage());
                } finally {
                    String value = r.get(lockKey);
                    //当缓存里还有该key对应的值时，才去删除锁，避免执行时间过长导致锁被释放
                    if (!StringUtils.isEmpty(value) && lockValue.equals(value)) {
                        // 避免若在此时，这把锁突然不是这个客户端的，则会误解锁
                        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                        r.eval(script, Collections.singletonList(lockKey), Collections.singletonList(lockValue));
                    }
                }
                logger.debug("lock method exec success");
                return;
            } else {
                long waitMills = random.nextInt(WAIT_INTERVAL_MIN_MILLS, WAIT_INTERVAL_MAX_MILLS);
                try {
                    Thread.sleep(waitMills);
                } catch (InterruptedException ex) {
                    throw new UnexpectedStateException(ex);
                }
                begin = begin + waitMills;
                logger.debug("等待获取锁，当前等待时间：%sms", begin);
            }
        }
        logger.error("等待获取锁超时");
    }
}
