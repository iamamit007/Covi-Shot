package com.help.android.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.help.android.R;
import com.help.android.fragments.ImageListFragment;
import com.help.android.login.LoginPopup;
import com.help.android.options.CartListActivity;
import com.help.android.options.MyAccountActivity;
import com.help.android.options.WishlistActivity;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.Catagories;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.velectico.rbm.network.callbacks.NetworkError;
import com.help.android.utility.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int notificationCountCart = 0;
    static ViewPager viewPager;
    static TabLayout tabLayout;
    String usrId = "";

    Button loginBtn;
    NavigationView navigationView;
    public  static  MainActivity activity;

    DB snappydb =null;
    SessionManager manager;
    ImageView iv;
    TextView username;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        manager = new SessionManager(this);
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

        String fname = sh.getString("firstName", "");
        String lname = sh.getString("lastName", "");
        String id = sh.getString("id", "true");
        usrId = id;
        String image = sh.getString("image", "");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        // Get the textview of the title
        ImageView customTitle = (ImageView) customView.findViewById(R.id.actionbarTitle);


        // Change the font family (optional)
        //customTitle.setTypeface(Typeface.MONOSPACE);
        // Set the on click listener for the title
        customTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("MainActivity", "ActionBar's title clicked.");
                cnageFragment(new BannerFragment());
            }
        });
        // Apply the custom view
        getSupportActionBar().setCustomView(customView);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
         iv = (ImageView) headerView.findViewById(R.id.imageView);
         username = (TextView) headerView.findViewById(R.id.navtitle);
        loginBtn = (Button) headerView.findViewById(R.id.newlog);
        callApiList();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginPopup popUpClass = new LoginPopup(getBaseContext());
                popUpClass.showPopupWindow(getWindow().getDecorView().findViewById(android.R.id.content)
                );
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (usrId == "true") {
            iv.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
        else{
            iv.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
            username.setText(fname +" "+ lname);
            Uri uri = Uri.parse(image);
            iv.setImageURI(uri);
        }

        String data = getIntent().getStringExtra("fromAddress");
        if (data != null){
            loginBtn.setPressed(true);
        }


        try{
            snappydb= DBFactory.open(MainActivity.this);

        }
        catch (SnappydbException e) {
            e.printStackTrace();}
     //   cnageFragment(new BannerFragment());
        cnageFragment(new FeatureFragment());

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }


    public  void cnageFragment(Fragment fragment){
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.add(R.id.container,fragment, "NewFragmentTag");
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }
    public static void chnageFragment(Fragment fragment){
        final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

        ft.add(R.id.container,fragment, "NewFragmentTag");
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }
    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String fname = sh.getString("firstName", "");
        String lname = sh.getString("lastName", "");
        String id = sh.getString("id", "true");
        usrId = id;
        if (usrId == "true") {
            iv.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
        else{
            iv.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
            username.setText(fname +" "+ lname);
//            Uri uri = Uri.parse(image);
//            iv.setImageURI(uri);
        }


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
           onResume();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    public void callApiList(){
        showHud();
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<Catagories>> responseCall = apiInterface.getCatagories(30,true,0);
        responseCall.enqueue(callBack);
    }

    List<String> titles= new ArrayList<>();
    List<String> ids= new ArrayList<>();
    List<Catagories> responseData = new ArrayList<>();


    private NetworkCallBack callBack = new NetworkCallBack<List<Catagories>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            hide();
            responseData = (List<Catagories>) response.getData();
            Menu menu = navigationView.getMenu();


            Gson gson = new Gson();
            JSONArray arr = new JSONArray();
////transform a java object to json

////Transform a json to java object
//            String json = string_json;
//            List<Object> lstObject = gson.fromJson(json_ string, Object.class);

            for (Catagories i:responseData) {
                arr.put(gson.toJson(i));
                titles.add(""+i.getName());
                ids.add(""+i.getId());
                callChildApiList(i.getId());

            }


            try {
                snappydb=DBFactory.open(MainActivity.this);
                snappydb.put("main_cat",arr.toString());
                snappydb.close();
                for (String s:titles
                ) {
                    String f = s.replace("&amp;", "&");
                    String f1 = f.replace("?", "");
                    navigationView.getMenu().findItem(R.id.cata).getSubMenu().add(f1).setIcon(R.drawable.ic_local_offer_black_24dp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {
            hide();

        }
    };


    public  void callChildApiList(int parentId){
        ApiInterface apiInterface = ApiClient.getInstance().getClient().create(ApiInterface.class);
        Call<List<Catagories>> responseCall = apiInterface.getCatagories(30,true,parentId);
        responseCall.enqueue(callBack2);
    }
    private  NetworkCallBack callBack2 = new NetworkCallBack<List<Catagories>>() {
        @Override
        public void onSuccessNetwork(@Nullable Object data, @NotNull NetworkResponse response) {
            List<Catagories>   responseData1= (List<Catagories>) response.getData();


            Gson gson = new Gson();

////transform a java object to json

////Transform a json to java object
//            String json = string_json;
//            List<Object> lstObject = gson.fromJson(json_ string, Object.class);



            try {

                HashMap<Integer,String> mapTodb = new HashMap<>();
                HashMap<Integer,JSONArray> immap = new HashMap<>();
               JSONArray jsonArray = manager.getSUBCatJ();

                for (Catagories c:responseData1
                     ) { jsonArray.put(gson.toJson(c));
                }



//
//                jsonArray.put(responseData1);
////                if (subcat.size() == 0){
////
////                }else {
//                    Iterator it = subcat.entrySet().iterator();
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry)it.next();
//                        JSONArray arr = new JSONArray();
//                        int key = (int) pair.getKey();
//                        List<Catagories> v = (List<Catagories>) pair.getValue();
//                        for (int i = 0;i< v.size();i++){
//                            arr.put(gson.toJson(v.get(i)));
//                        }
//                        immap.put(key,arr);
//                        System.out.println(pair.getKey() + " = " + pair.getValue());
//                        it.remove(); // avoids a ConcurrentModificationException
//                    }
//                    Iterator it2 = immap.entrySet().iterator();
//                    while (it2.hasNext()) {
//                        Map.Entry pair = (Map.Entry)it2.next();
//                        int key = (int) pair.getKey();
//                        JSONArray v = (JSONArray) pair.getValue();
//                        mapTodb.put(key,v.toString());
//                        it2.remove(); // avoids a ConcurrentModificationException
//                    }
//           //     }

                snappydb=DBFactory.open(MainActivity.this);
                snappydb.put("sub_cat",jsonArray.toString());
                snappydb.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

        }
    };




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        return super.onPrepareOptionsMenu(menu);
//        MenuItem item = menu.findItem(R.id.action_cart);
//        NotificationCountSetClass.setAddToCart(MainActivity.this, item,notificationCountCart);
//        // force the ActionBar to relayout its MenuItems.
//        // onCreateOptionsMenu(Menu) will be called again.
//        invalidateOptionsMenu();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(MainActivity.this,"Home",Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_search) {
            Toast.makeText(MainActivity.this,"Coming Soon.",Toast.LENGTH_SHORT).show();

            //  startActivity(new Intent(MainActivity.this, SearchResultActivity.class));
            return true;
        }else if (id == R.id.action_cart) {
            if (checkLoggedin()==true){
                Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
                intent.putExtra("id",usrId);
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this,"Please login to continue",Toast.LENGTH_SHORT).show();
            }
            return true;
        }else {
            Toast.makeText(MainActivity.this,"Coming Soon.",Toast.LENGTH_SHORT).show();
           // startActivity(new Intent(MainActivity.this, EmptyActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager(),titles);
        for (Catagories i:responseData
        ) {
//            ImageListFragment fragment = new ImageListFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("type", i.getId());
//            fragment.setArguments(bundle);
//            adapter.addFragment(fragment,i.getName());
        }

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Check if this is the page you want.
            }
        });

//        ImageListFragment fragment = new ImageListFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("type", 1);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_1));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 2);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_2));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 3);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_3));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 4);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_4));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 5);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_5));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 6);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_6));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item1) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_item2) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_item3) {
            viewPager.setCurrentItem(2);
        } else if (id == R.id.nav_item4) {
            viewPager.setCurrentItem(3);
        } else if (id == R.id.nav_item5) {
            viewPager.setCurrentItem(4);
        }else if (id == R.id.nav_item6) {
            viewPager.setCurrentItem(5);
        }else if (id == R.id.my_wishlist) {
            if (checkLoggedin()==true){
                Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
                intent.putExtra("id",usrId);
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this,"Please login to continue",Toast.LENGTH_SHORT).show();
            }

            //startActivity(new Intent(MainActivity.this, WishlistActivity.class));
        }
        else if (id == R.id.my_account){
            if (checkLoggedin()==true){
                startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
            }
            else{
                Toast.makeText(MainActivity.this,"Please login to continue",Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.my_orders) {
            if (checkLoggedin()==true){
                CartListActivity.setScreenName("my_orders");
                startActivity(new Intent(MainActivity.this, CartListActivity.class));
            }
            else{
                Toast.makeText(MainActivity.this,"Please login to continue",Toast.LENGTH_SHORT).show();
            }

        }
        else if (id == R.id.contact_us) {
            Toast.makeText(MainActivity.this,"Coming Soon",Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.terms_conditions) {
            Toast.makeText(MainActivity.this,"Coming Soon",Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.help_center) {
            Toast.makeText(MainActivity.this,"Coming Soon",Toast.LENGTH_SHORT).show();
        }

        else {
            int catgId = getcatIdbyName(item.getTitle().toString());
            //Toast.makeText(this,"catid."+catgId,Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
//            intent.putExtra("catgId",catgId);
//            startActivity(intent);
            if (activity!=null){
                CategoryFragment.setCataGoryId(catgId);
                activity.cnageFragment(new CategoryFragment());

            }

            //startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm ,List<String> mFragmentTitles) {
            super(fm);
            this.mFragmentTitles = mFragmentTitles;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    public static  void gotoprodDetails(int catId){
        if (activity!=null){
            ImageListFragment.setCataGoryId(catId);
            activity.cnageFragment(new ImageListFragment());

        }

    }
    public int getcatIdbyName(String name)
    {
        List<Catagories> dat = new ArrayList<>();
        int catgId = 0;
        for (int i = 0; i < responseData.size(); i++) {

            String f = responseData.get(i).getName().replace("&amp;", "&");
            String f1 = f.replace("?", "");
            if (name.equalsIgnoreCase(f1) ){
                catgId = responseData.get(i).getId();
                break;
            }
        }
        return catgId;
    }

    KProgressHUD hud  = null;
    void   showHud(){
        hud =  KProgressHUD.create(activity)
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
    public boolean checkLoggedin(){
        SharedPreferences sh
                = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String uid = sh.getString("id", "true");
        if (uid == "true") {
            return false;
        }
        else {
            return true;
        }

    }

}