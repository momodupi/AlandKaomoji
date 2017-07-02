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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        if (Build.VERSION.SDK_INT >= 19) {
            //if (Settings.canDrawOverlays(this)) {

            if (isAccessibilitySettingsOn(getApplicationContext())) {
                Intent intent = new Intent(BlankActivity.this, RealService.class);
                //Intent intent = new Intent(ACCESSIBILITY_SERVICE);
                startService(intent);
                this.finish();
            } else {
                showpermissiondialog();

                SharedPreferences preferences = getSharedPreferences("kaomojipref", MODE_PRIVATE);
                if (!preferences.getBoolean("nonvirgin", false)) {
                    SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
                    int cnt;
                    for (cnt = 0; cnt < getkaomojilist().size(); cnt++) {
                        editor.putString("kao" + String.valueOf(cnt), getkaomojilist().get(cnt));
                    }
                    editor.putInt("kaonum", cnt);
                    editor.putBoolean("firstrunning", true);
                    editor.putFloat("transsetting", 50);
                    editor.putBoolean("leftsetting", false);
                    editor.putBoolean("rootsetting", false);
                    editor.apply();
                }
            }
        }
        else {
            SharedPreferences preferences = getSharedPreferences("kaomojipref", MODE_PRIVATE);
            if (!preferences.getBoolean("nonvirgin", false)) {
                SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
                int cnt;
                for (cnt = 0; cnt < getkaomojilist().size(); cnt++) {
                    editor.putString("kao" + String.valueOf(cnt), getkaomojilist().get(cnt));
                }
                editor.putInt("kaonum", cnt);
                editor.putBoolean("firstrunning", true);
                editor.putFloat("transsetting", 50);
                editor.putBoolean("leftsetting", false);
                editor.putBoolean("rootsetting", false);
                editor.apply();
            }/**/

            //Intent intent = new Intent(BlankActivity.this, RealService.class);
            //startService(intent);
            //this.finish();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nonvgn), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        this.finish();
        super.onDestroy();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" +RealService.class.getCanonicalName();

        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            //Log.v("ACC", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            //Log.e("ACC", "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            //Log.v("ACC", "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    //Log.v("ACC", "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        //Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
/**/
    private void showpermissiondialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(BlankActivity.this);
        builder.setTitle(R.string.rqsttitle);
        builder.setMessage(R.string.rqsttext);
        builder.setPositiveButton(R.string.rqstyes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent intent=new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    //intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    onDestroy();
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
