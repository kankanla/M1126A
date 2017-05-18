package sun.dodofei.e560.m1126a.ToolsClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * Created by E560 on 2016/11/26.
 */

public class test_FlickrSQL extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        LinearLayout linearLayout = new LinearLayout(this);
        Button button1 = new Button(this);
        button1.setText("Synchronize");
        linearLayout.addView(button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Synchronize_server s = new Synchronize_server();
                s.execute(test_FlickrSQL.this);
            }
        });

        Button button2 = new Button(this);
        button2.setText("FTP_filterid FTP_idolname");
        linearLayout.addView(button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FTP_send ftp_send = new FTP_send();
                ftp_send.execute(getApplicationContext());
            }
        });

        setContentView(linearLayout);
    }

    public class Synchronize_server extends AsyncTask<Context, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Context... params) {
            Boolean Evel = false;
            FlickrSQL flickrSQL = new FlickrSQL(params[0]);
            if (flickrSQL.Synchronize_filter() && flickrSQL.Synchronize_toplist()) {
                Evel = true;
            }
            return Evel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Synchronize");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean Evel) {
            super.onPostExecute(Evel);
            progressDialog.dismiss();
            if (Evel) {
                Toast.makeText(test_FlickrSQL.this, "true", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(test_FlickrSQL.this, "flase", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class FTP_send extends AsyncTask<Context, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Context... params) {
            FlickrSQL flickrSQL = new FlickrSQL(params[0]);
            flickrSQL.FTP_filterid("null");
            flickrSQL.FTP_idolname("null");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("FTP server");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
        }
    }
}


