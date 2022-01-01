package com.example.handformula;

import android.os.AsyncTask;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;


public class API extends AsyncTask<StringBuffer, Void, String> {
    /*
    * API.execute(url, target)
    *
    */

    private TextView textView;

    @Override
    protected String doInBackground(StringBuffer... stringBuffers) {
        String result = httpRequest(stringBuffers[0].toString(), stringBuffers[1].toString());
        try {
            result = getFromJson(result, stringBuffers[2].toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuffers[3].append(result);
        this.textView.setText(result);
        return result;
    }

    public API(TextView container) {
        this.textView = container;
    }


    private static String httpRequest(String requesturl, String method) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(requesturl);
            System.out.println(requesturl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStream.close();
            inputStreamReader.close();
            httpURLConnection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buffer.toString();
    }

    private static String getFromJson(String jsonstr, String key) throws IOException {

        JSONObject jsonObject = new JSONObject().parseObject(jsonstr);

        return jsonObject.getString(key);
    }

}
