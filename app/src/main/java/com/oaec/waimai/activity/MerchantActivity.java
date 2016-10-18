package com.oaec.waimai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.oaec.waimai.R;
import com.oaec.waimai.entity.Cart;
import com.oaec.waimai.entity.Food;
import com.oaec.waimai.entity.FoodInfo;
import com.oaec.waimai.fragment.GoodsFragment;
import com.oaec.waimai.fragment.StoreFragment;
import com.oaec.waimai.util.AddCartAnimation;
import com.oaec.waimai.util.CommonAdapter;
import com.oaec.waimai.util.ViewHolder;
import com.oaec.waimai.util.WaiMaiConfig;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

@ContentView(R.layout.activity_merchant)
public class MerchantActivity extends AppCompatActivity implements MaterialTabListener, View.OnClickListener, GoodsFragment.Add2ShoppingCartListener {

    private static final String TAG = "MerchantActivity";
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.tabHost)
    private MaterialTabHost tabHost;
    @ViewInject(R.id.container)
    private ViewPager viewPager;
    @ViewInject(R.id.iv_cart)
    private ImageView iv_cart;
    @ViewInject(R.id.tv_dot)
    private TextView tv_dot;
    @ViewInject(R.id.tv_totalPrice)
    private TextView tv_totalPrice;
    @ViewInject(R.id.btn_ok)
    private Button btn_ok;
    @ViewInject(R.id.rl)
    private RelativeLayout mRl;
    @ViewInject(R.id.layout_cart)
    private LinearLayout layout_cart;

    private List<Fragment> fragments;

    private GoodsFragment f1;
    private StoreFragment f2;

    private float totalPrice;//总价钱
    private int totalCount;//总数量
    private float qs;//起送价
    private Animation anim_add;
    private Animation anim_sub;
    private ListPopupWindow listPopupWindow;
    private DecimalFormat format;
    private int id;//店铺id
    private View popView;
    private PopupWindow popupWindow;
    private ListView lv_pop_cart;
    private ImageView iv_pop_cart;
    private TextView tv_pop_dot;
    private TextView tv_pop_totalPrice;
    private Button btn_pop_ok;
    private CommonAdapter<Cart> cartAdapter;

    private List<Cart> carts;
    private List<Food> foods;
    private boolean isFirstOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabHost.addTab(tabHost.newTab().setText("商品").setTabListener(this));
        tabHost.addTab(tabHost.newTab().setText("商家").setTabListener(this));
        initView();
        initAnim();
        initData();
        f1.setAdd2ShoppingCartListener(this);
    }

    private void initData() {
        RequestParams params = new RequestParams(WaiMaiConfig.URL_FOODS);
        params.addParameter("mid", id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                foods = JSON.parseArray(result, Food.class);
                Log.d(TAG, "onSuccess() called with: " + "result = [" + result + "]");
                getCarts();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError() called with: " + "ex = [" + ex + "], isOnCallback = [" + isOnCallback + "]");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled() called with: " + "cex = [" + cex + "]");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished() called with: " + "");
            }
        });
    }

    private void getCarts() {
        RequestParams params = new RequestParams(WaiMaiConfig.URL_CART_QUERY);
        params.addParameter("uid", 1);
        params.addParameter("mid", id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess() called with: " + "result = [" + result + "]");
                carts = JSON.parseArray(result, Cart.class);
                if(isFirstOpen){
                    f1.initCarts(carts, foods);
                    initCartDetail(carts);
                    isFirstOpen = false;
                }
                if (cartAdapter == null){
                    cartAdapter = new CommonAdapter<Cart>(MerchantActivity.this,carts,R.layout.cart_item) {
                        @Override
                        public void convert(int position, ViewHolder holder, Cart cart) {
                            holder.setText(R.id.tv_name,cart.getName())
                                    .setText(R.id.tv_price,cart.getCount()*cart.getPrice())
                                    .setText(R.id.tv_count,cart.getCount());
                        }
                    };
                    lv_pop_cart.setAdapter(cartAdapter);
                }else {
                    cartAdapter.onDataSetChanged(carts);
                    int size = carts.size();
                    if (size>5){
                        size = 5;
                        ViewGroup.LayoutParams params = lv_pop_cart.getLayoutParams();
                        params.height = DensityUtil.dip2px(46*5);
                        lv_pop_cart.setLayoutParams(params);
                    }
                    int height = DensityUtil.dip2px(50* size)+DensityUtil.dip2px(100);
                    popupWindow.setHeight(height);

                    popupWindow.showAsDropDown(layout_cart,0,-DensityUtil.dip2px(48));
                    backgroundAlpha(0.4f);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError() called with: " + "ex = [" + ex + "], isOnCallback = [" + isOnCallback + "]");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled() called with: " + "cex = [" + cex + "]");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished() called with: " + "");
            }
        });
    }

    /**
     * 设置下方购物车信息的内容显示
     * @param carts
     */
    private void initCartDetail(List<Cart> carts) {
        totalCount = 0;
        totalPrice = 0;
        for (Cart cart: carts) {
            totalCount += cart.getCount();
            totalPrice += cart.getPrice()*cart.getCount();
        }
        initCartDetail();
    }

    private void initCartDetail() {
//        totalCount = 0;
//        totalPrice = 0;
//        for (Cart cart: carts) {
//            totalCount += cart.getCount();
//            totalPrice += cart.getPrice()*cart.getCount();
//        }
        if(totalCount > 0){
            iv_cart.setImageResource(R.drawable.ic_cart_red);
            iv_pop_cart.setImageResource(R.drawable.ic_cart_red);
            tv_dot.setVisibility(View.VISIBLE);
            tv_pop_dot.setVisibility(View.VISIBLE);
            tv_dot.setText(totalCount+"");
            tv_pop_dot.setText(totalCount+"");
            tv_totalPrice.setText("共：￥"+totalPrice);
            tv_pop_totalPrice.setText("共：￥"+totalPrice);
            if(totalPrice >= qs && totalCount > 0){
                btn_ok.setEnabled(true);
                btn_pop_ok.setEnabled(true);
                btn_ok.setText("去下单");
                btn_pop_ok.setText("去下单");
            }else {
                btn_ok.setEnabled(false);
                btn_pop_ok.setEnabled(false);
                btn_ok.setText("还差￥"+(qs - totalPrice)+"起送");
                btn_pop_ok.setText("还差￥"+(qs - totalPrice)+"起送");
            }
        }else {
            iv_cart.setImageResource(R.drawable.ic_cart);
            iv_pop_cart.setImageResource(R.drawable.ic_cart);
            tv_dot.setVisibility(View.INVISIBLE);
            tv_pop_dot.setVisibility(View.INVISIBLE);
            tv_dot.setText("");
            tv_pop_dot.setText("");
            tv_totalPrice.setText("购物车是空的");
            tv_pop_totalPrice.setText("购物车是空的");
            btn_ok.setEnabled(false);
            btn_pop_ok.setEnabled(false);
            btn_ok.setText("￥"+qs+"起送");
            btn_pop_ok.setText("￥"+qs+"起送");
        }
    }


    private void initAnim() {
        anim_add = AnimationUtils.loadAnimation(MerchantActivity.this, R.anim.anim_add2cart);
        anim_add.setStartOffset(1000);
        anim_sub = AnimationUtils.loadAnimation(MerchantActivity.this, R.anim.anim_add2cart);
    }

    private void initView() {
        popView = getLayoutInflater().inflate(R.layout.popup_cart, null);
        lv_pop_cart = (ListView) popView.findViewById(R.id.lv_cart);
        iv_pop_cart = (ImageView) popView.findViewById(R.id.iv_cart);
        tv_pop_dot = (TextView) popView.findViewById(R.id.tv_dot);
        tv_pop_totalPrice = (TextView) popView.findViewById(R.id.tv_totalPrice);
        btn_pop_ok = (Button) popView.findViewById(R.id.btn_ok);
        popupWindow = new PopupWindow(popView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);//可以获得焦点
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_pop_bg));
        //消失的监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.update();
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        qs = intent.getFloatExtra("qs", 0);
        id = intent.getIntExtra("id", 0);
        setTitle(name);
        btn_ok.setText("￥" + qs + "起送");
        fragments = new ArrayList<>();
        f1 = new GoodsFragment();
        f2 = new StoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("id", id);
        f1.setArguments(bundle);
        f2.setArguments(bundle);
        fragments.add(f1);
        fragments.add(f2);

        format = new DecimalFormat("0.00");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });

        iv_cart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cart:
                //显示购物车详情
//                popupWindow.setHeight(DensityUtil.dip2px(200));
//                popupWindow.showAsDropDown(layout_cart,0,-DensityUtil.dip2px(48));
//                backgroundAlpha(0.4f);
                getCarts();
                break;
        }
    }

    /**
     * 设置背景的透明度
     *
     * @param bgAlpha
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void addToCart(FoodInfo foodInfo, ImageView imageView, ImageButton ib_sub, final TextView tv_count) {
        updateCart(WaiMaiConfig.URL_CART_ADD,foodInfo,imageView,ib_sub);
    }

    @Override
    public void subFromCart(FoodInfo foodInfo, ImageButton ib_sub, TextView tv_count) {
        updateCart(WaiMaiConfig.URL_CART_SUB,foodInfo,null,ib_sub);
    }

    private void updateCart(String url, final FoodInfo foodInfo, final ImageView imageView, final ImageButton ib_sub){
        RequestParams params = new RequestParams(url);
        params.addParameter("uid",1);
        params.addParameter("mid",1);
        params.addParameter("fid",foodInfo.getId());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String json) {
                Log.d(TAG, "onSuccess() called with: " + "json = [" + json + "]");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String result = jsonObject.getString("result");
                    if(result.equals("AddSuccess")){
                        if (!ib_sub.isShown()) {
                            ib_sub.setVisibility(View.VISIBLE);
                            ib_sub.setEnabled(true);
                            Animation animation = AnimationUtils.loadAnimation(x.app(), R.anim.anim_ib_sub_in);
                            ib_sub.startAnimation(animation);

                        }
                        Log.d(TAG, "addToCart() called with: " + "foodInfo = [" + foodInfo + "], imageView = [" + imageView + "], ib_sub = [" + ib_sub + "]");
                        if(imageView != null){
                            AddCartAnimation.AddToCart(imageView,iv_cart,MerchantActivity.this,mRl,1);
                        }
                        String name = foodInfo.getName();
                        int count = 0;
                        if (name.contains("?")) {
                            String countStr = name.substring(name.lastIndexOf("?") + 1);
                            count = Integer.parseInt(countStr);
                            name = name.substring(0, name.lastIndexOf("?"));
                        }
                        count++;
                        foodInfo.setName(name + "?" + count);
                        f1.notifyDataSetChanged();
                        totalCount++;
                        totalPrice+=foodInfo.getPrice();
                        initCartDetail();
                        Log.d(TAG, "addToCart: " + count + "--" + name);
                    }else if (result.equals("SubSuccess")){
                        Log.d(TAG, "subFromCart() called with: " + "foodInfo = [" + foodInfo + "], ib_sub = [" + ib_sub + "]");
                        String name = foodInfo.getName();
                        int count = 0;
                        String countStr = name.substring(name.lastIndexOf("?") + 1);
                        name = name.substring(0, name.lastIndexOf("?"));
                        count = Integer.parseInt(countStr);
                        count--;
                        foodInfo.setName(name + "?" + count);
                        if (count == 0) {
                            foodInfo.setName(name);
//                            tv_count.setVisibility(View.INVISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(x.app(), R.anim.anim_ib_sub_out);
                            ib_sub.startAnimation(animation);
                            ib_sub.setVisibility(View.INVISIBLE);
                            ib_sub.setEnabled(false);
                        }
                        totalCount--;
                        totalPrice-=foodInfo.getPrice();
                        initCartDetail();
                        f1.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError() called with: " + "ex = [" + ex + "], isOnCallback = [" + isOnCallback + "]");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem() called with: " + "position = [" + position + "]");
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "TAB" + position;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        int position = tab.getPosition();
        Log.d(TAG, "onTabSelected: " + position);
        tabHost.setSelectedNavigationItem(position);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
