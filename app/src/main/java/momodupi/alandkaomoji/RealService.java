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
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    DisplayMetrics metric;

    Button movebutton, closebutton;
    TextView transtextView, lefttextView, brifetextView;
    SeekBar transseekBar;
    Switch leftswitch;

    ArrayAdapter<String> arrayAdapter;
    List<String> data;
    ListView listView;

    ClipboardManager clipbrd;
    ClipData clipData;

    private boolean moveflag = false, hideflag = false, dragflag = false, scrollheadflag = false, leftflag = false;
    private int menusts = 0;
    public final static int WM_MWNU = 0;
    public final static int ST_MENU = 1;
    public final static int HD_MENU = 2;

    private int orirawx = 0, orirawy = 0;
    private int listdrag = 0;

    private String butstr;

    private int curPower = 0;
    float transsetting = 1;

    private static String[] btykao_expd = {"(|||ﾟдﾟ)", "( ´_ゝ`)", "(　ﾟ 3ﾟ)", "( ﾟ∀ﾟ)", "( ﾟ∀ﾟ)"};
    private static String[] btykao_hide = {"|дﾟ )", "|д` )", "|-` )", "|∀` )", "|∀` )"};

    @Override
    public void onCreate()
    {
        super.onCreate();

        SharedPreferences preferences = getSharedPreferences("kaomojipref", MODE_PRIVATE);
        transsetting = preferences.getFloat("transsetting", 50);
        leftflag = preferences.getBoolean("leftsetting", false);

        data = new ArrayList<String>();
        int kaonum = preferences.getInt("kaonum", 87);
        for (int cnt = 0; cnt < kaonum; cnt++) {
            data.add(preferences.getString("kao" + String.valueOf(cnt), null));
        }

        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        metric = new DisplayMetrics();

        wmParams.width = dip2px(getApplicationContext(), 96);
        wmParams.height = dip2px(getApplicationContext(), 270);
        mWindowManager.getDefaultDisplay().getMetrics(metric);

        if (leftflag == true) {
            wmParams.x = 0;
        }
        else {
            wmParams.x = metric.widthPixels - wmParams.width;
        }

        wmParams.y = metric.widthPixels / 2;
        orirawx = wmParams.x;
        orirawy = wmParams.y;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        wmlayout = (LinearLayout) inflater.inflate(R.layout.service_wm, null);

        mWindowManager.addView(wmlayout, wmParams);

        clipbrd = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        listView = (ListView) wmlayout.findViewById(R.id.listView);
        movebutton = (Button) wmlayout.findViewById(R.id.moveBtn);

        closebutton = (Button) wmlayout.findViewById(R.id.closeBtn);

        transtextView = (TextView) wmlayout.findViewById(R.id.transtextView);
        transseekBar = (SeekBar) wmlayout.findViewById(R.id.transseekBar);
        transseekBar.setMax(100);
        transseekBar.setProgress(50);

        lefttextView = (TextView) wmlayout.findViewById(R.id.lefttextView);
        leftswitch = (Switch) wmlayout.findViewById(R.id.leftswitch);

        brifetextView = (TextView) wmlayout.findViewById(R.id.brifetextView);

        transseekBar.setProgress((int) transsetting);
        leftswitch.setChecked(leftflag);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.service_item, data);
        listView.setAdapter(arrayAdapter);

        wmlayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                    int level = intent.getIntExtra("level", 0);
                    int scale = intent.getIntExtra("scale", 100);
                    curPower = (level * 100 / scale) / 25;
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

                        if (moveflag == false) {
                            //wmParams.x = orirawx - (int) event.getX();
                            //wmParams.y = orirawy - (int) event.getY();
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

                        moveflag = true;
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        movebutton.setText(butstr);

                        if (moveflag == false) {
                            if(menusts == HD_MENU) {
                                setnormalinterface();
                            }
                            else {
                                sethideinterface();
                            }
                        }
                        moveflag = false;
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
                SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();

                int cnt;
                for (cnt = 0; cnt < data.size(); cnt++) {
                    editor.putString("kao" + String.valueOf(cnt), data.get(cnt).toString());
                }
                editor.putInt("kaonum", cnt);
                editor.putFloat("transsetting", transsetting);
                editor.putBoolean("leftsetting", leftflag);
                editor.commit();

                stopSelf();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = arrayAdapter.getItem((int) id).toString();

                clipData = ClipData.newPlainText("kao", str);
                clipbrd.setPrimaryClip(clipData);
/**/
                sethideinterface();
                moveflag = false;

                Collections.reverse(data);
                data.remove(str);
                data.add(str);
                Collections.reverse(data);
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.service_item, data);

                listView.setAdapter(arrayAdapter);
            }
        });
/**/
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        listdrag = (int) event.getRawY();
                        dragflag = false;
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                        if ((event.getRawY() - listdrag) > 0) {
                            dragflag = true;
                        }
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        if ((dragflag == true) && (scrollheadflag == true)) {
                            setsettinginterface();
                        }
                        dragflag = false;
                    }
                    break;
                    default: {
                        ;
                    }
                }
                return false;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    scrollheadflag = true;
                }
                else {
                    scrollheadflag = false;
                }
            }
        });

        transseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                transsetting = (float) progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        leftswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    leftflag = true;
                }
                else {
                    leftflag = false;
                }
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
        if(wmlayout != null)
        {
            mWindowManager.removeView(wmlayout);
        }
        super.onDestroy();
    }

    private void setnormalinterface() {
        wmParams.width = dip2px(getApplicationContext(), 96);
        wmParams.height = dip2px(getApplicationContext(), 270);

        if (leftflag == true) {
            wmParams.x = 0;
        }
        else {
            if (wmParams.x > (metric.widthPixels - wmParams.width)) {
                wmParams.x = metric.widthPixels - wmParams.width;
            }
        }

        wmlayout.setAlpha(1);
        listView.setSelection(0);
        listView.setVisibility(View.VISIBLE);
        transtextView.setVisibility(View.GONE);
        transseekBar.setVisibility(View.GONE);
        lefttextView.setVisibility(View.GONE);
        leftswitch.setVisibility(View.GONE);
        brifetextView.setVisibility(View.GONE);

        mWindowManager.updateViewLayout(wmlayout, wmParams);

        movebutton.setText(btykao_expd[curPower]);
        menusts = WM_MWNU;
    }

    private void sethideinterface() {
        wmlayout.setAlpha(transsetting / 100);
        wmParams.width = dip2px(getApplicationContext(), 60);
        wmParams.height = dip2px(getApplicationContext(), 36);

        if (leftflag == true) {
            wmParams.x = 0;
        }
        else {
            wmParams.x = metric.widthPixels - wmParams.width;
        }

        listView.setVisibility(View.GONE);
        transtextView.setVisibility(View.GONE);
        transseekBar.setVisibility(View.GONE);
        lefttextView.setVisibility(View.GONE);
        leftswitch.setVisibility(View.GONE);
        brifetextView.setVisibility(View.GONE);

        mWindowManager.updateViewLayout(wmlayout, wmParams);

        movebutton.setText(btykao_hide[curPower]);
        menusts = HD_MENU;
    }

    private void setsettinginterface() {
        wmParams.width = dip2px(getApplicationContext(), 96);
        wmParams.height = dip2px(getApplicationContext(), 270);
        wmlayout.setAlpha(1);

        listView.setVisibility(View.GONE);
        transtextView.setVisibility(View.VISIBLE);
        transseekBar.setVisibility(View.VISIBLE);
        lefttextView.setVisibility(View.VISIBLE);
        leftswitch.setVisibility(View.VISIBLE);
        brifetextView.setVisibility(View.VISIBLE);

        mWindowManager.updateViewLayout(wmlayout, wmParams);

        movebutton.setText(btykao_expd[curPower]);
        menusts = ST_MENU;
    }


    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int px2dip(Context context, float pxValue) {
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
