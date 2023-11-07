package com.example.locationpinned;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Initializing variables to which views will be assigned too
    private Button newLocation;
    private Button populate;
    private ListView locationListView;
    private List<Location> locations = new ArrayList<>();
    private SearchView search;

    protected void onCreate(Bundle savedInstanceState) {
        //Once the app is created the home_screen.xml will load
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        locationListView = findViewById(R.id.listView);

        //Calling custom functions
        setLocationAdapter();
        loadFromDBToMemory();

        //Allow list items have onclick which allows them to be editied
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Location selectedLocation = (Location) locationListView.getItemAtPosition(position);
                Intent editLocationIntent = new Intent(getApplicationContext(), LocationDetails.class);
                editLocationIntent.putExtra(Location.LOCATION_EDIT_EXTRA, selectedLocation.getId());
                startActivity(editLocationIntent);
            }
        });


        newLocation = findViewById(R.id.newLocationButton);
        //Create onclick listener which will move to location page
        newLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newLocation(view);
            }
        });

        populate = findViewById(R.id.dataButton);
        //Create onclick listener which will retrieve location data from local file
        populate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readLocationData();
            }
        });
    }

    //Custom function which creates intent to switch pages
    private void newLocation(View view){
        Intent intent = new Intent(this, LocationDetails.class);
        startActivity(intent);
    }

    //Custom function which reads data from file within raw folder
    private void readLocationData(){
        //InputStream which will read each line within csv file and set each line as a location
        InputStream is = getResources().openRawResource(R.raw.locations);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";

        try{
            reader.readLine();
            int id = 1;

            while ((line = reader.readLine()) != null){
                String[] tokens = line.split(",");

                Location sample = new Location();

                Double x = Double.parseDouble(tokens[0]);
                Double y = Double.parseDouble(tokens[1]);
                String address = getAddress(x,y);

                sample.setId(id);
                sample.setLatitude(x);
                sample.setLongitude(y);
                sample.setAddress(address);
                locations.add(sample);

                saveLocation(x,y, address);

                //Log.d("MyActivity", "Just created: " + sample);
                id++;
            }
        } catch(IOException e){
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

        recreate();
    }

    //Custom function which initializes a custom adapter to allow location data cells to be list items
    private void setLocationAdapter() {
        LocationAdapater locationAdapater = new LocationAdapater(getApplicationContext(), Location.nonDeletedNotes());
        locationListView.setAdapter(locationAdapater);

        //Assign the virtual searchView to variable
        search = findViewById(R.id.searchView);

        //Create an text listener which will take user input when text is changed
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //When input is submitted filter for the input
            @Override
            public boolean onQueryTextSubmit(String s) {
                locationAdapater.getFilter().filter(s.toString());
                //locationListView.setAdapter(locationAdapater);
                return false;
            }
            //When input is changed filter for the input
            @Override
            public boolean onQueryTextChange(String s) {;
                locationAdapater.getFilter().filter(s);
                //locationListView.setAdapter(locationAdapater);
                return false;
            }
        });
    }

    //Custom function to initial SQLite manager which will pull data and add it to list
    private void loadFromDBToMemory() {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.populateLocationListArray();
    }

    //Custom function save location data to database
    private void saveLocation(Double latitude, Double longitude, String address){
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        int id = Location.locationArrayList.size();
        Location newLocation = new Location(id, latitude, longitude, address);
        Location.locationArrayList.add(newLocation);
        sqLiteManager.addLocationToDatabase(newLocation);
    }

    //Custom function which will obtain location address from lat and long
    private String getAddress(Double latitude, Double longitude){
        String address = "";

        if(Geocoder.isPresent()){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(!addresses.isEmpty()){
                    address = addresses.get(0).getAddressLine(0);
                } else{
                    address = "No Address Found";
                }
            } catch(IOException e){
                //e.printStackTrace();
                address = "No Address Found";
            }
        }

        return address;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLocationAdapter();
    }

}