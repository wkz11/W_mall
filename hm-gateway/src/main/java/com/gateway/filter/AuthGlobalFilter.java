package com.gateway.filter;

import com.gateway.config.AuthProperties;
import com.gateway.config.JwtProperties;
import com.gateway.util.JwtTool;
import com.hmall.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final JwtTool jwtTool;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 获取request
        ServerHttpRequest request = exchange.getRequest();
        //2.判断是否需要拦截,从yml中拿（需注入config中的AuthPropertises）
        if (isExclude(request.getPath().toString())){
            //放行
            return chain.filter(exchange);
        }
        //3.获取token
        String token=null;
        List<String> hearders = request.getHeaders().get("authorization");
        if (hearders!=null &&!hearders.isEmpty()){
            token=hearders.get(0);
        }
        //4.校验并解析token
        Long userId=null;
        try {
             userId = jwtTool.parseToken(token);
        }catch (UnauthorizedException e){
            //拦截，设置响应状态码为401，从response中拿到响应
            ServerHttpResponse response = exchange.getResponse();
            //重新写响应码,里面可以用枚举enum
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //最后退出拦截器；返回Mono<Void>
            return  response.setComplete();
        }

        //5. todo 传递用户信息
        String userInfo =userId.toString();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("user-info", userInfo))
                .build();
        System.out.println("userId=" +userId);
        //6.放行
        return chain.filter(swe);
    }

    private boolean isExclude(String path) {
        for (String pathpattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathpattern,path)){
                return  true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
