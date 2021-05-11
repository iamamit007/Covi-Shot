package com.help.android.login;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.help.android.R;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {
    EditText phone_edit_text ;
    TextView info,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone_edit_text = findViewById(R.id.username);
        title = findViewById(R.id.title);
        info =  findViewById(R.id.textView);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phno =  phone_edit_text.getText().toString();
                if (phno.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Phone No can't be empty",Toast.LENGTH_SHORT).show();

                }else {
                    if (phno.length()==10){
                        callLoginApi(phno);
                    }else {
                        Toast.makeText(LoginActivity.this,"Please enter a valid phone number",Toast.LENGTH_SHORT).show();

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

    public void callLoginApi(String otpText){
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<GetOTPResponse> responseCall = apiInterface.getOTP(new GETOTPRequestParams(otpText));
        responseCall.enqueue(callBack);

    }
    private NetworkCallBack callBack = new NetworkCallBack<GetOTPResponse>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
             hide();
            Log.d("COVI-SHOT Login",response.toString());
            Toast.makeText(LoginActivity.this,"OTP send Successfully",Toast.LENGTH_SHORT).show();
            GetOTPResponse getOTPResponse = (GetOTPResponse) response.getData();
            if (getOTPResponse!=null){
                String id = getOTPResponse.getTxnId();
                SharedPreferences sharedPreferences
                        = getBaseContext().getSharedPreferences("MySharedPref",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("txnid", id);
                myEdit.commit();
                Intent i = new Intent(LoginActivity.this, OTPActivity.class);
             //   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

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
}