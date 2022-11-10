package com.hsn.syedtaha.wally_wallpapers;

import static android.content.ContentValues.TAG;

import static java.lang.Math.floor;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface {

    private EditText searchEdit;
    private ImageView searchIV;
    private RecyclerView categoryRV,wallpaperRV;
    private ProgressBar loadingPB;
    private ArrayList<String> wallpaperArrayList;
    private ArrayList<CategoryRVModel> categoryRVModelArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private WallpaperRVAdapter wallpaperRVAdapter;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    int count=0;
    int total_pages, pics_per_page;
    ArrayList<JsonObjectRequest> page_data,pic_data;
    private String imUrl,imgUrl;
    private ArrayList<String[]> pictures;
    private String[] pictures1;

    // 563492ad6f91700001000001a487858e4280415b84167f0fa2e4700d

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        page_data = new ArrayList<JsonObjectRequest>();
        pic_data = new ArrayList<JsonObjectRequest>();
        pictures = new ArrayList<String[]>();
        pictures1 = new String[2];



//        Banner Ad
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        searchEdit = findViewById(R.id.idEditSearch);
        searchIV = findViewById(R.id.idIVSearch);
        categoryRV = findViewById(R.id.idRVCategory);
        wallpaperRV = findViewById(R.id.idRVWallpapers);
        loadingPB = findViewById(R.id.idPBLoading);
        wallpaperArrayList = new ArrayList<>();
        categoryRVModelArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,RecyclerView.HORIZONTAL,false);
        categoryRV.setLayoutManager(linearLayoutManager);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModelArrayList,this,this::onCategoryClick);
        categoryRV.setAdapter(categoryRVAdapter);

//        2 is the number of columns for wallpaper
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        wallpaperRV.setLayoutManager(gridLayoutManager);
        wallpaperRVAdapter = new WallpaperRVAdapter(wallpaperArrayList,this);
        wallpaperRV.setAdapter(wallpaperRVAdapter);

        getCategories();

        getWallpapers();

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count+1;
                String searchStr = searchEdit.getText().toString();
                if(searchStr.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter the wallpaper you want to search", Toast.LENGTH_SHORT).show();
                }else {
                    getWallpaperBySearch(searchStr);
                }
            }
        });
    }

    private void getWallpaperBySearch(String search) {
        wallpaperArrayList.clear();
        count+=1;
        loadingPB.setVisibility(View.VISIBLE);
        RequestQueue requestQueue1 = Volley.newRequestQueue(MainActivity.this);
        StringRequest StringRequest = new StringRequest(Request.Method.POST, "https://wallywallpapers.000webhostapp.com/search.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                assert groovy.util.Eval.me(response).class == ArrayList
                Log.d("tag",response.toString());
                Log.d("tag",response.toString());
                Log.d("tag",response.toString());
                Log.d("tag",response.toString());
                if (response.toString().equals("[]")){
                    Toast.makeText(MainActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                }else {
                    response = response.substring(1, response.length() - 1);
                    String[] array;
                    array = response.split(",");
                    for (int i = 0; i < array.length; i++) {
                        wallpaperArrayList.add("https://wallywallpapers.000webhostapp.com/" + array[i].substring(1, array[i].length() - 1).replace("\\", ""));
                    }
                }
                loadingPB.setVisibility(View.GONE);
                wallpaperRVAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> param=new HashMap<String,String>();
                param.put("search",search);
                return param;
            }}
                ;

        requestQueue1.add(StringRequest);

    }

    private void getWallpaperByCategory(String category) {
        wallpaperArrayList.clear();
        count+=1;
        loadingPB.setVisibility(View.VISIBLE);
        RequestQueue requestQueue1 = Volley.newRequestQueue(MainActivity.this);
        StringRequest StringRequest = new StringRequest(Request.Method.POST, "https://wallywallpapers.000webhostapp.com/categories.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                assert groovy.util.Eval.me(response).class == ArrayList
                response = response.substring(1, response.length()-1);
                String[] array;
                array = response.split(",");
                for (int i=0;i<array.length; i++) {
                    wallpaperArrayList.add("https://wallywallpapers.000webhostapp.com/"+array[i].substring(1, array[i].length()-1).replace("\\",""));
                }
                loadingPB.setVisibility(View.GONE);
                wallpaperRVAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> param=new HashMap<String,String>();
                param.put("category",category);
                return param;
            }}
        ;

        requestQueue1.add(StringRequest);

    }




    private void getWallpapers(){

        wallpaperArrayList.clear();
        loadingPB.setVisibility(View.VISIBLE);
        RequestQueue requestQueue1 = Volley.newRequestQueue(MainActivity.this);
        StringRequest StringRequest = new StringRequest(Request.Method.GET, "https://wallywallpapers.000webhostapp.com/get_wallpapers.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                assert groovy.util.Eval.me(response).class == ArrayList
                response = response.substring(1, response.length()-1);
                String[] array;
                array = response.split(",");
                for (int i=0;i<array.length; i++) {
                    wallpaperArrayList.add("https://wallywallpapers.000webhostapp.com/"+array[i].substring(1, array[i].length()-1).replace("\\",""));
                }
                loadingPB.setVisibility(View.GONE);
                wallpaperRVAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        }) {
        };

        requestQueue1.add(StringRequest);






    }




    private void getCategories() {
        categoryRVModelArrayList.add(new CategoryRVModel("Architecture","https://images.unsplash.com/photo-1659132394880-d55f55b3a794?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80"));
        categoryRVModelArrayList.add(new CategoryRVModel("Nature","https://images.unsplash.com/photo-1659296197284-199ec641f7f5?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=388&q=80"));
        categoryRVModelArrayList.add(new CategoryRVModel("Street Photography","https://images.unsplash.com/photo-1599557041284-7e2a15610388?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80"));
        categoryRVModelArrayList.add(new CategoryRVModel("Cars","https://images.unsplash.com/photo-1605559424843-9e4c228bf1c2?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=464&q=80"));
        categoryRVModelArrayList.add(new CategoryRVModel("Space","https://images.unsplash.com/photo-1462331940025-496dfbfc7564?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=611&q=80"));
        categoryRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryRVModelArrayList.get(position).getCategory();
        getWallpaperByCategory(category);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if (count>0){
            count=0;
            searchEdit.setText("");
            getWallpapers();
        }else {
            super.onBackPressed();
        }

    }



}