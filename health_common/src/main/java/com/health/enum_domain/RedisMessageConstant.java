package com.health.enum_domain;

import java.util.Objects;

public enum RedisMessageConstant {
    SENDTYPE_ORDER("001"),
    SENDTYPE_LOGIN("002"),
    SENDTYPE_GETPWD("003");

    private String code;
    RedisMessageConstant(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RedisMessageConstant instance(int code) {
        RedisMessageConstant redisMessageConstant = null;
        RedisMessageConstant[] redisMessageConstants = RedisMessageConstant.values();
        for (RedisMessageConstant redisMessageConstant1:redisMessageConstants) {
            if (Objects.equals(redisMessageConstant1.getCode(),code)) {
                redisMessageConstant = redisMessageConstant1;
            }
        }
        return redisMessageConstant;
    }


}
