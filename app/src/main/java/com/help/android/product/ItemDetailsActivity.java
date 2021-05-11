package com.help.android.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;
import com.help.android.R;
import com.help.android.fragments.ImageListFragment;
import com.help.android.fragments.ViewPagerActivity;
import com.help.android.login.LoginPopup;
import com.help.android.notification.NotificationCountSetClass;
import com.help.android.registration.AddAddressActivity;
import com.help.android.startup.MainActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.ImageUrlUtils;
import com.help.android.utility.Images;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.Product;
import com.help.android.utility.WishList;
import com.help.android.utility.createWishlistRequestParams;
import com.help.android.utility.createWishlistResponse;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.help.android.utility.ConstantAPIKt.customer_wishList_retrieveById;
import static com.help.android.utility.ConstantAPIKt.getwishListProductByKey;

public class ItemDetailsActivity extends AppCompatActivity {
    int imagePosition;
    String stringImageUri;
    public static Product product;
    String userId = "";
    String wishsharekey = "";
    List<WishList> wishListData = new ArrayList<>();
    public static void setProduct(Product product) {
        ItemDetailsActivity.product = product;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_details);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        // Get the textview of the title
        ImageView customTitle = (ImageView) customView.findViewById(R.id.actionbarTitle);
        getSupportActionBar().setCustomView(customView);
        // Change the font family (optional)
        //customTitle.setTypeface(Typeface.MONOSPACE);
        // Set the on click listener for the title
//        customTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.w("MainActivity", "ActionBar's title clicked.");
//               finish();
//            }
//        });


        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        userId = sh.getString("id", "true");
        ImageView mImageView = (ImageView) findViewById(R.id.image1);
        TextView textViewAddToCart = (TextView)findViewById(R.id.text_action_bottom1);
        TextView textViewBuyNow = (TextView)findViewById(R.id.text_action_bottom2);
        TextView prodName = (TextView)findViewById(R.id.prodName);
        TextView prodPrice = (TextView)findViewById(R.id.prodPrice);
        TextView descTxt = (TextView)findViewById(R.id.descTxt);
        TextView prodSalePrice = (TextView)findViewById(R.id.prodSalePrice);
        Spinner sizeSpinner = (Spinner)findViewById(R.id.sizeList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (product!=null && product.getImages()!=null){
            List<Images> imgArr = product.getImages();
            final String prodImage = imgArr.get(0).getSrc();
            Picasso.with(this).load(prodImage).into(mImageView);
            prodName.setText(product.getName());
           // Uri uri = Uri.parse(prodImage);
            //mImageView.setImageURI(uri);
      //      prodPrice.setText(product.getRegular_price());
            prodSalePrice.setText("Rs. "+product.getSale_price());
            prodPrice.setPaintFlags(prodPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            descTxt.setText(Html.fromHtml(product.getShort_description(), Html.FROM_HTML_MODE_COMPACT));

            //Getting image uri from previous screen
            if (getIntent() != null) {
                stringImageUri = getIntent().getStringExtra(ImageListFragment.STRING_IMAGE_URI);
                imagePosition = getIntent().getIntExtra(ImageListFragment.STRING_IMAGE_URI,0);
            }
            String x = product.getAverage_rating();
        }
        callApiList();

//        Array op = attrArr.get(1).getOptions();
//        Toast.makeText(ItemDetailsActivity.this,"Size"+op,Toast.LENGTH_SHORT).show();






        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPagerActivity.setProduct1(product);
                    Intent intent = new Intent(ItemDetailsActivity.this, ViewPagerActivity.class);
                    intent.putExtra("position", imagePosition);
                    startActivity(intent);

            }
        });

        textViewAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                imageUrlUtils.addCartListImageUri(stringImageUri);
                Toast.makeText(ItemDetailsActivity.this,"Item added to cart.",Toast.LENGTH_SHORT).show();
                MainActivity.notificationCountCart++;
                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
            }
        });

        textViewBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh
                        = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                String uid = sh.getString("id", "true");
                if (uid == "true") {
                    LoginPopup popUpClass = new LoginPopup(getBaseContext());
                    popUpClass.showPopupWindow(view);
                }
                else{
                    Intent intent = new Intent(ItemDetailsActivity.this, AddAddressActivity.class);
                    AddAddressActivity.setProduct(product);
                    intent.putExtra("fromScreen", "frompoductDetail");
                    intent.putExtra("action", "billing");
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),
                            "Buy now",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView share = (TextView)findViewById(R.id.text_action1);
        TextView wishlist = (TextView)findViewById(R.id.text_action3);
        share.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        product.getPermalink());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        wishlist.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               callCreateWishApiList();
            }
        });

    }





    public void callCreateWishApiList(){
        showHud();
        createWishlistRequestParams param = new createWishlistRequestParams(product.getId(),userId);
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<createWishlistResponse>> responseCall = apiInterface.createWishList(getwishListProductByKey+"/"+wishsharekey+"/add_product",param);
        responseCall.enqueue(callBack);

    }

    private NetworkCallBack callBack = new NetworkCallBack<List<createWishlistResponse>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("ytuytuytu",response.getData().toString());
            Toast.makeText(getApplicationContext(),
                    "Wishlist now"+response.getData().toString(),
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };

    public void callApiList(){
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<WishList>>responseCall = apiInterface.getMyWishlist(customer_wishList_retrieveById+"/"+userId);
        responseCall.enqueue(callBack2);

    }

    private NetworkCallBack callBack2 = new NetworkCallBack<List<WishList>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("ytuytuytu",response.getData().toString());
            wishListData.addAll ((List<WishList>)response.getData());
            wishsharekey = wishListData.get(0).getShare_key();
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
