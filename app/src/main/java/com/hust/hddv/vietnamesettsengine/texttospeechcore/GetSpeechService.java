package com.hust.hddv.vietnamesettsengine.texttospeechcore;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GetSpeechService {
    public static InputStream getSpeech(String apiUrl, String datajson) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(apiUrl);
        StringEntity body = new StringEntity(datajson, "UTF-8");
        /*add content-type, token into header request */
        request.addHeader("content-type", "application/json;charset=UTF-8");
        request.addHeader("token", "5ynWQ-GjfLLpTMpj9uvHwBFeM-bRST87SmTY8NxNNnj8LdG-D2SIiC4N7VevlSnO");
        request.getRequestLine();
        request.setEntity(body);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    public static byte[] inputStreamToByteArray(InputStream inputStream){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] bytes = new byte[2048];
            int n;
            while ((n = inputStream.read(bytes)) > 0) {
                baos.write(bytes, 0, n);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
