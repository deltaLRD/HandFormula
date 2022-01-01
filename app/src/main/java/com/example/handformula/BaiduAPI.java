package com.example.handformula;

import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Environment;
import android.content.ContextWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.baidu.ai.aip.utils.HttpUtil;

public class BaiduAPI extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        String result = null;
        try {
            result = HttpUtil.post(strings[0], strings[1], strings[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            FileOutputStream f = new FileOutputStream(
                    strings[3]
                    + "/" +strings[4] + ".txt"
            );
            byte[] data = result.getBytes(StandardCharsets.UTF_8);
            f.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
