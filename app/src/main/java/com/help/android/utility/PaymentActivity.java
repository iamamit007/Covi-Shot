package com.help.android.utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.help.android.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import retrofit2.Call;

import static com.help.android.utility.ConstantAPIKt.customer_order_retrieve;

public class PaymentActivity extends Activity implements PaymentResultListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();

    public  static  Order order;
TextView amount;
    public static void setOrder(Order order) {
        PaymentActivity.order = order;
    }

    Checkout checkout = new Checkout();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);
        checkout.setKeyID("rzp_live_ILgsfZCZoFIKMb");
        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(getApplicationContext());


        // Payment button created by you in XML layout
        Button button = (Button) findViewById(R.id.btn_pay);
        amount = (TextView) findViewById(R.id.amount);
        if (order.getTotal()!=null){
            amount.setText("INR "+order.getTotal());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });

        TextView privacyPolicy = (TextView) findViewById(R.id.txt_privacy_policy);

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse("https://razorpay.com/sample-application/"));
                startActivity(httpIntent);
            }
        });
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", 100);
            JSONObject preFill = new JSONObject();
          //  preFill.put("email", "test@razorpay.com");
          //  preFill.put("contact", "9876543210");

           // options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            callCreateOrder(razorpayPaymentID);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }



    public void callCreateOrder(String transactionId){



        UpdatePaymentOrderRequest request = new UpdatePaymentOrderRequest(14,"razorpay",true,transactionId);
        try {
            ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
            Call<Order> responseCall = apiInterface.updateOrder(customer_order_retrieve+"/"+order.getId() ,request);
            responseCall.enqueue(callBack2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private NetworkCallBack callBack2 = new NetworkCallBack<Order>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            Log.d("Order Places",response.getData().toString());
            Toast.makeText(PaymentActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };
    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }
}
