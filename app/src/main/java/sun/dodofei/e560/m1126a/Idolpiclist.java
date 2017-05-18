package sun.dodofei.e560.m1126a;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import sun.dodofei.e560.m1126a.ToolsClass.FlickrAPI;
import sun.dodofei.e560.m1126a.ToolsClass.FlickrHttp;
import sun.dodofei.e560.m1126a.ToolsClass.FlickrSQL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by E560 on 2016/11/29.
 */


public class Idolpiclist extends AppCompatActivity {
    private final String TAG = "Idolpiclist";
    private String idolname;
    private int img_size;
    private GridView gridView;
    private Myhandler myhandler;
    private int uNumColumns;
    private int grp;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(9, 100, 1, getString(R.string.gui_search));
        menu.add(9, 200, 2, R.string.gui_seting);
//        menu.add(9, 300, 3, "c");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 100:
                Search search = new Search(this, new Search.Callback() {
                    @Override
                    public void research(String name) {
                        idolname = name;
                        setTitle(name);
                        onStart();
                    }
                });
                search.requestWindowFeature(Window.FEATURE_NO_TITLE);
                search.show();
                break;
            case 200:
                Setopt setopt = new Setopt(this, new Setopt.CallBack() {
                    @Override
                    public void reStart() {
                        onStart();
                    }
                });
                setopt.requestWindowFeature(Window.FEATURE_NO_TITLE);

                setopt.show();
                break;
            case 300:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.color.black));
        gridView = new GridView(this);
        gridView.setBackgroundColor(Color.BLACK);
        Intent intent = getIntent();
        idolname = intent.getStringExtra("name");
        setTitle(idolname);
        sharedPreferences = this.getSharedPreferences(getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int x = point.x;
        if (x > 960) {
            uNumColumns = 4;
            img_size = x / uNumColumns;
        } else {
            uNumColumns = 4;
            img_size = x / uNumColumns;
        }
        gridView.setColumnWidth(img_size);
        gridView.setNumColumns(uNumColumns);
        setContentView(gridView);
        myhandler = new Myhandler();
        netThrread netThrread = new netThrread();
        netThrread.start();
    }

    public class Idols {
        private String id;
        private String owner;
        private String secret;
        private String server;
        private String farm;
        private String title;

        public Idols(String id, String owner, String secret, String server, String farm, String title) {
            this.id = id;
            this.owner = owner;
            this.secret = secret;
            this.server = server;
            this.farm = farm;
            this.title = title;
        }

        public String getImg_url(String size) {
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

    protected class netThrread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                String json_string = FlickrHttp.GetResultstring(FlickrAPI.Search(idolname));
                JSONObject jsonObject = new JSONObject(json_string);
                JSONObject jsonObject1 = jsonObject.getJSONObject("photos");
                JSONArray jsonArray = jsonObject1.getJSONArray("photo");
                HashMap<Integer, Idols> items = new HashMap<Integer, Idols>();
                FlickrSQL flickrSQL = new FlickrSQL(getApplicationContext());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject Jo2 = jsonArray.getJSONObject(i);
                    if (sharedPreferences.getInt(getString(R.string.nofilter), 1) == 1) {
                        if (flickrSQL.chk_filterid(Jo2.getString("id")) == 0) {
                            Idols idols = new Idols(Jo2.getString("id"), Jo2.getString("owner"), Jo2.getString("secret"), Jo2.getString("server"), Jo2.getString("farm"), Jo2.getString("title"));
                            items.put(items.size(), idols);
                        }
                    } else {
                        Idols idols = new Idols(Jo2.getString("id"), Jo2.getString("owner"), Jo2.getString("secret"), Jo2.getString("server"), Jo2.getString("farm"), Jo2.getString("title"));
                        items.put(items.size(), idols);
                    }
                }
                Log.i(TAG, "run items: " + items.size());
                Message msg = myhandler.obtainMessage();
                msg.obj = items;
                myhandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected class Myhandler extends Handler {

        private HashMap<Integer, Idols> temp;
        private String AllJsongString;

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            temp = (HashMap<Integer, Idols>) msg.obj;
            Idoladapter idoladapter = new Idoladapter(temp);

//            把Temp转换为JsonString 传递给下一个Activity.
            AllJsongString = DatatoJ(temp);
            gridView.setSelection(grp + 1);
            gridView.setAdapter(idoladapter);

//             点击打开图片。
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Idols idols = temp.get(position);
                    String url = idols.getImg_url("s");
                    if (sharedPreferences.getInt(getApplicationContext().getString(R.string.idol_icon_change), 1) == 1) {
                        FlickrSQL flickrSQL = new FlickrSQL(getApplicationContext());
                        flickrSQL.update_imgurl(idolname, url);
                    }
                    grp = gridView.getFirstVisiblePosition();
                    Intent intent = new Intent(getApplicationContext(), Viewidol.class);
//                  所有的图片信息
                    intent.putExtra("alljson", AllJsongString);
//                  当前图片在所有图片的位置
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });

//            长按登录数据库。
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Idols i = temp.get(position);
                    String iid = i.getId();
                    FlickrSQL flickrSQL = new FlickrSQL(getApplicationContext());
                    if (flickrSQL.chk_filterid(iid) == 0) {
                        flickrSQL.addidto_filter(iid);
                        Toast.makeText(Idolpiclist.this, "remove_" + iid, Toast.LENGTH_SHORT).show();
                    } else {
                        flickrSQL.removefrom_filter(iid);
                        Toast.makeText(Idolpiclist.this, "add_" + iid, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

        /**
         * 把Temp转换为JsonString 传递给下一个Activity.
         *
         * @param temp
         * @return
         */

        public String DatatoJ(HashMap<Integer, Idols> temp) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("total", String.valueOf(temp.size()));
                JSONArray jsonArray = new JSONArray();
                for (Map.Entry<Integer, Idols> entry : temp.entrySet()) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("index", String.valueOf(entry.getKey()));
                    jsonObject1.put("id", entry.getValue().getId());
                    jsonObject1.put("owner", entry.getValue().getOwner());
                    jsonObject1.put("secret", entry.getValue().getSecret());
                    jsonObject1.put("server", entry.getValue().getServer());
                    jsonObject1.put("farm", entry.getValue().getFarm());
                    jsonObject1.put("title", entry.getValue().getTitle());
                    jsonArray.put(jsonObject1);
                }
                jsonObject.put("items", jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }


    public class Idoladapter extends BaseAdapter {
        private HashMap<Integer, Idols> idolMap;

        public Idoladapter(HashMap<Integer, Idols> idolMap) {
            this.idolMap = idolMap;
        }

        @Override
        public int getCount() {
            return idolMap.size();
        }

        @Override
        public Object getItem(int position) {
            return idolMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = LayoutInflater.from(Idolpiclist.this).inflate(R.layout.idolpic_item, null);
            } else {
                view = convertView;
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.pic_list_item);
            imageView.setMinimumHeight(img_size);
            imageView.setMinimumWidth(img_size);
            String img_url = idolMap.get(position).getImg_url("s");
            Getbitmap getbitmap = new Getbitmap(imageView);
//            getbitmap.execute(img_url);
            getbitmap.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,img_url);
            return view;
        }

        public class Getbitmap extends AsyncTask<String, Integer, Bitmap> {
            private ImageView imageView;

            public Getbitmap(ImageView imageView) {
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    bitmap = FlickrHttp.GetBitmap(getApplication(), params[0]);
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
