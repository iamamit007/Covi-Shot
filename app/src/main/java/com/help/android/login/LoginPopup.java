package com.help.android.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.help.android.R;
import com.help.android.registration.SignUpActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import retrofit2.Call;

public class LoginPopup {

    public LoginPopup(Context context) {
        this.context = context;
    }

    //PopupWindow display method
    Button buttonSignup;
    EditText username;
    EditText password;
     View popupView;
    PopupWindow popupWindow;
    Context context;
    public void showPopupWindow(final View view) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
       popupView = inflater.inflate(R.layout.pop_up_layout, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
         popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

         username = (EditText) popupView.findViewById(R.id.username);
         password = (EditText) popupView.findViewById(R.id.password);
        Button close = (Button) popupView.findViewById(R.id.closeBtn);

        buttonSignup = popupView.findViewById(R.id.signup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //As an example, display the message
                //Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(popupView.getContext(), SignUpActivity.class);
                popupView.getContext().startActivity(i);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        Button buttonLogin = popupView.findViewById(R.id.login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginApi();
                Toast.makeText(popupView.getContext(),"Login.",Toast.LENGTH_SHORT).show();
//                if(username.getText().toString().trim().isEmpty() || username.getText().toString().trim().length() == 0 || username.getText().toString().trim().equals("") || username.getText().toString().trim() == null)
//                {
//                    Toast.makeText(popupView.getContext(),"Please enter username.",Toast.LENGTH_SHORT).show();
//                }
//                else if(password.getText().toString().trim().isEmpty() || password.getText().toString().trim().length() == 0 || password.getText().toString().trim().equals("") || password.getText().toString().trim() == null)
//                {
//                    Toast.makeText(popupView.getContext(),"Please enter password.",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    callLoginApi();
//                }
            }
        });


//
//
//
//        //Handler for clicking on the inactive zone of the window
//
//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                //Close the window when clicked
//                popupWindow.dismiss();
//                return true;
//            }
//        });
    }

    KProgressHUD hud  = null;
    void   showHud(){
        hud =  KProgressHUD.create(context)
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

    public void callLoginApi(){
        //showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<CustomerLoginResponse>> responseCall = apiInterface.getLoginDetails(username.getText().toString(),password.getText().toString());
        responseCall.enqueue(callBack);

    }
    private NetworkCallBack callBack = new NetworkCallBack<List<CustomerLoginResponse>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
           // hide();
            Log.d("ZINGAKART Login",response.toString());
            Toast.makeText(popupView.getContext(),"Loggedin Successfully",Toast.LENGTH_SHORT).show();
           List<CustomerLoginResponse> userDetailarr = (List<CustomerLoginResponse>) response.getData();
           if (userDetailarr.size() >0){
               CustomerLoginResponse userDetail = userDetailarr.get(0);
               String id = userDetail.getId();

               SharedPreferences sharedPreferences
                       = popupView.getContext().getSharedPreferences("MySharedPref",
                       Context.MODE_PRIVATE);

               SharedPreferences.Editor myEdit = sharedPreferences.edit();

               myEdit.putString("id", id);
               myEdit.putString("firstName", userDetail.getFirst_name());
               myEdit.putString("lastName", userDetail.getLast_name());
               myEdit.putString("email", userDetail.getEmail());
               myEdit.putString("image", userDetail.getAvatar_url());
               myEdit.commit();
               Log.d("sender", "Broadcasting message");
               Intent intent = new Intent("custom-event-name");
               // You can also include some extra data.
               intent.putExtra("message", "login");
               LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
           }else {
               Toast.makeText(context,"Invalid login id/password",Toast.LENGTH_SHORT).show();
           }

            popupWindow.dismiss();
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };

}