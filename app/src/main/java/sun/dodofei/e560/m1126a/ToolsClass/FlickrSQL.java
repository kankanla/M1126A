package sun.dodofei.e560.m1126a.ToolsClass;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


/**
 * Created by E560 on 2016/11/26.
 */

public class FlickrSQL {
    /**
     *
     */
    private Context context;
    private final String TAG = "FlickrSQL";
    private final int DB_VERSION = 1;
    private final String JLSON_FILTER = "http://cdefgab.web.fc2.com/filter.json";
    private final String DB_NAME = "IdolAPP.db";
    private String JSON_PATH = "http://cdefgab.web.fc2.com/idolsname.json";
    private mSQL mSQL_db;
    private SharedPreferences sharedPreferences;
    private String ftp_url;
    private String ftp_user;
    private String ftp_passwrod;

    /**
     * @param context
     */
    public FlickrSQL(Context context) {
        Log.i(TAG, "FlickrSQL");
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setopt", Context.MODE_PRIVATE);
        ftp_url = sharedPreferences.getString("ftp_URL", null);
        ftp_user = sharedPreferences.getString("ftp_USER", null);
        ftp_passwrod = sharedPreferences.getString("ftp_Password", null);
        JSON_PATH = sharedPreferences.getString("ftp_json_path", "http://cdefgab.web.fc2.com/idolsname.json");
        mSQL_db = new mSQL(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 檢查Name是否存在。count = 1 存在，0 不存在。
     *
     * @param name
     * @return
     * @throws Exception
     */
    private int check_name(String name) throws Exception {
        int count = 0;
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        String sql_cmd = "select name from toplist where name = ?";
        Cursor cursor = db.rawQuery(sql_cmd, new String[]{name});
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 返回Name個數。
     *
     * @return
     */
    private int db_count() {
        int count = 0;
        SQLiteDatabase db = mSQL_db.getReadableDatabase();
        String sql_cmd = "select count(*) from toplist";
        Cursor cursor = db.rawQuery(sql_cmd, null);
        while (cursor.moveToNext()) {
            count = Integer.parseInt(cursor.getString(cursor.getColumnIndex("count(*)")));
        }
        cursor.close();
        Log.i(TAG, "db_idols_count: " + count);
        db.close();
        return count;
    }

    /**
     * 返回所有Name條目。
     *
     * @return
     */
    public Cursor SelectallIdol() {
        Log.i(TAG, "SelectallIdol");
        Cursor cursor = null;
        SQLiteDatabase db = mSQL_db.getReadableDatabase();
        String sql_cmd = "select * from toplist WHERE active NOT IN(?)";
        cursor = db.rawQuery(sql_cmd, new String[]{"false"});
        return cursor;
    }

    public Cursor SearchIdol(String searchkey) {
        Cursor cursor = null;
        SQLiteDatabase db = mSQL_db.getReadableDatabase();
//        String sql= "SELECT title, body FROM notes WHERE body LIKE '%' || ? || '%' ESCAPE '$'";
        String sql_cmd = "select * from toplist where name LIKE '%' || ? || '%'";
        cursor = db.rawQuery(sql_cmd, new String[]{searchkey});
        return cursor;
    }


    public Cursor getAllnames() {
        Cursor cursor = null;
        SQLiteDatabase db = mSQL_db.getReadableDatabase();
        String sql_cmd = "select name from toplist";
        return db.rawQuery(sql_cmd, null);
    }

    public String[] getAllnameString() {
        SQLiteDatabase db = mSQL_db.getReadableDatabase();
        String sql_cmd = "select name from toplist";
        Cursor cursor = db.rawQuery(sql_cmd, null);
        String[] list = new String[cursor.getCount()];
        while (cursor.moveToNext()) {
            list[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex("name"));
        }
        return list;
    }

    /**
     * 從JSON_PATH 更新客戶端數據庫。
     * String JSON_PATH = "http://cdefgab.web.fc2.com/idolsname.json";
     *
     * @throws Exception
     */

    public Boolean Synchronize_toplist() {
        Log.i(TAG, "Synchronize_toplist");
        Boolean Evel = false;
        try {
            int insertcount = 0;
            SQLiteDatabase db = mSQL_db.getWritableDatabase();
            SharedPreferences sharedPreferences = context.getSharedPreferences("setopt", Context.MODE_PRIVATE);
            String json = FlickrHttp.GetResultstring(JSON_PATH);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("idolnames");

            int detaSize = jsonArray.length();
            if (sharedPreferences.getInt("AppFirstStart", 0) == 0) {
                detaSize = 100;
            }

            for (int i = 0; i < detaSize; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if (check_name(jsonObject1.getString("name")) == 1) {
                    update_active(jsonObject1.getString("name"), jsonObject1.getString("active"));
                } else {
                    insertcount++;
//                    insert(jsonObject1.getString("name"), jsonObject1.getString("img"));
                    insert(jsonObject1.getString("name"), "http://cdefgab.web.fc2.com/firstimg.png");
                    update_active(jsonObject1.getString("name"), jsonObject1.getString("active"));
                }
            }
            Log.i(TAG, "sysdb: new Insert " + insertcount);
            db.close();
            Evel = true;
        } catch (Exception e) {
            e.printStackTrace();
            Evel = false;
        }
        return Evel;
    }

    /**
     * 更新過濾器 filter_ID。
     */

    public boolean Synchronize_filter() {
        boolean Evel = false;
        try {
            Log.i(TAG, "Synchronize_filter");
            String json = FlickrHttp.GetResultstring(JLSON_FILTER);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (!jsonArray.getString(i).isEmpty()) {
                    if (chk_filterid(jsonArray.getString(i)) < 1) {
                        addidto_filter(jsonArray.getString(i));
                    }
                }
            }
            Evel = true;
        } catch (Exception e) {
            e.printStackTrace();
            Evel = false;
        }
        return Evel;
    }


    /**
     * 设置 Idolname 为非活动。
     *
     * @param idol_name
     */

    public void setIdolFalse(String idol_name) {
        Log.i(TAG, "setIdolFalse");
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", "false");
        String selection = "name = ?";
        String[] args = {idol_name};
        db.update("toplist", values, selection, args);
        db.close();
    }

    /**
     * 更新过滤器ID到服务器。
     */

    public void FTP_filterid(String filename) {
        Log.i(TAG, "filter_idToFTP");
        try {
            SQLiteDatabase db = mSQL_db.getReadableDatabase();
            String sql_cmd = "select id from filter_id";
            Cursor cursor = db.rawQuery(sql_cmd, null);

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            while (cursor.moveToNext()) {
                jsonArray.put(jsonArray.length(), cursor.getString(cursor.getColumnIndex("id")));
            }
            jsonObject.put("items", jsonArray);
            File jsonfile = new File(context.getFilesDir(), "filter.json");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonfile)));
            bufferedWriter.write(jsonObject.toString());
            db.close();
            bufferedWriter.close();


            sun_FTP sun_ftp = new sun_FTP(ftp_url, ftp_user, ftp_passwrod);
//            sun_FTP sun_ftp = new sun_FTP("192.168.11.10","user","user");
            sun_ftp.FTPconnect();
            sun_ftp.upload(new File[]{jsonfile});
            sun_ftp.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新过的 Idol name 列表上传到服务器。
     */

    public void FTP_idolname(String filename) {
        try {
            Log.i(TAG, "idolnameToFTP");
            SQLiteDatabase db = mSQL_db.getReadableDatabase();
            String sql_cmd = "select * from toplist";
            Cursor cursor = db.rawQuery(sql_cmd, null);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total", cursor.getCount());
            JSONArray jsonArray = new JSONArray();
            while (cursor.moveToNext()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name", cursor.getString(cursor.getColumnIndex("name")));
                jsonObject1.put("img", cursor.getString(cursor.getColumnIndex("img_url")));
                jsonObject1.put("count", cursor.getString(cursor.getColumnIndex("accc")));
                jsonObject1.put("active", cursor.getString(cursor.getColumnIndex("active")));
                jsonArray.put(jsonArray.length(), jsonObject1);
            }
            jsonObject.put("idolnames", jsonArray);

            File jsonfile = new File(context.getFilesDir(), "idolsname.json");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonfile)));
            bufferedWriter.write(jsonObject.toString());
            db.close();
            bufferedWriter.close();

            sun_FTP sun_ftp = new sun_FTP(ftp_url, ftp_user, ftp_passwrod);
            sun_ftp.FTPconnect();
            sun_ftp.upload(new File[]{jsonfile});
            sun_ftp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 檢查 filter_ID 是否存在。
     * =1 存在
     *
     * @param id
     * @return
     */
    public int chk_filterid(String id) {
//        Log.i(TAG, "chk_filterid > " + id);
        int chk = 0;
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        String sql_cmd = "select id from filter_id where id = ?";
        Cursor cursor = db.rawQuery(sql_cmd, new String[]{id});
        chk = cursor.getCount();
        db.close();
        cursor.close();
        return chk;
    }

    /**
     * 添加FilterID
     *
     * @param id
     */
    public void addidto_filter(String id) {
//        Log.i(TAG, "addidto_filter");
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        String sql_cmd = "INSERT INTO filter_id(id)values(?)";
        db.execSQL(sql_cmd, new String[]{id});
        db.close();
    }

    public void removefrom_filter(String id) {
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        String sql_cmd = "delete from filter_id where id = ?";
        db.execSQL(sql_cmd, new String[]{id});
        db.close();
    }

    /**
     * @param name
     * @param img_url
     */
    public void insert(String name, String img_url) {
//        Log.i(TAG, "insert");
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        String sql_cmd = "insert into toplist(name,img_url) values (? ,?)";
        db.execSQL(sql_cmd, new String[]{name, img_url});
        db.close();
    }

    /**
     * @param name
     * @param img_url
     */
    public void update_imgurl(String name, String img_url) {
//        Log.i(TAG, "update_imgurl");
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        String sql_cmd = "update toplist set img_url = ? where name = ?";

        db.execSQL(sql_cmd, new String[]{img_url, name});
        db.close();
    }

    /**
     * @param name
     * @param active
     */
    public void update_active(String name, String active) {
//        Log.i(TAG, "update_active");
        SQLiteDatabase db = mSQL_db.getWritableDatabase();
        String sql_cmd = "update toplist set active = ? where name = ?";
        db.execSQL(sql_cmd, new String[]{active, name});
        db.close();
    }


    public class mSQL extends SQLiteOpenHelper {

        /**
         * mSQL_db = new mSQL(context, db_pak_name, null, db_version);
         * https://farm8.staticflickr.com/7566/15949681296_b6a869bdfd_q.jpg
         * https://farm8.staticflickr.com/7569/15975428145_c85aae84bd_s.jpg
         */

        /**
         * @param context
         * @param name
         * @param factory
         * @param version
         */
        public mSQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            Log.i(TAG, "mSQL");
        }

        /**
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "onCreate");
            String topidoltable = "create table toplist (rowid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL,up_date TEXT default current_timestamp,img_url TEXT NOT NULL, accc INTEGER DEFAULT 0, active TEXT DEFAULT 'ture')";
            String filter_id = "create table filter_id(rowid INTEGER PRIMARY KEY AUTOINCREMENT,id TEXT UNIQUE NOT NULL)";
            String filter_owner = "create table filter_owner (rowid INTEGER PRIMARY KEY AUTOINCREMENT,owner TEXT UNIQUE NOT NULL)";
            String setimgurl = "update toplist set img_url = ?";
            db.execSQL(topidoltable);
            db.execSQL(filter_id);
            db.execSQL(filter_owner);
            db.execSQL(setimgurl, new String[]{"https://farm8.staticflickr.com/7569/15975428145_c85aae84bd_s.jpg"});
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "onUpgrade");

        }
    }
}
