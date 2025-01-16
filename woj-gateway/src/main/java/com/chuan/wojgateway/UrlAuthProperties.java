package com.chuan.wojgateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "url-auth")
public class UrlAuthProperties {
    /***白名单，不做鉴权处理，只做简单的业务处理*/
    private List<String> whitelist=new ArrayList<>(Arrays.asList(
            "/api/web/announcement/get-activity-list",
            "/api/web/problem/get-problemtaglist",
            "/api/web/problem/search-problemtitlelist",
            "/api/web/course/get-first",
            "/api/web/problem/search-problemtitlelist",
            "/api/web/problem/get-problemtaglist",
            "/api/web/problem/get-problem",
            "/api/web/announcement/get-announcement-list",
            "/api/user/login",
            "/api/web/v3/api-docs",
            "/api/user/v3/api-docs",
            "/api/judge/v3/api-docs"
    ));
    /**
     * 黑名单
     */
    private List<String> blacklist  = new ArrayList<>();
    /**
     * 忽略名单，不做任何处理
     */
    private List<String> ignoreList = new ArrayList<>();
}