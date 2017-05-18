package sun.dodofei.e560.m1126a;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import sun.dodofei.e560.m1126a.ToolsClass.FirstStart;
import sun.dodofei.e560.m1126a.ToolsClass.FlickrAPI;
import sun.dodofei.e560.m1126a.ToolsClass.FlickrSQL;
import sun.dodofei.e560.m1126a.ToolsClass.SavetoFTP;
import sun.dodofei.e560.m1126a.ToolsClass.syncServer;
import sun.dodofei.e560.m1126a.ToolsClass.test_FlickrSQL;


public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawableResource(android.R.color.black);

        sharedPreferences = this.getSharedPreferences(getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);
        if (sharedPreferences.getString("ftp_json_path", null) == null) {
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString("ftp_json_path", "http://cdefgab.web.fc2.com/idolsname.json");
            e.commit();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), syncServer.class);
                startService(intent);
            }
        }).start();

        // 最初起動のため、サーバーと同期します。
        if (sharedPreferences.getInt("AppFirstStart", 0) == 0) {
            FirstStart firstStart = new FirstStart(this, new FirstStart.CallBack() {
                @Override
                public void pgRestart() {
                    Intent intent = new Intent(MainActivity.this, ListPage.class);
                    startActivity(intent);
                }
            });
        } else {
            Intent intent1 = new Intent(MainActivity.this, ListPage.class);
            startActivity(intent1);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = (ImageView) findViewById(R.id.topimageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, ListPage.class);
                startActivity(intent1);
            }
        });

    }
}
