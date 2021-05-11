package com.help.android.options;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.help.android.utility.Order;
import com.help.android.utility.Product;
import com.facebook.drawee.view.SimpleDraweeView;
import com.velectico.rbm.network.callbacks.NetworkError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.help.android.utility.ConstantAPIKt.customer_order_retrieve;
import static com.help.android.utility.ConstantAPIKt.product;

public class CartListActivity extends AppCompatActivity {
    private static Context mContext;

    public static String screenName;

    public static void setScreenName(String screenName) {
        CartListActivity.screenName = screenName;
    }
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        mContext = CartListActivity.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        // Get the textview of the title
        ImageView customTitle = (ImageView) customView.findViewById(R.id.actionbarTitle);
        getSupportActionBar().setCustomView(customView);

        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
        ArrayList<String> cartlistImageUri =imageUrlUtils.getCartListImageUri();
        //Show cart layout based on items
        setCartLayout();

         recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(recylerViewLayoutManager);

        callApiList();
    }


    public void setRecyclerView(List<Order> orderList){
        CartListActivity.SimpleStringRecyclerViewAdapter  adapter= new CartListActivity.SimpleStringRecyclerViewAdapter(recyclerView, orderList);
        recyclerView.setAdapter(adapter);
        recyclerView.getAdapter().notifyDataSetChanged();
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
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        int id = Integer.parseInt(sh.getString("id", "true"));
                ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<Order>> responseCall = apiInterface.getMyOrder(Integer.parseInt(sh.getString("id", "true")));
        responseCall.enqueue(callBack);

    }

    List<String> titles= new ArrayList<>();
    List<Product> responseData = new ArrayList<>();
    List<Order> orderData = new ArrayList<>();

    private NetworkCallBack callBack = new NetworkCallBack<List<Order>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            Log.d("Mita",response.getData().toString());
//            Toast.makeText(getApplicationContext(),
//                    "Order now"+response.getData(),
//                    Toast.LENGTH_SHORT).show();
           orderData.addAll ((List<Order>)response.getData());
//            Toast.makeText(getApplicationContext(),
//                    "Order now"+orderData.size(),
//                    Toast.LENGTH_SHORT).show();
            setRecyclerView(orderData);
            for (Order o:orderData
                 ) {
              //  callOrderItems(o.getLine_items().get(0).getId());

            }

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };




    public void callOrderItems(int producId){
        hide();
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<Product> responseCall = apiInterface.getProductDetails(product+"/"+producId);
        responseCall.enqueue(callBack2);

    }



    private NetworkCallBack callBack2 = new NetworkCallBack<Product>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            responseData.add((Product)response.getData());
            hide();
            CartListActivity.SimpleStringRecyclerViewAdapter  adapter =  (CartListActivity.SimpleStringRecyclerViewAdapter) recyclerView.getAdapter() ;
            if (adapter !=null){
                adapter.setProductList(responseData);
                adapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();
        }
    };


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder> {

       // private ArrayList<String> mCartlistImageUri;
        private RecyclerView mRecyclerView;

        public List<Product> productList = new ArrayList<>();
        public List<Order> orderList = new ArrayList<>();

        public void setProductList(List<Product> productList) {
            this.productList = productList;
        }

        public Product getProductById(int Prodid){
            Product res = null;
            for (Product p:
                    productList
                 ) {
                if (p.getId() == Prodid){
                    res =  p;
                }

            }
            return res;
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem, mLayoutRemove , mLayoutEdit,layout_action1;
            public final TextView order_stat, address,orderId;
            public final TextView   name,quantity,price;



            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_cartlist);
                layout_action1 = (LinearLayout) view.findViewById(R.id.layout_action1);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item_desc);
                mLayoutRemove = (LinearLayout) view.findViewById(R.id.action);
                mLayoutEdit = (LinearLayout) view.findViewById(R.id.layout_action2);
                order_stat = (TextView) view.findViewById(R.id.order_stat);
                address = (TextView) view.findViewById(R.id.address);
                name = (TextView) view.findViewById(R.id.name);
                orderId = (TextView) view.findViewById(R.id.orderId);
                quantity = (TextView) view.findViewById(R.id.quantity);
                price = (TextView) view.findViewById(R.id.price);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, List<Order> orderList) {
            this.orderList = orderList;
            mRecyclerView = recyclerView;
        }

        @Override
        public CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_item, parent, false);
            return new CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            //final Uri uri = Uri.parse("https://static.pexels.com/photos/7093/coffee-desk-notes-workspace-medium.jpg");
            //holder.mImageView.setImageURI(uri);
            final Order order = orderList.get(position);

            if (screenName.equalsIgnoreCase("my_orders")){
                holder.layout_action1.setVisibility(View.GONE);
                holder.mLayoutRemove.setVisibility(View.GONE);
                holder.mLayoutItem.setVisibility(View.VISIBLE);
                holder.orderId.setText("Order Id - # "+order.getId());
            }
            holder.name.setText(order.getLine_items().get(0).getName());
            holder.price.setText("₹ "+order.getTotal());
            holder.order_stat.setText(order.getStatus());
            holder.quantity.setText(("Qty: "+order.getLine_items().get(0).getQuantity()));
            holder.address.setText(order.getShipping().getFirst_name()+", "+order.getShipping().getLast_name()
                    +", "+order.getShipping().getCompany()+", "+order.getShipping().getAddress_1()
                    +", "+order.getShipping().getAddress_2()+", "+order.getShipping().getCity()
                    +", "+order.getShipping().getState()+", "+order.getShipping().getPostcode()
                    +", "+order.getShipping().getCountry());

            Product p = getProductById(order.getLine_items().get(0).getProduct_id());
            try {
                if (p !=null){
                    holder.name.setText(p.getName());
                    holder.price.setText(p.getPrice());

                }
                if (order.getLine_items()!=null){
                    holder.quantity.setText((order.getLine_items().get(0).getQuantity()));
                    holder.order_stat.setText(order.getStatus());
                    holder.order_stat.setText(order.getShipping().getAddress_1());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderDetailActivity.setOrder(order);
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    mContext.startActivity(intent);
                }
            });

           //Set click action
            holder.mLayoutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//             Toast.makeText(mContext,
//                            "Wishlist now"+order.getId(),
//                            Toast.LENGTH_SHORT).show();
                    callDeleteOrderList(order.getId());
                }
            });

            //Set click action
            holder.mLayoutEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OrderDetailActivity.setOrder(order);
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }




        public void callDeleteOrderList(int orderId){

            ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
            Call<Order>responseCall = apiInterface.cancelmyOrder(customer_order_retrieve+"/"+orderId +"?force=true");
            responseCall.enqueue(callBack3);

        }

        private NetworkCallBack callBack3 = new NetworkCallBack<Order>() {
            @Override
            public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
                Toast.makeText(mContext,
                        "Wishlist now"+response.getData().toString(),
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

            }
        };
    }

    protected void setCartLayout(){
        LinearLayout layoutCartItems = (LinearLayout) findViewById(R.id.layout_items);
        LinearLayout layoutCartPayments = (LinearLayout) findViewById(R.id.layout_payment);
        LinearLayout layoutCartNoItems = (LinearLayout) findViewById(R.id.layout_cart_empty);

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
