package momodupi.alandkaomoji;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(BlankActivity.this, RealService.class);
                startService(intent);
                this.finish();
            } else {
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

                    showpermissiondialog();
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

                showpermissiondialog();
            }

            Intent intent = new Intent(BlankActivity.this, RealService.class);
            startService(intent);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        this.finish();
        super.onDestroy();
    }

    private void showpermissiondialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlankActivity.this);
        builder.setTitle("需要权限Σ( ﾟдﾟ)");
        builder.setMessage("请设置允许在其他应用的上层显示(=ﾟωﾟ)=");
        builder.setPositiveButton("前去设置(＾o＾)ﾉ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent  intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                    onDestroy();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("不设咋地(╬ﾟдﾟ)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "那就算了吧( ´_ゝ`)", Toast.LENGTH_SHORT).show();
                onDestroy();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
