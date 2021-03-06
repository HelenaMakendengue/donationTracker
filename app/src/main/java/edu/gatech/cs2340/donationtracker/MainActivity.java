package edu.gatech.cs2340.donationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * The main activity of the application. Shows all locations
 * and allows users to connect to the Location Detail Activity
 * by tapping on a location as well as back to the welcome
 * screen by pressing the logout button.
 */
public class MainActivity extends AppCompatActivity {

    private final Model model = Model.getInstance();

    private static final String TAG = "DONATION_TRACKER";

    private DatabaseReference databaseLocations;
    private static final Map<Integer, Location> db = new HashMap<>();

    /**
     * A method for accessing the in-app database
     * @return the database
     */
    public static Map<Integer,Location> getDb() {
        return Collections.unmodifiableMap(db);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database References
        databaseLocations = FirebaseDatabase.getInstance().getReference("locations");

        //Button References
        Button logoutBtn = findViewById(R.id.button_logout);
        Button mapBtn = findViewById(R.id.button_mapView);

        //Methods
        LocationReader();

        //Recycler Stuff
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(getDb());
        recyclerView.setAdapter(adapter);


        //Button Event Listeners
        logoutBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                WelcomeActivity.class)));

        mapBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                MapActivity.class)));
    }

    /**
     * A method that takes in the csv file in the resources and adds them as location
     * objects to the database.
     */
    private void LocationReader() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.locationdata);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8));
            br.mark(1000);
            br.readLine();
            String text = br.readLine();
            while (text != null) {
                String[] ar = text.split(",");
                for (int i = 0; i < ar.length; i++) {
                    ar[i] = ar[i].trim();
                }
                String address = ar[4] + ", " + ar[5] + ", " + ar[6] + " " + ar[7];
                LocationType locationType;
                switch (ar[8]) {
                    case "Store":
                        locationType = LocationType.STORE;
                        break;
                    case "Drop Off":
                        locationType = LocationType.DROP_OFF_ONLY;
                        break;
                    default:
                        locationType = LocationType.WAREHOUSE;
                        break;
                }
                addNewLocation(address, locationType, ar);
                text = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            Log.e(MainActivity.TAG, "error reading assets", e);
        }
    }

    /**
     *
     * Adds a new location to the Firebase Database
     *
     * @param address takes a string of a location's address
     * @param locationType takes a LocationType of a location's type
     * @param ar Takes a String array of the individuals location's details
     */
    private void addNewLocation(String address, LocationType locationType, String[] ar)
    {
        String id = databaseLocations.push().getKey();
        Location newLocation = new Location(ar[0], ar[1], ar[2], ar[3], address,
                locationType, ar[9], ar[10]);
        databaseLocations.child(Objects.requireNonNull(id)).setValue(newLocation);
        db.put(ar[9].hashCode(), newLocation);
        model.addLocation(newLocation);
    }
}