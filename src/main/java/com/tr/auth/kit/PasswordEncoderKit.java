package com.tr.auth.kit;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author TR
 */
public class PasswordEncoderKit {

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码，加密后密码不能转成明文
     */
    public static String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * @param password 密码明文
     * @param encodePassword 密码密文
     */
    public static boolean matches(String password, String encodePassword) {
        return encoder.matches(password, encodePassword);
    }

}
