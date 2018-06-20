package com.app.yellowpages;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ArrayList;
import android.provider.MediaStore;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.fragment.FavouriteFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.LatestFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_REQUEST = 1;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    MyApplication MyApp;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    AdView mAdView;
    NavigationView navigationView;
    Toolbar toolbar;
    private LocationManager locManager;
    private Location lastLocation;

    //camera stuff
    Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());

    //folder stuff
    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");

    File image = new File(imagesFolder, "LMR_ACCIDENT_PHOTO_" + timeStamp + "_1" + ".jpg");

    Uri uriSavedImage = Uri.fromFile(image);

    private final LocationListener locListener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            updateLocation(loc);
        }
        public void onProviderEnabled(String provider) {
            updateLocation();
        }
        public void onProviderDisabled(String provider) {
            updateLocation();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-2470262152368078~6132650976");
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        MyApp = MyApplication.getInstance();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());


        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();

        // Set default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.menu_go_home:
                        toolbar.setTitle(getString(R.string.menu_home));
                        HomeFragment homeFragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();
                        return true;
                    case R.id.menu_go_latest:
                        toolbar.setTitle(getString(R.string.menu_latest));
                        LatestFragment latestFragment = new LatestFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, latestFragment).commit();
                        return true;
                    case R.id.menu_go_favourite:
                        toolbar.setTitle(getString(R.string.menu_favourite));
                        FavouriteFragment favouriteFragment = new FavouriteFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, favouriteFragment).commit();
                        return true;
                    case R.id.menu_go_call:
                        CallApp();
                        return true;
                    case R.id.menu_go_about:
                        Intent about = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(about);
                        return true;
                    case R.id.menu_go_authorize:
                        AuthorizeApp();
                        return true;
                    case R.id.menu_go_camera:
                        PictureApp();
                        return true;
                    case R.id.menu_go_share_pic:
                        SharePicApp();
                        return true;
                    case R.id.menu_go_share:
                        ShareApp();
                        return true;
                    case R.id.menu_go_privacy:
                        Intent privacy = new Intent(MainActivity.this, PrivacyActivity.class);
                        startActivity(privacy);
                        return true;
                    case R.id.menu_go_dashcam:
                        DashCamApp();
                        return true;
                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            locManager.removeUpdates(locListener);
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to stop listening for location updates", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRequestingLocation();
    }

    private void updateLocation() {
        // Trigger a UI update without changing the location
        updateLocation(lastLocation);
    }

    private void updateLocation(Location location) {
        boolean locationEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean waitingForLocation = locationEnabled && !validLocation(location);
        boolean haveLocation = locationEnabled && !waitingForLocation;

            lastLocation = location;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    MenuItemCompat.collapseActionView(searchMenuItem);
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void ShareApp() {
        if (!validLocation(lastLocation)) {
            return;
        }

        String link = formatLocation(lastLocation, PreferenceManager.getDefaultSharedPreferences(this).getString("prefLinkType", ""));

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String ShareSub = "Share Accident Location";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(sharingIntent, "Share Accident Location"));
    }

    private void startRequestingLocation() {
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            return;
        }

        // GPS enabled and have permission - start requesting location updates
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
    }

    private boolean validLocation(Location location) {
        if (location == null) {
            return false;
        }

        // Location must be from less than 30 seconds ago to be considered valid
        if (Build.VERSION.SDK_INT < 17) {
            return System.currentTimeMillis() - location.getTime() < 30e3;
        } else {
            return SystemClock.elapsedRealtime() - location.getElapsedRealtimeNanos() < 30e9;
        }

    }

    private String getAccuracy(Location location) {
        float accuracy = location.getAccuracy();
        if (accuracy < 0.01) {
            return "?";
        } else if (accuracy > 99) {
            return "99+";
        } else {
            return String.format(Locale.US, "%2.0fm", accuracy);
        }
    }

    private String getLatitude(Location location) {
        return String.format(Locale.US, "%2.5f", location.getLatitude());
    }

    private String getLongitude(Location location) {
        return String.format(Locale.US, "%3.5f", location.getLongitude());
    }

    private String formatLocation(Location location, String format) {
        return MessageFormat.format(format,
                getLatitude(location), getLongitude(location));
    }

    private void CallApp() {
        Uri number = Uri.parse("tel:**567");
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }

    private void AuthorizeApp() {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://legalmedresources.com/authorize-lmr/"));
        startActivity(i);
    }

    private void DashCamApp() {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://legalmedresources.com/claim-dash-cam/"));
        startActivity(i);
    }

    private void PictureApp() {
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void SharePicApp() {
        final Intent ei = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ei.setType("image/jpeg");
        ei.putExtra(Intent.EXTRA_EMAIL, new String[] {"matt.gorski@gmail.com"});
        ei.putExtra(Intent.EXTRA_SUBJECT, "LMR Accident Photos");

        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(Uri.fromFile(new File(imagesFolder, "LMR_ACCIDENT_PHOTO_" + timeStamp + "_1" + ".jpg")));

        ei.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivityForResult(Intent.createChooser(ei, "Sending Accident Photos"), 12345);
    }

    private void RateApp() {
        final String appName = getPackageName();//your application package name i.e play store application url
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                            + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + appName)));
        }
    }

    @Override
    public void onBackPressed() {
        ExitApp();
        //super.onBackPressed();
    }

    private void ExitApp() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.exit_msg))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST &&
                grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRequestingLocation();
        } else {
            Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            finish();
        }


        boolean canUseExternalStorage = false;

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(MainActivity.this, "You cannot see images without requested permission", Toast.LENGTH_SHORT).show();
                } else {
                    // user now provided permission
                    // perform function for what you want to achieve
                    Log.i("Permission", "granted");
                }
            }
        }
    }
}
