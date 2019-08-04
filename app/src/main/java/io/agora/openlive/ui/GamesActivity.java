package io.agora.openlive.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.openlive.R;
import io.agora.openlive.model.MatchedActivity;

public class GamesActivity extends AppCompatActivity {

    private Button letsPlayButton;
    private boolean fl = false;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private boolean tempv;
    //private P
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        tempv = true;

        letsPlayButton = (Button) findViewById(R.id.letsplay);
        letsPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progress = ProgressDialog.show(GamesActivity.this, "",
                        "Searching Your buddy... please wait", true);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(false);
                progress.show();
                Log.e("showed","now");
                final String currentTemp = database.getReference("games").child("quiz").push().getKey();
                Log.e("temp push is ",""+currentTemp);
                database.getReference("games").child("quiz").child(currentTemp).setValue(firebaseAuth.getCurrentUser().getEmail());
                //startActivity(new Intent(GamesActivity.this,MainActivity.class));
                final Timer t = new Timer();
                final intClass ct = new intClass();
                 final ChildEventListener childEventListener = database.getReference("games").child("quiz").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String k = dataSnapshot.getKey();
                        Log.e("Child added ","added "+k);
                        if(!k.equals(currentTemp) && !k.equals("321")) {
                            fl = true;
                            Log.e("inside","yeah");
                            String v = dataSnapshot.getValue().toString();
                            database.getReference("games").child("quiz").child(k).removeValue();
                            database.getReference("games").child("quiz").child(currentTemp).removeValue();
                            SharedPreferences.Editor editor = getSharedPreferences("room", MODE_PRIVATE).edit();
                            String temp1 = v.substring(0, 3);
                            String temp2 = firebaseAuth.getCurrentUser().getEmail().substring(0, 3);
                            String chroom = "";
                            if((temp1.compareTo(temp2))<0){
                                chroom = temp1+temp2;
                            }
                            else{
                                chroom =  temp2+temp1;
                            }
                            editor.putString("name", k.substring(0, 3) + firebaseAuth.getCurrentUser().getEmail().substring(0, 3));

                            Log.e("chroom is ", chroom);
                            String chatIDs = "";
                            if(currentTemp.compareTo(k)<0) {
                                chatIDs = database.getReference("chatrooms").child("quiz").push().getKey();
                                database.getReference("chatrooms").child("quiz").child(chatIDs).setValue(chroom);
                                editor.putString("roomID",chatIDs);
                            }
                            t.cancel();
                            editor.apply();
                            Intent ii = new Intent(GamesActivity.this, MatchedActivity.class);
                            ii.putExtra("room",chroom);

                            try{
                                database.getReference("games").child("quiz").child(currentTemp).removeValue();
                            }
                            catch (Exception e){}
                            progress.dismiss();
                            startActivity(ii);
                        }
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
                 if(fl==true){
                     database.getReference("games").child("quiz").removeEventListener(childEventListener );
                     try{
                         database.getReference("games").child("quiz").child(currentTemp).removeValue();
                     }
                     catch (Exception e){}
                 }
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //if(ct.var==1)
                            Log.e("test 1",""+ct.var);
                            //Toast.makeText(GamesActivity.this,"started",Toast.LENGTH_LONG).show();
                        if(ct.var==10) {
                            database.getReference("games").child("quiz").removeEventListener(childEventListener );
                            try{
                                database.getReference("games").child("quiz").child(currentTemp).removeValue();
                            }
                            catch (Exception e){}
                            progress.dismiss();
                            t.cancel();
                            //Toast.makeText(GamesActivity.this,"Done",Toast.LENGTH_LONG).show();

                        }
                        ct.var = ct.var+1;

                    }
                }, 0, 1000);

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(tempv==false){
            Bundle tempBundle = new Bundle();
            onCreate(tempBundle);
        }
    }
}
class intClass{
    int var;
}
class childClass{
    ChildEventListener ch;
}


