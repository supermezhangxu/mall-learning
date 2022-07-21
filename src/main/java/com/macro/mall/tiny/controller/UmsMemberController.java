package com.macro.mall.tiny.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sso")
@Api(tags = "UmsMemberController", description = "会员登录注册管理")
public class UmsMemberController {

    @Autowired
    private UmsMemberService umsMemberService;

    @GetMapping("/getAuthCode")
    @ResponseBody
    @ApiOperation("获取验证码")
    public CommonResult getAuthCode(@RequestParam String telephone){
        return umsMemberService.getAuthCode(telephone);
    }

    @PostMapping("/verifyAuthCode")
    @ResponseBody
    @ApiOperation("判断验证码是否正确")
    public CommonResult verifyAuthCode(@RequestParam String telephone, @RequestParam String authCode){
        return umsMemberService.verify(telephone, authCode);
    }
}
