package com.example.myecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPverificationActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "My_ecommerce";
    private static final String CHANNEL_NAME = "My ecommerce";
    private static final String CHANNEL_DESC = "My ecommerce notification";

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn, resentCode;
    private String userNo;
    private ProgressBar progressBar;

    private int OTP_number;
    private int OTP_No;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_pverification);

        progressBar = findViewById(R.id.progress_bar);
        phoneNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verify_btn);
        resentCode = findViewById(R.id.resent_code);
        userNo = getIntent().getStringExtra("mobileNo");

//        phoneNo.setText("Verification code has been sent to +977 " + userNo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        // SLEEP 5 SECONDS HERE ...
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                displayNotification();
            }
        }, 5000);


        resentCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        displayNotification();
                    }
                }, 5000);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (otp.getText().toString().equals(String.valueOf(OTP_number))){

                    Map<String,Object> updateStatus = new HashMap<>();
                    updateStatus.put("Order Status", "Ordered");
                    final String OrderID = getIntent().getStringExtra("OrderID");

                    FirebaseFirestore.getInstance().collection("ORDERS").document(OrderID).update(updateStatus)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Map<String, Object> userOrder = new HashMap<>();
                                        userOrder.put("order_id", OrderID);
                                        userOrder.put("time", FieldValue.serverTimestamp());
                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS")
                                                .document(OrderID).set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    DeliveryActivity.codOrderConfirmed = true;
                                                    finish();
                                                }else {
                                                    Toast.makeText(OTPverificationActivity.this, "Failed to update user's OrderList", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(OTPverificationActivity.this, "Order CANCELLED", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }else {
                    Toast.makeText(OTPverificationActivity.this, "Incorrect OTP !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void displayNotification(){

        OTP_No = OTP();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.blue_email)
                .setContentTitle("My Ecommerce")
                .setContentText("Your verification code is " + OTP_No)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        final NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(this);
        mNotificationMgr.notify(1, mBuilder.build());

        Handler h = new Handler();
        long delayInMilliseconds = 60000;
        h.postDelayed(new Runnable() {
            public void run() {
                mNotificationMgr.cancel(1);
                OTP();
            }
        }, delayInMilliseconds);
        progressBar.setVisibility(View.GONE);

    }

    private int OTP(){
        Random random = new Random();
        OTP_number = random.nextInt(999999 - 111111) + 111111;
        return OTP_number;
    }

}