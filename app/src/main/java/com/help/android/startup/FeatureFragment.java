package com.help.android.startup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.velectico.rbm.network.callbacks.NetworkError;
import com.help.android.R;
import com.help.android.fragments.ImageListFragment;
import com.help.android.product.ItemDetailsActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.AutoResizeTextView;
import com.help.android.utility.Catagories;
import com.help.android.utility.Images;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.Product;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeatureFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView catagory_list,feature_list,product_list;
   SliderView  sliderView;
    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    public FeatureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeatureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeatureFragment newInstance(String param1, String param2) {
        FeatureFragment fragment = new FeatureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private SliderAdapterExample adapter;

    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_feature, container, false);
        catagory_list = mView.findViewById(R.id.catagory_list);
        feature_list = mView.findViewById(R.id.feature_list);
        product_list = mView.findViewById(R.id.product_list);
        sliderView = mView.findViewById(R.id.imageSlider);
        adapter = new SliderAdapterExample();
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        catagory_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        feature_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        product_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        callApiList();
        callprodApiList();
        callCategoryApiList();
        return mView;
    }

    public void callApiList(){
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<Catagories>> responseCall = apiInterface.getCatagories(30,true,0);
        responseCall.enqueue(callBack);
    }

    List<String> titles= new ArrayList<>();
    List<Catagories> responseData = new ArrayList<>();

    private NetworkCallBack callBack = new NetworkCallBack<List<Catagories>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            responseData = (List<Catagories>) response.getData();
            catagory_list.setAdapter(new SubCatagoryListAdapter(responseData,getContext()));
            catagory_list.getAdapter().notifyDataSetChanged();


        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };
    public void callCategoryApiList(){
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<Catagories>> responseCall = apiInterface.getCatagoriesSelected(30,true,"272,273,274,276,284,1363,321,322,323,338,886");
        responseCall.enqueue(callBack2);
    }

    List<Catagories> responseData1 = new ArrayList<>();

    private NetworkCallBack callBack2 = new NetworkCallBack<List<Catagories>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            responseData1 = (List<Catagories>) response.getData();
            feature_list.setAdapter(new FeatureFragment.SimpleStringRecyclerViewAdapter2(feature_list, responseData1,getContext()));
            feature_list.getAdapter().notifyDataSetChanged();
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };

    public void callprodApiList(){

        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);

            Call<List<Product>> responseCall = apiInterface.getProductList(20,1,true);
            responseCall.enqueue(callBack3);




    }


    List<Product> responseData3 = new ArrayList<>();

    private NetworkCallBack callBack3 = new NetworkCallBack<List<Product>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {

            responseData3 = (List<Product>)response.getData();
            product_list.setAdapter(new FeatureFragment.SimpleStringRecyclerViewAdapter(product_list, responseData3,getContext()));
            product_list.getAdapter().notifyDataSetChanged();

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };
    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<FeatureFragment.SimpleStringRecyclerViewAdapter.ViewHolder> {

        private List<Product> mValues;
        private RecyclerView mRecyclerView;
        Context context;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist,img;
            public AutoResizeTextView product_name;
            public TextView product_des;
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
        public FeatureFragment.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent, false);
            return new FeatureFragment.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(FeatureFragment.SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final FeatureFragment.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {

            final Product product = mValues.get(position);
            List<Images> images = product.getImages();

            holder.product_name.setText(product.getName());

            holder.product_price.setText("INR "+product.getRegular_price());
            holder.product_sell.setText(" INR "+product.getSale_price());
            holder.product_price.setPaintFlags( holder.product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (images!=null && images.size() >0){

                Picasso.with(context).load(images.get(0).getSrc()).fit().centerInside().into(holder.img);
            }

            if (images!= null && images.size() >0){
                final Uri uri = Uri.parse(images.get(0).getSrc());
                holder.mImageView.setImageURI(uri);
            }

            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemDetailsActivity.class);
                    ItemDetailsActivity.setProduct(product);
                    context.startActivity(intent);


                }
            });


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }


    public static class SimpleStringRecyclerViewAdapter2
            extends RecyclerView.Adapter<FeatureFragment.SimpleStringRecyclerViewAdapter2.ViewHolder> {

        private List<Catagories> mValues;
        private RecyclerView mRecyclerView;
        Context context;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;

            public final ImageView img;
            public AutoResizeTextView product_name;
            public RelativeLayout mLayout;
            public TextView product_des;
            public  TextView product_price,product_sell;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                img = (ImageView) view.findViewById(R.id.img);
                mLayout = (RelativeLayout)view.findViewById(R.id.layout_item);

            }
        }

        public SimpleStringRecyclerViewAdapter2(RecyclerView recyclerView,List<Catagories> items,Context context) {
            mValues = items;
            mRecyclerView = recyclerView;
            this.context = context;
        }

        @Override
        public FeatureFragment.SimpleStringRecyclerViewAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catlist_item, parent, false);
            return new FeatureFragment.SimpleStringRecyclerViewAdapter2.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(FeatureFragment.SimpleStringRecyclerViewAdapter2.ViewHolder holder) {

        }

        @Override
        public void onBindViewHolder(final FeatureFragment.SimpleStringRecyclerViewAdapter2.ViewHolder holder, final int position) {

            final Catagories product = mValues.get(position);
            Images images = product.getImage();

            //holder.product_name.setText(product.getName());



            if (images!=null){
                Picasso.with(context).load(images.getSrc()).into(holder.img);
            }

            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ImageListFragment.setCataGoryId(product.getId());
                    MainActivity.chnageFragment(new ImageListFragment());

                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }


}