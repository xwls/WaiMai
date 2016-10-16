package com.oaec.waimai.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oaec.waimai.R;
import com.oaec.waimai.activity.LoginActivity;
import com.oaec.waimai.activity.SettingActivity;
import com.oaec.waimai.util.WaiMaiConfig;
import com.umeng.analytics.MobclickAgent;
import com.wx.ovalimageview.RoundImageView;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Kevin on 2016/10/8.
 * Description：首页的Fragment
 */
@ContentView(R.layout.fragment_mine)
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQ_LOGIN = 0x11;
    @ViewInject(R.id.btn_toLogin)
    private Button btn_toLogin;
    @ViewInject(R.id.iv_img)
    private ImageView iv_img;
    @ViewInject(R.id.tv_nickName)
    private TextView tv_nickName;
    @ViewInject(R.id.riv_border)
    private RoundImageView riv_border;
    @ViewInject(R.id.ib_setting)
    private ImageButton ib_setting;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEvent();
    }

    private void initEvent() {
        btn_toLogin.setOnClickListener(this);
        ib_setting.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("我的");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){

            case REQ_LOGIN:
                if(resultCode == Activity.RESULT_OK){
                    String nickName = data.getStringExtra("nickName");
                    String img = data.getStringExtra("img");
                    btn_toLogin.setVisibility(View.INVISIBLE);
                    tv_nickName.setVisibility(View.VISIBLE);
                    tv_nickName.setText(nickName);
                    ImageOptions options = new ImageOptions.Builder()
                            .setSize(DensityUtil.dip2px(90),DensityUtil.dip2px(90))
                            .setUseMemCache(true)
                            .setCircular(true)
                            .setLoadingDrawableId(R.drawable.user)
                            .build();
                    x.image().bind(iv_img, WaiMaiConfig.URL+img,options);
                    riv_border.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_setting:
                Intent intent1 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_toLogin:
                Intent intent = new Intent(x.app(), LoginActivity.class);
                startActivityForResult(intent,REQ_LOGIN);
                break;
        }
    }
}
