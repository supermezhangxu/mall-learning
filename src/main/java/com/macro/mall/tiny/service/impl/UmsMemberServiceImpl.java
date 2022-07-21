package com.macro.mall.tiny.service.impl;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.service.RedisService;
import com.macro.mall.tiny.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    private RedisService redisService;
    @Value("${redis.key.prefix.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${redis.key.expire.authCode}")
    private Long AUTH_CODE_EXPIRE_SECONDS;

    @Override
    public CommonResult getAuthCode(String telephone) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        redisService.set(REDIS_KEY_PREFIX_AUTH_CODE + telephone, code.toString());
        redisService.expire(REDIS_KEY_PREFIX_AUTH_CODE + telephone, AUTH_CODE_EXPIRE_SECONDS);
        return CommonResult.success(code.toString(), "获取验证码成功！");
    }

    @Override
    public CommonResult verify(String telephone, String authCode) {
        if (!StringUtils.hasLength(authCode)){
            return CommonResult.failed("请输入验证码！");
        }

        String code = redisService.get(REDIS_KEY_PREFIX_AUTH_CODE + telephone);
        if (code.equals(authCode)){
            return CommonResult.success(null, "验证码校验成功！");
        }else {
            return CommonResult.failed("验证码错误，请重新输入！");
        }
    }
}
