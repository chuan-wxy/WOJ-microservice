package com.chuan.wojcommon.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/19 9:31
 * @Description:
 */
@Data
@Slf4j
//@ConfigurationProperties(prefix = "woj.jwt")
public class JwtUtil {
    // 过期时间 --3天
    private static  long staticExpire = 72*60*60*1000;

    private static  String staticSecret = "23e2q1exd2q1e4dxq14edxq2145rq23r5fasdasdq2wed21qed2q1edq21";

    private JwtUtil(){};

    /**
     * 生成 JwtToken
     *
     * @param userAccount
     * @return
     */
    public static String generateJwt(String userAccount) {
        Date nowDate = new Date();

        Date expireDate = new Date(nowDate.getTime() + staticExpire );


        Map<String,Object> claims = new HashMap<>();
        claims.put("userAccount", userAccount);

        String token = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setSubject(userAccount)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, staticSecret)// 签名算法
                .compact();
        return token;
    }

    /**
     * 解析 JwtToken
     *
     * @param token
     * @return
     */
    public static Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(staticSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.info("[getClaimByToken] 解析jwt失败");
            log.debug("validate is token error ", e);
            return null;
        }
    }

    /**
     * token是否过期
     *
     * @return true：过期
     */
    public static boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

    /**
     * true表示令牌无效/过期
     *
     * @param jwt
     * @return
     */
    public static boolean isExpire(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(staticSecret)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            // 解析 JWT 失败（可能是令牌无效、签名不匹配等）
            // 在这里，我们可以选择记录日志、抛出异常或返回 true（表示令牌无效/过期）
            // 这里我们简单地返回 true，表示令牌有问题
            return true;
        }
    }

}
