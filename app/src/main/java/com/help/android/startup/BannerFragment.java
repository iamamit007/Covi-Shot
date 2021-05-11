package com.help.android.startup;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.help.android.R;
import com.help.android.fragments.ImageListFragment;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.Catagories;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.velectico.rbm.network.callbacks.NetworkError;
import com.help.android.utility.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BannerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    boolean isAll = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public  static BannerFragment bannerFragment;

    public BannerFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BannerFragment newInstance(String param1, String param2) {
        BannerFragment fragment = new BannerFragment();
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

    View mview;
    RecyclerView catagory_list;
    SliderView sliderView;
    private SliderAdapterExample adapter;
    RelativeLayout con;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview =  inflater.inflate(R.layout.fragment_banner, container, false);
        bannerFragment = this;
        catagory_list = mview.findViewById(R.id.catagory_list);
        sliderView = mview.findViewById(R.id.imageSlider);
        con = mview.findViewById(R.id.con);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        catagory_list.setLayoutManager(layoutManager);


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


        managefromLocaldb();
        if (!isAll){
            ImageListFragment.setCataGoryId(0);
            ImageListFragment.setFeatured(true);
            changeChildFragment(new ImageListFragment());
        }

        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
            }
        });



        return mview;
    }

public void changeChildFragment(Fragment fragment){
    Fragment f = getChildFragmentManager().findFragmentById(R.id.con);

    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

   // if (f == null) {
        Log.d(TAG, "onCreateView: fragment doesn't exist");
        transaction.add(R.id.con, fragment);
//    } else {
//        Log.d(TAG, "onCreateView: fragment already exists");
//        transaction.replace(R.id.placeholder, f);
//    }
    transaction.commit();
}

    public  void managefromLocaldb(){
      SessionManager manager = new SessionManager(getContext());
       String x =  manager.getMainCat();
        try {
            JSONArray arr = new JSONArray(x);
            if (arr.length() == 0){
                callApiList();
            }else {
                Gson gson = new Gson();
                for (int i = 0;i<arr.length();i++){
                    responseData.add(gson.fromJson(arr.get(i).toString(), Catagories.class));
                }
                if (isAll){
                    catagory_list.setAdapter(new CatagoryListAdapter(responseData,getContext()));
                    catagory_list.getAdapter().notifyDataSetChanged();
                }


                JSONArray j = manager.getSUBCatJ();
                if (j.length()>0){
                    for (int i = 0;i<j.length();i++){
                        child.add(gson.fromJson(j.get(i).toString(),Catagories.class));
                    }


                    if (isAll){
                        CatagoryListAdapter adapter =(CatagoryListAdapter) bannerFragment.catagory_list.getAdapter();

                        adapter.setChildlist(child);
                        adapter.notifyDataSetChanged();
                    }
                    //   /HashMap<Integer,List<Catagories>> map= (HashMap<Integer, List<Catagories>>) manager.getSUBCat();

                }


//                if (map.size()>0){
//                    if (adapter!=null){
//
//                            adapter.;
//                            adapter.notifyDataSetChanged();
//
//                    }
//                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }











public  static  class CatagoryListAdapter extends   RecyclerView.Adapter<CatagoryListAdapter.ViewHolder>{

   public List<Catagories> list =  new ArrayList<>();
   public List<Catagories> childlist =  new ArrayList<>();
   Context context;

    public CatagoryListAdapter(List<Catagories> list,Context context) {
        this.list = list;
        this.context = context;


    }

    public void setChildlist(List<Catagories> childlist) {
        this.childlist = childlist;
    }

    public void setSublist(HashMap<Integer, List<Catagories>> sublist) {
        this.sublist = sublist;
    }

    HashMap<Integer ,List<Catagories>> sublist = new HashMap<>();

    public void addMember(int id,List<Catagories> val){
        sublist.put(id,val);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final SimpleDraweeView mImageView;



        public  TextView product_name;
       // public  TextView product_des;
        public  TextView product_price;
        public  RelativeLayout view_all;
        public  RecyclerView sub_cat;


        public ViewHolder(View view) {
            super(view);
            mView = view;
           // mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);
            product_name = (TextView) view.findViewById(R.id.catagory_name);
           // view_all = (RelativeLayout) view.findViewById(R.id.view_all);
            sub_cat = (RecyclerView) view.findViewById(R.id.sub_cat);


        }
    }

    @NonNull
    @Override
    public CatagoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catagory_item, parent, false);
        return new CatagoryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatagoryListAdapter.ViewHolder holder, int position) {

        Catagories catagories = list.get(position);
        String n = catagories.getName().replace("&amp;", "&");

        holder.product_name.setText(n);


        holder.sub_cat.setLayoutManager(new LinearLayoutManager(holder.sub_cat.getContext(), LinearLayoutManager.HORIZONTAL, true));
        if (sublist.get(catagories.getId())!=null){

            holder.sub_cat.setAdapter(new SubCatagoryListAdapter(sublist.get(catagories.getId()),context));
            holder.sub_cat.getAdapter().notifyDataSetChanged();
        }else {
            if (childlist.size()>0){
                List<Catagories> c = getItemById(catagories.getId());
                if (c !=null && c.size()>0){
                    holder.sub_cat.setAdapter(new SubCatagoryListAdapter(c,context));
                    holder.sub_cat.getAdapter().notifyDataSetChanged();
                }else {
                    BannerFragment.callChildApiList(catagories.getId(),position);
                }

            }else {
                BannerFragment.callChildApiList(catagories.getId(),position);

            }
        }

//        if (catagories.getImage()!=null){
//            final Uri uri = Uri.parse(catagories.getImage());
//            holder.mImageView.setImageURI(uri);
//        }else {
//            final Uri uri = Uri.parse("https://static.pexels.com/photos/5949/food-nature-autumn-nuts-medium.jpg");
//            holder.mImageView.setImageURI(uri);
//
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public List<Catagories> getItemById(int x){
        List<Catagories> catagories = new ArrayList<>();
        for (Catagories c:childlist
             ) {
            if (c!=null && c.getParent()== x){
               catagories.add(c);
            }
        }
        return catagories;
    }
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

            catagory_list.setAdapter(new CatagoryListAdapter(responseData,getContext()));
            catagory_list.getAdapter().notifyDataSetChanged();

//            for (Catagories i:responseData) {
//                titles.add(i.getName());
//                if (viewPager != null) {
//                    setupViewPager(viewPager);
//                    tabLayout.setupWithViewPager(viewPager);
//                }
//            }

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };










  public static   List<Catagories>  child = new ArrayList();
    public static void callChildApiList(int parentId,int layoutPosition){
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<Catagories>> responseCall = apiInterface.getCatagories(30,true,parentId);
        responseCall.enqueue(callBack2);
    }
    private static NetworkCallBack callBack2 = new NetworkCallBack<List<Catagories>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
                 child = (List<Catagories>) response.getData();
                Log.d("vvvvvv",child.toString());
                CatagoryListAdapter adapter =(CatagoryListAdapter) bannerFragment.catagory_list.getAdapter();
                if (child != null && child.size()>0){
                    adapter.addMember(child.get(0).getParent(),child);
                    adapter.notifyDataSetChanged();
                }




        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };






}