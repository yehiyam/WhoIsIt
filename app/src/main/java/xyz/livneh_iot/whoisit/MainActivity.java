package xyz.livneh_iot.whoisit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> missingPermissions=new ArrayList<>();
        if (!checkPermission(Manifest.permission.READ_CALL_LOG)){
            missingPermissions.add(Manifest.permission.READ_CALL_LOG);
        }
        if (!checkPermission(Manifest.permission.READ_CONTACTS)){
            missingPermissions.add(Manifest.permission.READ_CONTACTS);
        }
        if (!checkPermission(Manifest.permission.READ_PHONE_STATE)){
            missingPermissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (missingPermissions.size()>0){
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[missingPermissions.size()]), MAKE_CALL_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                        dial.setEnabled(true);
                    Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

}
