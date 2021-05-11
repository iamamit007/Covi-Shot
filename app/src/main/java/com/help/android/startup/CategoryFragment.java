package com.help.android.startup;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;
import com.help.android.R;
import com.help.android.fragments.ImageListFragment;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.Catagories;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class CategoryFragment extends Fragment {

    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    private static int cataGoryId;

    RecyclerView rv;


    public static void setCataGoryId(int cataGoryId) {
        CategoryFragment.cataGoryId = cataGoryId;
    }
    // TODO: Rename and change types and number of parameters


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
        return rv;
    }
    KProgressHUD hud  = null;
    void   showHud(){
        hud =  KProgressHUD.create(getActivity())
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
        Call<List<Catagories>> responseCall = apiInterface.getCatagories(30,true,cataGoryId);
        responseCall.enqueue(callBack2);
    }

    List<Catagories> wishListData = new ArrayList<>();
    private NetworkCallBack callBack2 = new NetworkCallBack<List<Catagories>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            List<Catagories>  child = (List<Catagories>) response.getData();
            if (child.size()>0){
                wishListData.addAll ((List<Catagories>)response.getData());
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                rv.setLayoutManager(layoutManager);
                rv.setAdapter(new SimpleStringRecyclerViewAdapter(rv, wishListData,getContext()));
            }
            else {
                Toast.makeText(getActivity(),"Sorry No data Available on this catagory",Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private List<Catagories> mWishlistImageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public TextView product_name;
            public  TextView product_des;
            public  TextView product_price;
            public final LinearLayout mLayoutItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image1);
                product_name = (TextView) view.findViewById(R.id.product_name);
                product_des = (TextView) view.findViewById(R.id.product_des);
                product_price = (TextView) view.findViewById(R.id.product_price);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);

            }
        }
        Context context;
        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, List<Catagories> wishlistImageUri, Context context) {
            mWishlistImageUri = wishlistImageUri;
            mRecyclerView = recyclerView;
            this.context = context;

        }

        @Override
        public SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(SimpleStringRecyclerViewAdapter.ViewHolder holder) {
//            if (holder.mImageView.getController() != null) {
//                holder.mImageView.getController().onDetach();
//            }
//            if (holder.mImageView.getTopLevelDrawable() != null) {
//                holder.mImageView.getTopLevelDrawable().setCallback(null);
////                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
//            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Catagories product = mWishlistImageUri.get(position);
            holder.product_name.setText(product.getName().replace("&amp;", "&"));
            holder.product_des.setVisibility(View.GONE);
            holder.product_price.setVisibility(View.GONE);
            if (product.getImage()!= null) {
                Picasso.with(context).load(product.getImage().getSrc()).into(holder.mImageView);

            }
            else{
               // holder.mImageView.setImageURI(Uri.parse("https://cdn.fcglcdn.com/brainbees/images/products/438x531/3569032a.jpg"));
            }
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity!=null){

                        ImageListFragment.setCataGoryId(product.getId());
                        mActivity.cnageFragment(new ImageListFragment());

                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mWishlistImageUri.size() ;
        }
    }
}