package com.macro.mall.tiny.service;

import com.macro.mall.tiny.common.api.CommonResult;

public interface UmsMemberService {

    CommonResult getAuthCode(String telephone);

    CommonResult verify(String telephone, String authCode);

}
