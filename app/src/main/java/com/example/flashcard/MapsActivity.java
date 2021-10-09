package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.example.flashcard.dbclass.FlashCardDbHelper;
import com.example.flashcard.dbclass.GPS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;


import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final static String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ArrayList<Pair<LatLng, Integer>> gps_list;
    private ArrayList<Integer> colors;
    private HashMap<Integer, Integer> setIDToColor;
    private static final int NUM_REPRESENTATIVE = 300;
    private int circle_radius = 10;
    private Random r;
    private static FlashCardDbHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // set map of setID -> Color
        myDBHelper=new FlashCardDbHelper(MapsActivity.this);
        colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);

    }

    private void setupMap(int user_id) {
//        configureTestColorMap();
        configureColorMap(user_id);
    }

    // color are given to word sets based on their frequencies
    // the most frequent set gets red, the second gets blue...
    private void configureColorMap(int user_id) {
        if (myDBHelper==null) myDBHelper=new FlashCardDbHelper(MapsActivity.this);
        ArrayList<Integer> set_ordered_by_frequency = myDBHelper.getSetFrequency(user_id);
        setIDToColor = new HashMap<>();
        for (int i=0; i<Integer.min(set_ordered_by_frequency.size(), colors.size()); i++)
            setIDToColor.put(set_ordered_by_frequency.get(i), colors.get(i));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(18.0f);
        final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
        int user_id = sp.getInt("current_user", -1);
        setupMap(user_id);
        gps_list = getGPSLocations(user_id);
        Log.i(TAG, "Size of location list: "+gps_list.size());
        if (gps_list.size()>NUM_REPRESENTATIVE) Collections.shuffle(gps_list);
        Log.i(TAG, String.valueOf(gps_list.size()));
        int curr_set=-1;
        for (Pair<LatLng, Integer> loc: gps_list) {
            if (loc.second!=curr_set) curr_set=loc.second;
            mMap.addCircle(new CircleOptions()
                    .center(loc.first)
                    .radius(circle_radius)
                    .strokeColor(setIDToColor.getOrDefault(curr_set, Color.BLACK))
                    .fillColor(setIDToColor.getOrDefault(curr_set, Color.BLACK)));
        }
        // calculate camera center
        int size = Integer.min(NUM_REPRESENTATIVE, gps_list.size());
        double cam_x=0, cam_y=0;
        for (int i=0; i<size; i++) {
            LatLng temp = gps_list.get(i).first;
            cam_x += temp.latitude;
            cam_y += temp.longitude;
        }
        LatLng init_camera = new LatLng(cam_x/size, cam_y/size);
        if (size==0) init_camera=new LatLng(40.765644, 73.979927);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(init_camera, 15.2f));
    }

    public ArrayList<Pair<LatLng, Integer>> getGPSLocations(int user_id) {
//        return getTestLocations();
        if (myDBHelper==null) myDBHelper=new FlashCardDbHelper(MapsActivity.this);
        return transform(myDBHelper.getGPSLocations(user_id));
    }

    private ArrayList<Pair<LatLng, Integer>> transform(ArrayList<GPS> gpsLocations) {
        ArrayList<Pair<LatLng, Integer>> res = new ArrayList<>();
        for (GPS gps: gpsLocations) {
            LatLng temp = new LatLng(gps.getLatitude(), gps.getLongitude());
            res.add(new Pair(temp, gps.getSet_id()));
        }
        return res;
    }

    private void configureTestColorMap() {
        setIDToColor = new HashMap<>();
        setIDToColor.put(0, Color.RED);
        setIDToColor.put(1, Color.BLUE);
        setIDToColor.put(2, Color.MAGENTA);
        setIDToColor.put(3, Color.CYAN);
        setIDToColor.put(4, Color.GREEN);
        setIDToColor.put(5, Color.YELLOW);
    }

    private ArrayList<Pair<LatLng, Integer>> getTestLocations() {
        if (r==null) r=new Random();
        ArrayList<Pair<LatLng, Integer>> testLocations = new ArrayList<>();
        // add 1st group of samples
        int num_samples = 50;
        double src_x=35.660072, src_y=139.724937;
        double dst_x=35.666044, dst_y=139.737714;
        double delta_x=(dst_x-src_x)/num_samples, delta_y=(dst_y-src_y)/num_samples;
        double noise_x=2*delta_x, noise_y=2*delta_y;
        for (int i=0; i<=num_samples; i++) {
            testLocations.add(new Pair<>(new LatLng(src_x, src_y), 0));
            src_x+=delta_x; src_y+=delta_y;
            src_x += (r.nextDouble()-0.5)*noise_x;
            src_y += (r.nextDouble()-0.5)*noise_y;
        }
        // add 2nd group of samples
        num_samples = 40;
        src_x=35.665125; src_y=139.724743;
        dst_x=35.660869; dst_y=139.730047;
        delta_x=(dst_x-src_x)/num_samples; delta_y=(dst_y-src_y)/num_samples;
        noise_x=2*delta_x; noise_y=2*delta_y;
        for (int i=0; i<=num_samples; i++) {
            testLocations.add(new Pair<>(new LatLng(src_x, src_y), 1));
            src_x+=delta_x; src_y+=delta_y;
            src_x += (r.nextDouble()-0.5)*noise_x;
            src_y += (r.nextDouble()-0.5)*noise_y;
        }
        // add 3rd group of samples
        num_samples = 40;
        src_x=35.657267; src_y=139.733749;
        dst_x=35.664320; dst_y=139.735257;
        delta_x=(dst_x-src_x)/num_samples; delta_y=(dst_y-src_y)/num_samples;
        noise_x=2*delta_x; noise_y=2*delta_y;
        for (int i=0; i<=num_samples; i++) {
            testLocations.add(new Pair<>(new LatLng(src_x, src_y), 0));
            src_x+=delta_x; src_y+=delta_y;
            src_x += (r.nextDouble()-0.5)*noise_x;
            src_y += (r.nextDouble()-0.5)*noise_y;
        }
        // add 4th group of samples
        num_samples = 120;
        src_x=35.646125; src_y=139.716483;
        dst_x=35.674766; dst_y=139.740744;
        delta_x=(dst_x-src_x)/80; delta_y=(dst_y-src_y)/80;
        for (int i=0; i<=num_samples; i++) {
            int temp_setID = r.nextInt(8);
            dst_x = src_x+r.nextDouble()*delta_x*80;
            dst_y = src_y+r.nextDouble()*delta_y*80;
            int temp_size = 2+r.nextInt(4);
            for (int j=0; j<temp_size; j++) {
                dst_x += r.nextDouble() * delta_x;
                dst_y += r.nextDouble() * delta_y;
                testLocations.add(new Pair<>(new LatLng(dst_x, dst_y), temp_setID));
            }
        }
        // return test samples
        return testLocations;
    }

}