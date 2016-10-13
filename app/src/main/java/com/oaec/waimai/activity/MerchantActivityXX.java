package com.oaec.waimai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;
import com.oaec.waimai.R;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_merchantxx)
public class MerchantActivityXX extends AppCompatActivity {
    @ViewInject(R.id.iv_img)
    private ImageView iv_img;
    @ViewInject(R.id.ratingbar)
    private RatingBar ratingBar;
    @ViewInject(R.id.tv_grade)
    private TextView tv_grade;
    @ViewInject(R.id.tv_xl)
    private TextView tv_xl;
    @ViewInject(R.id.tv_qs)
    private TextView tv_qs;
    @ViewInject(R.id.tv_psf)
    private TextView tv_psf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initTitle();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * 初始化标题栏
     */
    private void initTitle() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");
        String img = intent.getStringExtra("img");
        float grade = intent.getFloatExtra("grade", 0);
        int xl = intent.getIntExtra("xl", 0);
        float qs = intent.getFloatExtra("qs", 0);
        float psf = intent.getFloatExtra("psf", 0);
        setTitle(name);
        ImageOptions options = new ImageOptions.Builder().setUseMemCache(true).setRadius(2).build();
        x.image().bind(iv_img,img,options);
        tv_grade.setText(" "+grade);
        ratingBar.setStar(grade);
        tv_xl.setText("月销量："+xl+"单");
        tv_qs.setText("￥"+qs+"起送");
        tv_psf.setText(" / 配送费￥"+psf);
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
}
