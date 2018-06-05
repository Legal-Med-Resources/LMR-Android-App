package com.app.yellowpages;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.adapter.ListingAdapter;
import com.example.item.ItemListing;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class CategoryItemActivity extends AppCompatActivity {

    ArrayList<ItemListing> mListItem;
    public RecyclerView recyclerView;
    ListingAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Id, Name;
    Toolbar toolbar;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        Name = intent.getStringExtra("name");
        setTitle(Name);
        mListItem = new ArrayList<>();
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CategoryItemActivity.this, LinearLayoutManager.VERTICAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(CategoryItemActivity.this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        if (JsonUtils.isNetworkAvailable(CategoryItemActivity.this)) {
            new getCategory().execute(Constant.CATEGORY_ITEM_URL + Id);
        }
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
    }

    private class getCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (null == result || result.length() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemListing objItem = new ItemListing();
                        objItem.setId(objJson.getString(Constant.LISTING_ID));
                        objItem.setListingName(objJson.getString(Constant.LISTING_NAME));
                        objItem.setListingImageB(objJson.getString(Constant.LISTING_IMAGE));
                        objItem.setListingPhone(objJson.getString(Constant.LISTING_PHONE));
                        objItem.setListingAddress(objJson.getString(Constant.LISTING_ADDRESS));
                        objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                        objItem.setListingLatitude(objJson.getString(Constant.LISTING_LATITUDE));
                        objItem.setListingLongitude(objJson.getString(Constant.LISTING_LONGITUDE));
                        mListItem.add(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }


    private void displayData() {
        adapter = new ListingAdapter(CategoryItemActivity.this, mListItem);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
