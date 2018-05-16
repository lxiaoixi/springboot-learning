package com.lrq.get;

import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpGet {
    private final OkHttpClient client = new OkHttpClient();

    @Test
    public void runGet() throws Exception {
        //发送get请求
        Request request = new Request.Builder()
                .url("https://api.github.com/gists/c2a7c39532239ff261be")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        //获取响应，.execute() 同步请求
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
        System.out.println(response.body().string());
    }

    @Test
    //从上文已经能知道call.execute()就是在执行http请求了，但是这是个同步操作，是在主线程运行的。如果你在android的UI线程直接执行这句话就出异常了。
    //OkHttp也帮我们实现了异步，写法是：
    public void runGetAsync() throws Exception {

        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .build();

        Call call = client.newCall(request);
        //获取响应，.enqueue() 异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());
            }

        });
        for (int i = 0; i < 10; i++) {
            System.out.println("我是主线程,线程Id为:" + Thread.currentThread().getId());
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
