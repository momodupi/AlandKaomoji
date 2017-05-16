package momodupi.alandkaomoji;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;


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
            }
            else {
                showpermissiondialog();
            }
        }
        else {
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
        builder.setNegativeButton("前去设置(＾o＾)ﾉ", new DialogInterface.OnClickListener() {
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
        builder.setPositiveButton("拒绝(╬ﾟдﾟ)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "需要上层权限才能运行( ´_ゝ`)", Toast.LENGTH_SHORT).show();
                onDestroy();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
