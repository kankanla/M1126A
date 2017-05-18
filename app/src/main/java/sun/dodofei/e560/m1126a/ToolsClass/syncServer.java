package sun.dodofei.e560.m1126a.ToolsClass;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by E560 on 2017/01/15.
 */

public class syncServer extends IntentService {

    public syncServer() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FlickrSQL flickrSQL = new FlickrSQL(getApplicationContext());
                flickrSQL.Synchronize_toplist();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                FlickrSQL flickrSQL = new FlickrSQL(getApplicationContext());
                flickrSQL.Synchronize_filter();
            }
        }).start();
    }
}
