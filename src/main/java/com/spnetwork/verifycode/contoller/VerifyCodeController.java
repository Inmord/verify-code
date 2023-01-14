package com.spnetwork.verifycode.contoller;

import com.spnetwork.verifycode.dto.CoordinateDTO;
import com.spnetwork.verifycode.dto.VerifyCodeDTO;
import com.spnetwork.verifycode.service.VerifyCodeService;
import com.spnetwork.verifycode.vo.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author aiker
 * @implSpec Nu bug no errors
 * @since 2022/12/3
 */
@Slf4j
@RestController
@RequestMapping("/api/verify-code")
public class VerifyCodeController {

    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 获取验证码图片
     */
    @GetMapping("/one")
    public RestResponse<Object> getOne() {
        Map<String, Object> ans = verifyCodeService.getOne();
        int code = (int) ans.getOrDefault("code", 500);
        if (code != 200) {
            return RestResponse.failure(code, (String) ans.get("msg"));
        }
        return RestResponse.success(ans.get("data"));
    }

    /**
     * 验证码信息校验
     */
    @PostMapping("/{imageId}")
    public RestResponse<Object> verifyCode(@PathVariable("imageId") String id, @RequestBody VerifyCodeDTO verifyCodeDTO) {
        CoordinateDTO[] verifyCode = verifyCodeDTO.getVerifyCode();
        if (verifyCode == null || verifyCode.length < 2) {
            return RestResponse.failure(RestResponse.FailCode.ARGS.getCode(), "验证参数有误，请重试");
        }
        Map<String, Object> ans = verifyCodeService.verifyCode(id, verifyCode);
        int code = (int) ans.getOrDefault("code", 500);
        if (code != 200) {
            return RestResponse.failure(code, (String) ans.get("msg"));
        }
        return RestResponse.success();
    }

}
