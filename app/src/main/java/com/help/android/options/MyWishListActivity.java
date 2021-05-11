package com.help.android.options;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.help.android.R;
import com.help.android.login.CustomerDetailResponse;
import com.help.android.product.ItemDetailsActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.Images;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.Product;
import com.help.android.utility.WishListProducts;
import com.facebook.drawee.view.SimpleDraweeView;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.help.android.utility.ConstantAPIKt.getwishListProductByKey;
import static com.help.android.utility.ConstantAPIKt.product;

public class MyWishListActivity extends AppCompatActivity {
    //String key;
    private static Context mContext;
    RecyclerView recyclerView;
    List<WishListProducts> wishListData = new ArrayList<>();
    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wish_list);
        String key = getIntent().getStringExtra("shareKey");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        // Get the textview of the title
        ImageView customTitle = (ImageView) customView.findViewById(R.id.actionbarTitle);
        getSupportActionBar().setCustomView(customView);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        callApiList(key);
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

//    KProgressHUD hud  = null;
//    void   showHud(){
//        hud =  KProgressHUD.create(this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setCancellable(true)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .show();
//    }
//
//    void hide(){
//        hud.dismiss();
//    }
    public void callProfileApiList(){

       // showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        int id = Integer.parseInt(sh.getString("id", "true"));
        Call<CustomerDetailResponse> responseCall = apiInterface.getCustomerProfile(id);
        responseCall.enqueue(callBack);

    }

    public void callApiList(String key){
      //  showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<WishListProducts>> responseCall = apiInterface.getMyWishlistProduct(getwishListProductByKey+"/"+key+"/get_products");
        responseCall.enqueue(callBack);

    }

    private NetworkCallBack callBack = new NetworkCallBack<List<WishListProducts>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
          //  hide();
            Log.d("ytuytuytu",response.getData().toString());
//            Toast.makeText(getApplicationContext(),
//                    "Wishlist now"+response.getData().toString(),
//                    Toast.LENGTH_SHORT).show();
            wishListData.addAll ((List<WishListProducts>)response.getData());
            if (wishListData.size() > 0){
                for (WishListProducts w:wishListData
                ) {

                    callOrderItems(w.getProduct_id());

                }
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "No product found",
                        Toast.LENGTH_SHORT).show();
            }



        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
           // hide();
        }
    };

    public void callOrderItems(int producId){
        //showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<Product> responseCall = apiInterface.getProductDetails(product+"/"+producId);
        responseCall.enqueue(callBack2);

    }


    List<Product> productList = new ArrayList<>();

    private NetworkCallBack callBack2 = new NetworkCallBack<Product>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
             //   hide();
//            Toast.makeText(getApplicationContext(),
//                    "Product now"+response.getData().toString(),
//                    Toast.LENGTH_SHORT).show();
            productList.add((Product)response.getData());
            RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(recylerViewLayoutManager);
            recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(recyclerView, productList,MyWishListActivity.this));
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            //hide();
        }
    };

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<MyWishListActivity.SimpleStringRecyclerViewAdapter.ViewHolder> {

        private List<Product> mWishlistImageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;
            public final TextView wishTitle;
            public final TextView wishKey;
            public final TextView price;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_wishlist);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item_desc);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
                wishTitle = (TextView) view.findViewById(R.id.wishtitle);
                wishKey = (TextView) view.findViewById(R.id.wishKey);
                price = (TextView) view.findViewById(R.id.proPrice);


            }
        }
        Context context;

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, List<Product> wishlistImageUri,Context context) {
            mWishlistImageUri = wishlistImageUri;
            mRecyclerView = recyclerView;
            this.context = context;
        }

        @Override
        public MyWishListActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wishlist_item, parent, false);
            return new MyWishListActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(MyWishListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final MyWishListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {

            final Product product = mWishlistImageUri.get(position);
            List<Images> imgArr = mWishlistImageUri.get(position).getImages();
            String prodImage = imgArr.get(0).getSrc();
            final Uri uri = Uri.parse(prodImage);
            holder.mImageView.setImageURI(uri);
            holder.mImageView.setVisibility(View.VISIBLE);
            holder.wishKey.setVisibility(View.GONE);
            holder.price.setVisibility(View.VISIBLE);
            //holder.mImageViewWishlist.setVisibility(View.VISIBLE);
            holder.wishTitle.setText(mWishlistImageUri.get(position).getName());
            holder.price.setText(Html.fromHtml(mWishlistImageUri.get(position).getPrice_html(), Html.FROM_HTML_MODE_COMPACT));
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemDetailsActivity.class);
                    ItemDetailsActivity.setProduct(product);
                    intent.putExtra(STRING_IMAGE_URI, "https://www.zingakart.com/wp-content/uploads/2020/09/b19-1.png");
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    context.startActivity(intent);

                }
            });
//
//            //Set click action for wishlist
            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mWishlistImageUri.size() ;
        }
    }

}