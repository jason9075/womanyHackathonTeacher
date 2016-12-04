package com.jason9075.womanyhackathonteacher;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jason9075.womanyhackathonteacher.Utils.DateFormatCached;
import com.jason9075.womanyhackathonteacher.manager.AlertManager;
import com.jason9075.womanyhackathonteacher.manager.MyLocationManager;
import com.jason9075.womanyhackathonteacher.manager.RetrofitManager;
import com.jason9075.womanyhackathonteacher.model.GoogleMapLocationResult;
import com.jason9075.womanyhackathonteacher.model.StudentLocationData;
import com.jason9075.womanyhackathonteacher.view.MyInfoWindowAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Inject
    MyLocationManager locationManager;

    @Inject
    AlertManager alertManager;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private Subscription centerMapIntervalSubscription;

    private List<MarkerOptions> markerOptionses = new ArrayList<>();
    private List<Marker> mMarkers = new ArrayList<>();

    private boolean isAtLeastCenterUserLocation = false; // 至少有一次有定位到使用者的位置
    private DatabaseReference mFirebaseDatabaseReference;
    private TextView addressTextview;
    private ImageButton alertButton;

    private GoogleMap googleMap;
    private MapFragment map;
    private List<StudentLocationData> studentDataList;
    private SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.getComponents().inject(this);
        addressTextview = (TextView) findViewById(R.id.address_text_view);
        alertButton = (ImageButton) findViewById(R.id.alert_button);
        studentDataList = new ArrayList<>();


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertManager.closeRingtonIfNeed();
            }
        });

        /* 使用者沒有答應權限的情況 */
        RxPermissions.getInstance(this)
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            locationManager.start();
                            map.getMapAsync(MainActivity.this);
                        }
                    }
                });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.child("StudentLocationTable").getValue();
                if (objectMap == null) {
                    return;
                }
                studentDataList.clear();
                for (Object obj : objectMap.values()) {
                    if (obj instanceof Map) {
                        Map<String, Object> mapObj = (HashMap<String, Object>) obj;
                        StudentLocationData studentLocationData = new StudentLocationData((String) mapObj.get("id"), (String) mapObj.get("studentName"));
                        studentLocationData.setLatitude((Double) mapObj.get("latitude"));
                        studentLocationData.setLongitude((Double) mapObj.get("longitude"));
                        studentLocationData.setAddress((String) mapObj.get("address"));
                        studentLocationData.setDate((String) mapObj.get("date"));
                        studentDataList.add(studentLocationData);
                    }
                }

                formatter = DateFormatCached.INSTANCE.getFormat("yyyy/MM/dd HH:mm:ss");

                Collections.sort(studentDataList, new Comparator<StudentLocationData>() {
                    @Override
                    public int compare(StudentLocationData lhs, StudentLocationData rhs) {
                        try {
                            return formatter.parse(lhs.getDate()).compareTo(formatter.parse(rhs.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }

                    }
                });

                drawMarkers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(">>>>取消讀取");
            }
        };
        mFirebaseDatabaseReference.addValueEventListener(postListener);

        compositeSubscription.add(Observable.just(1)
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.repeat();
                    }
                })
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "網路連線錯誤", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        if(studentDataList.size()==0)
                            return;
                        try {
                            alertManager.checkIsUserTriggerAlertTemp(MainActivity.this, formatter.parse(studentDataList.get(studentDataList.size() - 1).getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }));

    }

    private void drawMarkers() {
        List<LatLng> list = new ArrayList<>();
        for (StudentLocationData studentLocationData : studentDataList) {
            list.add(new LatLng(studentLocationData.getLatitude(), studentLocationData.getLongitude()));
        }
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.BLUE);
        polyOptions.width(12);
        polyOptions.addAll(list);
        googleMap.clear();
        googleMap.addPolyline(polyOptions);
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (LatLng latLng : list) {
//            builder.include(latLng);
//        }
//        final LatLngBounds bounds = builder.build();
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 4);
//        googleMap.animateCamera(cu);

    }

    @Override
    protected void onDestroy() {
        compositeSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MyInfoWindowAdapter myInfoWindowAdapter = new MyInfoWindowAdapter(this, "王小美", new Date());

        Location location = MyLocationManager.tryGetLastLocation();


        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        /* 使用者沒有答應權限的情況 */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(false);
        googleMap.setInfoWindowAdapter(myInfoWindowAdapter);

        /* 第一次定位 */
        centerTheMap(location);
        setMarker(location);

        /* 在Android 6.0 以上的版本會先問權限 所以locationManager.tryGetLastLocation()有可能會拿不到座標 所以用observable 持續polling 到有為止 */
        if (!isAtLeastCenterUserLocation) {
            centerMapIntervalSubscription = Observable.interval(5, TimeUnit.SECONDS, Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<Long, Observable<GoogleMapLocationResult>>() {
                        @Override
                        public Observable<GoogleMapLocationResult> call(Long aLong) {
                            Location location = MyLocationManager.tryGetLastLocation();
                            centerTheMap(location);
                            setMarker(location);
                            return RetrofitManager.INSTANCE.requestAddress(location, true);
                        }
                    })
                    .subscribe(new Subscriber<GoogleMapLocationResult>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(GoogleMapLocationResult googleMapLocationResult) {
                            addressTextview.setText(RetrofitManager.INSTANCE.getLastAddress());
                        }
                    });
            compositeSubscription.add(centerMapIntervalSubscription);
        }


    }


    private void centerTheMap(Location location) {
        if (location == null) {
            return;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(17)
                .bearing(90)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        isAtLeastCenterUserLocation = true;

        if (centerMapIntervalSubscription != null)
            centerMapIntervalSubscription.unsubscribe();

        setMarker(location);
    }

    private void setMarker(Location location) {
        if (location == null) {
            return;
        }
        markerOptionses.clear();
//        BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
//                .icon(mapIcon);


        markerOptionses.add(markerOptions);
        updateEventPoints();
    }


    private void updateEventPoints() {
        GoogleMap map = googleMap;
        if (map == null) return;

        //先移除old markers,以免有markers重疊
        for (Marker marker : mMarkers) {
            marker.remove();
        }

        mMarkers.clear();

        for (MarkerOptions markerOptions : markerOptionses) {

            Marker marker = map.addMarker(markerOptions);
            marker.showInfoWindow();
            mMarkers.add(marker);
        }

    }
}
