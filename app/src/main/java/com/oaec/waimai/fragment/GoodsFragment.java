package com.oaec.waimai.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.oaec.waimai.R;
import com.oaec.waimai.adapter.FoodAdapter;
import com.oaec.waimai.entity.Food;
import com.oaec.waimai.util.CommonAdapter;
import com.oaec.waimai.util.ViewHolder;
import com.oaec.waimai.util.WaiMaiConfig;
import com.oaec.waimai.widget.PinnedHeaderListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2016/10/12.
 * Description：
 */
@ContentView(R.layout.fragment_goods)
public class GoodsFragment extends BaseFragment {
    @ViewInject(R.id.lv_left)
    private ListView lv_left;

    @ViewInject(R.id.lv_right)
    private PinnedHeaderListView lv_right;
    private FoodAdapter adapter;
    private boolean isScroll;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        int id = bundle.getInt("id");
        getFoods(id);
//        initEvent();
        LayoutInflater inflator = LayoutInflater.from(x.app());
        LinearLayout footer = (LinearLayout) inflator.inflate(R.layout.list_item, null);
//        ((TextView) footer.findViewById(R.id.textItem)).setText("FOOTER");
//        lv_left.addFooterView(footer);
        lv_right.addFooterView(footer);
    }

    private void initEvent() {
        lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isScroll = false;
                setLeft(position);
                int rightSection = 0;
                for(int i=0;i<position;i++){
                    rightSection += adapter.getCountForSection(i)+1;
                }
                lv_right.setSelection(rightSection);
            }
        });
        lv_right.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(isScroll){
                    setLeft(adapter.getSectionForPosition(firstVisibleItem));
                    isScroll = false;
                }else{
                    isScroll = true;
                }
            }
        });
    }

    private void getFoods(int id) {
        RequestParams params = new RequestParams(WaiMaiConfig.URL_FOODS);
        params.addParameter("mid",id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                List<Food> foods = JSON.parseArray(result, Food.class);
                List<String> types = new ArrayList<String>();
                for (int i = 0; i < foods.size(); i++) {
                    types.add(foods.get(i).getType());
                }
                lv_left.setAdapter(new CommonAdapter<String>(x.app(),types,R.layout.list_item_left) {
                    @Override
                    public void convert(int position, ViewHolder holder, String s) {
                        if(position == 0){
                            holder.setBackgroundColor(R.id.iv_left,Color.RED);
                            holder.setBackgroundColor(R.id.tv_title,Color.WHITE);
                        }else{
                            holder.setBackgroundColor(R.id.iv_left,Color.argb(255,245,245,245));
                            holder.setBackgroundColor(R.id.tv_title,Color.argb(255,245,245,245));
                        }
                        holder.setText(R.id.tv_title,s);
                    }
                });
                adapter = new FoodAdapter(x.app(), foods);
                lv_right.setAdapter(adapter);
                initEvent();
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

    private void setLeft(int position){
        int color = Color.argb(255, 245, 245, 245);
        for (int i = 0; i < lv_left.getChildCount(); i++) {
            View view = lv_left.getChildAt(i);
            ImageView iv_left = (ImageView) view.findViewById(R.id.iv_left);
            TextView tv_left = (TextView) view.findViewById(R.id.tv_title);
            if (i == position){
                tv_left.setBackgroundColor(Color.WHITE);
                iv_left.setBackgroundColor(Color.RED);
            }else {
                tv_left.setBackgroundColor(color);
                iv_left.setBackgroundColor(color);
            }

        }
    }
}
