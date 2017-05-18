package sun.dodofei.e560.m1126a;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.style.BackgroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import sun.dodofei.e560.m1126a.ToolsClass.FlickrHttp;
import sun.dodofei.e560.m1126a.ToolsClass.FlickrSQL;


import java.util.HashMap;


/**
 * 显示所有的Idol名字
 * Created by E560 on 2016/11/27.
 */

public class ListPage extends AppCompatActivity {
    private Cursor cursor;
    private Myhand myhand;
    private int img_size;
    private int uNumColumns;
    private GridView gridView;
    private int grp;
    private String searchkey;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(this.getString(R.string.sharedPreferencesFilename), MODE_PRIVATE);
        setContentView(R.layout.listpage_layout);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        Intent intent = getIntent();
        searchkey = intent.getStringExtra("key");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        取得画面尺寸
        gridView = (GridView) findViewById(R.id.List_page_gridview);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int x = point.x;
//        设置Column的大小
        if (x > 960) {
            uNumColumns = 4;
            img_size = x / uNumColumns;
        } else {
            uNumColumns = 4;
            img_size = x / uNumColumns;
        }
        gridView.setColumnWidth(img_size);
        setContentView(gridView);
//        启动新线程
        myhand = new Myhand();
        MyThread myThread = new MyThread();
        myThread.start();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String img_url = cursor.getString(cursor.getColumnIndex("img_url"));
                Toast.makeText(ListPage.this, img_url, Toast.LENGTH_SHORT).show();
                grp = gridView.getFirstVisiblePosition();
                Intent intent = new Intent(getApplicationContext(), Idolpiclist.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                FlickrSQL flickrSQL = new FlickrSQL(getApplicationContext());
                flickrSQL.setIdolFalse(name);
                Toast.makeText(ListPage.this, "false", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            FlickrSQL flickrSQL = new FlickrSQL(getApplicationContext());
            Message message = myhand.obtainMessage();
            if (searchkey == null) {
                message.obj = flickrSQL.SelectallIdol();
            } else {
                message.obj = flickrSQL.SearchIdol(searchkey);
            }
            myhand.sendMessage(message);
        }
    }

    private class Myhand extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            cursor = (Cursor) msg.obj;
            if (cursor.getCount() == 0) {
                Toast.makeText(ListPage.this, getString(R.string.gui_searchIdol_countzero), Toast.LENGTH_SHORT).show();
            }
            GridView gridView = (GridView) findViewById(R.id.List_page_gridview);
            gridView.setSelection(grp + 1);
            gridView.setAdapter(new Myadapter());
        }
    }


    public class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            cursor.moveToPosition(position);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String img_url = cursor.getString(cursor.getColumnIndex("img_url"));
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put("name", name);
            temp.put("img_url", img_url);
            return temp;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = LayoutInflater.from(ListPage.this).inflate(R.layout.list_item, null);
            } else {
                view = convertView;
            }

            ImageView imageView = (ImageView) view.findViewById(R.id.list_item);
            imageView.setMinimumHeight(img_size);
            imageView.setMinimumWidth(img_size);
            TextView textView = (TextView) view.findViewById(R.id.List_itemtextView);
            cursor.moveToPosition(position);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String img_url = cursor.getString(cursor.getColumnIndex("img_url"));
            if (sharedPreferences.getInt("show_name", 0) == 1) {
                textView.setText(name);
            }else {
                textView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            }
//            new Gimg(imageView).execute(img_url);
            new Gimg(imageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,img_url);
            return view;
        }

        public class Gimg extends AsyncTask<String, Integer, Bitmap> {
            private ImageView imageView;

            public Gimg(ImageView imageView) {
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    bitmap = FlickrHttp.GetBitmap(getApplication(), params[0]);
//                    bitmap = FlickrHttp.BigBitmap(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}