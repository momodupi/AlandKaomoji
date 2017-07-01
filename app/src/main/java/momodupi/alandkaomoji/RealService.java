package momodupi.alandkaomoji;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.IBinder;
//import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
//import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.io.DataOutputStream;
//import java.io.File;
//import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by momodupi on 2017/5/15.
 */


public class RealService extends Service {

    LinearLayout prstlayout, wmlayout, setttinglayout, notelayout, kaolayout, callayout, paylayout, hidelayout;

    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    DisplayMetrics metric;

    Button kaocloseBtn, kaomoveBtn, menumoveBtn, menubackBtn, notebackBtn, noteclearBtn, notemoveBtn, setbackBtn, setmoveBtn, calbackBtn, calmoveBtn, paymoveBtn, paybackBtn, hidemoveBtn;
    Button touchBtn = null;

    TextView caltextView, transtextView, brifetextView;
    SeekBar transseekBar;
    Switch leftswitch, rootswitch;
    //ImageView noteimageView;
    EditText noteeditText;
    String notedata;

    ArrayAdapter<String> arrayAdapter, kaoarrayAdapter, calarrayAdapter, payarrayAdapter;
    List<String> kaodata, caldata;
    ListView menulistView, kaolistView, paylistView;
    GridView calgridView;

    //SensorManager sensorManager;
    OrientationEventListener orientationEventListener;

    //private Bitmap notebmp, notebmpbak;
    //private Canvas notecanvas;
    //private Paint notepaint;
    CountDownTimer countDownTimer;

    ClipboardManager clipbrd;
    ClipData clipData;

    private boolean moveableflag = false, moveflag = false, hideflag = false, leftflag = false, rootflag = false, dragflag = false, scrollheadflag = false;
    private boolean fstnumflag = true, screenlandscape = false, wehcatinstalled = false, alipayinstalled = false;
    private int sideflag = 0;
    public final static int NON_SDIE = 0;
    public final static int LEFT_SDIE = 1;
    public final static int RIGHT_SDIE = 2;

    public final static int WM_MENU = 0;
    public final static int ST_MENU = 1;
    public final static int HD_MENU = 2;
    public final static int KM_MENU = 3;
    public final static int NT_MENU = 4;
    public final static int CL_MENU = 5;
    public final static int PY_MENU = 6;

    public final static int ADD_CL = 1;
    public final static int MIN_CL = 2;
    public final static int MUL_CL = 3;
    public final static int DEV_CL = 4;
    public final static int POW_CL = 5;
    private int calsymbol = 0;

    private int orirawx = 0, orirawy = 0, listdrag = 0;
    //private int notex = 0, notey = 0;
    private int curPower = 0;
    private float transsetting = 1;
    private double fstnum = 0, sndnum = 0;
    private String savenumstr = "", getnumstr = "", butstr;

    private static String[] menutext_r = {"颜文字", "便签", "计算器", "收付款", "设置"};
    private static String[] menutext_nr = {"颜文字", "便签", "计算器", "设置"};
    private static String[] paymenutext = {"微信付款", "微信收款", "微信扫码", "支付宝付款", "支付宝收款", "支付宝扫码"};

    private static String[] calbuttontext = {
            "1/x", "√", "x^y", "<",
            "C", "M", "M+", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "±", "0", ".", "="
    };

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
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.windowAnimations = android.R.style.Animation_Dialog;

        metric = new DisplayMetrics();

        wmParams.width = dip2px(getApplicationContext(), 96);
        wmParams.height = dip2px(getApplicationContext(), 270);
        mWindowManager.getDefaultDisplay().getMetrics(metric);

        if (leftflag) {
            wmParams.x = 0;
        }
        else {
            wmParams.x = metric.widthPixels - wmParams.width;
        }

        wmParams.y = metric.widthPixels / 2;
        orirawx = wmParams.x;
        orirawy = wmParams.y;

        //wmlayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        clipbrd = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        /*
        sensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                screenlandscape=  ((orientation >= 0 && orientation <= 45) || (orientation >= 315 && orientation <= 360)) ? true : false;
                Log.i("MyOrientationDetector ","onOrientationChanged:" + screenlandscape);
            }
        };
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        }
        else {
            orientationEventListener.disable();
        }*/

        //kao layout
        kaolayout = (LinearLayout) inflater.inflate(R.layout.service_kao, kaolayout, true);
        kaolistView = (ListView) kaolayout.findViewById(R.id.kaolistView);
        kaoarrayAdapter = new ArrayAdapter<>(this, R.layout.service_item, kaodata);
        kaolistView.setAdapter(kaoarrayAdapter);
        kaocloseBtn = (Button) kaolayout.findViewById(R.id.kaocloseBtn);
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

        kaocloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savepref();
                stopSelf();
            }
        });

        kaolistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = kaoarrayAdapter.getItem((int) id);

                clipData = ClipData.newPlainText("kao", str);
                clipbrd.setPrimaryClip(clipData);

                sethideinterface();

                Collections.reverse(kaodata);
                kaodata.remove(str);
                kaodata.add(str);
                Collections.reverse(kaodata);
                kaoarrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.service_item, kaodata);

                kaolistView.setAdapter(kaoarrayAdapter);
            }
        });

        kaolistView.setOnTouchListener(new View.OnTouchListener() {
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
                        if (dragflag && scrollheadflag) {
                            setnormalinterface();
                        }
                        dragflag = false;
                    }
                    break;
                    default: {
                    }
                }
                return false;
            }
        });

        kaolistView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                scrollheadflag = (firstVisibleItem == 0);
            }
        });

        //wm layout
        wmlayout = (LinearLayout) inflater.inflate(R.layout.service_wm, wmlayout, true);
        menulistView = (ListView) wmlayout.findViewById(R.id.menulistView);
        menumoveBtn = (Button) wmlayout.findViewById(R.id.menumoveBtn);
        menubackBtn = (Button) wmlayout.findViewById(R.id.menubackBtn);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.service_item, getmenulist(rootflag));
        menulistView.setAdapter(arrayAdapter);

        menumoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                wmguesture(event, WM_MENU);
                return false;
            }
        });

        menubackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setkaomojiinterface();
            }
        });

        menulistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        setcalinterface();
                    }
                    break;
                    case 3: {
                        if (rootflag) {
                            setpayinterface();
                        }
                        else {
                            setsettinginterface();
                        }
                    }
                    break;
                    case 4: {
                        if (rootflag) {
                            setsettinginterface();
                        }
                    }
                    break;
                    default: {
                    }
                }
            }
        });

        //setting layout
        setttinglayout = (LinearLayout) inflater.inflate(R.layout.service_setting, setttinglayout, true);
        setbackBtn = (Button) setttinglayout.findViewById(R.id.setbackBtn);
        setmoveBtn = (Button) setttinglayout.findViewById(R.id.setmoveBtn);
        setmoveBtn.setText(butstr);

        transtextView = (TextView) setttinglayout.findViewById(R.id.transtextView);
        transseekBar = (SeekBar) setttinglayout.findViewById(R.id.transseekBar);
        transseekBar.setMax(100);
        transseekBar.setProgress(50);

        leftswitch = (Switch) setttinglayout.findViewById(R.id.leftswitch);
        rootswitch = (Switch) setttinglayout.findViewById(R.id.rootswitch);
        brifetextView = (TextView) setttinglayout.findViewById(R.id.brifetextView);

        transseekBar.setProgress((int) transsetting);
        leftswitch.setChecked(leftflag);
        rootswitch.setChecked(rootflag);

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
                leftflag = isChecked;
            }
        });

        rootswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && checkroot()) {
                    Toast.makeText(getApplicationContext(), "获☆取☆root☆大☆成☆功☆", Toast.LENGTH_SHORT).show();
                    rootflag = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "获☆取☆root☆大☆失☆败☆", Toast.LENGTH_SHORT).show();
                    rootswitch.setChecked(false);
                    rootflag = false;
                }
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.service_item, getmenulist(rootflag));
                menulistView.setAdapter(arrayAdapter);
            }
        });

        //note layout
        notelayout = (LinearLayout) inflater.inflate(R.layout.service_note, notelayout, true);
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
        hidelayout = (LinearLayout) inflater.inflate(R.layout.service_hide, hidelayout, true);
        hidemoveBtn = (Button) hidelayout.findViewById(R.id.hidemoveBtn);

        hidemoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmguesture(event, HD_MENU);
                return false;
            }
        });

        //calculator layout
        callayout = (LinearLayout) inflater.inflate(R.layout.service_calculator, callayout, true);
        calbackBtn = (Button) callayout.findViewById(R.id.calbackBtn);
        calmoveBtn = (Button) callayout.findViewById(R.id.calmoveBtn);
        calmoveBtn.setText(butstr);
        caltextView = (TextView) callayout.findViewById(R.id.caltextView);
        caltextView.setText("");
        calgridView = (GridView) callayout.findViewById(R.id.calgridView);
        caldata = getcalgrid();
        calarrayAdapter = new ArrayAdapter<>(this, R.layout.service_calitem, caldata);
        calgridView.setAdapter(calarrayAdapter);

        calmoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmguesture(event, CL_MENU);
                return false;
            }
        });

        calbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setnormalinterface();
            }
        });

        calgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        //1/x
                        getnumstr = caltextView.getText().toString();
                        fstnum = Double.valueOf(getnumstr);
                        if (fstnum == 0) {
                            getnumstr = "";
                            caltextView.setText(R.string.error);
                            fstnumflag = true;
                            fstnum = 0;
                            sndnum = 0;
                            calsymbol = 0;
                        }
                        else {
                            fstnum = Math.pow(fstnum, -1);
                            getnumstr = String.valueOf(fstnum);
                            caltextView.setText(getnumstr);
                        }
                    }
                    break;
                    case 1: {
                        //sqrt
                        getnumstr = caltextView.getText().toString();
                        fstnum = Double.valueOf(getnumstr);
                        if (fstnum < 0) {
                            getnumstr = "";
                            caltextView.setText(R.string.error);
                            fstnumflag = true;
                            fstnum = 0;
                            sndnum = 0;
                            calsymbol = 0;
                        }
                        else {
                            fstnum = Math.sqrt(fstnum);
                            getnumstr = String.valueOf(fstnum);
                            caltextView.setText(getnumstr);
                        }
                    }
                    break;
                    case 2: {
                        //x^y
                        calculator(POW_CL);
                    }
                    break;
                    case 3: {
                        //delete
                        getnumstr = caltextView.getText().toString();
                        if (getnumstr.length() != 0) {
                            getnumstr = getnumstr.substring(0, getnumstr.length() - 1);
                        }
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 4: {
                        //C
                        getnumstr = "";
                        caltextView.setText(getnumstr);
                        fstnumflag = true;
                        fstnum = 0;
                        sndnum = 0;
                        calsymbol = 0;
                    }
                    break;
                    case 5: {
                        //M
                        caltextView.setText(savenumstr);
                    }
                    break;
                    case 6: {
                        //M+
                        savenumstr = caltextView.getText().toString();
                    }
                    break;
                    case 7: {
                        ///
                        calculator(DEV_CL);
                    }
                    break;
                    case 8: {
                        //7
                        getnumstr += "7";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 9: {
                        //8
                        getnumstr += "8";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 10: {
                        //9
                        getnumstr += "9";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 11: {
                        //*
                        calculator(MUL_CL);
                    }
                    break;
                    case 12: {
                        //4
                        getnumstr += "4";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 13: {
                        //5
                        getnumstr += "5";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 14: {
                        //6
                        getnumstr += "6";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 15: {
                        //-
                        calculator(MIN_CL);
                    }
                    break;
                    case 16: {
                        //1
                        getnumstr += "1";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 17: {
                        //2
                        getnumstr += "2";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 18: {
                        //3
                        getnumstr += "3";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 19: {
                        //+
                        calculator(ADD_CL);
                    }
                    break;
                    case 20: {
                        //nega/posi
                        getnumstr = caltextView.getText().toString();
                        fstnum = Double.valueOf(getnumstr);
                        fstnum *= -1;
                        getnumstr = String.valueOf(fstnum);
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 21: {
                        //0
                        getnumstr += "0";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 22: {
                        //.
                        getnumstr += ".";
                        caltextView.setText(getnumstr);
                    }
                    break;
                    case 23: {
                        //=
                        calculator(calsymbol);
                        calsymbol = 0;
                        fstnumflag = true;
                    }
                    break;
                    default: {
                    }
                }
            }
        });

        //pay layout
        paylayout = (LinearLayout) inflater.inflate(R.layout.service_pay, paylayout, true);
        paylistView = (ListView) paylayout.findViewById(R.id.paylistView);
        payarrayAdapter = new ArrayAdapter<>(this, R.layout.service_item, getpaylist());
        paylistView.setAdapter(payarrayAdapter);
        paybackBtn = (Button) paylayout.findViewById(R.id.paybackBtn);
        paymoveBtn = (Button) paylayout.findViewById(R.id.paymoveBtn);
        paymoveBtn.setText(butstr);

        paymoveBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                wmguesture(event, PY_MENU);
                return false;
            }
        });

        paybackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setnormalinterface();
            }
        });

        paylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(wehcatinstalled || alipayinstalled)) {
                    position = 999;
                }
                else if (alipayinstalled && (!wehcatinstalled)) {
                    position += 3;
                }

                switch (position) {
                    case 0: {
                        //wechat pay
                        sucmd("am start -n com.tencent.mm/com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI");
                    }
                    break;
                    case 1: {
                        //wechat receive
                        sucmd("am start -n com.tencent.mm/com.tencent.mm.plugin.collect.ui.CollectMainUI");
                    }
                    break;
                    case 2: {
                        //wechat scan
                        sucmd("am start -n com.tencent.mm/com.tencent.mm.plugin.scanner.ui.BaseScanUI");
                    }
                    break;
                    case 3: {
                        //alipay pay
                        sucmd("am start -n com.eg.android.AlipayGphone/com.alipay.mobile.onsitepay9.payer.OspTabHostActivity");
                    }
                    break;
                    case 4: {
                        //alipay receive
                        sucmd("am start -n com.eg.android.AlipayGphone/com.alipay.mobile.payee.ui.PayeeQRActivity_");
                    }
                    break;
                    case 5: {
                        //alipay scan
                        sucmd("am start -n com.eg.android.AlipayGphone/com.alipay.mobile.scan.as.main.MainCaptureActivity");
                    }
                    break;
                    default: {
                        Toast.makeText(getApplicationContext(), "没有就别点啦！", Toast.LENGTH_SHORT).show();
                    }
                }
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
                    if (!hideflag) {
                        touchBtn.setText(btykao_expd[curPower]);
                    }
                    else {
                        touchBtn.setText(btykao_hide[curPower]);
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        mWindowManager.addView(kaolayout, wmParams);
        prstlayout = kaolayout;
        touchBtn = kaomoveBtn;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(wmlayout != null) {
            mWindowManager.removeView(kaolayout);
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration oriConfig) {
        super.onConfigurationChanged(oriConfig);
        if (oriConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!screenlandscape) {
                screenlandscape = true;
                metric = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(metric);
            }
        }
        else if (oriConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (screenlandscape) {
                screenlandscape = false;
                metric = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(metric);
            }
        }
    }

    private void wmguesture(MotionEvent event, int itfc) {
        int action = event.getAction();

        switch (itfc) {
            case WM_MENU: {
                touchBtn = menumoveBtn;
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
            break;
            case CL_MENU: {
                touchBtn = calmoveBtn;
            }
            break;
            case PY_MENU: {
                touchBtn = paymoveBtn;
            }
            break;
            default: {
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

                if (wmParams.x > (metric.widthPixels - wmParams.width)) {
                    wmParams.x = metric.widthPixels - wmParams.width;
                    sideflag = RIGHT_SDIE;
                }
                else if (wmParams.x < 0) {
                    wmParams.x  = 0;
                    sideflag = LEFT_SDIE;
                }
                else {
                    sideflag = NON_SDIE;
                }

                if (wmParams.y > (metric.heightPixels - wmParams.height)) {
                    wmParams.y = metric.heightPixels - wmParams.height;
                }
                else if (wmParams.y < 0) {
                    wmParams.y  = 0;
                }

                if (itfc != HD_MENU) {
                    mWindowManager.updateViewLayout(prstlayout, wmParams);
                }
                else  {
                    mWindowManager.updateViewLayout(hidelayout, wmParams);
                }

                if (((int) event.getRawX() - orirawx > 2)
                        || ((int) event.getRawY() - orirawy < -2)
                        || ((int) event.getRawY() - orirawy > 2)
                        || ((int) event.getRawY() - orirawy < -2)) {
                    moveflag = true;
                }

                orirawx = (int) event.getRawX();
                orirawy = (int) event.getRawY();
            }
            break;
            case MotionEvent.ACTION_UP: {
                touchBtn.setText(butstr);

                if (!moveflag) {
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
    private void getsideflag() {
        if (wmParams.x >= (metric.widthPixels - wmParams.width)) {
            wmParams.x = metric.widthPixels - wmParams.width;
            sideflag = RIGHT_SDIE;
        }
        else if (wmParams.x <= 0) {
            wmParams.x  = 0;
            sideflag = LEFT_SDIE;
        }
        else {
            sideflag = NON_SDIE;
        }
    }

    private void setlocation(int w, int h) {
        getsideflag();
        wmParams.width = dip2px(getApplicationContext(), w);
        wmParams.height = dip2px(getApplicationContext(), h);

        if (hideflag) {
            if (leftflag) {
                wmParams.x = 0;
            }
            else {
                wmParams.x = metric.widthPixels - wmParams.width;
            }
        }
        else {
            if (sideflag == LEFT_SDIE) {
                wmParams.x  = 0;
            }
            else if (sideflag == RIGHT_SDIE) {
                wmParams.x = metric.widthPixels - wmParams.width;
            }
            else {
                if (wmParams.x > (metric.widthPixels - wmParams.width)) {
                    wmParams.x = metric.widthPixels - wmParams.width;
                }
                else if (wmParams.x < 0) {
                    wmParams.x  = 0;
                }

                if (wmParams.y > (metric.heightPixels - wmParams.height)) {
                    wmParams.y = metric.heightPixels - wmParams.height;
                }
                else if (wmParams.y < 0) {
                    wmParams.y  = 0;
                }
            }
        }
    }

    private void updatelayout(LinearLayout lt) {
        mWindowManager.addView(lt, wmParams);
        mWindowManager.updateViewLayout(lt, wmParams);
        mWindowManager.removeView(prstlayout);
        prstlayout = lt;
    }

    private void readpref() {
        SharedPreferences preferences = getSharedPreferences("kaomojipref", MODE_PRIVATE);
        transsetting = preferences.getFloat("transsetting", 50);
        leftflag = preferences.getBoolean("leftsetting", false);
        rootflag = preferences.getBoolean("rootsetting", false);

        kaodata = new ArrayList<>();
        int kaonum = preferences.getInt("kaonum", 87);
        for (int cnt = 0; cnt < kaonum; cnt++) {
            kaodata.add(preferences.getString("kao" + String.valueOf(cnt), null));
        }
        notedata = preferences.getString("note", null);
    }

    private void savepref() {
        SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();

        int cnt;
        for (cnt = 0; cnt < kaodata.size(); cnt++) {
            editor.putString("kao" + String.valueOf(cnt), kaodata.get(cnt));
        }
        editor.putString("note", notedata);
        editor.putInt("kaonum", cnt);
        editor.putFloat("transsetting", transsetting);
        editor.putBoolean("leftsetting", leftflag);
        editor.putBoolean("rootsetting", rootflag);
        editor.putBoolean("nonvirgin", true);
        editor.apply();
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

    private void sethideinterface() {
        hidelayout.setAlpha(transsetting / 100);
        hideflag = true;
        setlocation(60, 36);
        mWindowManager.addView(hidelayout, wmParams);
        mWindowManager.updateViewLayout(hidelayout, wmParams);
        mWindowManager.removeView(prstlayout);
        hidemoveBtn.setText(btykao_hide[curPower]);
    }

    private void setoriinterface() {
        getsideflag();
        wmParams.width = prstlayout.getWidth();
        wmParams.height = prstlayout.getHeight();
        hideflag = false;

        if (sideflag == LEFT_SDIE) {
            wmParams.x  = 0;
        }
        else if (sideflag == RIGHT_SDIE) {
            wmParams.x = metric.widthPixels - wmParams.width;
        }
        else {
            if (wmParams.x > (metric.widthPixels - wmParams.width)) {
                wmParams.x = metric.widthPixels - wmParams.width;
            }
            else if (wmParams.x < 0) {
                wmParams.x  = 0;
            }

            if (wmParams.y > (metric.heightPixels - wmParams.height)) {
                wmParams.y = metric.heightPixels - wmParams.height;
            }
            else if (wmParams.y < 0) {
                wmParams.y  = 0;
            }
        }

        mWindowManager.addView(prstlayout, wmParams);
        mWindowManager.updateViewLayout(prstlayout, wmParams);
        mWindowManager.removeView(hidelayout);
        setmoveBtn.setText(btykao_expd[curPower]);
    }

    private void setnormalinterface() {
        setlocation(108, 276);
        updatelayout(wmlayout);
        menumoveBtn.setText(btykao_expd[curPower]);
    }

    private void setsettinginterface() {
        hideflag = false;
        setlocation(108, 276);
        updatelayout(setttinglayout);
        setmoveBtn.setText(btykao_expd[curPower]);
    }

    private void setnoteinterface() {
        hideflag = false;
        setlocation(200, 300);
        updatelayout(notelayout);
        notemoveBtn.setText(btykao_expd[curPower]);
        noteeditText.setText(notedata);
    }

    private void setkaomojiinterface() {
        hideflag = false;
        setlocation(108, 276);
        updatelayout(kaolayout);
        kaomoveBtn.setText(btykao_expd[curPower]);
    }

    private void setcalinterface() {
        hideflag = false;
        setlocation(200, 300);
        updatelayout(callayout);
        calmoveBtn.setText(btykao_expd[curPower]);
    }

    private void setpayinterface() {
        hideflag = false;
        setlocation(108, 276);
        payarrayAdapter = new ArrayAdapter<>(this, R.layout.service_item, getpaylist());
        paylistView.setAdapter(payarrayAdapter);
        updatelayout(paylayout);
        paymoveBtn.setText(btykao_expd[curPower]);
    }

    private void calculator(int cs) {
        getnumstr = caltextView.getText().toString();

        if (fstnumflag) {
            if (getnumstr.equals("")) {
                getnumstr = "0";
            }
            fstnum = Double.valueOf(getnumstr);
            calsymbol = cs;
        }
        else {
            sndnum = Double.valueOf(getnumstr);
            switch (calsymbol) {
                case ADD_CL: {
                    if (getnumstr.equals("")) {
                        getnumstr = "0";
                    }
                    if (fstnumflag) {
                        fstnum = Double.valueOf(getnumstr);
                    }
                    else {
                        sndnum = Double.valueOf(getnumstr);
                        fstnum += sndnum;
                    }
                    getnumstr = String.valueOf(fstnum);
                }
                break;
                case MIN_CL: {
                    if (getnumstr.equals("")) {
                        getnumstr = "0";
                    }
                    if (fstnumflag) {
                        fstnum = Double.valueOf(getnumstr);
                        getnumstr = String.valueOf(fstnum);
                    }
                    else {
                        sndnum = Double.valueOf(getnumstr);
                        fstnum -= sndnum;
                        getnumstr = String.valueOf(fstnum);
                    }
                }
                break;
                case MUL_CL: {
                    if (getnumstr.equals("")) {
                        getnumstr = "0";
                    }

                    if (fstnumflag) {
                        fstnum = Double.valueOf(getnumstr);
                    }
                    else {
                        sndnum = Double.valueOf(getnumstr);
                        fstnum *= sndnum;
                    }
                    getnumstr = String.valueOf(fstnum);
                }
                break;
                case DEV_CL: {
                    if (getnumstr.equals("")) {
                        getnumstr = "0";
                    }

                    if (fstnumflag) {
                        fstnum = Double.valueOf(getnumstr);
                        getnumstr = String.valueOf(fstnum);
                    }
                    else {
                        sndnum = Double.valueOf(getnumstr);
                        if (sndnum == 0) {
                            getnumstr = "error";
                            fstnumflag = true;
                            fstnum = 0;
                            sndnum = 0;
                            calsymbol = 0;
                        }
                        else {
                            fstnum /= sndnum;
                            getnumstr = String.valueOf(fstnum);
                        }
                    }
                }
                break;
                case POW_CL: {
                    if (getnumstr.equals("")) {
                        getnumstr = "0";
                    }

                    if (fstnumflag) {
                        fstnum = Double.valueOf(getnumstr);
                        getnumstr = String.valueOf(fstnum);
                    }
                    else {
                        sndnum = Double.valueOf(getnumstr);
                        if (sndnum == 0) {
                            fstnum = 0;
                        }
                        else if (sndnum > 100) {
                            getnumstr = "out of range";
                            fstnumflag = true;
                            fstnum = 0;
                            sndnum = 0;
                            calsymbol = 0;
                        }
                        else {
                            fstnum = Math.pow(fstnum, sndnum);
                            getnumstr = String.valueOf(fstnum);
                        }
                    }
                }
                break;
                default: {
                }
            }
            calsymbol = cs;
        }
        fstnumflag = false;
        if (getnumstr.indexOf(".") > 0) {
            getnumstr = getnumstr.replaceAll("0+?$", "");
            getnumstr = getnumstr.replaceAll("[.]$", "");
        }
        caltextView.setText(getnumstr);
        getnumstr = "";
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
/*
    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
*/
    private int sucmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            //dataOutputStream.writeBytes("export LD_LIBRARY_PATH=/vendor/lib:/system/lib\n");
            //cmd = String.valueOf(cmd);
            dataOutputStream.writeBytes(cmd + "\n");
            //dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            return process.exitValue();
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
    }

    private boolean checkroot() {
        try {
            return (sucmd("chmod 777" + getPackageCodePath()) != -1);
        } catch (Exception e) {
            return false;
        }
    }


    private List<String> getmenulist(boolean rf) {
        List<String> mdata = new ArrayList<>();
        boolean sts = (rf)? Collections.addAll(mdata, menutext_r): Collections.addAll(mdata, menutext_nr);
        if (!sts) {
            mdata.add(getResources().getString(R.string.error));
        }
        return mdata;
    }

    private List<String> getcalgrid() {
        List<String> mdata = new ArrayList<>();
        boolean sts = Collections.addAll(mdata, calbuttontext);
        if (!sts) {
            mdata.add(getResources().getString(R.string.error));
        }
        return mdata;
    }

    private List<String> getpaylist() {
        List<String> mdata = new ArrayList<>();

        PackageManager packageManager = getApplicationContext().getPackageManager();
        try {
            packageManager.getPackageInfo("com.tencent.mm", PackageManager.GET_ACTIVITIES);
            wehcatinstalled = true;
            mdata.add(paymenutext[0]);
            mdata.add(paymenutext[1]);
            mdata.add(paymenutext[2]);
        } catch (PackageManager.NameNotFoundException e) {
            wehcatinstalled = false;
        }

        try {
            packageManager.getPackageInfo("com.eg.android.AlipayGphone", PackageManager.GET_ACTIVITIES);
            alipayinstalled = true;
            mdata.add(paymenutext[3]);
            mdata.add(paymenutext[4]);
            mdata.add(paymenutext[5]);
        } catch (PackageManager.NameNotFoundException e) {
            alipayinstalled = false;
        }

        if (!(wehcatinstalled || alipayinstalled)) {
            mdata.add(getResources().getString(R.string.noapp));
        }

        return mdata;
    }
}
