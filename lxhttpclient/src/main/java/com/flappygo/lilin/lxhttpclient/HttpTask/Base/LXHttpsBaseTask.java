package com.flappygo.lilin.lxhttpclient.HttpTask.Base;

import android.content.Context;

import com.flappygo.lilin.lxhttpclient.Exception.LXHttpException;
import com.flappygo.lilin.lxhttpclient.Holder.LXHttpHeaderHolder;
import com.flappygo.lilin.lxhttpclient.HttpTaskInterface.LXHttpTaskPreParseInterface;
import com.flappygo.lilin.lxhttpclient.Verifier.DefaultTrustManager;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/************************
 * 抽象类，实现基础的获取数据功能，留出数据处理接口
 * @param <T>
 */
public abstract class LXHttpsBaseTask<T> extends LXHttpTaskBase implements LXHttpTaskPreParseInterface<T> {
    // 请求的地址
    private String url;
    // 请求的Param参数
    private HashMap<String, Object> hashMap;
    // 读取超时时间
    private int readTimeOut = 60;
    // 连接超时时间
    private int connectTimeOut = 60;
    // 编码类型
    private String charset = "utf-8";
    // 验证主机
    private HostnameVerifier mHostnameVerifier;
    // cookie
    private LXHttpHeaderHolder headerHolder;
    //重定向
    private boolean instanceFollowRedirects = true;
    // 用于增加的参数
    private HashMap<String, String> mRequestProperty;


    /**************
     * 构造器
     *
     * @param url     地址
     * @param hashMap 请求参数
     */
    public LXHttpsBaseTask(String url, HashMap<String, Object> hashMap) {
        super();
        this.url = url;
        this.hashMap = hashMap;
    }

    public void addRequestProperty(String key, String value) {
        // 如果为空，创建
        if (mRequestProperty == null) {
            this.mRequestProperty = new HashMap<String, String>();
        }
        mRequestProperty.put(key, value);
    }

    public boolean isInstanceFollowRedirects() {
        return instanceFollowRedirects;
    }

    public void setInstanceFollowRedirects(boolean instanceFollowRedirects) {
        this.instanceFollowRedirects = instanceFollowRedirects;
    }

    public HostnameVerifier getmHostnameVerifier() {
        return mHostnameVerifier;
    }

    public void setmHostnameVerifier(HostnameVerifier mHostnameVerifier) {
        this.mHostnameVerifier = mHostnameVerifier;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public LXHttpHeaderHolder getHeaderHolder() {
        return headerHolder;
    }

    public void setHeaderHolder(LXHttpHeaderHolder holder) {
        this.headerHolder = holder;
    }

    /***************
     * 设置初始化的keyStrore
     * @param context        上下文
     * @param serverResource 服务器keysotre
     * @param serverPassword 服务器服务器keysotre密码
     * @throws Exception 错误
     */
    public void initKeyStore(Context context, int serverResource, String serverPassword)
            throws Exception {

        //服务器的keystore
        KeyStore trustStore = KeyStore.getInstance("BKS");
        //加载进来
        trustStore.load(context.getResources().openRawResource(serverResource),
                serverPassword.toCharArray());
        //创建工厂
        TrustManagerFactory trustStoreFactory = TrustManagerFactory.getInstance("X509");
        //初始化通莞trustStore
        trustStoreFactory.init(trustStore);
        //获取true
        TrustManager[] trustM = trustStoreFactory.getTrustManagers();
        //获取sslcontext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        //初始化
        sslContext.init(null, trustM, null);
        //设置factory
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    /*****************
     * 设置初始化的keyStrore
     *
     * @param context        上下文
     * @param serverResource 服务器keysotre
     * @param serverPassword 服务器服务器keysotre密码
     * @param clientResource 客户端keysotre
     * @param clientPassword 客户端服务器keysotre密码
     */
    public void initKeyStore(Context context,
                             int serverResource,
                             String serverPassword,
                             int clientResource,
                             String clientPassword)
            throws Exception {

        //服务器的keystore
        KeyStore trustStore = KeyStore.getInstance("BKS");
        //客户端的keystore
        KeyStore keyStore = KeyStore.getInstance("BKS");
        //加载进来
        trustStore.load(context.getResources().openRawResource(serverResource),
                serverPassword.toCharArray());
        //加载进来
        keyStore.load(context.getResources().openRawResource(clientResource),
                clientPassword.toCharArray());
        //创建工厂
        TrustManagerFactory trustStoreFactory = TrustManagerFactory.getInstance("X509");
        //创建工厂
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
        //初始化
        trustStoreFactory.init(trustStore);
        //初始化
        keyManagerFactory.init(keyStore, clientPassword.toCharArray());
        //获取true
        TrustManager[] trustM = trustStoreFactory.getTrustManagers();
        //获取sslcontext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        //初始化
        sslContext.init(keyManagerFactory.getKeyManagers(), trustM, null);
        //设置factory
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }


    /*************
     * 默认信任所有服务器
     *
     * @throws Exception
     */
    public void setTrustManagerDefault() throws Exception {
        X509TrustManager[] trustCerts = new X509TrustManager[]{new DefaultTrustManager()};
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    /**************
     *
     * 设置manager
     * @param km  keyManager
     * @param tm  trustManager
     * @throws Exception
     */
    public void setTrustManager(KeyManager[] km, TrustManager[] tm) throws Exception {
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(km, tm, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }


    /*************
     * 以json的形式进行post
     *
     * @return 请求结果
     * @throws Exception 异常
     */
    public T postAsJson() throws Exception {
        return postJson(getPostJsonStr(hashMap));
    }

    /*************
     * 传入json进行post
     *
     * @return 请求结果
     * @throws Exception 异常
     */
    public T postJson(JSONObject jb) throws Exception {
        return postJson(jb.toString());
    }

    /*************
     * 以param的形式进行post
     *
     * @return 请求结果
     * @throws Exception 异常
     */
    public T postAsParam() throws Exception {
        return postParam(getPostParamStr(hashMap, charset));
    }

    /*************
     * 传入param进行post
     *
     * @return 请求结果
     * @throws Exception 异常
     */
    public T postParam(HashMap<String, Object> param) throws Exception {
        return postParam(getPostParamStr(param, charset));
    }

    /********************
     * 直接postJson数据
     *
     * @param jsonData json数据
     * @return 请求结果
     * @throws Exception 异常
     */
    private T postJson(String jsonData) throws Exception {
        URL surl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) surl.openConnection();
        //重定向
        conn.setInstanceFollowRedirects(instanceFollowRedirects);
        // 方式为post
        conn.setRequestMethod("POST");
        // 时间
        conn.setReadTimeout(readTimeOut * 1000);
        // 时间
        conn.setConnectTimeout(connectTimeOut * 1000);
        // 设置通用属性
        conn.setRequestProperty("Content-Type", "application/json;charset=" + charset);
        // 设置charset
        conn.setRequestProperty("Accept-Charset", charset);
        // 设置通用属性
        conn.setRequestProperty("accept", "*/*");
        // 在连接期间可以处理多个请求/响应
        conn.setRequestProperty("connection", "close");
        // 对conn进行预处理
        supplementaryConnection(conn);
        // 输出
        conn.setDoOutput(true);
        // 输入
        conn.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        conn.getOutputStream().write(jsonData.getBytes(charset));
        // flush
        conn.getOutputStream().flush();
        // 关闭流数据
        conn.getOutputStream().close();
        //获取返回数据
        int responseCode = conn.getResponseCode();
        //成功
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            //保存session
            generateSession(conn);
            //获得流数据
            InputStream inputStream = conn.getInputStream();
            //流数据进行转换
            return preParseData(convertStreamToStr(inputStream, charset));
        } else {
            //抛出异常
            throw new LXHttpException(responseCode, url, conn.getResponseMessage());
        }
    }

    /********************
     * 直接postParam数据
     *
     * @param paramData param数据
     * @return 请求结果
     * @throws Exception 异常
     */
    private T postParam(String paramData) throws Exception {
        URL surl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) surl.openConnection();
        //重定向
        conn.setInstanceFollowRedirects(instanceFollowRedirects);
        // 方式为post
        conn.setRequestMethod("POST");
        // 时间
        conn.setReadTimeout(readTimeOut * 1000);
        // 时间
        conn.setConnectTimeout(connectTimeOut * 1000);
        // param类型
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded;charset=" + charset);

        conn.setRequestProperty("Accept-Charset", charset);
        // 设置通用属性
        conn.setRequestProperty("accept", "*/*");
        // 在连接期间可以处理多个请求/响应
        conn.setRequestProperty("connection", "close");
        // 用户自定义Property
        supplementaryConnection(conn);
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        conn.getOutputStream().write(paramData.getBytes(charset));
        //flush
        conn.getOutputStream().flush();
        //关闭
        conn.getOutputStream().close();
        //返回状态
        int responseCode = conn.getResponseCode();
        //如果成功
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            //保存session
            generateSession(conn);
            //输入流
            InputStream inputStream = conn.getInputStream();
            //流数据转换为字符串
            return preParseData(convertStreamToStr(inputStream, charset));
        } else {
            //抛出异常
            throw new LXHttpException(responseCode, url, conn.getResponseMessage());
        }
    }


    /************
     * 执行get请求
     *
     * @return 请求结果
     * @throws Exception
     */
    public T get() throws Exception {
        String urlstr = url + getParamStr(hashMap, charset);
        URL surl = new URL(urlstr);
        HttpsURLConnection conn = (HttpsURLConnection) surl.openConnection();
        // 重定向
        conn.setInstanceFollowRedirects(instanceFollowRedirects);
        // get方式
        conn.setRequestMethod("GET");
        // 时间
        conn.setReadTimeout(readTimeOut * 1000);
        // 时间
        conn.setConnectTimeout(connectTimeOut * 1000);
        // 请求类型
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
        // 设置编码
        conn.setRequestProperty("Accept-Charset", charset);
        // 设置通用属性
        conn.setRequestProperty("accept", "*/*");
        // 在连接期间可以处理多个请求/响应
        conn.setRequestProperty("connection", "close");
        // 进行初始化
        supplementaryConnection(conn);
        // 获取返回值
        int responseCode = conn.getResponseCode();
        //成功
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            //保存session
            generateSession(conn);
            //获取
            InputStream inputStream = conn.getInputStream();
            //转换为字符串
            return preParseData(convertStreamToStr(inputStream, charset));
        } else {
            throw new LXHttpException(responseCode, urlstr, conn.getResponseMessage());
        }
    }

    /***********
     * 对连接进行初始化
     * @param conn  连接
     */
    private void supplementaryConnection(HttpsURLConnection conn) {
        // 如果任务设置了cookie，执行cookie
        if (headerHolder != null
                && headerHolder.getCookie() != null
                && !headerHolder.getCookie().equals("")) {
            conn.setRequestProperty("Cookie", headerHolder.getCookie());
        }
        if (mHostnameVerifier != null) {
            conn.setHostnameVerifier(mHostnameVerifier);
        }
        // 设置增添的参数
        if (mRequestProperty != null) {
            Iterator<Map.Entry<String, String>> iter = mRequestProperty.entrySet()
                    .iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }


    /************
     * 保存当前的cookie
     * @param conn 连接
     */
    private void generateSession(HttpsURLConnection conn) {
        //保存headerFields
        headerHolder = new LXHttpHeaderHolder(conn.getHeaderFields());
    }


}
