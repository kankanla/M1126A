package sun.dodofei.e560.m1126a.ToolsClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import sun.dodofei.e560.m1126a.R;


/**
 * Created by E560 on 2017/01/11.
 */

public class FirstStart extends AppCompatActivity {
    protected ProgressDialog progressDialog;
    protected Context context;
    protected SharedPreferences sharedPreferences;

    public interface CallBack {
        public void pgRestart();
    }

    public FirstStart(Context context, CallBack callBack) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);
        SyncItemList syncItemList = new SyncItemList(callBack);
//        syncItemList.execute(context);
        syncItemList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,context);
    }

    private class SyncItemList extends AsyncTask<Context, Integer, Boolean> {
        private CallBack callBack;

        public SyncItemList(CallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            Boolean errorlave = false;
            FlickrSQL flickrSQL = new FlickrSQL(context);
            if (flickrSQL.Synchronize_toplist()) {
                errorlave = true;
            }
            return errorlave;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(context.getString(R.string.gui_firstsany));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean errorlave) {
            super.onPostExecute(errorlave);
            progressDialog.dismiss();
            System.out.println(Thread.currentThread().getName());
            if (errorlave) {
                Toast.makeText(context, context.getString(R.string.gui_synctrue), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor et = sharedPreferences.edit();
                et.putInt("AppFirstStart", 1);
                et.commit();
                callBack.pgRestart();
            } else {
                Toast.makeText(context, "flase", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, context.getString(R.string.gui_syncflase), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor et = sharedPreferences.edit();
                et.putInt("AppFirstStart", 0);
                et.commit();
            }
        }
    }

}
