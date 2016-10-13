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

import com.oaec.waimai.R;
import com.oaec.waimai.fragment.GoodsFragment;
import com.oaec.waimai.fragment.StoreFragment;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

@ContentView(R.layout.activity_merchant)
public class MerchantActivity extends AppCompatActivity implements MaterialTabListener {

    private static final String TAG = "MerchantActivity";
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.tabHost)
    private MaterialTabHost tabHost;
    @ViewInject(R.id.container)
    private ViewPager viewPager;

    private List<Fragment> fragments;

    private GoodsFragment f1;
    private StoreFragment f2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabHost.addTab(tabHost.newTab().setText("商品").setTabListener(this));
        tabHost.addTab(tabHost.newTab().setText("商家").setTabListener(this));
        initView();

    }

    private void initView() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        int id = intent.getIntExtra("id", 0);
        setTitle(name);
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

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
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
