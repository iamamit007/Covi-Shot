package com.help.android.fragments;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.help.android.R;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.Catagories;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.velectico.rbm.network.callbacks.NetworkError;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class CategoryActivity extends AppCompatActivity {
    private static Context mContext;
    RecyclerView recyclerView;
    public  static CategoryActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        int catid = getIntent().getIntExtra("catgId",0);
        Toast.makeText(this,"catid."+catid,Toast.LENGTH_SHORT).show();
        callApiList(catid);
    }

    public void callApiList(int catid){
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<Catagories>> responseCall = apiInterface.getCatagories(30,true,17);
        responseCall.enqueue(callBack2);
    }

    List<Catagories> wishListData = new ArrayList<>();
    private NetworkCallBack callBack2 = new NetworkCallBack<List<Catagories>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
           List<Catagories>  child = (List<Catagories>) response.getData();
            Log.d("yyyyyyyyyyyyyyyyyyy",child.toString());
            wishListData.addAll ((List<Catagories>)response.getData());
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new CategoryActivity.SimpleStringRecyclerViewAdapter(recyclerView, wishListData));

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<CategoryActivity.SimpleStringRecyclerViewAdapter.ViewHolder> {

        private List<Catagories> mWishlistImageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
           public final SimpleDraweeView mImageView;
            public  TextView product_name;
            public  TextView product_des;
            public  TextView product_price;
            public final LinearLayout mLayoutItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);
                product_name = (TextView) view.findViewById(R.id.product_name);
                product_des = (TextView) view.findViewById(R.id.product_des);
                product_price = (TextView) view.findViewById(R.id.product_price);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);

            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, List<Catagories> wishlistImageUri) {
            mWishlistImageUri = wishlistImageUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public CategoryActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new CategoryActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(CategoryActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final CategoryActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            final Catagories product = mWishlistImageUri.get(position);
            holder.product_name.setText(product.getName());
            holder.product_des.setVisibility(View.GONE);
            holder.product_price.setVisibility(View.GONE);

            if (product.getImage()!= null) {
                final Uri uri = Uri.parse(product.getImage().getSrc());
                holder.mImageView.setImageURI(uri);
            }
            else{
            holder.mImageView.setImageURI(Uri.parse("https://cdn.fcglcdn.com/brainbees/images/products/438x531/3569032a.jpg"));
            }
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (activity!=null){
//                        ImageListFragment.setCataGoryId(product.getId());
//                        activity.cnageFragment(new ImageListFragment());
//
//                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mWishlistImageUri.size() ;
        }
    }
    public  void cnageFragment(Fragment fragment){
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.add(R.id.container,fragment, "NewFragmentTag");
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

   }