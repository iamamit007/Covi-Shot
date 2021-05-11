package com.help.android.options;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.help.android.R;
import com.help.android.login.CustomerDetailResponse;
import com.help.android.login.Shipping;
import com.help.android.registration.AddAddressActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import retrofit2.Call;

public class MyAccountActivity extends AppCompatActivity {
    TextView name;
    TextView email;
    TextView address;
    Shipping addr;
    String add;
    String add1;
    String add2;
    String add3;
    String cntry;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        name = (TextView) findViewById(R.id.fullname);
        address = (TextView) findViewById(R.id.addressdetail);
        email = (TextView) findViewById(R.id.email);
        ImageView iv = (ImageView)findViewById(R.id.imageView2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        // Get the textview of the title
        ImageView customTitle = (ImageView) customView.findViewById(R.id.actionbarTitle);
        getSupportActionBar().setCustomView(customView);
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

        String fname = sh.getString("firstName", "");
        String lname = sh.getString("lastName", "");
        String emailTxt = sh.getString("email", "");
        String image = sh.getString("image", "");
        name.setText(fname + "" + lname);
        email.setText(emailTxt);
        Uri uri = Uri.parse(image);
        iv.setImageURI(uri);

        callProfileApiList();

        ImageView edit = (ImageView)findViewById(R.id.imageedit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, AddAddressActivity.class);
                intent.putExtra("add", add);
                intent.putExtra("add1", add1);
                intent.putExtra("add2", add2);
                intent.putExtra("add3", add3);
                intent.putExtra("cntry", cntry);
                intent.putExtra("action", "add_address");
                startActivity(intent);
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
    public void callProfileApiList(){

        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        int id = Integer.parseInt(sh.getString("id", "true"));
        Call<CustomerDetailResponse> responseCall = apiInterface.getCustomerProfile(id);
        responseCall.enqueue(callBack);

    }



    private NetworkCallBack callBack = new NetworkCallBack<CustomerDetailResponse>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("ZINGAKART",response.toString());
            CustomerDetailResponse userDetail = (CustomerDetailResponse) response.getData();
            addr = userDetail.getShipping();
             add = addr.getAddress_1();
             add1 = addr.getCity();
             add2 = addr.getState();
             add3 = addr.getPostcode();
             cntry = addr.getCountry();
            address.setText(add+", "+ add1+", "+add2+" - "+add3);
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };


}