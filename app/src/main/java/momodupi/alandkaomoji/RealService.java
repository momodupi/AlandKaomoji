package momodupi.alandkaomoji;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 40910 on 2017/5/15.
 */

public class RealService extends Service {

    LinearLayout wmlayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;

    Button movebutton, closebutton;

    ArrayAdapter<String> arrayAdapter;
    List<String> data;
    ListView listView;

    private boolean moveflag = false, hideflag = false;
    private int orirawx = 0, orirawy = 0;
    private short clickcnt = 0;
    private String butstr;

    @Override
    public void onCreate()
    {
        super.onCreate();

        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 800;
        wmParams.y = 800;
        wmParams.width = dip2px(getApplicationContext(), 96);
        wmParams.height = dip2px(getApplicationContext(), 270);
        orirawx = wmParams.x;
        orirawy = wmParams.y;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        wmlayout = (LinearLayout) inflater.inflate(R.layout.service_wm, null);
        mWindowManager.addView(wmlayout, wmParams);

        listView = (ListView) wmlayout.findViewById(R.id.listView);
        movebutton = (Button) wmlayout.findViewById(R.id.moveBtn);

        closebutton = (Button) wmlayout.findViewById(R.id.closeBtn);
        closebutton.setText("x");

        SharedPreferences preferences = getSharedPreferences("kaomojifreqlist", MODE_PRIVATE);
        String json = preferences.getString("kaomojifreq", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            List<String> rjson = new ArrayList<String>();
            rjson = gson.fromJson(json, type);
            data = rjson;
        }
        else {
            data = getkaomojilist();
        }

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.service_item, data);
        listView.setAdapter(arrayAdapter);

        wmlayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String[] btykao_expd = {"(|||ﾟдﾟ)", "( ´_ゝ`)", "(　ﾟ 3ﾟ)", "( ﾟ∀ﾟ)", "( ﾟ∀ﾟ)"};
                String[] btykao_hide = {"|дﾟ )", "|д` )", "|-` )", "|∀` )", "|∀` )"};

                if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                    int level = intent.getIntExtra("level", 0);
                    int scale = intent.getIntExtra("scale", 100);
                    int curPower = (level * 100 / scale) / 25;
                    if (hideflag == false) {
                        movebutton.setText(btykao_expd[curPower]);
                    }
                    else {
                        movebutton.setText(btykao_hide[curPower]);
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        movebutton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN: {

                        orirawx = (int) event.getRawX();
                        orirawy = (int) event.getRawY();

                        clickcnt += 1;
                        if (clickcnt == 2)
                        {
                            clickcnt = 0;
                            Toast.makeText(RealService.this, "powered by momodupi", Toast.LENGTH_SHORT).show();
                        }

                        if (moveflag == false) {
                            wmParams.x = orirawx - (int) event.getX();
                            wmParams.y = orirawy - (int) event.getY();

                            wmParams.width = dip2px(getApplicationContext(), 96);
                            wmParams.height = dip2px(getApplicationContext(), 270);
                            wmlayout.setAlpha((float)1);
                            listView.setVisibility(v.VISIBLE);
                            mWindowManager.updateViewLayout(wmlayout, wmParams);
                            hideflag = false;
                            moveflag = true;
                        }

                        butstr = (String) movebutton.getText();
                        movebutton.setText("( ﾟ∀。)");
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                        wmParams.x += (int) event.getRawX() - orirawx;
                        wmParams.y += (int) event.getRawY() - orirawy;
                        mWindowManager.updateViewLayout(wmlayout, wmParams);
                        orirawx = (int) event.getRawX();
                        orirawy = (int) event.getRawY();

                        clickcnt = 0;
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        movebutton.setText(butstr);
                    }
                    break;
                    default: {
                        ;
                    }
                }

                return false;
            }
        });

        closebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("kaomojifreqlist", MODE_PRIVATE).edit();
                Gson gson = new Gson();
                String json = gson.toJson(data);
                editor.putString("kaomojifreq", json);
                editor.commit();

                stopSelf();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = arrayAdapter.getItem((int) id).toString();

                ClipboardManager clipbrd = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData;
                clipData = ClipData.newPlainText("kao", str);
                clipbrd.setPrimaryClip(clipData);

                //movebutton.setText(str);

                //closebutton.setVisibility(view.GONE);

                //movebutton.setWidth(dip2px(getApplicationContext(), 32));
                //movebutton.setHeight(dip2px(getApplicationContext(), 32));
                movebutton.setText("|∀` )");

                wmParams.x = 8000;  //force
                moveflag = false;
                wmlayout.setAlpha((float)0.5);
                wmParams.width = dip2px(getApplicationContext(), 60);
                wmParams.height = dip2px(getApplicationContext(), 36);
                mWindowManager.updateViewLayout(wmlayout, wmParams);

                Collections.reverse(data);
                data.remove(str);
                data.add(str);
                Collections.reverse(data);
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.service_item, data);

                listView.setAdapter(arrayAdapter);
                listView.setVisibility(view.GONE);
                hideflag = true;
            }
        });
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        if(wmlayout != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(wmlayout);
        }
        super.onDestroy();
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private List<String> getkaomojilist() {

        List<String> mdata = new ArrayList<String>();
        mdata.add("|∀ﾟ");
        mdata.add("(´ﾟДﾟ`)");
        mdata.add("(;´Д`)");
        mdata.add("(｀･ω･)");
        mdata.add("(=ﾟωﾟ)=");
        mdata.add("| ω・´)");
        mdata.add("|-` )");
        mdata.add("|д` )");
        mdata.add("|ー` )");
        mdata.add("|∀` )");
        mdata.add("(つд⊂)");
        mdata.add("(ﾟДﾟ≡ﾟДﾟ)");
        mdata.add("(＾o＾)ﾉ");
        mdata.add("(|||ﾟДﾟ)");
        mdata.add("( ﾟ∀ﾟ)");
        mdata.add("( ´∀`)");
        mdata.add("(*´∀`)");
        mdata.add("(*ﾟ∇ﾟ)");
        mdata.add("(*ﾟーﾟ)");
        mdata.add("(　ﾟ 3ﾟ)");
        mdata.add("( ´ー`)");
        mdata.add("( ・_ゝ・)");
        mdata.add("( ´_ゝ`)");
        mdata.add("(*´д`)");
        mdata.add("(・ー・)");
        mdata.add("(・∀・)");
        mdata.add("(ゝ∀･)");
        mdata.add("(〃∀〃)");
        mdata.add("(*ﾟ∀ﾟ*)");
        mdata.add("( ﾟ∀。)");
        mdata.add("( `д´)");
        mdata.add("(`ε´ )");
        mdata.add("(`ヮ´ )");
        mdata.add("σ`∀´)");
        mdata.add(" ﾟ∀ﾟ)σ");
        mdata.add("ﾟ ∀ﾟ)ノ");
        mdata.add("(╬ﾟдﾟ)");
        mdata.add("(|||ﾟдﾟ)");
        mdata.add("( ﾟдﾟ)");
        mdata.add("Σ( ﾟдﾟ)");
        mdata.add("( ;ﾟдﾟ)");
        mdata.add("( ;´д`)");
        mdata.add("(　д ) ﾟ ﾟ");
        mdata.add("( ☉д⊙)");
        mdata.add("(((　ﾟдﾟ)))");
        mdata.add("( ` ・´)");
        mdata.add("( ´д`)");
        mdata.add("( -д-)");
        mdata.add("(>д<)");
        mdata.add("･ﾟ( ﾉд`ﾟ)");
        mdata.add("( TдT)");
        mdata.add("(￣∇￣)");
        mdata.add("(￣3￣)");
        mdata.add("(￣ｰ￣)");
        mdata.add("(￣ . ￣)");
        mdata.add("(￣皿￣)");
        mdata.add("(￣艸￣)");
        mdata.add("(￣︿￣)");
        mdata.add("(￣︶￣)");
        mdata.add("ヾ(´ωﾟ｀)");
        mdata.add("(*´ω`*)");
        mdata.add("(・ω・)");
        mdata.add("( ´・ω)");
        mdata.add("(｀・ω)");
        mdata.add("(´・ω・`)");
        mdata.add("(`・ω・´)");
        mdata.add("( `_っ´)");
        mdata.add("( `ー´)");
        mdata.add("( ´_っ`)");
        mdata.add("( ´ρ`)");
        mdata.add("( ﾟωﾟ)");
        mdata.add("(oﾟωﾟo)");
        mdata.add("(　^ω^)");
        mdata.add("(｡◕∀◕｡)");
        mdata.add("/( ◕‿‿◕ )\\");
        mdata.add("ヾ(´ε`ヾ)");
        mdata.add("(ノﾟ∀ﾟ)ノ");
        mdata.add("(σﾟдﾟ)σ");
        mdata.add("(σﾟ∀ﾟ)σ");
        mdata.add("|дﾟ )");
        mdata.add("┃電柱┃");
        mdata.add("ﾟ(つд`ﾟ)");
        mdata.add("ﾟÅﾟ )　");
        mdata.add("⊂彡☆))д`)");
        mdata.add("⊂彡☆))д´)");
        mdata.add("⊂彡☆))∀`)");
        mdata.add("(´∀((☆ミつ");

        return mdata;
    }
}
