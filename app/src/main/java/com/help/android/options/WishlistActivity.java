package com.help.android.options;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.help.android.R;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.ImageUrlUtils;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.WishList;
import com.facebook.drawee.view.SimpleDraweeView;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.help.android.utility.ConstantAPIKt.customer_wishList_retrieveById;

public class WishlistActivity extends AppCompatActivity {
    private static Context mContext;
    RecyclerView recyclerView;
    List<WishList> wishListData = new ArrayList<>();
    String usrId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recylerview_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        // Get the textview of the title
        ImageView customTitle = (ImageView) customView.findViewById(R.id.actionbarTitle);
        getSupportActionBar().setCustomView(customView);

        mContext = WishlistActivity.this;
        usrId = getIntent().getStringExtra("id");

        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
         recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        callApiList();

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


    public void callApiList(){
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<WishList>>responseCall = apiInterface.getMyWishlist(customer_wishList_retrieveById+"/"+usrId);
        responseCall.enqueue(callBack);

    }

    private NetworkCallBack callBack = new NetworkCallBack<List<WishList>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("ytuytuytu",response.getData().toString());
            wishListData.addAll ((List<WishList>)response.getData());
            RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(recylerViewLayoutManager);
            recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(recyclerView, wishListData));

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder> {

        private List<WishList> mWishlistImageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;
            public final TextView wishTitle;
            public final TextView wishKey;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_wishlist);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item_desc);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
                wishTitle = (TextView) view.findViewById(R.id.wishtitle);
                wishKey = (TextView) view.findViewById(R.id.wishKey);

            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, List<WishList> wishlistImageUri) {
            mWishlistImageUri = wishlistImageUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wishlist_item, parent, false);
            return new WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {

           // if (position == 0){
                holder.wishTitle.setText("Shopping List");
            //}
//            else {
//                holder.wishTitle.setText("Shopping List " + position + mWishlistImageUri.get(position).getShare_key());
//            }
            holder.wishKey.setText(mWishlistImageUri.get(position).getTitle());
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MyWishListActivity.class);
                    intent.putExtra("shareKey",mWishlistImageUri.get(position).getShare_key());
                    //intent.putExtra(STRING_IMAGE_POSITION, position);
                    mContext.startActivity(intent);
                }
            });
//
//            //Set click action for wishlist
//            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                    imageUrlUtils.removeWishlistImageUri(position);
//                    notifyDataSetChanged();
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return 1 ;
        }
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
