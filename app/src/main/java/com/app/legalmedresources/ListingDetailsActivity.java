package com.app.legalmedresources;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.db.DatabaseHelper;
import com.example.item.ItemListing;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListingDetailsActivity extends AppCompatActivity {

    ImageView image, imageMap;
    TextView text, textCategory, txtPhone, txtAddress, txtEmail, txtWebsite;
    WebView webView;
    ScrollView mScrollView;
    ProgressBar mProgressBar;
    String Id;
    DatabaseHelper databaseHelper;
    ItemListing objBean;
    AdView mAdView;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);
        image = (ImageView) findViewById(R.id.image);
        imageMap = (ImageView) findViewById(R.id.imageMap);
        text = (TextView) findViewById(R.id.text);
        textCategory = (TextView) findViewById(R.id.textCategory);
        txtPhone = (TextView) findViewById(R.id.textPhone);
        txtAddress = (TextView) findViewById(R.id.textAddress);
        txtEmail = (TextView) findViewById(R.id.textEmail);
        txtWebsite = (TextView) findViewById(R.id.textWebsite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        objBean = new ItemListing();

        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        databaseHelper = new DatabaseHelper(getApplicationContext());

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        webView = (WebView) findViewById(R.id.webView);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        webView.setBackgroundColor(Color.TRANSPARENT);

        if (JsonUtils.isNetworkAvailable(ListingDetailsActivity.this)) {
            new getListing().execute(Constant.SINGLE_LISTING_URL + Id);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        txtWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite();
            }
        });

        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });

        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber();
            }
        });

        imageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListingDetailsActivity.this, MapActivity.class);
                intent.putExtra("latitude", objBean.getListingLatitude());
                intent.putExtra("longitude", objBean.getListingLongitude());
                intent.putExtra("title", objBean.getListingName());
                startActivity(intent);

            }
        });

    }

    private class getListing extends AsyncTask<String, Void, String> {

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
                        objBean.setId(objJson.getString(Constant.LISTING_ID));
                        objBean.setListingName(objJson.getString(Constant.LISTING_NAME));
                        objBean.setListingImageB(objJson.getString(Constant.LISTING_IMAGE));
                        objBean.setListingPhone(objJson.getString(Constant.LISTING_PHONE));
                        objBean.setListingAddress(objJson.getString(Constant.LISTING_ADDRESS));
                        objBean.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                        objBean.setListingLatitude(objJson.getString(Constant.LISTING_LATITUDE));
                        objBean.setListingLongitude(objJson.getString(Constant.LISTING_LONGITUDE));
                        objBean.setListingWebsite(objJson.getString(Constant.LISTING_WEBSITE));
                        objBean.setListingEmail(objJson.getString(Constant.LISTING_EMAIL));
                        objBean.setListingDescription(objJson.getString(Constant.LISTING_DESC));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {


        text.setText(objBean.getListingName());
        textCategory.setText(objBean.getCategoryName());
        txtPhone.setText(objBean.getListingPhone());
        txtAddress.setText(objBean.getListingAddress());
        txtWebsite.setText(objBean.getListingWebsite());
        txtEmail.setText(objBean.getListingEmail());

        Picasso.with(ListingDetailsActivity.this).load(objBean.getListingImageB()).placeholder(R.drawable.placeholder).into(image);

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = objBean.getListingDescription();

        String text = "<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #9E9E9E;text-align:left;font-size:14px;margin-left:0px}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

    }

    public void showToast(String msg) {
        Toast.makeText(ListingDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        this.menu = menu;
        isFavourite();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_favourite:
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(Id)) {
                    databaseHelper.removeFavouriteById(Id);
                    menu.getItem(0).setIcon(R.drawable.ic_favourite_1);
                    Toast.makeText(ListingDetailsActivity.this, getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, Id);
                    fav.put(DatabaseHelper.KEY_TITLE, objBean.getListingName());
                    fav.put(DatabaseHelper.KEY_IMAGE, objBean.getListingImageB());
                    fav.put(DatabaseHelper.KEY_CATEGORY, objBean.getCategoryName());
                    fav.put(DatabaseHelper.KEY_PHONE, objBean.getListingPhone());
                    fav.put(DatabaseHelper.KEY_ADDRESS, objBean.getListingAddress());
                    fav.put(DatabaseHelper.KEY_LATITUDE, objBean.getListingLatitude());
                    fav.put(DatabaseHelper.KEY_LONGITUDE, objBean.getListingLongitude());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    menu.getItem(0).setIcon(R.drawable.ic_favourite_hover);
                    Toast.makeText(ListingDetailsActivity.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        objBean.getListingName() + "\n" +
                                "Phone :- " + objBean.getListingPhone() + "\n" +
                                "Email :- " + objBean.getListingEmail() + "\n" +
                                "Website :- " + objBean.getListingWebsite() + "\n" +
                                "Address :- " + objBean.getListingAddress() + "\n\n" +
                                "Download Application here https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            menu.getItem(0).setIcon(R.drawable.ic_favourite_hover);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_favourite_1);
        }
    }

    private void openWebsite() {
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(addHttp(objBean.getListingWebsite()))));
    }

    private void openEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", objBean.getListingEmail(), null));
        emailIntent
                .putExtra(Intent.EXTRA_SUBJECT, "Inquire for the  " + objBean.getListingName());
        startActivity(Intent.createChooser(emailIntent, "Send suggestion..."));
    }

    protected String addHttp(String string1) {
        // TODO Auto-generated method stub
        if (string1.startsWith("http://"))
            return String.valueOf(string1);
        else
            return "http://" + String.valueOf(string1);
    }

    private void dialNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", objBean.getListingPhone(), null));
        startActivity(intent);
    }
}
