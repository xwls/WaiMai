package com.oaec.waimai.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.oaec.waimai.R;
import com.oaec.waimai.adapter.FoodAdapter;
import com.oaec.waimai.entity.Food;
import com.oaec.waimai.entity.FoodInfo;
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
    private static final String TAG = "GoodsFragment";
    @ViewInject(R.id.lv_left)
    private ListView lv_left;

    @ViewInject(R.id.lv_right)
    private PinnedHeaderListView lv_right;
    private FoodAdapter adapter;
    private boolean isScroll;
    private List<Food> foods;
    private Add2ShoppingCartListener add2ShoppingCartListener;

    public void setAdd2ShoppingCartListener(Add2ShoppingCartListener add2ShoppingCartListener) {
        this.add2ShoppingCartListener = add2ShoppingCartListener;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        int id = bundle.getInt("id");
        foods = new ArrayList<Food>();
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
                for (int i = 0; i < position; i++) {
                    rightSection += adapter.getCountForSection(i) + 1;
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
                if (isScroll) {
                    setLeft(adapter.getSectionForPosition(firstVisibleItem));
                    isScroll = false;
                } else {
                    isScroll = true;
                }
            }
        });

        adapter.setOnConvertListener(new FoodAdapter.OnConvertListener() {
            @Override
            public void onConvert(final FoodAdapter.ViewHolder holder, int section, int position) {
                final FoodInfo foodInfo = foods.get(section).getFoods().get(position);
                View.OnClickListener onClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ImageButton ib_sub = holder.ib_sub;
                        ImageButton ib_add = holder.ib_add;
                        final TextView tv_count = holder.tv_count;
                        int count = 0;
                        String name = foodInfo.getName();
                        if(name.indexOf("?") > 0){
                            count = Integer.parseInt(name.substring(name.lastIndexOf("?")+1));
                            name = name.substring(0,name.lastIndexOf("?"));
                        }
                        switch (v.getId()) {
                            case R.id.ib_add://加入购物车
                                if (!ib_sub.isShown()) {
                                    ib_sub.setVisibility(View.VISIBLE);
                                    ib_sub.setEnabled(true);
                                    Animation animation = AnimationUtils.loadAnimation(x.app(), R.anim.anim_ib_sub_in);
                                    ib_sub.startAnimation(animation);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            tv_count.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                }
                                count += 1;
                                break;
                            case R.id.ib_sub:
                                count -= 1;
                                if (count == 0) {
                                    tv_count.setVisibility(View.INVISIBLE);
                                    Animation animation = AnimationUtils.loadAnimation(x.app(), R.anim.anim_ib_sub_out);
                                    ib_sub.startAnimation(animation);
                                    ib_sub.setVisibility(View.INVISIBLE);
                                    ib_sub.setEnabled(false);
                                }
                                break;
                        }
                        if(count > 0){
                            foodInfo.setName(name+"?"+count);
                        }else {
                            foodInfo.setName(name);
                        }
                        tv_count.setText(count + "");
//                        foodInfo.setCount(count);
//                        Log.d(TAG, "onClick: "+foodInfo+"--"+count);
//                        adapter.notifyDataSetChanged();
//                        FoodAdapter adapter = new FoodAdapter(x.app(), foods);
//                        lv_right.setAdapter(adapter);
                        if (add2ShoppingCartListener != null) {
                            add2ShoppingCartListener.add2ShoppingCart(v, holder.iv_img, foodInfo, count);
                        }
                    }
                };
                holder.ib_add.setOnClickListener(onClickListener);
                holder.ib_sub.setOnClickListener(onClickListener);
            }
        });
    }

    public interface Add2ShoppingCartListener {
        void add2ShoppingCart(View v, ImageView imageView, FoodInfo foodInfo, int count);
    }

    private void getFoods(int id) {
        RequestParams params = new RequestParams(WaiMaiConfig.URL_FOODS);
        params.addParameter("mid", id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                foods = JSON.parseArray(result, Food.class);
                List<String> types = new ArrayList<String>();
                for (int i = 0; i < foods.size(); i++) {
                    types.add(foods.get(i).getType());
                }
                lv_left.setAdapter(new CommonAdapter<String>(x.app(), types, R.layout.list_item_left) {
                    @Override
                    public void convert(int position, ViewHolder holder, String s) {
//                        if(position == 0){
//                            holder.setBackgroundColor(R.id.iv_left,Color.RED);
//                            holder.setBackgroundColor(R.id.tv_title,Color.WHITE);
//                        }else{
//                            holder.setBackgroundColor(R.id.iv_left,Color.argb(255,245,245,245));
//                            holder.setBackgroundColor(R.id.tv_title,Color.argb(255,245,245,245));
//                        }
                        holder.setText(R.id.tv_title, s);
                    }
                });
                adapter = new FoodAdapter(x.app(), foods);
                lv_right.setAdapter(adapter);
                setLeft(0);
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

    private void setLeft(int position) {
        int color = Color.argb(255, 245, 245, 245);
        for (int i = 0; i < lv_left.getChildCount(); i++) {
            View view = lv_left.getChildAt(i);
            ImageView iv_left = (ImageView) view.findViewById(R.id.iv_left);
            TextView tv_left = (TextView) view.findViewById(R.id.tv_title);
            if (i == position) {
                tv_left.setBackgroundColor(Color.WHITE);
                iv_left.setBackgroundColor(Color.RED);
            } else {
                tv_left.setBackgroundColor(color);
                iv_left.setBackgroundColor(color);
            }

        }
    }
}
