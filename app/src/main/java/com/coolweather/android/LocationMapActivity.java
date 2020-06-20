package com.coolweather.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.coolweather.android.util.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationMapActivity extends AppCompatActivity {
   public LocationClient mlicationClient;
   private TextView textView;
   private MapView mapView;
   private BaiduMap map;
   private boolean isFirstLocate=true;//第一次获取经纬度之后后边不再获取

    private String ceshi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlicationClient=new LocationClient(getApplicationContext());
        mlicationClient.registerLocationListener(new MyLocationListener());//注册定位
        SDKInitializer.initialize(getApplicationContext());//地图初始化 必须在布局之前
        setContentView(R.layout.location_layout);
        textView=findViewById(R.id.position_textview);
        mapView=findViewById(R.id.mapview);
        map = mapView.getMap();
        map.setMyLocationEnabled(true);//把我显示到地图上
        //设置定位图标是否有箭头
        map.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true,null));

        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(LocationMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(LocationMapActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(LocationMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
           String [] permissions= permissionList.toArray(new String[permissionList.size()]);
            Log.d("locationTest",permissionList.size()+"");
            Log.d("locationTest",permissions.length+"");
            ActivityCompat.requestPermissions(LocationMapActivity.this,permissions,1);
        }else{
            requestLocation();
        }

        Button button=findViewById(R.id.button_ceshi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("cccc",ceshi+"6------");
                    HttpUtil.sendOkHttpRequest("https://free-api.heweather.com/s6/weather/now?&location="+ceshi+"&key=b82ed2471e984e6883f2f3fab4ff3b50", new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        Toast.makeText(LocationMapActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                        Log.d("cccc","失败");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String string = response.body().string();
                        try {
                            JSONObject jsonObject=new JSONObject(string);
                            JSONArray HeWeather6=jsonObject.getJSONArray("HeWeather6");
                            JSONObject basic = (JSONObject)HeWeather6.get(0);
                            JSONObject basicObj = basic.getJSONObject("basic");
                            String cid = basicObj.getString("cid");
                            Intent intent=new Intent(LocationMapActivity.this,WeatherActivity.class);
                            intent.putExtra("weatherId",cid);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                //查看是否同意权限
                if(grantResults.length>0){
                    for (int result: grantResults) {
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"You denied the Permission!!",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else{
                    Toast.makeText(this,"未知错误！！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    public void requestLocation(){
        initLocation();
        mlicationClient.start();//开启定位
    }
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);//每五秒获取一次定位  0就是只获取一次
        option.setIsNeedAddress(true);//获取更多信息
        /*
            三种模式
            Hight_Accuracy 优先使用GPS 若GPS不能使用则使用网络(默认）
            Battery_saving 只会使用网络
            Device_Sensors只会使用GPS
         */
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);//设置是否使用gps，
        //option.setCoorType("bd09ll");//坐标类型
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        mlicationClient.setLocOption(option);
    }

    //把地图展示到当前位置
    public void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);
            map.animateMapStatus(update);//设置到当前位置
            update=MapStatusUpdateFactory.zoomBy(3f);//3-19  越大越清楚
            map.animateMapStatus(update);//设置缩放级别
            isFirstLocate=false;
        }
        //把我显示到地图上  因为我会一直移动 所以坐标改变我就会移动
        MyLocationData.Builder builder=new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData=builder.build();
        map.setMyLocationData(locationData);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        //调用LocationClient的start()方法，便可发起定位请求
        mlicationClient.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlicationClient.stop();//定位关闭
        mapView.onDestroy();//地图注销
        map.setMyLocationEnabled(false);//把我关闭

    }

    //用于异步定位
    public class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            ceshi=bdLocation.getCity();
            StringBuilder str=new StringBuilder();
            str.append("纬度：").append(bdLocation.getLatitude()).append("\n");
            str.append("经度：").append(bdLocation.getLongitude()).append("\n");
            str.append("国家：").append(bdLocation.getCountry()).append("\n");
            str.append("省：").append(bdLocation.getProvince()).append("\n");
            str.append("市：").append(bdLocation.getCity()).append("\n");
            str.append("区：").append(bdLocation.getDistrict()).append("\n");
            str.append("街道：").append(bdLocation.getStreet()).append("\n");
            str.append("详细信息：").append(bdLocation.getAddrStr()).append("\n");
            str.append("定位方式：");
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                str.append("GPS");
            }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                str.append("网络");
            }else {
                str.append("Other");
            }
            textView.setText(str);

            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation ||
            bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                //如果有网络
                navigateTo(bdLocation);//展示当前定位的地图
            }
        }
    }
}
