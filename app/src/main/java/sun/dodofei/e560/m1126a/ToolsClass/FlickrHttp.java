package sun.dodofei.e560.m1126a.ToolsClass;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by E560 on 2016/11/26.
 */

public class FlickrHttp {

    public static InputStream GetinputStream(String url_path) throws Exception {
        URL url = new URL(url_path);
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        httpURLConnection.setConnectTimeout(10 * 1000);
        httpURLConnection.setReadTimeout(10 * 1000);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() == 200) {
            inputStream = httpURLConnection.getInputStream();
        }
        return inputStream;
    }

    public static String GetResultstring(String url_path) throws Exception {
        InputStream inputStream = GetinputStream(url_path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] b = new byte[4096];
        int len = 0;
        while ((len = inputStream.read(b)) != -1) {
            byteArrayOutputStream.write(b, 0, len);
        }
        String GetResultstring = byteArrayOutputStream.toString();
        inputStream.close();
        byteArrayOutputStream.close();
        return GetResultstring;
    }

    public static Bitmap BigBitmap(String img_path) throws Exception {
        return BitmapFactory.decodeStream(GetinputStream(img_path));
    }

    public static HashMap<String, String> getBitmapSize(String img_path) throws Exception {
        HashMap<String, String> temp = new HashMap<String, String>();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(GetinputStream(img_path), null, options);
        temp.put("outHeight", String.valueOf(options.outHeight));
        temp.put("outWidth", String.valueOf(options.outWidth));
        temp.put("outMimeType", options.outMimeType);
        temp.put("img_url", img_path);
        return temp;
    }

    public static Bitmap GetBitmap(Application application, String img_path) throws Exception {
        Bitmap bitmap = null;
        String[] path_split = img_path.split("/");
        SharedPreferences sharedPreferences = application.getSharedPreferences("setopt", Context.MODE_PRIVATE);
        File file = null;
        if (sharedPreferences.getInt("switch_saveitemstoMobile", 1) == 0) {
            file = new File(application.getCacheDir(), path_split[path_split.length - 1]);
        } else {
            file = new File(application.getFilesDir(), path_split[path_split.length - 1]);
        }

        if (file.isFile()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } else {
            bitmap = BitmapFactory.decodeStream(GetinputStream(img_path));
            InputStream inputStream = GetinputStream(img_path);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] b = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
                fileOutputStream.flush();
            }
            fileOutputStream.close();
            inputStream.close();
        }
        return bitmap;
    }


}
