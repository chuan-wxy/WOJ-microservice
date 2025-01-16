package com.chuan.wojgateway.filter;

import com.alibaba.fastjson.JSON;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.common.ResultStatus;
import com.chuan.wojcommon.constant.RedisContant;
import com.chuan.wojcommon.utils.JwtUtil;
import com.chuan.wojcommon.utils.RedisUtil;
import com.chuan.wojgateway.UrlAuthProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered{

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private String secret = "23e2q1exd2q1e4dxq14edxq2145rq23r5fasdasdq2wed21qed2q1edq21";

    @Autowired
    private UrlAuthProperties urlAuthProperties;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Content-Type", "application/json;charset=utf-8");

        DataBufferFactory dataBufferFactory = response.bufferFactory();

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 获取jwt令牌
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("Authorization");

        // 判断路径是否包含inner
        if(antPathMatcher.match("/**/inner/**", path)) {
            BaseResponse<String> res = new BaseResponse<>(ResultStatus.FORBIDDEN);
            DataBuffer dataBuffer = dataBufferFactory.wrap(JSON.toJSONString(res).getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }

        // 白名单直接放行,不做 token 校验
        if (isWhitelist(request)) {
            return chain.filter(exchange);
        }

        // 获取 redis 中的登记信息
        Object userAccount = redisUtil.get(RedisContant.USER_TOKEN + token);

        //验证 token （ token 为空或 redis 中不存在登记信息）
        if (token == null || token.isBlank() || userAccount == null) {
            BaseResponse<String> res = new BaseResponse<>(ResultStatus.UNAUTHORIZED);
            DataBuffer dataBuffer = dataBufferFactory.wrap(JSON.toJSONString(res).getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }

        // 需要管理员权限
        if(antPathMatcher.match("/**/admin/**", path)) {
            Claims claimByToken = JwtUtil.getClaimByToken(token);

            System.out.println(claimByToken);

            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }

//        // token为空或者过期
//        if (StringUtils.isBlank(token) || JwtUtil.isExpire(token)) {
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            DataBuffer dataBuffer = dataBufferFactory.wrap("未登录或登录过期".getBytes(StandardCharsets.UTF_8));
//            return response.writeWith(Mono.just(dataBuffer));
//        }
//        System.out.println(request.getPath()+"不是白名单danyoutoken");

//        LoginContext loginContext = JWTUtil.getCurrentUser(token);
//        loginContext.setClientType(ClientType.ADMIN);
//        LoginContextHolder.set(loginContext);
//
//        String text = JSON.toJSONString(loginContext);
//        log.info("LoginContext:{}", text);
//        String unicode = UnicodeUtil.toUnicode(text, true);
//        String[] headerValues = new String[]{unicode};
//
//        ServerHttpRequest newRequest = exchange.getRequest().mutate()
//                .header(LoginContextConst.LOGIN_USER, headerValues)
//                .header(LoginContextConst.TOKEN, JWT).build();
//        //构建新的ServerWebExchange实例
//        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
//
//        return chain.filter(newExchange);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 将此拦截器优先级设为最高
        return 0;
    }

    /**
     * 白名单校验
     *
     * @param request
     * @return
     */
    public boolean isWhitelist(ServerHttpRequest request) {
        boolean result = false;
        String path = request.getPath().toString();
        HttpMethod httpMethod = request.getMethod();
        // 如果HTTP方法是OPTIONS，则直接将result设置为true（通常OPTIONS方法用于CORS预检请求，不需要鉴权）
        if (httpMethod != null && httpMethod.name().equals(HttpMethod.OPTIONS.name())) {
            result = true;
        } else if (StringUtils.isNotBlank(path)) {
            List<String> urls = urlAuthProperties.getWhitelist();
            result = matchUrl(urls,path);
        }
        if(result == true) {
            log.info("path:{}, whitelist:{}", path, result);
        }
        return result;

    }

    /**
     * 黑名单校验
     *
     * @param request
     * @return
     */

    public boolean isBlacklist(ServerHttpRequest request) {
        boolean result = false;
        String path = request.getPath().toString();
        HttpMethod httpMethod = request.getMethod();
        if (httpMethod != null && httpMethod.name().equals(HttpMethod.OPTIONS.name())) {
            result = true;
        } else if (StringUtils.isNotBlank(path)) {
            List<String> urls = urlAuthProperties.getBlacklist();
            return urls.contains(path);
        }
        log.debug("path:{},blacklist:{}", path, result);
        return result;
    }

    /**
     * 匹配白名单
     *
     * @param urls
     * @param path
     * @return Boolean
     */

    private Boolean matchUrl(List<String> urls,String path) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (Iterator<String> iterator = urls.iterator(); iterator.hasNext();) {
            String string = (String) iterator.next();
            if(string.equals(path) || antPathMatcher.match(string, path)) {
                return true;
            }
        }
        return false;
    }

}
