package com.help.android.startup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.help.android.R;
import com.help.android.utility.Catagories;
import com.help.android.utility.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

public    class SubCatagoryListAdapter extends   RecyclerView.Adapter<SubCatagoryListAdapter.ChildViewHolder>{
    public List<Catagories> list =  new ArrayList<>();
    Context context;
    public SubCatagoryListAdapter(List<Catagories> list, Context context) {
        this.list = list;
        this.context = context;

    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;



        public TextView product_name;
        // public  TextView product_des;
        public  TextView product_price;
        public RelativeLayout view_all;
        public LinearLayout con;


        public ChildViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.image1);
            product_name = (TextView) view.findViewById(R.id.catagory_name);
            view_all = (RelativeLayout) view.findViewById(R.id.view_all);
            con = (LinearLayout) view.findViewById(R.id.con);


        }
    }

    @NonNull
    @Override
    public SubCatagoryListAdapter.ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_catagory_item, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {

        String[] urls = ImageUrlUtils.getLifeStyleUrls();
      int x = (int) (Math.random() * ( (urls.length-1)-0 ));
        final Catagories catagories = list.get(position);
        String n = catagories.getName().replace("&amp;", "&");

        holder.product_name.setText(n);
        if (list.size() == 1){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 25;
            holder.con.setLayoutParams(params);


        }
        holder.con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryFragment.setCataGoryId(catagories.getId());
                MainActivity.chnageFragment(new CategoryFragment());

            }
        });
        if (catagories.getImage()!=null){
                Picasso.with(context).load(catagories.getImage().getSrc()).into(holder.mImageView);
            //final Uri uri = Uri.parse(catagories.getImage().getSrc());
            //holder.mImageView.setImageURI(uri);
        }else {

            if (catagories.getName().equalsIgnoreCase("Men")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cat1));

            }else if(catagories.getName().equalsIgnoreCase("Women")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.women));


            }
            else if(catagories.getName().startsWith("Baby")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.baby));


            }else if(catagories.getName().equalsIgnoreCase("Women")){

            }else if(catagories.getName().startsWith("Home")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ho));


            }else if(catagories.getName().startsWith("Elect")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.el));


            }else if(catagories.getName().startsWith("Sports")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fit));


            }else if(catagories.getName().startsWith("Tea")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tea));


            }else if(catagories.getName().startsWith("Daily")){
                holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.daily));

            }



            //Picasso.with(context).load(urls[x]).into(holder.mImageView);


        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
