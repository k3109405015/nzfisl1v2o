package com.kun.tools;

import com.kun.enums.ContentTypeEnum;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP 请求工具类，基于 {@link java.net.http.HttpClient} 封装 GET/POST 调用。
 */
public class HttpUtil {

    private static final HttpClient CLIENT;

    static {
        CLIENT = HttpClient.newHttpClient();
    }

    /**
     * 发送 GET 请求，默认超时 10 秒，Content-Type 为 JSON。
     *
     * <p>将 object 的简单类型字段拼接为 URL 查询参数。</p>
     *
     * @param url    请求地址
     * @param object 查询参数对象
     * @return HTTP 响应
     * @throws Exception 请求发送或响应处理失败时抛出
     */
    public static HttpResponse<String> get(String url, Object object) throws Exception {
        return get(url, object, 10);
    }

    /**
     * 发送 GET 请求，Content-Type 为 JSON。
     *
     * <p>将 object 的简单类型字段拼接为 URL 查询参数。</p>
     *
     * @param url     请求地址
     * @param object  查询参数对象
     * @param timeout 超时时间（秒）
     * @return HTTP 响应
     * @throws Exception 请求发送或响应处理失败时抛出
     */
    public static HttpResponse<String> get(String url, Object object, long timeout) throws Exception {
        return get(url, object, ContentTypeEnum.JSON, timeout);
    }

    /**
     * 发送 GET 请求。
     *
     * <p>将 object 的简单类型字段拼接为 URL 查询参数后追加到 url。</p>
     *
     * @param url         请求地址
     * @param object      查询参数对象
     * @param contentType Content-Type
     * @param timeout     超时时间（秒）
     * @return HTTP 响应
     * @throws Exception 请求发送或响应处理失败时抛出
     */
    public static HttpResponse<String> get(String url, Object object, ContentTypeEnum contentType, long timeout) throws Exception {
        url = url + StringUtil.buildGetUrl(object);
        HttpRequest request = buildRequestBuilder(url, contentType, timeout).GET().build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送 POST 请求，默认超时 10 秒，Content-Type 为 JSON。
     *
     * @param url    请求地址
     * @param object 请求体对象（序列化为 JSON）
     * @return HTTP 响应
     * @throws Exception 请求发送或响应处理失败时抛出
     */
    public static HttpResponse<String> post(String url, Object object) throws Exception {
        return post(url, object, 10);
    }

    /**
     * 发送 POST 请求，Content-Type 为 JSON。
     *
     * @param url     请求地址
     * @param object  请求体对象（序列化为 JSON）
     * @param timeout 超时时间（秒）
     * @return HTTP 响应
     * @throws Exception 请求发送或响应处理失败时抛出
     */
    public static HttpResponse<String> post(String url, Object object, long timeout) throws Exception {
        return post(url, object, ContentTypeEnum.JSON, timeout);
    }

    /**
     * 发送 POST 请求。
     *
     * @param url         请求地址
     * @param object      请求体对象（序列化为 JSON）
     * @param contentType Content-Type
     * @param timeout     超时时间（秒）
     * @return HTTP 响应
     * @throws Exception 请求发送或响应处理失败时抛出
     */
    public static HttpResponse<String> post(String url, Object object, ContentTypeEnum contentType, long timeout) throws Exception {
        String json = getJson(url, object);
        HttpRequest request = buildRequestBuilder(url, contentType, timeout)
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 将请求对象序列化为 JSON 字符串。
     *
     * @param url    请求地址（非空校验）
     * @param object 请求体对象
     * @return JSON 字符串
     */
    private static String getJson(String url, Object object) {
        AssertUtil.notNull(url, "url must not be empty");
        String json = JsonUtil.toJson(object);
        AssertUtil.notNull(json, "object must not be empty");
        return json;
    }

    /**
     * 构建 HTTP 请求 Builder。
     *
     * @param url         请求地址
     * @param contentType Content-Type
     * @param timeout     超时时间（秒）
     * @return {@link HttpRequest.Builder}
     */
    private static HttpRequest.Builder buildRequestBuilder(String url, ContentTypeEnum contentType,
                                                           long timeout) {
        return HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(timeout))
                .header("Content-Type", contentType.getValue());
    }

}
