package com.example.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.yellowpages.R;
import com.app.yellowpages.SearchActivity;
import com.example.adapter.HomeAdapter;
import com.example.item.ItemListing;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ArrayList<ItemListing> mListItem;
    public RecyclerView recyclerView;
    HomeAdapter adapter;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        editText = (EditText) rootView.findViewById(R.id.etSearch);
        mListItem = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_featured);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new getFeatured().execute(Constant.HOME_URL);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!editText.getText().toString().isEmpty()) {
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        intent.putExtra("search", editText.getText().toString());
                        startActivity(intent);
                        editText.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    private class getFeatured extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
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
        adapter = new HomeAdapter(getActivity(), mListItem);
        recyclerView.setAdapter(adapter);
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
