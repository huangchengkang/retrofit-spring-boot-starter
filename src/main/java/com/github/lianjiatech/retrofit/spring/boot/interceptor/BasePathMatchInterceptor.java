package com.github.lianjiatech.retrofit.spring.boot.interceptor;

import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;

/**
 * 路径匹配拦截器, 如果使用spring-bean方式，使用原型模式
 *
 * @author 陈添明
 */
public abstract class BasePathMatchInterceptor implements PrototypeInterceptor {

    private String[] include;

    private String[] exclude;

    private PathMatcher pathMatcher = new AntPathMatcher();


    public void setInclude(String[] include) {
        this.include = include;
    }

    public void setExclude(String[] exclude) {
        this.exclude = exclude;
    }

    @Override
    public final Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String path = request.url().encodedPath();

        if (isMatch(exclude, path)) {
            return chain.proceed(request);
        }

        if (!isMatch(include, path)) {
            return chain.proceed(request);
        }
        return doIntercept(chain);
    }

    /**
     * 执行拦截
     *
     * @param chain 拦截器链
     * @return http Response
     * @throws IOException 可能因为网络IO问题，抛出IOException
     */
    protected abstract Response doIntercept(Chain chain) throws IOException;

    /**
     * <p>
     * 当前http的url路径是否与指定的patterns匹配
     * </p>
     * 有一个pattern匹配中，就算匹配
     *
     * @param patterns 路径匹配patterns
     * @param path     http请求路径
     * @return 匹配结果
     */
    private boolean isMatch(String[] patterns, String path) {
        if (patterns == null || patterns.length == 0) {
            return false;
        }
        for (String pattern : patterns) {
            boolean match = pathMatcher.match(pattern, path);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
