package sun.dodofei.e560.m1126a.ToolsClass;


import android.app.IntentService;
import android.content.Intent;

/**
 * Created by E560 on 2016/12/18.
 */

public class SavetoFTP extends IntentService {

    public SavetoFTP(String name) {
        super(name);
    }

    public SavetoFTP() {
        super("SavetoFTPServer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}