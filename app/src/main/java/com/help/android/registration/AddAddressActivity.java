package com.help.android.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.help.android.R;
import com.help.android.login.CustomerAddressRequestParams;
import com.help.android.login.CustomerAddressResponse;
import com.help.android.login.CustomerDetailResponse;
import com.help.android.login.Shipping;
import com.help.android.startup.MainActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.CreateOrderRequest;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.Order;
import com.help.android.utility.OrderLines;
import com.help.android.utility.Product;
import com.help.android.utility.UpdatePaymentOrderRequest;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

import static com.help.android.utility.ConstantAPIKt.customer_order_retrieve;

public class AddAddressActivity extends AppCompatActivity {

    EditText pintx;
    EditText address;
    EditText citytx;
    EditText statetx;
    EditText country;
    EditText confirmPassword,landmrk;
    EditText first_name,lastg_name;
    TextView title;
    LinearLayout paybox;
    Button cod,checkout;
    Boolean isCod = true;

    public static Product product;

    public static void setProduct(Product product) {
        AddAddressActivity.product = product;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        // Get the textview of the title
        ImageView customTitle = (ImageView) customView.findViewById(R.id.actionbarTitle);
        getSupportActionBar().setCustomView(customView);
        pintx = (EditText) findViewById(R.id.pin);
        address = (EditText) findViewById(R.id.address);
        citytx = (EditText) findViewById(R.id.city);
        statetx = (EditText) findViewById(R.id.state);
        country = (EditText) findViewById(R.id.country);
        Button save = (Button) findViewById(R.id.saveadd);
         title = (TextView) findViewById(R.id.title);
         first_name = (EditText) findViewById(R.id.first_name);
         lastg_name = (EditText) findViewById(R.id.last_name);
        landmrk = (EditText) findViewById(R.id.landmrk);
        cod = (Button) findViewById(R.id.cod);
        checkout = (Button) findViewById(R.id.checkout);
        paybox = (LinearLayout) findViewById(R.id.paybox);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        callProfileApiList();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().hasExtra("action")){
                    if (getIntent().getStringExtra("action").equalsIgnoreCase("billing")){

                        callCreateOrder();
                    }else {
                        try {

                            callAddAddressApi();
                        }catch (Exception e){
                            Log.d("FFF",e.getLocalizedMessage());
                        }

                    }
                }


            }
        });
        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isCod = true;
                    startThread();
                }catch (Exception e){
                    Log.d("FFF",e.getLocalizedMessage());
                }


            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isCod = false;
                    callCreateOrder();
                }catch (Exception e){
                    Log.d("FFF",e.getLocalizedMessage());
                }


            }
        });
        String add = getIntent().getStringExtra("add");
        String pin = getIntent().getStringExtra("add3");
        String city = getIntent().getStringExtra("add1");
        String state = getIntent().getStringExtra("add2");
        String cntry = getIntent().getStringExtra("cntry");

        if (getIntent().hasExtra("action")){
            if (getIntent().getStringExtra("action").equalsIgnoreCase("billing")){
                title.setText("Add Billing Address");
                title.setText("Place order");
                save.setVisibility(View.GONE);
                paybox.setVisibility(View.VISIBLE);
             }else {
                first_name.setVisibility(View.GONE);
                lastg_name.setVisibility(View.GONE);
                title.setText("Add address");
                save.setVisibility(View.VISIBLE);
                paybox.setVisibility(View.GONE);
            }
        }


        if (add != null){
            address.setText(add);
        }
        if (pin != null){
            pintx.setText(pin);
        }
        if (city != null){
            citytx.setText(city);
        }
        if (cntry != null){
            country.setText(cntry);
        }
        if (state != null){
            statetx.setText(state);
        }
    }

    Timer timerObj = null;
    public void startThread(){
        timerObj = new Timer();
        timerObj.schedule(new TimerTask() {
            public void run() {
                Log.d("TIMER", "TimerTask run");
               callCreateOrder();
            }}, 1, 1000);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void hide(){
        hud.dismiss();
    }


    public void callAddAddressApi(){
        showHud();
        Shipping sdata = new Shipping(getIntent().getStringExtra("fname"),getIntent().getStringExtra("lname"),"",
                address.getText().toString(),"",citytx.getText().toString(),
                pintx.getText().toString(),country.getText().toString(),
                statetx.getText().toString());
        CustomerAddressRequestParams param = new CustomerAddressRequestParams(Collections.singletonList(sdata));
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        int id = Integer.parseInt(sh.getString("id", "true"));
        Call<CustomerAddressResponse> responseCall = apiInterface.addAddressDetails(id,param);
        responseCall.enqueue(callBack);

    }
    private NetworkCallBack callBack = new NetworkCallBack<CustomerAddressResponse>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("ZINGAKART Login",response.toString());
            Toast.makeText(AddAddressActivity.this,"Sign UP."+response.getData().toString(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddAddressActivity.this, MainActivity.class);
            intent.putExtra("fromAddress", "true");
            //intent.putExtra("lname", lastName.getText().toString());
            startActivity(intent);
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };


    public void callCreateOrder(){

        Shipping shipping = new Shipping(
                first_name.getText().toString(),
                lastg_name.getText().toString(),
                "",
                address.getText().toString(),
                landmrk.getText().toString(),
                citytx.getText().toString(),
                statetx.getText().toString(),
                pintx.getText().toString(),
                country.getText().toString()
                );
        List<OrderLines> items =  new ArrayList<>();
        items.add(lineItem);
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        CreateOrderRequest request = new CreateOrderRequest( Integer.parseInt(sh.getString("id", "true")),"cash","bank",true,shipping,items

                );
        try {
          //  showHud();
            ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
            Call<Order> responseCall = apiInterface.createMyOrder(request);
            responseCall.enqueue(callBack2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private NetworkCallBack callBack2 = new NetworkCallBack<Order>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            Log.d("ZINGAKART Login",response.toString());
           // hide();
//            Order order = (Order)response.getData();
//            if (isCod){
//                updateOrder(order);
//            }else {
//                PaymentActivity.setOrder(order);
//                Intent intent = new Intent(AddAddressActivity.this, PaymentActivity.class);
//                intent.putExtra("action", "add_address");
//                startActivity(intent);
//            }

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };


    OrderLines lineItem = null;

    public void callProfileApiList(){
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        int id = Integer.parseInt(sh.getString("id", "true"));
        Call<CustomerDetailResponse> responseCall = apiInterface.getCustomerProfile(id);
        responseCall.enqueue(myprofile);

    }



    private NetworkCallBack myprofile = new NetworkCallBack<CustomerDetailResponse>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("ZINGAKART",response.toString());
            CustomerDetailResponse customerDetailResponse = (CustomerDetailResponse)response.getData();
            try {
                if (product!=null){
                    lineItem = new OrderLines(

                            product.getId(),
                            1,
                            12
                    );
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };


    public void updateOrder(Order order){

        showHud();
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        int id = Integer.parseInt(sh.getString("id", "true"));
        UpdatePaymentOrderRequest request = new UpdatePaymentOrderRequest(id,"COD",false,"");
        try {
            ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
            Call<Order> responseCall = apiInterface.updateOrder(customer_order_retrieve+"/"+order.getId() ,request);
            responseCall.enqueue(updatecallBack2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private NetworkCallBack updatecallBack2 = new NetworkCallBack<Order>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("Order Places",response.getData().toString());
            Toast.makeText(AddAddressActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
            finish();

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };
}