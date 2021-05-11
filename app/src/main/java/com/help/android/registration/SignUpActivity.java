package com.help.android.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.help.android.R;
import com.help.android.login.CustomerRegisterRequestParams;
import com.help.android.login.CustomerRegisterResponse;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

public class SignUpActivity extends AppCompatActivity {
    EditText firstName;
    EditText lastName;
    EditText username;
    EditText email;
    EditText password;
    EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button buttonSignup = (Button) findViewById(R.id.register);
         firstName = (EditText) findViewById(R.id.fname);
         lastName = (EditText) findViewById(R.id.lname);
         email = (EditText) findViewById(R.id.email);
         password = (EditText) findViewById(R.id.password);
         confirmPassword = (EditText) findViewById(R.id.confirmpassword);
        username = (EditText) findViewById(R.id.usrname);


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpActivity.this,"Sign UP.",Toast.LENGTH_SHORT).show();
                startThread();
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
    Timer timerObj = null;
    public void startThread(){
        timerObj = new Timer();
        timerObj.schedule(new TimerTask() {
            public void run() {
                Log.d("TIMER", "TimerTask run");
                callRegisterApi();
            }}, 1, 1000);
    }
    public void callRegisterApi(){
        //showHud();
        CustomerRegisterRequestParams param = new CustomerRegisterRequestParams(getAlphaNumericString(5)+"@covid.com",
                getAlphaNumericString(8),
                getAlphaNumericString(8),
                getAlphaNumericString(5)+"@covid.com",
                "!!!!!!!!@#");
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<CustomerRegisterResponse> responseCall = apiInterface.getregisterDetails(param);
        responseCall.enqueue(callBack);

    }
    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }


    private NetworkCallBack callBack = new NetworkCallBack<CustomerRegisterResponse>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            Log.d("ZINGAKART Login",response.toString());
//            Toast.makeText(SignUpActivity.this,"Sign UP."+response.getData().toString(),Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(SignUpActivity.this, AddAddressActivity.class);
//            intent.putExtra("fname", firstName.getText().toString());
//            intent.putExtra("lname", lastName.getText().toString());
//            startActivity(intent);

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };




}