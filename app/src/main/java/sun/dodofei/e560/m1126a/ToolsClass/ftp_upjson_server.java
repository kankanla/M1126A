package sun.dodofei.e560.m1126a.ToolsClass;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by E560 on 2017/01/29.
 */

public class ftp_upjson_server extends IntentService {

    public ftp_upjson_server() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FlickrSQL flickrSQL = new FlickrSQL(getApplication().getApplicationContext());
        flickrSQL.FTP_filterid("null");
        flickrSQL.FTP_idolname("null");
    }
}
