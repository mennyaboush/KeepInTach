package com.example.hannybuns.firebaseproject2.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;
import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.helper.MyLocationHelper;
import com.example.hannybuns.firebaseproject2.R;

import com.example.hannybuns.firebaseproject2.helper.MyfirebaseHelper;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

public class MyMapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MyMapsFragment";
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    MyfirebaseHelper databaseReference;
    ArrayList<UserInformation> myFrindsLocation;

    public MyMapsFragment() {
        databaseReference = KeepInTouchLogic.getInstance().getMyfirebaseHelperRef();
        myFrindsLocation = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFrindsLocation.add((UserInformation) getArguments().getSerializable(Keys.NEATBY_USERS1));
        myFrindsLocation.add((UserInformation) getArguments().getSerializable(Keys.NEATBY_USERS2));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_my_maps, container, false);
        return mView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity().getApplicationContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng location=null;
        for (UserInformation frind : myFrindsLocation) {
            location = new LatLng(frind.getLatitude(), frind.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(location).title(frind.getName()).snippet("Hi, I am near to you!"));
        }
        getRoute(googleMap,myFrindsLocation);

        if(location!=null) {
            CameraPosition Liberty = CameraPosition.builder().target(location).zoom(16).bearing(0).tilt(45).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
        }
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void getRoute(GoogleMap googleMap, ArrayList<UserInformation> frindsLoca) {
        List<LatLng> path = new ArrayList();

        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("YOUR_API_KEY")
                .build();
        String myLocationCor = frindsLoca.get(0).getLatitude()+"," +frindsLoca.get(0).getLongitude();
        String frindLocationCor =  frindsLoca.get(1).getLatitude()+"," +frindsLoca.get(1).getLongitude();

        DirectionsApiRequest req = DirectionsApi.getDirections(context, myLocationCor, frindLocationCor);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            googleMap.addPolyline(opts);
        }
    }

}
