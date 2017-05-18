package sun.dodofei.e560.m1126a;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import sun.dodofei.e560.m1126a.ToolsClass.ftp_upjson_server;
import sun.dodofei.e560.m1126a.ToolsClass.syncServer;


/**
 * Created by E560 on 2017/01/15.
 */

public class Setopt extends Dialog {
    private Context context;
    private Setopt.CallBack callBack;
    private SharedPreferences sharedPreferences;
    private Switch sw, sw1, sw2, sw3, sw4;
    private LinearLayout linearLayout;

    public Setopt(Context context){
        super(context);
    }

    public Setopt(Context context, CallBack callBack) {
        super(context);
        this.context = context;
        this.callBack = callBack;
    }

    public Setopt(Context context, int themeResId) {
        super(context, themeResId);
    }

    public Setopt(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    interface CallBack {
        void reStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sharedPreferences = context.getSharedPreferences("setopt", Context.MODE_PRIVATE);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedPreferencesFilename),Context.MODE_PRIVATE);
//        sharedPreferences.getInt("savetoDIMM", 0);
//        sharedPreferences.getInt("savetoFTP", 0);
//        sharedPreferences.getInt("ChangeIcon", 1);
//        sharedPreferences.getInt("show_name", 1);
//        sharedPreferences.getString("ftp_URL", "null");
//        sharedPreferences.getString("ftp_USER", "null");
//        sharedPreferences.getString("ftp_Password", "null");
//        sharedPreferences.getString("ftp_json_path", "http://cdefgab.web.fc2.com/idolsname.json");
//        sharedPreferences.getInt("switch_saveitemstoMobile",0);
        ScrollView scrollView = new ScrollView(context);
        linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.setopt, null);
        scrollView.addView(linearLayout);

        sw = (Switch) linearLayout.findViewById(R.id.switch_Filter);
        if (sharedPreferences.getInt("filter", 1) == 1) {
            sw.setChecked(true);
        } else {
            sw.setChecked(false);
        }
        sw1 = (Switch) linearLayout.findViewById(R.id.switch_savetoMobile);
        if (sharedPreferences.getInt("savetoDIMM", 0) == 0) {
            sw1.setChecked(false);
        } else {
            sw1.setChecked(true);
        }
        sw2 = (Switch) linearLayout.findViewById(R.id.switch_saveitemstoMobile);
        if (sharedPreferences.getInt("switch_saveitemstoMobile", 1) == 1) {
            sw2.setChecked(true);
        } else {
            sw2.setChecked(false);
        }
        sw3 = (Switch) linearLayout.findViewById(R.id.switch_icon_change);
        if (sharedPreferences.getInt("ChangeIcon", 1) == 0) {
            sw3.setChecked(false);
        } else {
            sw3.setChecked(true);
        }
        sw4 = (Switch) linearLayout.findViewById(R.id.switch_show_name);
        if (sharedPreferences.getInt("show_name", 0) == 0) {
            sw4.setChecked(false);
        } else {
            sw4.setChecked(true);
        }


        EditText e1 = (EditText) linearLayout.findViewById(R.id.edit_ftp_url);
        EditText e2 = (EditText) linearLayout.findViewById(R.id.edit_ftp_user);
        EditText e3 = (EditText) linearLayout.findViewById(R.id.edit_ftp_password);
        e1.setText(sharedPreferences.getString("ftp_URL", ""));
        e2.setText(sharedPreferences.getString("ftp_USER", ""));
        e3.setText(sharedPreferences.getString("ftp_Password", ""));
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        linearLayout.setMinimumWidth(point.x);
        linearLayout.setMinimumHeight(point.y - 500);
        setContentView(scrollView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button bt = (Button) findViewById(R.id.buttonOK);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putInt("filter", 1);
                }
                if (!isChecked) {
                    editor.putInt("filter", 0);
                }
                editor.commit();
            }
        });

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putInt("savetoDIMM", 1);
                }
                if (!isChecked) {
                    editor.putInt("savetoDIMM", 0);
                }
                editor.commit();
            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putInt("switch_saveitemstoMobile", 1);
                }
                if (!isChecked) {
                    editor.putInt("switch_saveitemstoMobile", 0);
                }
                editor.commit();
            }
        });

        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putInt(context.getString(R.string.idol_icon_change), 1);
                }
                if (!isChecked) {
                    editor.putInt(context.getString(R.string.idol_icon_change), 0);
                }
                editor.commit();
            }
        });

        sw4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putInt("show_name", 1);
                }
                if (!isChecked) {
                    editor.putInt("show_name", 0);
                }
                editor.commit();
            }
        });


        Button button = (Button) linearLayout.findViewById(R.id.ftp_json_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, R.string.gui_Send_jsonfile, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ftp_upjson_server.class);
                context.startService(intent);
            }
        });

        TextView textView = (TextView) linearLayout.findViewById(R.id.ftp_json_path);
        textView.setText(sharedPreferences.getString("ftp_json_path", "http://cdefgab.web.fc2.com/idolsname.json"));

        Button button1 = (Button) linearLayout.findViewById(R.id.ftp_sync);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, R.string.gui_Sync_jsonfile, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, syncServer.class);
                context.startService(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(context, "onStop", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        TextView url = (TextView) linearLayout.findViewById(R.id.edit_ftp_url);
        TextView name = (TextView) linearLayout.findViewById(R.id.edit_ftp_user);
        TextView pas = (TextView) linearLayout.findViewById(R.id.edit_ftp_password);
        TextView path = (TextView) linearLayout.findViewById(R.id.ftp_json_path);
        editor.putString("ftp_URL", url.getText().toString());
        editor.putString("ftp_USER", name.getText().toString());
        editor.putString("ftp_Password", pas.getText().toString());
        editor.putString("ftp_json_path", path.getText().toString());
        editor.commit();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        callBack.reStart();
    }
}