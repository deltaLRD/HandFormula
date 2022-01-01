package com.example.handformula;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.HashMap;
import java.util.Map;

import eu.janmuller.android.simplecropimage.CropImage;
import com.baidu.ai.aip.utils.FileUtil;
import com.baidu.ai.aip.utils.Base64Util;
import com.judemanutd.katexview.KatexView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private KatexView katexView;
    private TextView container;
    private String access_token;
    private File imageFile;
    private File currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @SuppressLint("QueryPermissionsNeeded")
    public void CaptureImg(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        System.out.print("Capture\n");
    }

    public static final int PICTURE_CROPPING_CODE = 200;
    private void pictureCropping() {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, imageFile.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 2);

        startActivityForResult(intent, PICTURE_CROPPING_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return ;
        }

        Bitmap bitmap;

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                pictureCropping();
                break;
            }
            case PICTURE_CROPPING_CODE:
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {

                    return;
                }

                bitmap = BitmapFactory.decodeFile(this.imageFile.getPath());
                this.imageView.setImageBitmap(bitmap);
                break;
        }
    }

    public void Transform(View view) throws Exception {
        System.out.print("Transform\n");
        this.access_token = this.container.getText().toString();
        System.out.println(access_token);
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/formula";
        String param = "image="+Image2Base64(imageFile.getPath());
        BaiduAPI result = new BaiduAPI();
        result.execute(url, this.access_token, param, this.currentPath.toString(),"OcrResult");
        FileInputStream ocrResult = new FileInputStream(this.currentPath+"/"+"OcrResult.txt");
        byte[] bytes = new byte[0];
        bytes = new byte[ocrResult.available()];

        ocrResult.read(bytes);
        KatexDecode katexDecoder = new KatexDecode(new String(bytes));
        
        this.katexView.setText("$$"+katexDecoder.getKatexText()+"$$");

    }

    private void initView() throws IOException {
        this.imageView = findViewById(R.id.imageView);
        this.katexView = findViewById(R.id.katexView);
        this.container = findViewById(R.id.container);
        this.currentPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        getAccessToken();
        this.access_token = this.container.getText().toString();

    }

    private String Image2Base64(String path) throws IOException {
        byte[] imgData = FileUtil.readFileByBytes(path);
        String imgStr = Base64Util.encode(imgData);
        String imgParam = URLEncoder.encode(imgStr, "UTF-8");
        return imgParam;
    }

    private void getAccessToken() {
        StringBuffer host = new StringBuffer("https://aip.baidubce.com/oauth/2.0/token");
        StringBuffer target = new StringBuffer("access_token");
        StringBuffer result = new StringBuffer();
        Map params = new HashMap();
        params.put("grant_type", "client_credentials");
        params.put("client_id", "lWNnhXQQLrDj0C7GFvEPB37c");
        params.put("client_secret", "dULoP2nZ0gUPeIHK1vZPd3M4LRPz2wER");
        host.append("?");
        host.append(urlencode(params));
        API api = new API(container);
        api.execute(host, new StringBuffer("GET"), target, result);

    }

    private static String urlencode(Map<String, Object>data) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        return sb.toString();
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_"+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        this.imageFile = image;
        return image;
    }
}