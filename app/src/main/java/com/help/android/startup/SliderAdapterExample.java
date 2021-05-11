package com.help.android.startup;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.help.android.R;
import com.help.android.utility.Catagories;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH>{


    List<Catagories> catagories = new ArrayList<>();
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

       // SliderItem sliderItem = mSliderItems.get(position);

   //     viewHolder.textViewDescription.setText(sliderItem.getDescription());
     //   viewHolder.textViewDescription.setTextSize(16);
      //  viewHolder.textconViewDescription.setTextColor(Color.WHITE);

//        Glide.with(viewHolder.itemView)
//                .load("https://static.pexels.com/photos/5949/food-nature-autumn-nuts-medium.jpg")
//                .fitCenter()
//                .into(viewHolder.imageViewBackground);

       if (position == 0){
           Drawable res = viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.ac);
           viewHolder.imageViewBackground.setImageDrawable(res);

       }else if (position == 1){
           Drawable res = viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.phone);
           viewHolder.imageViewBackground.setImageDrawable(res);

       }else {
           Drawable res = viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.fridge);
           viewHolder.imageViewBackground.setImageDrawable(res);

       }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getCount() {
        return 3;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
