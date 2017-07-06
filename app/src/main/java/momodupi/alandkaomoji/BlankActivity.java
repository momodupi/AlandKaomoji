package momodupi.alandkaomoji;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class BlankActivity extends Activity {

    SeekBar transseekBar;
    Switch wmswitch, rootswitch, magswitch, navswitch;
    Spinner gstspineer, gstfunspinner;
    private boolean navflag = true;

    //private boolean magflag = false, rootflag = false;
    //private float transsetting = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                showovlyprmsdialog();
            }
        }

        final SharedPreferences preferences = getSharedPreferences("kaomojipref", MODE_PRIVATE);
        if (!preferences.getBoolean("nonvirgin", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();

            Resources resources =getResources();
            String[] kaomoji = resources.getStringArray(R.array.kaomoji);
            int cnt;
            for (cnt = 0; cnt < kaomoji.length; cnt++) {
                editor.putString("kao" + String.valueOf(cnt), kaomoji[cnt]);
            }

            editor.putInt("kaonum", cnt);
            editor.putBoolean("firstrunning", true);
            editor.putFloat("transsetting", 50);
            editor.putBoolean("magsetting", false);
            editor.putBoolean("rootsetting", false);
            editor.putBoolean("wmsetting", false);
            editor.putBoolean("navsetting", true);
            editor.putInt("gstclick", RealService.dragdirc[RealService.DRG_CLICK]);
            editor.putInt("gstleft", RealService.dragdirc[RealService.DRG_LEFT]);
            editor.putInt("gstright", RealService.dragdirc[RealService.DRG_RIGHT]);
            editor.putInt("gstup", RealService.dragdirc[RealService.DRG_UP]);
            editor.putInt("gstdown", RealService.dragdirc[RealService.DRG_DOWN]);
            editor.putBoolean("nonvirgin", true);
            editor.apply();

            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.nonvgn), Toast.LENGTH_SHORT).show();
        }

        RealService.rootflag = preferences.getBoolean("rootsetting", false);
        RealService.magflag = preferences.getBoolean("magsetting", false);
        RealService.transsetting = preferences.getFloat("transsetting", 50);
        navflag = preferences.getBoolean("navsetting", false);
        RealService.dragdirc[RealService.DRG_CLICK] = preferences.getInt("gstclick", 0);
        RealService.dragdirc[RealService.DRG_LEFT] = preferences.getInt("gstleft", 0);
        RealService.dragdirc[RealService.DRG_RIGHT] = preferences.getInt("gstright", 0);
        RealService.dragdirc[RealService.DRG_UP] = preferences.getInt("gstup", 0);
        RealService.dragdirc[RealService.DRG_DOWN] = preferences.getInt("gstdown", 0);
        RealService.wmflag = preferences.getBoolean("wmsetting", false) && isAccessibilitySettingsOn(this);

        wmswitch = (Switch) findViewById(R.id.wmswitch);
        rootswitch = (Switch) findViewById(R.id.rootswitch);
        magswitch = (Switch) findViewById(R.id.magswitch);
        navswitch = (Switch) findViewById(R.id.navswitch);
        transseekBar = (SeekBar) findViewById(R.id.transseekBar);
        gstspineer = (Spinner) findViewById(R.id.gstspinner);
        gstfunspinner =(Spinner) findViewById(R.id.gstfuncspinner);

        transseekBar.setMax(100);
        transseekBar.setProgress(50);

        transseekBar.setProgress((int) RealService.transsetting);
        magswitch.setChecked(RealService.magflag);
        rootswitch.setChecked(RealService.rootflag);
        wmswitch.setChecked(RealService.wmflag && isAccessibilitySettingsOn(getApplicationContext()));
        navswitch.setChecked(navflag);

        wmswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
                //editor.putBoolean("wmsetting", isChecked);
                //editor.apply();
                RealService.wmflag = isChecked && isAccessibilitySettingsOn(getApplicationContext());
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (!isAccessibilitySettingsOn(getApplicationContext())) {
                            showacsblyprmsdialog();
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (!Settings.canDrawOverlays(getApplication())) {
                                    showovlyprmsdialog();
                                }
                            }
                        }
                        else {
                            if (RealService.hideflag) {
                                RealService.mWindowManager.addView(RealService.hidelayout, RealService.wmParams);
                                RealService.mWindowManager.updateViewLayout(RealService.hidelayout, RealService.wmParams);
                            }
                            else {
                                RealService.mWindowManager.addView(RealService.prstlayout, RealService.wmParams);
                                RealService.mWindowManager.updateViewLayout(RealService.prstlayout, RealService.wmParams);
                            }/**/

                        }
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (isAccessibilitySettingsOn(getApplicationContext())) {
                            if (RealService.hideflag) {
                                RealService.mWindowManager.removeView(RealService.hidelayout);
                            }
                            else {
                                RealService.mWindowManager.removeView(RealService.prstlayout);
                            }/**/
                        }
                    }
                }
            }
        });

        transseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

        navswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sucmd("settings put global policy_control null");
                }
                else {
                    sucmd("settings put global policy_control immersive.navigation=*\n");
                }
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


        gstspineer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gstfunspinner.setSelection(RealService.dragdirc[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gstfunspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RealService.dragdirc[gstspineer.getSelectedItemPosition()] = position;
                SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
                editor.putInt("gstclick", RealService.dragdirc[RealService.DRG_CLICK]);
                editor.putInt("gstleft", RealService.dragdirc[RealService.DRG_LEFT]);
                editor.putInt("gstright", RealService.dragdirc[RealService.DRG_RIGHT]);
                editor.putInt("gstup", RealService.dragdirc[RealService.DRG_UP]);
                editor.putInt("gstdown", RealService.dragdirc[RealService.DRG_DOWN]);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        //this.finish();
        SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();

        editor.putFloat("transsetting", RealService.transsetting);
        editor.putBoolean("magsetting", RealService.magflag);
        editor.putBoolean("rootsetting", RealService.rootflag);
        editor.putBoolean("wmsetting", RealService.wmflag);
        editor.putInt("gstclick", RealService.dragdirc[RealService.DRG_CLICK]);
        editor.putInt("gstleft", RealService.dragdirc[RealService.DRG_LEFT]);
        editor.putInt("gstright", RealService.dragdirc[RealService.DRG_RIGHT]);
        editor.putInt("gstup", RealService.dragdirc[RealService.DRG_UP]);
        editor.putInt("gstdown", RealService.dragdirc[RealService.DRG_DOWN]);
        editor.putBoolean("nonvirgin", true);
        editor.apply();
        super.onDestroy();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" +RealService.class.getCanonicalName();

        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            return false;
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.rqstrfstoast), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.rqstrfstoast), Toast.LENGTH_SHORT).show();
                onDestroy();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();/**/
    }

    public int sucmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes(cmd + "\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            return process.exitValue();
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
    }

    public boolean checkroot() {
        return (sucmd("chmod 777" + getPackageCodePath() + "\n") != -1);
    }

}
