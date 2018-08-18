package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    LatLng friendLatLng;
    String latitude,longitude,name,userid,prevdate,prevImage;
    Toolbar toolbar;
    Marker marker;
    DatabaseReference reference;
    String myImage;
    String myName,myLat,myLng,myDate;
    ArrayList<String> mKeys;
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_map);

        toolbar=(Toolbar)findViewById(R.id.livemap_toolbar);

        markerOptions=new MarkerOptions();
        Intent intent=getIntent();
        mKeys=new ArrayList<>();

        if(intent!=null){
            latitude=intent.getStringExtra("latitude");
            longitude=intent.getStringExtra("longitude");
            name=intent.getStringExtra("name");
            userid=intent.getStringExtra("userid");
            prevdate=intent.getStringExtra("date");
            prevImage=intent.getStringExtra("image");
        }
        toolbar.setTitle(name+"'s location");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.live_map);
        mapFragment.getMapAsync(this);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(LiveMapActivity.this, "Child added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myName=dataSnapshot.child("name").getValue(String.class);
                        myLat=dataSnapshot.child("lat").getValue(String.class);
                        myLng=dataSnapshot.child("lng").getValue(String.class);
                        myDate=dataSnapshot.child("date").getValue(String.class);
                        myImage=dataSnapshot.child("profile_image").getValue(String.class);

                        friendLatLng=new LatLng(Double.parseDouble(myLat),Double.parseDouble(myLng));
                        markerOptions.position(friendLatLng);
                        markerOptions.snippet("Last seen:"+myDate);
                        markerOptions.title(myName);

                        if(marker==null){
                            marker=mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng,15f));
                        }
                        else{
                            marker.setPosition(friendLatLng);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View row=getLayoutInflater().inflate(R.layout.custom_snippet,null);
                TextView nameTxt=row.findViewById(R.id.snippetName);
                TextView dateTxt=row.findViewById(R.id.snippetDate);
                CircleImageView imageTxt=row.findViewById(R.id.snippetImage);

                if(myName==null&&myDate==null){
                    nameTxt.setText(name);
                    dateTxt.setText(dateTxt.getText().toString()+prevdate);
                    Picasso.get().load(prevImage).placeholder(R.drawable.icon_profile).into(imageTxt);
                }
                else{
                    nameTxt.setText(myName);
                    dateTxt.setText(dateTxt.getText().toString()+myDate);
                    Picasso.get().load(myImage).placeholder(R.drawable.icon_profile).into(imageTxt);
                }
                return row;
            }
        });
        friendLatLng=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        MarkerOptions optionsNew=new MarkerOptions();
        optionsNew.position(friendLatLng);
        optionsNew.title(name);
        optionsNew.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        if(marker==null){
            marker=mMap.addMarker(optionsNew);
        }
        else{
            marker.setPosition(friendLatLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng,15f));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
