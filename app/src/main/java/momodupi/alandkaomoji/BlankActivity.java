package momodupi.alandkaomoji;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;


public class BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_blank);

        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(BlankActivity.this, RealService.class);
                startService(intent);
            } else {
                try{
                    Intent  intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        } else {
            Intent intent = new Intent(BlankActivity.this, RealService.class);
            startService(intent);
        }

        this.finish();
    }
}
