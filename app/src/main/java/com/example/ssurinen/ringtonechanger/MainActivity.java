package com.example.ssurinen.ringtonechanger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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
    private static final int MY_PERMISSIONS_WRITE_SETTINGS = 2;
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

        changeTextView();
        requestForWriteSettingPrmsn();
    }

    private void requestForWriteSettingPrmsn() {
//        if(! Settings.System.canWrite(this)) {
//            ActivityCompat.requestPermissions(this,
//                    new String[] {Manifest.permission.WRITE_SETTINGS},
//                    MY_PERMISSIONS_WRITE_SETTINGS);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            this.startActivityForResult(intent, MainActivity.MY_PERMISSIONS_WRITE_SETTINGS);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, MY_PERMISSIONS_WRITE_SETTINGS);
        }

    }

    private void changeTextView() {
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
                        if (Arrays.asList(allColumns).contains(MediaStore.Audio.AudioColumns.TITLE)) {
                            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),
                                    RingtoneManager.TYPE_RINGTONE,
                                    uri);
                            changeTextView();
//                        persistSong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)), uri);
                        } else {
                            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),
                                    RingtoneManager.TYPE_RINGTONE,
                                    uri);
                            changeTextView();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectFile();
                }
                break;

//        case MY_PERMISSIONS_WRITE_SETTINGS:
//            toggleCallDetectService();
//            break;
//        case MY_PERMISSIONS_READ_PHONE_STATE:
//            if (grantResults.length > 0
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(TAG, "READ_PHONE_STATE granted");
//            }
//        }
        }
    }
}
