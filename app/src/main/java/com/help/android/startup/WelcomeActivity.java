package com.help.android.startup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.velectico.rbm.network.callbacks.NetworkError;
import com.help.android.R;
import com.help.android.utility.ApiClient;
import com.help.android.utility.ApiInterface;
import com.help.android.utility.Catagories;
import com.help.android.utility.NetworkCallBack;
import com.help.android.utility.NetworkResponse;
import com.help.android.utility.PrefManager;
import com.help.android.utility.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2};
               // R.layout.welcome_slide3,
                //R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        callApiList();
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    DB snappydb =null;
    SessionManager manager;

    public void callApiList(){
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

            responseData = (List<Catagories>) response.getData();



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
                snappydb= DBFactory.open(WelcomeActivity.this);
                snappydb.put("main_cat",arr.toString());
                snappydb.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailureNetwork(@Nullable Object data, @NotNull NetworkError error) {

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

                snappydb=DBFactory.open(WelcomeActivity.this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
