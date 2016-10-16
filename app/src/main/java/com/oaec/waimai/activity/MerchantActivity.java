package com.oaec.waimai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oaec.waimai.R;
import com.oaec.waimai.entity.FoodInfo;
import com.oaec.waimai.fragment.GoodsFragment;
import com.oaec.waimai.fragment.StoreFragment;
import com.oaec.waimai.util.AddCartAnimation;
import com.umeng.analytics.MobclickAgent;

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
public class MerchantActivity extends AppCompatActivity implements MaterialTabListener, GoodsFragment.Add2ShoppingCartListener {

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

    private List<Fragment> fragments;

    private GoodsFragment f1;
    private StoreFragment f2;

    private float totalPrice;//总价钱
    private int totalCount;//总数量
    private float qs;//起送价
    private Animation anim_add;
    private Animation anim_sub;

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
    }

    private void initAnim() {
        anim_add = AnimationUtils.loadAnimation(MerchantActivity.this, R.anim.anim_add2cart);
        anim_add.setStartOffset(1000);
        anim_sub = AnimationUtils.loadAnimation(MerchantActivity.this, R.anim.anim_add2cart);
    }

    private void initView() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        qs = intent.getFloatExtra("qs", 0);
        int id = intent.getIntExtra("id", 0);
        setTitle(name);
        btn_ok.setText("￥"+qs+"起送");
        fragments = new ArrayList<>();
        f1 = new GoodsFragment();
        f2 = new StoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("id",id);
        f1.setArguments(bundle);
        f2.setArguments(bundle);
        fragments.add(f1);
        fragments.add(f2);

        f1.setAdd2ShoppingCartListener(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });
    }

    @Override
    public void add2ShoppingCart(View v, ImageView imageView, FoodInfo foodInfo, int count) {
        Log.d(TAG, "add2ShoppingCart() called with: " + "foodInfo = [" + foodInfo + "], count = [" + count + "]");
        switch (v.getId()){
            case R.id.ib_add:
                totalPrice+=foodInfo.getPrice();
                totalCount++;
                AddCartAnimation.AddToCart(imageView,iv_cart,MerchantActivity.this,mRl,1);
                tv_dot.startAnimation(anim_add);
                break;
            case R.id.ib_sub:
                totalPrice -= foodInfo.getPrice();
                totalCount--;
                tv_dot.startAnimation(anim_sub);
                break;
        }
        DecimalFormat format = new DecimalFormat("0.00");
        totalPrice = Float.parseFloat(format.format(totalPrice));
        if (totalPrice > 0){
            iv_cart.setImageResource(R.drawable.ic_cart_red);
            tv_dot.setVisibility(View.VISIBLE);
            tv_dot.setText(totalCount+"");
            tv_totalPrice.setText("共：￥"+totalPrice +"");
        }else{
            iv_cart.setImageResource(R.drawable.ic_cart);
            tv_dot.setVisibility(View.INVISIBLE);
            tv_dot.setText("");
            tv_totalPrice.setText("购物车是空的");
        }
        if(totalPrice >= qs && totalCount > 0){
            btn_ok.setEnabled(true);
            btn_ok.setText("去下单");
        }else{
            btn_ok.setEnabled(false);
            if (totalPrice == 0){
                btn_ok.setText("￥"+qs+"起送");
            }else {
                String subPrice = format.format(qs - totalPrice);
                btn_ok.setText("还差￥"+subPrice+"起送");
            }
        }

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
            return "TAB"+position;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        int position = tab.getPosition();
        Log.d(TAG, "onTabSelected: "+ position);
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
