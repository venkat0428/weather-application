package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.VoiceInteractor;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String CITY;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String API = "98ed13acba37b8f64a391fd3a3748865";
    DecimalFormat df = new DecimalFormat("#.##");
    ImageView search;
    EditText etCity;
    TextView city,country,time,temp,forecast,humidity,min_temp,max_temp,sunrises,sunsets,pressure,windSpeed,cloudy;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onStart() {
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient ( this );
        if(ActivityCompat.checkSelfPermission ( MainActivity.this , Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation ().addOnCompleteListener ( new OnCompleteListener<Location> () {
                @Override
                public void onComplete(@NonNull  Task<Location> task) {
                    /* initializing location */
                    Location location=task.getResult ();
                    if(location!=null)
                    {
                        try {
                            // initialize geocoder
                            Geocoder geocoder=new Geocoder ( MainActivity.this,Locale.getDefault () );
                            List<Address> addresses=geocoder.getFromLocation ( location.getLatitude (),location.getLongitude (),1 );
                            etCity.setText ( addresses.get ( 0 ).getLocality () );
                            getWeatherDetails ( etCity.getText ().toString () );
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace ();
                        }
                    }

                }
            } );

        }
        else
        {
            ActivityCompat.requestPermissions ( MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44 );
        }
        super.onStart ();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        etCity = (EditText) findViewById(R.id.Your_city);
        search = (ImageView) findViewById(R.id.search);

        // CALL ALL ANSWERS :

        city = (TextView) findViewById(R.id.city);
        country = (TextView) findViewById(R.id.country);
        time = (TextView) findViewById(R.id.time);
        temp = (TextView) findViewById(R.id.temp);
        forecast = (TextView) findViewById(R.id.forecast);
        humidity = (TextView) findViewById(R.id.humidity);
        min_temp = (TextView) findViewById(R.id.min_temp);
        max_temp = (TextView) findViewById(R.id.max_temp);
        sunrises = (TextView) findViewById(R.id.sunrises);
        sunsets = (TextView) findViewById(R.id.sunsets);
        pressure = (TextView) findViewById(R.id.pressure);
        windSpeed = (TextView) findViewById(R.id.wind_speed);
        cloudy=findViewById ( R.id.cloudy );

        search.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getWeatherDetails(etCity.getText ().toString ());
            }
        } );
    }

    private void getWeatherDetails(String ecity) {
        String tempUrl="";
        tempUrl = url + "?q=" + ecity + "&appid=" + API;
        StringRequest stringRequest=new StringRequest ( Request.Method.POST, tempUrl, new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONObject main = jsonObj.getJSONObject("main");
                    JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                    JSONObject wind = jsonObj.getJSONObject("wind");
                    JSONObject sys = jsonObj.getJSONObject("sys");
                    JSONObject jsonObjectClouds = jsonObj.getJSONObject("clouds");




                    // CALL VALUE IN API :
                    String city_name = jsonObj.getString("name");
                    String countryname = sys.getString("country");
                    Long updatedAt = jsonObj.getLong("dt");
                    String updatedAtText = "Last Updated at: " + new SimpleDateFormat ("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date (updatedAt * 1000));
                    double temperature = main.getDouble ("temp")-273.15;
                    String cast = weather.getString("description");
                    String humi_dity = main.getString("humidity");
                    double temp_min = main.getDouble ("temp_min")-273.15;
                    double temp_max = main.getDouble ("temp_max")-273.15;
                    String pre = main.getString("pressure");
                    String windspeed = wind.getString("speed");
                    String clouds = jsonObjectClouds.getString("all");
                    Long rise = sys.getLong("sunrise");
                    String sunrise = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(rise * 1000));
                    Long set = sys.getLong("sunset");
                    String sunset = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(set * 1000));






                    // SET ALL VALUES IN TEXTBOX :

                    city.setText(city_name);
                    country.setText(countryname);
                    time.setText(updatedAtText);
                    temp.setText(df.format ( temperature )+ "°C");
                    forecast.setText(cast);
                    humidity.setText(humi_dity+ "%");
                    min_temp.setText(df.format ( temp_min )+ "°C");
                    max_temp.setText(df.format ( temp_max )+ "°C");
                    sunrises.setText(sunrise);
                    sunsets.setText(sunset);
                    pressure.setText(pre+ "hPa");
                    windSpeed.setText(windspeed+ "m/s");
                    cloudy.setText ( clouds+ "%" );

                } catch (Exception e) {

                    Toast.makeText(MainActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        } );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}