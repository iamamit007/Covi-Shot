package com.help.android.login;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.help.android.R;
import com.help.android.startup.MainActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.OtpEditText;
import com.help.android.utility.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;

public class OTPActivity extends AppCompatActivity {
    OtpEditText phone_edit_text ;
    TextView info,title;
    String markup = "<p>\n" +
            "This is a free application for help can be use foer vaccination information and tutorial purpose.\n" +
            "</p>\n" +
            "\n" +
            "<h1>Used Opensource API </h1>\n" +
            "\n" +
            "<p><a href=\"https://apisetu.gov.in/public/api/cowin#/\">API Setu</a></p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        phone_edit_text = findViewById(R.id.phone_edit_text);
        title = findViewById(R.id.title);
        info =  findViewById(R.id.textView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            info.setText(Html.fromHtml(markup, Html.FROM_HTML_MODE_COMPACT));
        } else {
            info.setText(Html.fromHtml(markup));
        }
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phno =  phone_edit_text.getText().toString();
                String  txn = new PrefManager(OTPActivity.this).getTXN();
                if (phno.isEmpty()){
                    Toast.makeText(OTPActivity.this,"OTP can't be empty",Toast.LENGTH_SHORT).show();

                }else {
                    if (phno.length()==6){
                        callLoginApi(phno,txn);
                    }else {
                        Toast.makeText(OTPActivity.this,"Please enter a valid OTP",Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

    }

    KProgressHUD hud  = null;
    void   showHud(){
        hud =  KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    void hide(){
        hud.dismiss();
    }

    public void callLoginApi(String otpText,String txnId){
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<VerifyOtpResponse> responseCall = apiInterface.verifyOTP(new VerifyOTPRequestParams(getSha256Hash(otpText),txnId));
        responseCall.enqueue(callBack);

    }
    private NetworkCallBack callBack = new NetworkCallBack<VerifyOtpResponse>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
             hide();
            Log.d("COVI-SHOT Login",response.toString());
            Toast.makeText(OTPActivity.this,"OTP send Successfully",Toast.LENGTH_SHORT).show();
            GetOTPResponse getOTPResponse = (GetOTPResponse) response.getData();
            if (getOTPResponse!=null){
                String id = getOTPResponse.getTxnId();
                SharedPreferences sharedPreferences
                        = getBaseContext().getSharedPreferences("MySharedPref",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("txnid", id);
                myEdit.commit();
                Intent i = new Intent(OTPActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // startActivity(i);

            }

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };


    private  ObjectAnimator createBottomUpAnimation(View view,
                                                          AnimatorListenerAdapter listener, float distance) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -distance);
//        animator.setDuration(???)
        animator.removeAllListeners();
        if (listener != null) {
            animator.addListener(listener);
        }
        return animator;
    }

    public  ObjectAnimator createTopDownAnimation(View view, AnimatorListenerAdapter listener,
                                                        float distance) {
        view.setTranslationY(-distance);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.removeAllListeners();
        if (listener != null) {
            animator.addListener(listener);
        }
        return animator;
    }

    public static String getSha256Hash(String password) {
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(password.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }
}