package sun.dodofei.e560.m1126a;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import sun.dodofei.e560.m1126a.ToolsClass.FlickrSQL;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import static android.text.InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;

/**
 * Created by E560 on 2017/02/04.
 */

public class Search extends Dialog {
    private Context context;
    private Button button;
    private Callback callback;
    private AutoCompleteTextView autoCompleteTextView;

    public Search(Context context, Callback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
    }

    public interface Callback {
        public void research(String name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point point = new Point();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        display.getSize(point);
        ScrollView scrollView = new ScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        autoCompleteTextView = new AutoCompleteTextView(context);
        autoCompleteTextView.setMinimumWidth(point.x);
//        autoCompleteTextView.setListSelection(0);
//        autoCompleteTextView.setLines(1);
        autoCompleteTextView.setInputType(TYPE_TEXT_FLAG_IME_MULTI_LINE);
        linearLayout.addView(autoCompleteTextView);
        addadmo(AdSize.BANNER,linearLayout);
        linearLayout.setMinimumWidth(point.x);
        linearLayout.setMinimumHeight(point.y - 850);
        scrollView.addView(linearLayout);
        setContentView(scrollView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlickrSQL flickrSQL = new FlickrSQL(context);
        String[] data = flickrSQL.getAllnameString();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, data);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String name = (String) listView.getItemAtPosition(position);
                dismiss();
                callback.research(name);
            }
        });

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(autoCompleteTextView.getText());
//                Toast.makeText(context, autoCompleteTextView.getText(), Toast.LENGTH_SHORT).show();
                if(!autoCompleteTextView.getText().toString().isEmpty()) {
                    Intent intent = new Intent(context, ListPage.class);
                    intent.putExtra("key", autoCompleteTextView.getText().toString());
                    context.startActivity(intent);
                    autoCompleteTextView.setText(null);
                }
            }
        });



    }

    private void addadmo(AdSize adSize, ViewGroup viewGroup){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        AdView adView = new AdView(context);
        adView.setLayoutParams(layoutParams);
        adView.setAdSize(adSize);
        adView.setAdUnitId(context.getString(R.string.setAdUnitId2));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        viewGroup.addView(adView);
    }
}
