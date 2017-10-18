package com.wbapp.omxvideo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.wbapp.suspend.ShowSuspendWindows;

public class StartActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.start_screen);
    }
    
    public void onStartCameraClick(View view) {
        /*Intent cameraIntent = new Intent(getApplicationContext(),
                OMXVideoActivity.class);
        startActivity(cameraIntent);*/
        // get back to camera activity
        finish();
    }
    
    public void onStartReplayClick(View view) {
        final EditText pswdEditText = new EditText(this);
        pswdEditText.setHint("Password");
        pswdEditText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        /*new AlertDialog.Builder(this).setTitle("输入密码")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(pswdEditText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {*/
        Intent replayIntent = new Intent(
                getApplicationContext(), ReplayListActivity.class);
        //if (pswdEditText.getText().toString().equals("888888")) {
        startActivity(replayIntent);
                        /*}
                    }
                }).setNegativeButton("取消", null).show();*/
    }

    public void onStartAudiorecClick(View view) {
        Intent audioRecordIntent = new Intent(getApplicationContext(),
                AudioRecordActivity.class);
        startActivity(audioRecordIntent);
    }

    public void onStartSettingsClick(View view) {
        Intent prefsIntent = new Intent(getApplicationContext(),
                VideoSettingsActivity.class);
        startActivity(prefsIntent);
    }

    public void onMonitoredClick(View view) {  //监控
        ClientActivity.execHandler(false);
        Intent clientIntent = new Intent(
                getApplicationContext(), ClientActivity.class);
        startActivity(clientIntent);
    }

    public void onExitClick(View view) {  //退出
        if (OMXVideoActivity.instance != null) {
            ShowSuspendWindows.setShowStatus(false);
            OMXVideoActivity.instance.exit();
            finish();
            System.exit(0);
        }
    }
}