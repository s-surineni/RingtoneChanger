package com.example.ssurinen.ringtonechanger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button getLoc;
    private TextView currRing;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final int SELECTED_A_FILE = 1;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoc = findViewById(R.id.getLoc);
        currRing = findViewById(R.id.currRing);
        getLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkForPermsnAndSelectFile();
                }
            });

        currRing.setText(findCurrRingtone());
    }

    private CharSequence findCurrRingtone() {
        return RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE).toString();
    }

    private void checkForPermsnAndSelectFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //                        should show reason here
            } else {
                ActivityCompat.requestPermissions(this,
                                                  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            selectFile();
        }
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, SELECTED_A_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
        case SELECTED_A_FILE:
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Log.i(TAG, "Selected file " + data.getData());
                //                playASong(data.getData());
                Cursor cursor = getContentResolver().query(
                                                           uri,
                                                           null,
                                                           null,
                                                           null,
                                                           null);
                while (cursor.moveToNext()) {
                    String[] allColumns = cursor.getColumnNames();
                    if(Arrays.asList(allColumns).contains(MediaStore.Audio.AudioColumns.TITLE)) {
//                        persistSong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)), uri);
                    } else {
//                        persistSong(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)), uri);
                    }
                    Log.i(TAG, Arrays.toString(allColumns));

                }
            }
            break;
        case 2:
            if (Settings.System.canWrite(this)) {
//                toggleCallDetectService();
            }
            break;
        }
    }
}
