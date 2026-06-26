package com.kun.tools;

import com.kun.enums.ContentTypeEnum;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP 请求工具类，基于 {@link java.net.http.HttpClient} 发送 POST 请求。
 *
 * <p>默认使用 JSON 请求体，超时时间单位为秒。</p>
 *
 * @author GaoYu
 */
public class HttpUtil {

    private static final HttpClient CLIENT;

    static {
        CLIENT = HttpClient.newHttpClient();
    }

//    public static HttpResponse<String> get(String url, Object object) throws Exception {
//        return get(url, object, 10);
//    }
//
//    public static HttpResponse<String> get(String url, Object object, long timeout) throws Exception {
//        return get(url, object, ContentTypeEnum.JSON, timeout);
//    }

//    public static HttpResponse<String> get(String url, Object object, ContentTypeEnum contentType, long timeout) throws Exception {
//        String json = getJson(url, object);
//        HttpRequest request = buildRequestBuilder(url, contentType, timeout)
//                .GET(HttpRequest.BodyPublishers.ofString(json)).build();
//        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
//    }

    /**
     * 发送 POST 请求，默认超时 10 秒，Content-Type 为 JSON。
     *
     * @param url    请求地址
     * @param object 请求体对象，将序列化为 JSON
     * @return HTTP 响应（响应体为字符串）
     * @throws Exception 序列化或网络请求失败时抛出
     */
    public static HttpResponse<String> post(String url, Object object) throws Exception {
        return post(url, object, 10);
    }

    /**
     * 发送 POST 请求，指定超时时间，Content-Type 为 JSON。
     *
     * @param url     请求地址
     * @param object  请求体对象，将序列化为 JSON
     * @param timeout 超时时间（秒）
     * @return HTTP 响应（响应体为字符串）
     * @throws Exception 序列化或网络请求失败时抛出
     */
    public static HttpResponse<String> post(String url, Object object, long timeout) throws Exception {
        return post(url, object, ContentTypeEnum.JSON, timeout);
    }

    /**
     * 发送 POST 请求，指定 Content-Type 与超时时间。
     *
     * @param url         请求地址
     * @param object      请求体对象，将序列化为 JSON
     * @param contentType 请求 Content-Type
     * @param timeout     超时时间（秒）
     * @return HTTP 响应（响应体为字符串）
     * @throws Exception 序列化或网络请求失败时抛出
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
     * @param object 请求体对象（非空校验）
     * @return JSON 字符串
     */
    private static String getJson(String url, Object object) {
        AssertUtil.notNull(url, "url must not be empty");
        String json = JsonUtil.toJson(object);
        AssertUtil.notNull(json, "object must not be empty");
        return json;
    }

    /**
     * 构建 HTTP 请求 Builder，设置 URI、超时与 Content-Type 请求头。
     *
     * @param url         请求地址
     * @param contentType 请求 Content-Type
     * @param timeout     超时时间（秒）
     * @return {@link HttpRequest.Builder}
     */
    private static HttpRequest.Builder buildRequestBuilder(String url, ContentTypeEnum contentType,
                                                           long timeout) {
        return HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(timeout))
                .header("Content-Type", contentType.getValue());
    }

}
