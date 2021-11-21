package com.podcasts.jpa.handler.validate;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * 验证码
 */
@Data
public class ImageCode {

    private BufferedImage image; // image图片
    private String code; // code验证码
    private LocalDateTime expireTime; // expireTime过期时间

    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}