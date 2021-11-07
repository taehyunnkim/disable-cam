package com.kim.disablecam;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button button;
    Button adminButton;
    TextView header;
    DevicePolicyManager dpm;
    ComponentName deviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        adminButton = findViewById(R.id.button2);
        header = findViewById(R.id.header);

        deviceAdmin = new ComponentName(MainActivity.this, AdminReceiver.class);
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        boolean adminActive = dpm.isAdminActive(deviceAdmin);

        adminButton.setOnClickListener(v -> {
            dpm.removeActiveAdmin(deviceAdmin);
            adminButton.setEnabled(false);
        });

        if (adminActive) {
            setupUI();
            adminButton.setEnabled(true);
        } else {
            adminButton.setEnabled(false);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
            activityResultLauncher.launch(intent);
        }
    }

    public void setupUI () {
        if (dpm.getCameraDisabled(deviceAdmin)) {
            header.setText(R.string.camera_is_disabled);
            button.setText(R.string.enable_camera);

            button.setOnClickListener(v -> {
                dpm.setCameraDisabled(deviceAdmin, false);
                setupUI();
            });
        } else {
            header.setText(R.string.camera_is_enabled);
            button.setText(R.string.disable_camera);

            button.setOnClickListener(v -> {
                dpm.setCameraDisabled(deviceAdmin, true);
                setupUI();
            });
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        // End user cancels the request
                        case Activity.RESULT_CANCELED:
                            break;
                        // End user accepts the request
                        case Activity.RESULT_OK:
                            adminButton.setEnabled(true);
                            setupUI();
                            break;
                    }
                }
            }
    );


}