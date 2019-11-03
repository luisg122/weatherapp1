package com.example.weatherapp1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.testfairy.TestFairy;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {
        private static final String TAG = "WeatherChannel";
        private SharedPreferences settings;
        private SharedPreferences.Editor editor;

        private EditText weatherList_textField1;
        private EditText weatherListIndex_textField1;
        private EditText weatherListItem_textField1;

        private ImageButton submit_getWeatherInfo;
        private Button locationDeterminer;

        private String[] weatherInforList;
        private String queryString = "";

        private Spinner spinner;
        private String[] items = {
                "https://weather.com/weather/today/l/",
                "https://www.google.com/search?q="
        };

        String urlString = "";
        LocationService locationService;

        @SuppressLint("WrongViewCast")
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                //TestFairy.begin(this, "tokenId");

                locationService = new LocationService(MainActivity.this);

                settings = PreferenceManager.getDefaultSharedPreferences(this);
                editor   = settings.edit();

                setContentView(R.layout.activity_main);

                weatherListItem_textField1 = (EditText)findViewById(R.id.editText1_weatherItem);
                weatherListItem_textField1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                weatherListItem_textField1.setTextColor(Color.parseColor("#000000"));

                // What does this line do? What function does it perform?
                weatherListItem_textField1.setText( settings.getString("selection_queryString", "") );

                submit_getWeatherInfo = (ImageButton)findViewById(R.id.button_getWeatherInfo);
                submit_getWeatherInfo.setOnClickListener(this);

                spinner = (Spinner)findViewById(R.id.spinner1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, items);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);



                locationDeterminer = (Button) findViewById(R.id.button);
                locationDeterminer.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Location location = locationService.getLocation(LocationManager.GPS_PROVIDER);
                        if(location != null){
                            double latitude  = location.getLatitude();
                            double longitude = location.getLongitude();
                            AddressService.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
                        }
                        else showSettingsAlert();
                    }
                });
        }

        private void showSettingsAlert() {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialog.setTitle("Settings");
                alertDialog.setMessage("Turn on location settings");
                alertDialog.setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        MainActivity.this.startActivity(intent);
                                }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                }
                        });
                alertDialog.show();
        }

        @SuppressLint("HandlerLeak")
        private class GeocoderHandler extends Handler {
            @Override
            public void handleMessage(Message message) {
                String locationAddress;
                switch (message.what) {
                    case 1:
                        Bundle bundle = message.getData();
                        locationAddress = bundle.getString("address");
                        break;
                    default:
                        locationAddress = null;
                }
                weatherListItem_textField1.setText(locationAddress);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                // default starting URL
                urlString = (String) parent.getItemAtPosition(0);
        }
        // link to project
        // testfairy and share with class, user experience outcome learned from users

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                switch (position) {
                        case 0:
                                urlString = (String) parent.getItemAtPosition(0);
                                break;
                        case 1:
                                urlString = (String) parent.getItemAtPosition(1);
                                break;
                }
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void onClick(View v) {
                // TODO Auto-generated method stub
                String validateResult;
                if (v == submit_getWeatherInfo) {
                        String query = "";
                        String searchString = weatherListItem_textField1.getText().toString();
                        if (urlString.equals("https://www.google.com/search?q=")) {
                        // do something .... searchString = searchString.trim().replaceAll("\\s+", " ").replace(" ", "+");
                                 query = urlString + searchString + "+weather";  // proper string?
                        }

                        else if (urlString.equals("https://weather.com/weather/today/l/")) {
                        // do something .... searchString = searchString.trim().replaceAll("\\s+", " ").replace(" ", "+");
                                 query = urlString + searchString + ":4:US";
                        }


                        //query = urlString + searchString;
                        Log.e(TAG, query);

                        URL url;
                        try {
                                url = new URL(query);

                                WebView myWebView = (WebView) findViewById(R.id.webview);
                                myWebView.getSettings().setJavaScriptEnabled(true);
                                myWebView.clearCache(true);
                                myWebView.loadUrl(url.toString());

                                myWebView.setWebViewClient(new WebViewClient() {
                                        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                                                try {
                                                        webView.stopLoading();
                                                } catch (Exception e) {
                                                }

                                                if (webView.canGoBack()) {
                                                        webView.goBack();
                                                }

                                                webView.loadUrl("about:blank");
                                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                alertDialog.setTitle("Error");
                                                alertDialog.setMessage("Check your internet connection and try again.");
                                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                                startActivity(getIntent());
                                                        }
                                                });

                                                alertDialog.show();
                                                super.onReceivedError(webView, errorCode, description, failingUrl);
                                        }
                                });

                        } catch (MalformedURLException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                }

        }

}