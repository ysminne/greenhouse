package com.example.lab3_yasminathira;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FrontpageActivity extends AppCompatActivity {

//    private void askNotificationPermission() {
//        // This is only necessary for API Level > 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                // FCM SDK (and your app) can post notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//    }
    EditText etName;
    private Button btnGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);

        etName = findViewById(R.id.etName);
        btnGet = findViewById(R.id.buttonEnter);

        btnGet.setOnClickListener (new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            sendData();
        }
    });
    }

    public void sendData()
    {
        String name = etName.getText().toString().trim();
        Intent intent = new Intent(this, MainActivity.class);
        Toast.makeText(this, "Let's take care of your new plant,  "+name,Toast.LENGTH_SHORT).show();

        intent.putExtra(MainActivity.NAME,name);

        startActivity(intent);

    }



    public void start(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        String name = etName.getText().toString().trim();
        Toast.makeText(this, "Let's take care of your new plant,  "+name,Toast.LENGTH_SHORT).show();

    }
}