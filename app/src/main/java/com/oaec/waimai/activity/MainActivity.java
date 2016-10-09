package com.oaec.waimai.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.oaec.waimai.R;
import com.oaec.waimai.adapter.FragmentTabAdapter;
import com.oaec.waimai.fragment.HomeFragment;
import com.oaec.waimai.fragment.MineFragment;
import com.oaec.waimai.fragment.OrderFragment;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    @ViewInject(R.id.rg_tab)
    private RadioGroup rg_tab;

    @ViewInject(R.id.rb_home)
    private RadioButton rb_home;
    @ViewInject(R.id.rb_order)
    private RadioButton rb_order;
    @ViewInject(R.id.rb_mine)
    private RadioButton rb_mine;

    private List<Fragment> fragments;
    private List<String> tags;
    private HomeFragment homeFragment;
    private OrderFragment orderFragment;
    private MineFragment mineFragment;
    private FragmentTabAdapter tabAdapter;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        fragments = new ArrayList<>();
        tags = new ArrayList<>();
        //初始化Fragment
        homeFragment = new HomeFragment();
        orderFragment = new OrderFragment();
        mineFragment = new MineFragment();
        //添加Fragment到集合
        fragments.add(homeFragment);
        fragments.add(orderFragment);
        fragments.add(mineFragment);
        //添加对应的TAG
        tags.add("home");
        tags.add("order");
        tags.add("mine");
        rg_tab.check(R.id.rb_home);
        tabAdapter = new FragmentTabAdapter(this, fragments, tags, R.id.frame, rg_tab);

        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                Log.d(TAG, "OnRgsExtraCheckedChanged() called with: " + "radioGroup = [" + radioGroup + "], checkedId = [" + checkedId + "], index = [" + index + "]");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(x.app());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(x.app());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(tabAdapter.getCurrentTab() != 0){
                rg_tab.check(R.id.rb_home);
                return true;
            }else {
                long cur = System.currentTimeMillis();
                if(lastTime == 0 || cur - lastTime >= 2000){
                    lastTime = cur;
                    Toast.makeText(MainActivity.this, "再按一次返回键退出！", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
