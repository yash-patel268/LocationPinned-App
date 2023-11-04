package com.example.locationpinned;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationDetails extends AppCompatActivity {
    private Button cancelButton;
    private TextView addressTitle;
    private TextInputEditText latitude;
    private TextInputEditText longitude;
    private TextInputEditText address;
    private Button deleteButton;
    private Button saveButton;
    private Location selectedLocation;

    private Double latitudeInput;
    private Double longitudeInput;

    private String addressInput;

    Toast toast;

    protected void onCreate(Bundle savedInstanceState) {
        //Once the app is created the home_screen.xml will load
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_details);

        addressTitle = findViewById(R.id.addressPageTitle);
        address = findViewById(R.id.addressInput);

        latitude = findViewById(R.id.latitudeInput);
        longitude = findViewById(R.id.longitudeInput);

        CharSequence text = "Enter lat or long with coordinates";
        int duration = Toast.LENGTH_SHORT;
        toast = Toast.makeText(getApplicationContext(), text, duration);


        saveButton = findViewById(R.id.saveLocationButton);
        //Create onclick listener which will move to new note page
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(latitude.getText().toString().isEmpty() || longitude.getText().toString().isEmpty()){
                    toast.show();
                } else {
                    saveLocation(view);
                    finish();
                }
            }
        });

        cancelButton = findViewById(R.id.cancelLocationButton);
        //Create onclick listener which will move to new note page
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteButton = findViewById(R.id.deleteLocationButton);
        //Create onclick listener which will move to new note page
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLocation(view);
                finish();
            }
        });

        checkForEditLocation();
    }

    private void checkForEditLocation() {
        Intent previousIntent = getIntent();

        int passedLocationID = previousIntent.getIntExtra(Location.LOCATION_EDIT_EXTRA, -1);
        selectedLocation = Location.getNoteForID(passedLocationID);

        if (selectedLocation != null) {
            Double x = selectedLocation.getLatitude();
            latitude.setText(x.toString());
            Double y = selectedLocation.getLongitude();
            longitude.setText(y.toString());
            address.setText(selectedLocation.getAddress());

        } else {
            addressTitle.setVisibility(View.INVISIBLE);
            address.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void saveLocation(View view){
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String str= String.valueOf(latitude.getText());
        latitudeInput = Double.valueOf(str);
        str= String.valueOf(longitude.getText());
        longitudeInput = Double.valueOf(str);
        String addressFound = getAddress(latitudeInput, longitudeInput);
        addressInput = String.valueOf(address);

        if (selectedLocation == null){
            int id = Location.locationArrayList.size();
            Location newLocation = new Location(id, latitudeInput, longitudeInput, addressFound);
            Location.locationArrayList.add(newLocation);
            sqLiteManager.addLocationToDatabase(newLocation);
        } else{
            selectedLocation.setLatitude(latitudeInput);
            selectedLocation.setLongitude(longitudeInput);
            selectedLocation.setAddress(addressInput);
            sqLiteManager.updateLocationInDB(selectedLocation);
        }
    }

    //Custom function which will allow notes to be deleted from sqlite table
    public void deleteLocation(View view)
    {
        selectedLocation.setDeleted(new Date());
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.updateLocationInDB(selectedLocation);
        finish();
    }

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

}
