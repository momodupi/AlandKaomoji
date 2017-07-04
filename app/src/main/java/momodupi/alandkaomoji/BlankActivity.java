package momodupi.alandkaomoji;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;



public class BlankActivity extends Activity {

    SeekBar transseekBar;
    Switch wmswitch, rootswitch, magswitch;

    //private boolean magflag = false, rootflag = false;
    //private float transsetting = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        if (Build.VERSION.SDK_INT >= 23) {
            /*
            if (!Settings.canDrawOverlays(this)) {
                showovlyprmsdialog();
            }*/
            while (!Settings.canDrawOverlays(this)) {
                showovlyprmsdialog();
            }
        }

        final SharedPreferences preferences = getSharedPreferences("kaomojipref", MODE_PRIVATE);
        if (!preferences.getBoolean("nonvirgin", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
            int cnt;
            for (cnt = 0; cnt < getkaomojilist().size(); cnt++) {
                editor.putString("kao" + String.valueOf(cnt), getkaomojilist().get(cnt));
            }
            editor.putInt("kaonum", cnt);
            editor.putBoolean("firstrunning", true);
            editor.putFloat("transsetting", 50);
            editor.putBoolean("magtsetting", false);
            editor.putBoolean("rootsetting", false);
            editor.putBoolean("wmsetting", false);
            editor.putBoolean("nonvirgin", true);
            editor.apply();
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.nonvgn), Toast.LENGTH_SHORT).show();
        }

        RealService.rootflag = preferences.getBoolean("rootsetting", false);
        RealService.magflag = preferences.getBoolean("magtsetting", false);
        RealService.transsetting = preferences.getFloat("transsetting", 50);

        if (isAccessibilitySettingsOn(this)) {
            RealService.wmflag = preferences.getBoolean("wmsetting", false);
        }
        else {
            RealService.wmflag = false;
        }

        transseekBar = (SeekBar) findViewById(R.id.transseekBar);
        transseekBar.setMax(100);
        transseekBar.setProgress(50);

        wmswitch = (Switch) findViewById(R.id.wmswitch);
        rootswitch = (Switch) findViewById(R.id.rootswitch);
        magswitch = (Switch) findViewById(R.id.magswitch);

        transseekBar.setProgress((int) RealService.transsetting);
        magswitch.setChecked(RealService.magflag);
        rootswitch.setChecked(RealService.rootflag);
        wmswitch.setChecked(RealService.wmflag);

        wmswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
                editor.putBoolean("wmsetting", isChecked);
                editor.apply();
                RealService.wmflag = isChecked && isAccessibilitySettingsOn(getApplicationContext());
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (!isAccessibilitySettingsOn(getApplicationContext())) {
                            showacsblyprmsdialog();

                            if (Build.VERSION.SDK_INT >= 23) {
                                if (!Settings.canDrawOverlays(getApplication())) {
                                    showovlyprmsdialog();
                                    /**/
                                }
                            }
                        }
                        else {
                            RealService.mWindowManager.addView(RealService.hidelayout, RealService.wmParams);
                            RealService.mWindowManager.updateViewLayout(RealService.hidelayout, RealService.wmParams);
                        }
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (isAccessibilitySettingsOn(getApplicationContext())) {
                           /**/
                            if (RealService.hideflag) {
                                RealService.mWindowManager.removeView(RealService.hidelayout);
                            }
                            else {
                                RealService.mWindowManager.removeView(RealService.prstlayout);
                            }
                        }
                    }
                }
            }
        });

        transseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
                editor.putFloat("transsetting", progress);
                editor.apply();
                RealService.transsetting = progress;
                if (isAccessibilitySettingsOn(getApplicationContext())) {
                    if (RealService.hideflag) {
                        RealService.hidelayout.setAlpha(RealService.transsetting / 100);
                        RealService.mWindowManager.updateViewLayout(RealService.hidelayout, RealService.wmParams);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        magswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
                editor.putBoolean("magsetting", isChecked);
                editor.apply();
                RealService.magflag = isChecked;
            }
        });

        rootswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && checkroot()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.rootpmstext), Toast.LENGTH_SHORT).show();
                    RealService.rootflag = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.rootrfstext), Toast.LENGTH_SHORT).show();
                    rootswitch.setChecked(false);
                    RealService.rootflag = false;
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        //this.finish();
        super.onDestroy();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" +RealService.class.getCanonicalName();

        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
/**/
    private void showovlyprmsdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlankActivity.this);
        builder.setTitle(R.string.rqsttitle);
        builder.setMessage(R.string.ovlyrqsttext);
        builder.setPositiveButton(R.string.rqstyes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(R.string.rqstno, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.rqsttoast), Toast.LENGTH_SHORT).show();
                onDestroy();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();/**/
    }

    private void showacsblyprmsdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlankActivity.this);
        builder.setTitle(R.string.rqsttitle);
        builder.setMessage(R.string.acsblyrqsttext);
        builder.setPositiveButton(R.string.rqstyes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent intent=new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(R.string.rqstno, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.rqsttoast), Toast.LENGTH_SHORT).show();
                onDestroy();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();/**/
    }


    public boolean checkroot() {

        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            //dataOutputStream.writeBytes("export LD_LIBRARY_PATH=/vendor/lib:/system/lib\n");
            //cmd = String.valueOf(cmd);
            dataOutputStream.writeBytes("chmod 777" + getPackageCodePath() + "\n");
            //dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            if (process.exitValue() != -1) {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
            return false;
        }
    }

    private List<String> getkaomojilist() {

        List<String> mdata = new ArrayList<>();
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
