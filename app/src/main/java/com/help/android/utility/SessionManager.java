package com.help.android.utility;

import android.content.Context;

import com.google.gson.Gson;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionManager {
    String uid,username,email,mobile;
    Context context;
    Boolean access;
    public SessionManager(Context context) {
        this.context=context;
        uid=null;
        username=null;
        mobile=null;
        access=false;
    }
    public String getMobile() {
        try
        {
            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("mobile"))
                mobile=snappyDB.get("mobile");
            snappyDB.close();
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
        return mobile;
    }
    public String getUsername() {
        try
        {
            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("username"))
                username=snappyDB.get("username");
            snappyDB.close();
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
        return username;
    }

    public String getUId() {
        try
        {
            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("uid"))
                uid=snappyDB.get("uid");
            snappyDB.close();
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
        return uid;
    }
    public String getMainCat() {
        String res = "";
        try
        {
            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("main_cat"))
                res=snappyDB.get("main_cat");
            snappyDB.close();
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public Map<Integer, List<Catagories>> getSUBCat() {
        Map<Integer, List<Catagories>> res = new HashMap<>();

        try
        {

            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("sub_cat"))
               res =  stringCatagoriesHashMap(snappyDB.get("sub_cat"));
            snappyDB.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public JSONArray getSUBCatJ() {
      JSONArray res = new JSONArray();

        try
        {

            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("sub_cat"))
                res =  new JSONArray( snappyDB.get("sub_cat"));

            snappyDB.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public Map<Integer, List<Catagories>> stringCatagoriesHashMap(String value ) throws JSONException {
        value = value.substring(1, value.length()-1);           //remove curly brackets
        String[] keyValuePairs = value.split(",");              //split the string to creat key-value pairs
        Map<String,String> map = new HashMap<>();
        Map<Integer,List<Catagories> >map2 = new HashMap<>();

        Gson gson = new Gson();
        for(String pair : keyValuePairs)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value
            map.put(entry[0].trim(), entry[1].trim());
            List<Catagories> list = new ArrayList<>();
            JSONArray jsonElements = new JSONArray(entry[1].trim().toString());
            if (jsonElements.length()>0){
                for (int i= 0;i<jsonElements.length();i++){
                    list.add(gson.fromJson(jsonElements.get(i).toString(), Catagories.class));
                }
            }
            map2.put(Integer.parseInt(entry[0].trim()),list);
            //add them to the hashmap and trim whitespaces
        }

        return  map2;
    }
    public String getEmail() {
        try
        {
            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("email"))
                email=snappyDB.get("email");
            snappyDB.close();
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
        return email;
    }


    public Boolean getAccess()
    {
        try
        {
            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("access")) {
                access = snappyDB.getBoolean("access");
                return access;
            }
            snappyDB.close();
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean isLoggedIn()
    {
        try
        {
            DB snappyDB= DBFactory.open(context);
            if (snappyDB.exists("email") && snappyDB.exists("uid") && snappyDB.exists("username")) {
                snappyDB.close();
                return true;
            }
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public void clear()
    {
        try
        {
            DB snappyDB=DBFactory.open(context);
            snappyDB.destroy();
            snappyDB.close();
        }
        catch (SnappydbException e)
        {
            e.printStackTrace();
        }
    }
}