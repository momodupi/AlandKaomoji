package momodupi.alandkaomoji;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by 40910 on 2017/5/15.
 */


public class RealService extends Service {

    LinearLayout prstlayout, wmlayout, setttinglayout, notelayout, kaolayout, hidelayout;

    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    DisplayMetrics metric;

    Button movebutton, closebutton, notebackBtn, noteclearBtn, notemoveBtn, setbackBtn, setmoveBtn, kaobackBtn, kaomoveBtn, hidemoveBtn;
    Button touchBtn = null;

    TextView transtextView, lefttextView, brifetextView;
    SeekBar transseekBar;
    Switch leftswitch;
    //ImageView noteimageView;
    EditText noteeditText;
    String notedata;

    ArrayAdapter<String> arrayAdapter, kaoarrayAdapter;
    List<String> kaodata;
    ListView listView, kaolistView;

    //private Bitmap notebmp, notebmpbak;
    //private Canvas notecanvas;
    //private Paint notepaint;

    ClipboardManager clipbrd;
    ClipData clipData;

    private boolean moveflag = false, hideflag = false, leftflag = false;

    public final static int WM_MENU = 0;
    public final static int ST_MENU = 1;
    public final static int HD_MENU = 2;
    public final static int KM_MENU = 3;
    public final static int NT_MENU = 4;

    private int orirawx = 0, orirawy = 0, notex = 0, notey = 0;

    private String butstr;

    private int curPower = 0;
    float transsetting = 1;

    private static String[] btykao_expd = {"(|||ﾟдﾟ)", "( ´_ゝ`)", "(　ﾟ 3ﾟ)", "( ﾟ∀ﾟ)", "( ﾟ∀ﾟ)"};
    private static String[] btykao_hide = {"|дﾟ )", "|д` )", "|-` )", "|∀` )", "|∀` )"};

    @Override
    public void onCreate()
    {
        super.onCreate();

        readpref();

        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager)this.getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //wmParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //wmParams.windowAnimations = R.style.animation;

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
        clipbrd = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        //wm layout
        wmlayout = (LinearLayout) inflater.inflate(R.layout.service_wm, null);
        mWindowManager.addView(wmlayout, wmParams);
        prstlayout = wmlayout;

        listView = (ListView) wmlayout.findViewById(R.id.listView);
        movebutton = (Button) wmlayout.findViewById(R.id.moveBtn);
        closebutton = (Button) wmlayout.findViewById(R.id.closeBtn);
        touchBtn = movebutton;


        arrayAdapter = new ArrayAdapter<String>(this, R.layout.service_item, getmenulist());
        listView.setAdapter(arrayAdapter);

        wmlayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        movebutton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                wmguesture(event, WM_MENU);
                return false;
            }
        });

        closebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savepref();
                stopSelf();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        setkaomojiinterface();
                    }
                    break;
                    case 1: {
                        setnoteinterface();
                    }
                    break;
                    case 2: {
                        setsettinginterface();
                    }
                    break;
                    default: {
                        ;
                    }
                }
            }
        });

        //setting layout
        setttinglayout = (LinearLayout) inflater.inflate(R.layout.service_setting, null);
        setbackBtn = (Button) setttinglayout.findViewById(R.id.setbackBtn);
        setmoveBtn = (Button) setttinglayout.findViewById(R.id.setmoveBtn);
        setmoveBtn.setText(butstr);

        transtextView = (TextView) setttinglayout.findViewById(R.id.transtextView);
        transseekBar = (SeekBar) setttinglayout.findViewById(R.id.transseekBar);
        transseekBar.setMax(100);
        transseekBar.setProgress(50);

        lefttextView = (TextView) setttinglayout.findViewById(R.id.lefttextView);
        leftswitch = (Switch) setttinglayout.findViewById(R.id.leftswitch);
        brifetextView = (TextView) setttinglayout.findViewById(R.id.brifetextView);

        transseekBar.setProgress((int) transsetting);
        leftswitch.setChecked(leftflag);

        setmoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmguesture(event, ST_MENU);
                return false;
            }
        });

        setbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setnormalinterface();
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

        //note layout
        notelayout = (LinearLayout) inflater.inflate(R.layout.service_note, null);
        notebackBtn = (Button) notelayout.findViewById(R.id.notebackBtn);
        noteclearBtn = (Button) notelayout.findViewById(R.id.noteclearBtn);
        notemoveBtn = (Button) notelayout.findViewById(R.id.notemoveBtn);
        notemoveBtn.setText(butstr);
        //noteimageView = (ImageView) notelayout.findViewById(R.id.noteimageView);
        noteeditText = (EditText) notelayout.findViewById(R.id.noteeditText);
        noteeditText.setText(notedata);

        //setnotebmp();
        notemoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                wmguesture(event, NT_MENU);
                return false;
            }
        });

        noteclearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setnotebmp();
                notedata = "";
                noteeditText.setText(notedata);
                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowManager.updateViewLayout(notelayout, wmParams);
            }
        });

        notebackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notedata = noteeditText.getText().toString();
                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                setnormalinterface();
            }
        });

        noteeditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                mWindowManager.updateViewLayout(notelayout, wmParams);
            }
        });
/*
        noteimageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                notepaint(event);
                return true;
            }
        });*/

        //hide layout
        hidelayout = (LinearLayout) inflater.inflate(R.layout.service_hide, null);
        hidemoveBtn = (Button) hidelayout.findViewById(R.id.hidemoveBtn);

        hidemoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmguesture(event, HD_MENU);
                return false;
            }
        });

        //kao layout
        kaolayout = (LinearLayout) inflater.inflate(R.layout.service_kao, null);
        kaolistView = (ListView) kaolayout.findViewById(R.id.kaolistView);
        kaoarrayAdapter = new ArrayAdapter<String>(this, R.layout.service_item, kaodata);
        kaolistView.setAdapter(kaoarrayAdapter);
        kaobackBtn = (Button) kaolayout.findViewById(R.id.kaobackBtn);
        kaomoveBtn = (Button) kaolayout.findViewById(R.id.kaomoveBtn);
        kaomoveBtn.setText(butstr);

        kaomoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmguesture(event, KM_MENU);
                return false;
            }
        });

        kaobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setnormalinterface();
            }
        });

        kaolistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = kaoarrayAdapter.getItem((int) id).toString();

                clipData = ClipData.newPlainText("kao", str);
                clipbrd.setPrimaryClip(clipData);

                sethideinterface();

                Collections.reverse(kaodata);
                kaodata.remove(str);
                kaodata.add(str);
                Collections.reverse(kaodata);
                kaoarrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.service_item, kaodata);

                kaolistView.setAdapter(kaoarrayAdapter);
            }
        });

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                    int level = intent.getIntExtra("level", 0);
                    int scale = intent.getIntExtra("scale", 100);
                    curPower = (level * 100 / scale) / 25;
                    if (hideflag == false) {
                        touchBtn.setText(btykao_expd[curPower]);
                    }
                    else {
                        touchBtn.setText(btykao_hide[curPower]);
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
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

    private void wmguesture(MotionEvent event, int itfc) {
        int action = event.getAction();

        switch (itfc) {
            case WM_MENU: {
                touchBtn = movebutton;
            }
            break;
            case HD_MENU: {
                touchBtn = hidemoveBtn;
            }
            break;
            case KM_MENU: {
                touchBtn = kaomoveBtn;
            }
            break;
            case ST_MENU: {
                touchBtn = setmoveBtn;
            }
            case NT_MENU: {
                touchBtn = notemoveBtn;
            }
            default: {
                ;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                orirawx = (int) event.getRawX();
                orirawy = (int) event.getRawY();

                butstr = (String) touchBtn.getText();
                touchBtn.setText("( ﾟ∀。)");
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                wmParams.x += (int) event.getRawX() - orirawx;
                wmParams.y += (int) event.getRawY() - orirawy;

                if (itfc != HD_MENU) {
                    mWindowManager.updateViewLayout(prstlayout, wmParams);
                }
                else  {
                    mWindowManager.updateViewLayout(hidelayout, wmParams);
                }

                orirawx = (int) event.getRawX();
                orirawy = (int) event.getRawY();

                moveflag = true;
            }
            break;
            case MotionEvent.ACTION_UP: {
                touchBtn.setText(butstr);

                if (moveflag == false) {
                    if (itfc == HD_MENU) {
                        setoriinterface();
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
    }
/*
    private void notepaint(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                notex = (int) event.getX();
                notey = (int) event.getY();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                int px = (int) event.getX();
                int py = (int) event.getY();
                notecanvas.drawLine(notex, notey, px, py, notepaint);
                notex = px;
                notey = py;
                noteimageView.setImageBitmap(notebmp);
            }
            break;
            case MotionEvent.ACTION_UP: {

            }
            break;
            default: {
                ;
            }
        }
    }
*/
    public void setlocation() {
        if (leftflag == true) {
            wmParams.x = 0;
        }
        else {
            wmParams.x = metric.widthPixels - wmParams.width;
        }
    }

    public void readpref() {
        SharedPreferences preferences = getSharedPreferences("kaomojipref", MODE_PRIVATE);
        transsetting = preferences.getFloat("transsetting", 50);
        leftflag = preferences.getBoolean("leftsetting", false);

        kaodata = new ArrayList<String>();
        int kaonum = preferences.getInt("kaonum", 87);
        for (int cnt = 0; cnt < kaonum; cnt++) {
            kaodata.add(preferences.getString("kao" + String.valueOf(cnt), null));
        }
        notedata = preferences.getString("note", null);
    }

    public void savepref() {
        SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();

        int cnt;
        for (cnt = 0; cnt < kaodata.size(); cnt++) {
            editor.putString("kao" + String.valueOf(cnt), kaodata.get(cnt).toString());
        }
        editor.putString("note", notedata.toString());
        editor.putInt("kaonum", cnt);
        editor.putFloat("transsetting", transsetting);
        editor.putBoolean("leftsetting", leftflag);
        editor.commit();
    }
/*
    public void setnotebmp() {
        notepaint = new Paint();
        notepaint.setStrokeWidth(3);
        notepaint.setColor(Color.parseColor("#eeeeee"));
        notebmp = Bitmap.createBitmap(dip2px(getApplicationContext(), 200), dip2px(getApplicationContext(), 264), Bitmap.Config.ARGB_8888);
        notecanvas = new Canvas(notebmp);
        notecanvas.drawColor(Color.parseColor("#414141"));
        noteimageView.setImageBitmap(notebmp);
    }
*/
    private void setnormalinterface() {
        wmParams.width = dip2px(getApplicationContext(), 108);
        wmParams.height = dip2px(getApplicationContext(), 276);
        setlocation();
        mWindowManager.addView(wmlayout, wmParams);
        mWindowManager.updateViewLayout(wmlayout, wmParams);
        mWindowManager.removeView(prstlayout);
        prstlayout = wmlayout;

        movebutton.setText(btykao_expd[curPower]);
    }

    private void sethideinterface() {
        hidelayout.setAlpha(transsetting / 100);
        wmParams.width = dip2px(getApplicationContext(), 60);
        wmParams.height = dip2px(getApplicationContext(), 36);
        hideflag = true;
        setlocation();
        mWindowManager.addView(hidelayout, wmParams);
        mWindowManager.updateViewLayout(hidelayout, wmParams);
        mWindowManager.removeView(prstlayout);

        hidemoveBtn.setText(btykao_hide[curPower]);
    }

    public void setoriinterface() {
        wmParams.width = prstlayout.getWidth();
        wmParams.height = prstlayout.getHeight();
        hideflag = false;
        setlocation();
        mWindowManager.addView(prstlayout, wmParams);
        mWindowManager.updateViewLayout(prstlayout, wmParams);
        mWindowManager.removeView(hidelayout);
    }

    private void setsettinginterface() {
        wmParams.width = dip2px(getApplicationContext(), 108);
        wmParams.height = dip2px(getApplicationContext(), 276);
        setlocation();
        mWindowManager.addView(setttinglayout, wmParams);
        mWindowManager.updateViewLayout(setttinglayout, wmParams);
        mWindowManager.removeView(prstlayout);
        prstlayout = setttinglayout;
        setmoveBtn.setText(btykao_expd[curPower]);
    }

    private void setnoteinterface() {
        //wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wmParams.width = dip2px(getApplicationContext(), 200);
        wmParams.height = dip2px(getApplicationContext(), 300);
        setlocation();
        mWindowManager.addView(notelayout, wmParams);
        mWindowManager.updateViewLayout(notelayout, wmParams);
        mWindowManager.removeView(prstlayout);
        prstlayout = notelayout;
        notemoveBtn.setText(btykao_expd[curPower]);
        noteeditText.setText(notedata);
    }

    private void setkaomojiinterface() {
        //wmParams.width = dip2px(getApplicationContext(), 108);
        //wmParams.height = dip2px(getApplicationContext(), 276);
        setlocation();
        mWindowManager.addView(kaolayout, wmParams);
        mWindowManager.updateViewLayout(kaolayout, wmParams);
        mWindowManager.removeView(prstlayout);
        prstlayout = kaolayout;
        kaomoveBtn.setText(btykao_expd[curPower]);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private List<String> getmenulist() {

        List<String> mdata = new ArrayList<String>();
        mdata.add("颜文字");
        mdata.add("便签");
        mdata.add("设置");

        return mdata;
    }
}
