package com.example.lab3_yasminathira;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

public class UserPrefsActivity extends AppCompatActivity {

    final String URL_ADAFRUIT = "https://io.adafruit.com/";
    Button deleteJournalBtn, linkAdafruitBtn,setdownloadLinkBtn;
    EditText downloadEditText;
    DataHelper dbcenter;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch reminderSwitch;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications permission granted",Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "FCM can't post notifications without POST_NOTIFICATIONS permission",
                            Toast.LENGTH_LONG).show();
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prefs);

        getSupportActionBar().setTitle("User Settings");
        SharedPreferences sharedPreferences = UserPrefsActivity.this.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        dbcenter = new DataHelper(this);
        deleteJournalBtn = findViewById(R.id.button_delete);
        linkAdafruitBtn = findViewById(R.id.button_link_adafruit);
        reminderSwitch  = findViewById(R.id.user_prefs_switch);
        downloadEditText = findViewById(R.id.editText_download_link);
        setdownloadLinkBtn = findViewById(R.id.button_set_download_link);
        downloadEditText.setText(sharedPreferences.getString("download_link",""));
        String switch_bool = sharedPreferences.getString("isSubscribed","") ;
        reminderSwitch.setChecked(Boolean.parseBoolean(switch_bool));
        setdownloadLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("download_link",downloadEditText.getText().toString());
                editor.commit();
                Snackbar.make(view,"Download Link Set",Snackbar.LENGTH_SHORT).show();

            }
        });
        deleteJournalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserPrefsActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("This action will delete the plant and journal data");

                // Set Alert Title
                builder.setTitle("Delete Confirmation");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Confirm", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Intent intent = new Intent(UserPrefsActivity.this, FrontpageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    deleteJournalData();
                    unsubscribeTopic();
                    editor.putString("isSubscribed","false");
                    editor.putString("plant_name","");

                    editor.commit();
                    startActivity(intent);
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();

            }
        });
        linkAdafruitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(URL_ADAFRUIT));
                    startActivity(i);
                }
                catch(ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_ADAFRUIT));
                    startActivity(i);
                }
            }
        });
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(isChecked){
                    subscribeTopic();
                    Snackbar.make(buttonView, "Subscribed to topic <reminder>", Snackbar.LENGTH_LONG)
                            .setAction("OKAY",null).show();

                    editor.putString("isSubscribed", String.valueOf(reminderSwitch.isChecked()));
                    editor.commit();

                }else{
                    unsubscribeTopic();
                    Snackbar.make(buttonView, "Unsubscribed to topic <reminder>", Snackbar.LENGTH_LONG)
                            .setAction("OKAY",null).show();
                    editor.putString("isSubscribed", String.valueOf(reminderSwitch.isChecked()));
                    editor.commit();
                }

            }
        });
    }
    private void subscribeTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("reminder")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed_reminder);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        }
                        Log.d(TAG, msg);
                    }
                });
    }
    private void unsubscribeTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("reminder")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_unsubscribed_reminder);
                        if (!task.isSuccessful()) {

                            msg = getString(R.string.msg_subscribe_failed);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        }
                        Log.d(TAG, msg);
                    }
                });
    }
    private void deleteJournalData(){
        SQLiteDatabase db = dbcenter.getWritableDatabase();
        db.execSQL("delete from biodata ");
        db.execSQL("vacuum");
        Toast.makeText(getApplicationContext(), "Data Successfully Removed", Toast.LENGTH_SHORT).show();
    }
}