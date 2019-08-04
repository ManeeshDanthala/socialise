package io.agora.openlive.model;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.common.Constant;
import io.agora.openlive.R;
import io.agora.openlive.ui.GamesActivity;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class MatchedActivity extends AppCompatActivity {

    private RtcEngine mRtcEngine;
    private SurfaceView mLocalView;
    private String exposeRoom;
    private DatabaseReference firebaseDatabasen;
    ArrayList<DataObject> all;
    int aside,bside;
    Random rr = new Random();
    private int qno=-1;
    private SurfaceView mRemoteView;
    private FrameLayout mLocalContainer;
    FirebaseDatabase firebaseDatabase;
    RadioGroup radioGroup;
    private FrameLayout mRemoteContainer;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQ_ID = 22;


    @Override
    public void onBackPressed() {


        Intent intent = new Intent(MatchedActivity.this, GamesActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched);

        all = new ArrayList<>();
        all.add(new DataObject("You're 3rd place right now in a race. What place are you in when you pass the person in 2nd place?","1st","2nd","3rd","None of above",1));
        all.add(new DataObject("There are two clocks of different colors: The red clock is broken and doesn't run at all, but the blue clock loses one second every 24 hours. Which clock is more accurate? ","The red clock.","The blue clock.","Neither","Both",1));
        all.add(new DataObject(
                "A farmer has 17 sheep, all of them but 8 die. How many sheep are still standing? ",8+"",9+"",""+25,"35",1));
        all.add(new DataObject(
                "The Chairman of the Public Accounts Commission(PAC) is? ","Elected by the Union Cabinet"+""," Appointed by the President"+"","Appointed by the Speaker","Elected by the members of PAC",1));
        all.add(new DataObject(
                "2010 World Cup football tournament was held in?? ","Austria"+""," Germany"+"","South Africa","Turin",1));
        all.add(new DataObject("There are two clocks of different colors: The red clock is broken and doesn't run at all, but the blue clock loses one second every 24 hours. Which clock is more accurate? ","The red clock.","The blue clock.","Neither","Both",1));
        all.add(new DataObject(
                "A farmer has 17 sheep, all of them but 8 die. How many sheep are still standing? ",8+"",9+"",""+25,"35",1));
        all.add(new DataObject(
                "The Chairman of the Public Accounts Commission(PAC) is? ","Elected by the Union Cabinet"+""," Appointed by the President"+"","Appointed by the Speaker","Elected by the members of PAC",1));
        all.add(new DataObject(
                "2010 World Cup football tournament was held in?? ","Austria"+""," Germany"+"","South Africa","Turin",1));

        Intent ki = getIntent();
        String rem = ki.getStringExtra("roomID");
        //Log.e("rem ",rem);
        if(rem!=null)
        firebaseDatabase.getReference("chatrooms").child("quiz").child(rem).removeValue();
        radioGroup = (RadioGroup)findViewById(R.id.radiogrp1);
        Intent intent = getIntent();
        exposeRoom = intent.getStringExtra("room");
        FirebaseDatabase.getInstance().getReference("gamestate").child("quiz").child("room").child(exposeRoom).push().setValue(0);
        firebaseDatabasen = FirebaseDatabase.getInstance().getReference("gamestate").child("quiz").child("room").child(exposeRoom);
        firebaseDatabasen.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ++qno;
                    contest();
                Log.e("on caddd ","chnage "+qno);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        check();

        final testb kb = new testb();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                Log.e("on changed ","chnage "+qno);

                int radioBtnID = rGroup.getCheckedRadioButtonId();

                View radioB = rGroup.findViewById(radioBtnID);

                int position = radioGroup.indexOfChild(radioB);
                Log.e("selected ",position+"");
                Random rand = new Random();
                FirebaseDatabase.getInstance().getReference("gamestate").child("quiz").child("room").child(exposeRoom).push().setValue(rand.nextInt(1000));
                //kb.b=true;

            }
        });

    }

    void contest(){
        if(qno>=all.size()){
            new AlertDialog.Builder(MatchedActivity.this)
                    .setTitle("Your Score is "+rr.nextInt(4))
                    .setMessage("Do you want to play game again?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            FirebaseDatabase.getInstance().getReference("gamestate").child("quiz").child("room").child(exposeRoom).removeValue();
                            startActivity(new Intent(MatchedActivity.this, GamesActivity.class));


                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            FirebaseDatabase.getInstance().getReference("gamestate").child("quiz").child("room").child(exposeRoom).removeValue();
                            startActivity(new Intent(MatchedActivity.this, GamesActivity.class));


                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).setCancelable(false)
                    .show();
        }
        else{
            TextView tv = (TextView)  findViewById(R.id.question1);
            tv.setText(all.get(qno).que);
            ((RadioButton) radioGroup.getChildAt(0)).setText(all.get(qno).a);
            ((RadioButton) radioGroup.getChildAt(1)).setText(all.get(qno).b);
            ((RadioButton) radioGroup.getChildAt(2)).setText(all.get(qno).c);
            ((RadioButton) radioGroup.getChildAt(3)).setText(all.get(qno).d);
        }

    }
    private void check(){
        Log.e("in check ","void");
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            Log.e("if ","passed");

            mLocalContainer = findViewById(R.id.local_video_view_container);
            mRemoteContainer = findViewById(R.id.remote_video_view_container);
            initializeEngine();

            /*final intClass ct = new intClass();
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(ct.var==5) {
                        t.cancel();
                        splFunction();
                    }
                    ct.var = ct.var+1;

                }
            }, 0, 1000);*/

            splFunction();


        }

    }
    void splFunction() {

        Log.e("intialized","perfect");
        mRtcEngine.enableVideo();
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        Log.e("enabled and"," config");



        Log.e("local ","fine");
        SharedPreferences prefs = getSharedPreferences("room", MODE_PRIVATE);

        //Log.e("room to join is ", room);
        Random rand = new Random();
        mRtcEngine.joinChannel(null, exposeRoom, "Extra Optional Data",rand.nextInt(1000));
        Log.e("joined ", "done");
    }
    private void setupLocalVideo(int uid) {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(MatchedActivity.this, "f2b6f5fb7c374ff3b03c5a8d05224ca8", mRtcEventHandler);
        } catch (Exception e) {
            Log.e("sample", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            check();
        }
    }
     int myID = 3;
    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(final String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    myID = uid;
                    Log.e("Join channel success ",channel+"   "+myID);
                    setupLocalVideo(uid);
                }
            });
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            Log.e("userJoined ",""+uid+"  "+myID);
//            super.onUserJoined(uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(uid!=myID)
                        setupRemoteVideo(uid);
                }
            });

        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onfirst ","came");
                    //setupRemoteVideo(uid);
                    Log.e("onfirst ","end");
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mLogger.logI("User offline, uid:" + uid);
                    Log.e("offline ","came");
                    onRemoteUserLeft();
                    Log.e("offline ","end");
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = mRemoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = mRemoteContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
        Log.e("remote ","setup fine");
    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    private void remoteRemoteVideo() {
        onRemoteUserLeft();
    }
    private void onRemoteUserLeft() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("came in ","destroy");
        // For latest Agora sdk versions (2.4.1+),
        // it is required no more to call leave
        // channel before destroying the engine.
        // But it is recommended in most cases.

//
//            leaveChannel();
        mRtcEngine.leaveChannel();
        Log.e("chnl"," left");
        RtcEngine.destroy();
        Log.e("chnl"," destroyed");
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }
}
class DataObject {
    String que;
    String a, b, c, d;
    int ans;

    DataObject(String que, String a, String b, String c, String d, int ans) {
        this.que = que;
        this.ans = ans;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}
class intClass{
    int var;
}
class testb{
    boolean b;
}