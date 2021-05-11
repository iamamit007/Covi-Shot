/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.help.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;
import com.help.android.R;
import com.help.android.product.ItemDetailsActivity;
import com.help.android.startup.MainActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.AutoResizeTextView;
import com.help.android.utility.ImageUrlUtils;
import com.help.android.utility.Images;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.Product;
import com.facebook.drawee.view.SimpleDraweeView;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class ImageListFragment extends Fragment {


    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    private static int cataGoryId = 0;
    static boolean   isFeatured = false;

    public static void setFeatured(boolean featured) {
        ImageListFragment.isFeatured = featured;
    }

    RecyclerView rv;

    public static void setCataGoryId(int cataGoryId) {
        ImageListFragment.cataGoryId = cataGoryId;
    }

    @Override
    public void onResume() {
        super.onResume();
        callApiList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       rv  = (RecyclerView) inflater.inflate(R.layout.layout_recylerview_list, container, false);

//    cataGoryId= ImageListFragment.this.getArguments().getInt("type");


        return rv;
    }
    public void callApiList(){
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        if (isFeatured){
            Call<List<Product>> responseCall = apiInterface.getProductList(20,1,isFeatured);
            responseCall.enqueue(callBack);
        }else {
            Call<List<Product>> responseCall = apiInterface.getProductList(cataGoryId,100,1,isFeatured);
            responseCall.enqueue(callBack);
        }


    }
    KProgressHUD hud  = null;
    void   showHud(){
        hud =  KProgressHUD.create(getContext())
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

    List<String> titles= new ArrayList<>();
    List<Product> responseData = new ArrayList<>();

    private NetworkCallBack callBack = new NetworkCallBack<List<Product>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("Mita",response.getData().toString());
            setupRecyclerView(rv,(List<Product>)response.getData());

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };


    private void setupRecyclerView(RecyclerView recyclerView,List<Product> products) {
      /*  if (ImageListFragment.this.getArguments().getInt("type") == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        } else if (ImageListFragment.this.getArguments().getInt("type") == 2) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 3);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }*/
//        String[] items=null;
//        if (ImageListFragment.this.getArguments().getInt("type") == 1){
//            items =ImageUrlUtils.getOffersUrls();
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 2){
//            items =ImageUrlUtils.getElectronicsUrls();
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 3){
//            items =ImageUrlUtils.getLifeStyleUrls();
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 4){
//            items =ImageUrlUtils.getHomeApplianceUrls();
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 5){
//            items =ImageUrlUtils.getBooksUrls();
//        }else {
//            items = ImageUrlUtils.getImageUrls();
      //  }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(recyclerView, products,getContext()));
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private List<Product> mValues;
        private RecyclerView mRecyclerView;
        Context context;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist,img;
            public AutoResizeTextView product_name;
            public  TextView product_des;
            public  TextView product_price,product_sell;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
                img = (ImageView) view.findViewById(R.id.img);
                product_name = (AutoResizeTextView) view.findViewById(R.id.product_name);
                product_des = (TextView) view.findViewById(R.id.descTxt);
                product_sell = (TextView) view.findViewById(R.id.product_sell);
                product_price = (TextView) view.findViewById(R.id.product_price);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView,List<Product> items,Context context) {
            mValues = items;
            mRecyclerView = recyclerView;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent, false);
            return new ViewHolder(view);
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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                layoutParams.height = 200;
            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutParams.height = 600;
            } else {
                layoutParams.height = 800;
            }*/
         //  String [] images =
            final Product product = mValues.get(position);
            List<Images> images = product.getImages();

            holder.product_name.setText(product.getName());

           holder.product_price.setText("INR "+product.getRegular_price());
            holder.product_sell.setText(" INR "+product.getSale_price());
            holder.product_price.setPaintFlags( holder.product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (images!=null && images.size() >0){
                Picasso.with(context).load(images.get(0).getSrc()).into(holder.img);
            }
//            }else {
//                Picasso.with(context).load(images.get(0).getSrc()).into(holder.img);
//
//            }



            //  holder.product_price.setText(Html.fromHtml(product.getPrice_html()));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                holder.product_des.setText(Html.fromHtml(product.getShort_description(), Html.FROM_HTML_MODE_COMPACT));
//                holder.product_price.setText(Html.fromHtml(product.getPrice_html(), Html.FROM_HTML_MODE_COMPACT));
//            } else {
//                holder.product_des.setText(Html.fromHtml(product.getShort_description()));
//                holder.product_price.setText(Html.fromHtml(product.getPrice_html()));
//            }
    if (images!= null && images.size() >0){
     final Uri uri = Uri.parse(images.get(0).getSrc());
     holder.mImageView.setImageURI(uri);
    }



           // holder.product_price.setText(product.getPrice());
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    ItemDetailsActivity.setProduct(product);
                    intent.putExtra(STRING_IMAGE_URI, "https://www.zingakart.com/wp-content/uploads/2020/09/b19-1.png");
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    mActivity.startActivity(intent);

                }
            });

            //Set click action for wishlist
            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                    imageUrlUtils.addWishlistImageUri("https://www.zingakart.com/wp-content/uploads/2020/09/b19-1.png");
                    holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_black_18dp);
                    notifyDataSetChanged();
                    Toast.makeText(mActivity,"Item added to wishlist.",Toast.LENGTH_SHORT).show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }





}
