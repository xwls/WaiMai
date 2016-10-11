package com.oaec.waimai.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.oaec.waimai.R;
import com.oaec.waimai.activity.AddressActivity;
import com.oaec.waimai.activity.MerchantActivity;
import com.oaec.waimai.entity.BannerBean;
import com.oaec.waimai.entity.Merchant;
import com.oaec.waimai.util.CommonAdapter;
import com.oaec.waimai.util.DensityUtils;
import com.oaec.waimai.util.ViewHolder;
import com.oaec.waimai.util.WaiMaiConfig;
import com.oaec.waimai.widget.LoadListView;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
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
public class HomeFragment extends BaseFragment implements View.OnClickListener, LoadListView.OnLoadListener, AdapterView.OnItemClickListener {

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
    @ViewInject(R.id.lv_home_merchant)
    private LoadListView loadListView;
    @ViewInject(R.id.sv_home)
    private ScrollView sv_home;

    private CommonAdapter<Merchant> adapter;
    private List<Merchant> list;
    private int pno = 1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAmap();
        mLocationClient.startLocation();
        initEvent();
        initBanner();
        initMerchant();
        getMerchant(pno);
    }

    private void initEvent() {
        tv_address.setOnClickListener(this);
        loadListView.setOnLoadListener(this);
        loadListView.setOnItemClickListener(this);
        sv_home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int scrollY = v.getScrollY();
                        int height = v.getHeight();
                        int scrollViewMeasuredHeight = sv_home.getChildAt(0).getMeasuredHeight();
                        if (scrollY == 0) {
                            System.out.println("滑动到了顶端 view.getScrollY()=" + scrollY);
                        }
                        if ((scrollY + height) == scrollViewMeasuredHeight) {
                            //滑动到底部
                            loadListView.startLoad();
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }

        });
    }

    private void getMerchant(int pno) {
        RequestParams params = new RequestParams(WaiMaiConfig.URL_MERCHANT_LIMIT);
        params.addParameter("pno", pno);
        params.addParameter("ps", 4);
        x.http().get(params, new Callback.CacheCallback<String>() {

            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false;
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    Log.d(TAG, "onSuccess() called with: " + "result = [" + result + "]");
                    List<Merchant> merchants = JSON.parseArray(result, Merchant.class);
//                adapter.notifyDataSetChanged();
                    list.addAll(merchants);
                    adapter.onDataSetChanged(list);
                    DensityUtils.setListViewHeightBasedOnChildren(loadListView);
                }
            }
        });
    }

    private void initMerchant() {
        list = new ArrayList<Merchant>();
        adapter = new CommonAdapter<Merchant>(x.app(), list, R.layout.list_item_merchant) {
            @Override
            public void convert(ViewHolder holder, Merchant merchant) {
                holder.setText(R.id.tv_name, merchant.getName())
                        .setText(R.id.tv_grade, merchant.getGrade())
                        .setText(R.id.tv_xl, "月售" + merchant.getXl() + "单")
                        .setText(R.id.tv_qs, "￥" + merchant.getQs() + "起送")
                        .setText(R.id.tv_psf, " / 配送费￥" + merchant.getPsf())
                        .setText(R.id.tv_distance, merchant.getDistance() + "米")
                        .setRating(R.id.ratingBar, merchant.getGrade())
                        .setImageUrl(R.id.iv_img, merchant.getImg());
            }
        };
        loadListView.setAdapter(adapter);
    }

    @Override
    public void onLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pno += 1;
                getMerchant(pno);
                loadListView.loadComplete();
            }
        }, 1000);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Merchant merchant = list.get(position);
        Intent intent = new Intent(x.app(), MerchantActivity.class);
        intent.putExtra("id",merchant.getId());
        intent.putExtra("name",merchant.getName());
        intent.putExtra("img",merchant.getImg());
        intent.putExtra("grade",merchant.getGrade());
        intent.putExtra("xl",merchant.getXl());
        intent.putExtra("qs",merchant.getQs());
        intent.putExtra("psf",merchant.getPsf());
        intent.putExtra("distance",merchant.getDistance());
        startActivity(intent);
    }

    class GlideImageLoader implements ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            ImageOptions options = new ImageOptions.Builder()
                    .setUseMemCache(true)
                    .build();
            x.image().bind(imageView, path.toString(), options);

        }
    }

    private void initBanner() {
        RequestParams params = new RequestParams(WaiMaiConfig.URL_BANNER);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                List<BannerBean> banners = JSON.parseArray(result, BannerBean.class);
                List<String> images = new ArrayList<String>();
                for (BannerBean b : banners) {
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
                    if (aMapLocation.getErrorCode() == 0) {
                        StringBuffer sb = new StringBuffer();
                        latitude = aMapLocation.getLatitude();
                        longitude = aMapLocation.getLongitude();
                        tv_address.setText("送至：" + aMapLocation.getAoiName());
                    } else {
                        String errorInfo = aMapLocation.getErrorInfo();
                        Log.e(TAG, "onLocationChanged: " + errorInfo);
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
        switch (v.getId()) {
            case R.id.tv_address:
                Intent intent = new Intent(x.app(), AddressActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivityForResult(intent, REQ_ADDRESS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra("address");
                    tv_address.setText("送至：" + address);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
