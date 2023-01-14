package com.spnetwork.verifycode.service;

import com.spnetwork.verifycode.dto.CoordinateDTO;

import java.util.Map;

/**
 * @author aiker
 * @implSpec Nu bug no errors
 * @since 2022/12/3
 */
public interface VerifyCodeService {

    Map<String, Object> getOne();

    Map<String, Object> verifyCode(String id, CoordinateDTO[] dtoArr);

}
