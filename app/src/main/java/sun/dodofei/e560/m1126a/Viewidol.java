package sun.dodofei.e560.m1126a;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import sun.dodofei.e560.m1126a.ToolsClass.FlickrHttp;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by E560 on 2016/12/11.
 */

public class Viewidol extends AppCompatActivity {
    private final String TAG = "Viewidol.class";
    private BitmapHandel bitmapHandel;
    private HashMap<Integer, Idol> jsonMap;
    private RelativeLayout relativeLayout;
    private ProgressDialog progressDialog;
    private MDialog mDialog;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        bitmapHandel = new BitmapHandel();
        relativeLayout = new RelativeLayout(this);
        relativeLayout.setBackgroundResource(android.R.color.black);
        setContentView(relativeLayout);
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
//        所有的图片信息的JSON
        String alljson = intent.getStringExtra("alljson");
        this.jsontomap(alljson);
//        当前图片在所有图片的位置
        position = intent.getIntExtra("position", 1);
        ViewData(jsonMap.get(position));
    }

    private void jsontomap(String alljson) {
        jsonMap = new HashMap<Integer, Idol>();
        try {
            JSONObject jsonObject = new JSONObject(alljson);
            String total = jsonObject.getString("total");
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Idol idol = new Idol(jsonObject1.getString("id"),
                        jsonObject1.getString("owner"),
                        jsonObject1.getString("secret"),
                        jsonObject1.getString("server"),
                        jsonObject1.getString("farm"),
                        jsonObject1.getString("title")
                );
                jsonMap.put(Integer.parseInt(jsonObject1.getString("index")), idol);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ViewData(Idol sidol) {
        mDialog = new MDialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.show();
        ImgThread imgThread = new ImgThread(sidol);
        imgThread.start();
    }

    protected class ImgThread extends Thread {
        private Idol idol;

        public ImgThread(Idol idol) {
            this.idol = idol;
        }

        @Override
        public void run() {
            super.run();
            HashMap<String, HashMap> temp = new HashMap<String, HashMap>();
            String[] size = {"null"};
//            String[] size = {"s", "c", "b", "k", "h", "null"};
            for (String temp2 : size) {
                try {
                    HashMap<String, String> temp3;
                    if (temp2.equals("null")) {
                        temp3 = FlickrHttp.getBitmapSize(idol.getImg_url(""));
                    } else {
                        temp3 = FlickrHttp.getBitmapSize(idol.getImg_url(temp2));
                    }
                    temp.put(temp2, temp3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Message message = bitmapHandel.obtainMessage();
            message.obj = temp;
            bitmapHandel.sendMessage(message);
        }
    }


    protected class BitmapHandel extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setBackgroundResource(android.R.color.black);
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);

            HashMap<String, HashMap> temp = (HashMap<String, HashMap>) msg.obj;
//            String[] size = {"c", "b", "k", "h", "null"};
//            for (Map.Entry<String, HashMap> t2 : temp.entrySet()) {
//                System.out.println(t2.getValue());
//                System.out.println(t2.getKey());
//            }

            HashMap<String, String> temp2 = temp.get("null");
            String img_url = temp2.get("img_url");
            ImgShow is = new ImgShow(imageView);
            is.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, img_url);
//            is.execute(img_url);
            Toast.makeText(Viewidol.this, getApplicationContext().getString(R.string.bigimg_url) + "\n\r" + img_url, Toast.LENGTH_SHORT).show();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < jsonMap.size() - 1) {
                        position++;
                        ViewData(jsonMap.get(position));
                    }
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (position != 0) {
                        position--;
                        ViewData(jsonMap.get(position));
                    }
                    return false;
                }
            });
        }
    }

    class ImgShow extends AsyncTask<String, Integer, Bitmap> {

        private ImageView iv;

        public ImgShow(ImageView iv) {
            this.iv = iv;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = FlickrHttp.BigBitmap(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            iv.setImageBitmap(bitmap);
//            progressDialog.dismiss();
            mDialog.dismiss();
            relativeLayout.addView(iv);
            addadmo(AdSize.BANNER,relativeLayout);
        }
    }

    private class MDialog extends Dialog {
        //AlertDialog
        private Context context;

        public MDialog(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ViewGroup viewGroup = new RelativeLayout(context);
            viewGroup.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent));
            TextView textView = new TextView(context);
            textView.setText(R.string.gui_wait);
            viewGroup.addView(textView);
            setContentView(viewGroup);
        }
    }

    private class Idol {
        private String id;
        private String owner;
        private String secret;
        private String server;
        private String farm;
        private String title;

        public Idol(String id, String owner, String secret, String server, String farm, String title) {
            this.id = id;
            this.owner = owner;
            this.secret = secret;
            this.server = server;
            this.farm = farm;
            this.title = title;
        }

        public String getImg_url(String size) {

//            z	中等尺寸 640，最長邊為 640
//            c	中等尺寸 800，最長邊為 800†
//            b	大尺寸，最長邊為 1024*
//            h	大型 1600，長邊 1600†
//            k	大型 2048，長邊 2048†
//            o	原始圖片, 根據來源格式可以是 jpg、gif 或 png

            StringBuffer sb = new StringBuffer();
            sb.append("https://farm");
            sb.append(getFarm());
            sb.append(".staticflickr.com/");
            sb.append(getServer());
            sb.append("/");
            sb.append(getId());
            sb.append("_");
            sb.append(getSecret());
            if (!size.isEmpty()) {
                sb.append("_");
                sb.append(size);
            }
            sb.append(".jpg");
            return sb.toString();
        }

        public String getId() {
            return id;
        }

        public String getOwner() {
            return owner;
        }

        public String getSecret() {
            return secret;
        }

        public String getServer() {
            return server;
        }

        public String getFarm() {
            return farm;
        }

        public String getTitle() {
            return title;
        }
    }

    private void addadmo(AdSize adSize,ViewGroup viewGroup){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        AdView adView = new AdView(this);
        adView.setLayoutParams(layoutParams);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adView.setAdSize(adSize);
        adView.setAdUnitId(getString(R.string.setAdUnitId2));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        viewGroup.addView(adView);
    }
}
