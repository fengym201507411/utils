# utils
公共类项目

本项目只提供一些解决方案思路，具体怎么实现需要自己评估

1、异步通过公共线程池执行的方法；通过拆分list数据的形式用线程池的方式去执行带参数的方法，分线程等待和非线程等待两种场景，可自定义执行线程池大小；通过拆分list的形式去执行一个待返回值的方法，以list里的元素作为key，方法返回的结果作为value，整个多线程执行带返回值的方法会全部执行完毕会返回一个Map，其中任意一个任务执行异常均会抛出异常；通过拆分list的形式去执行一个待返回值的方法，以list里的元素作为key，方法返回的结果会已一个response对象返回，该对象会记录任务执行的状态成功或者失败，任务的key，失败的的信息。后面两种多线程执行可用于大批量数据校验。
2、获取缓存值(可防止缓存穿透、基于huskar关闭全部缓存、或者某个key类型的缓存)、两种redis分布式锁（一种基于vine、一种自己基于redis实现）
3、提供基于注解的excel导出（生成二进制String）
4、基于接口参数的redis防重复提交。参考ResubmitFilter
5、大量常用操作类，尤其对金额、比率的计算，时间公共类。


本项目解决的痛点：
1、多线程执行方法（可带返回值的多线程执行，可用于大批量校验）
2、大量常用操作类 （huskar等的封装）
3、大量常用的方法抽象成公共接口:获取缓存值(可防止缓存穿透、关闭某个key的缓存)、两种redis分布式锁
4、提供基于注解的excel导出（生成二进制String）

缓存接口流程
1、需要传递过来rediscache
2、根据key拿到对应的string类型的value值，判断value是否为null 走第3步，不为null走第4步
3、根据从db拿值的方法函数取出对应的value，并对此次的value再次判空处理，为null第5步，不为null第6步
4、判断是否与设置的缓存穿透的值相等，如果相等直接返回null，不相等则进行反序列化，处理成对应的返回值类型返回数据
5、直接将缓存穿透的值设置到该key对应的value里，并返回null
6、将值设置到该key对应的value里，并返回该对象

    public String ping() throws Exception {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,1, 2, 3, 4, 5, 6, 7, 8, 9, 10,1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        }
        String key = "lock_test";
        redisCacheService.set(key, 100, 0);
        taskThreadPoolService.splitVoidTask(item -> {
            cacheCommonService.lock(redisService, key, () -> {
                Integer keyValue = redisCacheService.get(key);
                logger.info("当前size：%s", keyValue);
                int now = keyValue + item;
                redisCacheService.set(key, 100, now);
                keyValue = redisCacheService.get(key);
                logger.info("修改后的size：%s", now);
            }, 10,200);
        }, list);
        return "hello";
    }

极简RPC实现