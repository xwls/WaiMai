package com.oaec.waimai.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.oaec.waimai.R;
import com.oaec.waimai.activity.AddressActivity;
import com.oaec.waimai.entity.BannerBean;
import com.oaec.waimai.util.WaiMaiConfig;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

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
    @ViewInject(R.id.banner)
    private Banner banner;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAmap();
        mLocationClient.startLocation();
        tv_address.setOnClickListener(this);
        initBanner();
    }

    class GlideImageLoader implements ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            x.image().bind(imageView, path.toString());

        }
    }

    private void initBanner() {
        RequestParams params = new RequestParams(WaiMaiConfig.URL_BANNER);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                List<BannerBean> banners = JSON.parseArray(result, BannerBean.class);
                List<String> images = new ArrayList<String>();
                for (BannerBean b :  banners) {
                    images.add(b.getUrl());
                }
                banner.setImageLoader(new GlideImageLoader());
                banner.setImages(images);
                banner.setDelayTime(3000);
                banner.start();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void initAmap() {
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if(aMapLocation.getErrorCode() == 0){
                        StringBuffer sb = new StringBuffer();
                        latitude = aMapLocation.getLatitude();
                        longitude = aMapLocation.getLongitude();
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
