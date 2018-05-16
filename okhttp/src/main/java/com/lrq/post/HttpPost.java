package com.lrq.post;

import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpPost {
    private final OkHttpClient client = new OkHttpClient();

    @Test
    public void postForm() throws Exception {

        //设置表单请求体
        RequestBody formBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();

        //发送post请求
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(formBody)
                .build();

        //获取响应
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
    }
}
