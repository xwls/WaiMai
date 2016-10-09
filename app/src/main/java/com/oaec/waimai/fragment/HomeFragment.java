package com.oaec.waimai.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.oaec.waimai.R;
import com.oaec.waimai.activity.AddressActivity;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Kevin on 2016/10/8.
 * Description：首页的Fragment
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";
    private static final int REQ_ADDRESS = 0x11;
    @ViewInject(R.id.tv_address)
    private TextView tv_address;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    private double latitude;//经度
    private double longitude;//纬度

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAmap();
        mLocationClient.startLocation();
        tv_address.setOnClickListener(this);
    }

    private void initAmap() {
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if(aMapLocation.getErrorCode() == 0){
//                        String locationDetail = aMapLocation.getLocationDetail();
                        StringBuffer sb = new StringBuffer();
                        latitude = aMapLocation.getLatitude();
                        longitude = aMapLocation.getLongitude();
                        sb.append("\n纬度:"+ latitude);
                        sb.append("\n经度:"+ longitude);
                        sb.append("\n精度:"+aMapLocation.getAccuracy());
                        sb.append("\n海拔:"+aMapLocation.getAltitude());
                        sb.append("\n方向角:"+aMapLocation.getBearing());
                        sb.append("\n地址描述:"+aMapLocation.getAddress());
                        sb.append("\n国家:"+aMapLocation.getCountry());
                        sb.append("\n省:"+aMapLocation.getProvince());
                        sb.append("\n城市:"+aMapLocation.getCity());
                        sb.append("\n城区:"+aMapLocation.getDistrict());
                        sb.append("\n街道:"+aMapLocation.getStreet());
                        sb.append("\n街道门牌号:"+aMapLocation.getStreetNum());
                        sb.append("\n城市编码:"+aMapLocation.getCityCode());
                        sb.append("\n区域编码:"+aMapLocation.getAdCode());
                        sb.append("\n当前位置POI名称:"+aMapLocation.getPoiName());
                        sb.append("\n当前位置所处AOI名称:"+aMapLocation.getAoiName());
                        sb.append("\n定位来源:"+aMapLocation.getLocationType());
                        sb.append("\n定位信息描述:"+aMapLocation.getLocationDetail());
                        sb.append("\n定位错误信息描述:"+aMapLocation.getErrorInfo());
                        sb.append("\n定位错误码:"+aMapLocation.getErrorCode());
                        Log.d(TAG, "onLocationChanged: "+sb);
                        tv_address.setText("送至："+aMapLocation.getAoiName());
                    }else {
                        String errorInfo = aMapLocation.getErrorInfo();
                        Log.e(TAG, "onLocationChanged: "+errorInfo);
                    }

                }
            }
        };
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为高性能
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置单次定位
        mLocationOption.setOnceLocation(true);
        //设置需要返回地址信息
        mLocationOption.setNeedAddress(true);

        //允许模拟位置
        mLocationOption.setMockEnable(true);

        //初始化定位
        mLocationClient = new AMapLocationClient(x.app());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //将设置项设置到mLocationClient
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("首页");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("首页");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_address:
                Intent intent = new Intent(x.app(), AddressActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivityForResult(intent,REQ_ADDRESS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQ_ADDRESS:
                if(resultCode == Activity.RESULT_OK){
                    String address = data.getStringExtra("address");
                    tv_address.setText("送至："+address);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
