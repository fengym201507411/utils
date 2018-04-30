package com.fym.utils.study;

/**
 * Created by fengyiming on 2018/4/27.
 * 基于方法级别的的方重复提交
 */
public class ResubmitFilter extends AbstractResubmitFilter implements Filter {

    @Inject
    private Request request;

    @Inject
    private RedisCache redisCache;

    @Override
    public boolean enter() throws ServiceException {
        boolean check = resubmitCheck(request, redisCache);
        if (!check) {
            throw ExceptionFactory.setServiceException(ExceptionEnum.RESUBMIT_ERROR.getCode(), ExceptionEnum.RESUBMIT_ERROR.getMessage());
        }
        return true;
    }
}
