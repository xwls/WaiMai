package com.oaec.waimai.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oaec.waimai.R;
import com.oaec.waimai.adapter.FoodAdapter;
import com.oaec.waimai.entity.Cart;
import com.oaec.waimai.entity.Food;
import com.oaec.waimai.entity.FoodInfo;
import com.oaec.waimai.util.CommonAdapter;
import com.oaec.waimai.util.ViewHolder;
import com.oaec.waimai.widget.PinnedHeaderListView;

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
    private List<Cart> carts;
    private Add2ShoppingCartListener add2ShoppingCartListener;
    private int id;

    public void setAdd2ShoppingCartListener(Add2ShoppingCartListener add2ShoppingCartListener) {
        this.add2ShoppingCartListener = add2ShoppingCartListener;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        id = bundle.getInt("id");
        foods = new ArrayList<>();
        carts = new ArrayList<>();
        adapter = new FoodAdapter(getActivity(), foods);
        lv_right.setAdapter(adapter);
        LayoutInflater inflator = LayoutInflater.from(x.app());
        LinearLayout footer = (LinearLayout) inflator.inflate(R.layout.list_item, null);
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
            public void clickAdd(FoodInfo foodInfo,ImageView imageView, ImageButton ib_sub, final TextView tv_count) {
                if (add2ShoppingCartListener != null){
                    add2ShoppingCartListener.addToCart(foodInfo,imageView,ib_sub,tv_count);
                }
            }

            @Override
            public void clickSub(FoodInfo foodInfo, ImageButton ib_sub, TextView tv_count) {
                if (add2ShoppingCartListener != null){
                    add2ShoppingCartListener.subFromCart(foodInfo,ib_sub,tv_count);
                }
            }
        });

//        adapter.setOnConvertListener(new FoodAdapter.OnConvertListener() {
//            @Override
//            public void onConvert(final FoodAdapter.ViewHolder holder, int section, int position) {
//                final FoodInfo foodInfo = foods.get(section).getFoods().get(position);
//                View.OnClickListener onClickListener = new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        Log.d(TAG, "onClick() called with: " + "v = [" + v + "]");
//                        ImageButton ib_sub = holder.ib_sub;
//                        ImageButton ib_add = holder.ib_add;
//                        final TextView tv_count = holder.tv_count;
//                        int count = 0;
//                        String name = foodInfo.getName();
//                        if (name.indexOf("?") > 0) {
//                            count = Integer.parseInt(name.substring(name.lastIndexOf("?") + 1));
//                            name = name.substring(0, name.lastIndexOf("?"));
//                        }
//                        switch (v.getId()) {
//                            case R.id.ib_add://加入购物车
//                                if (!ib_sub.isShown()) {
//                                    ib_sub.setVisibility(View.VISIBLE);
//                                    ib_sub.setEnabled(true);
//                                    Animation animation = AnimationUtils.loadAnimation(x.app(), R.anim.anim_ib_sub_in);
//                                    ib_sub.startAnimation(animation);
//                                    animation.setAnimationListener(new Animation.AnimationListener() {
//                                        @Override
//                                        public void onAnimationStart(Animation animation) {
//
//                                        }
//
//                                        @Override
//                                        public void onAnimationEnd(Animation animation) {
//                                            tv_count.setVisibility(View.VISIBLE);
//                                        }
//
//                                        @Override
//                                        public void onAnimationRepeat(Animation animation) {
//
//                                        }
//                                    });
//                                }
//                                count += 1;
//                                break;
//                            case R.id.ib_sub:
//                                count -= 1;
//                                if (count == 0) {
//                                    tv_count.setVisibility(View.INVISIBLE);
//                                    Animation animation = AnimationUtils.loadAnimation(x.app(), R.anim.anim_ib_sub_out);
//                                    ib_sub.startAnimation(animation);
//                                    ib_sub.setVisibility(View.INVISIBLE);
//                                    ib_sub.setEnabled(false);
//                                }
//                                break;
//                        }
//                        if (count > 0) {
//                            foodInfo.setName(name + "?" + count);
//                        } else {
//                            foodInfo.setName(name);
//                        }
////                        tv_count.setText(count + "");
////                        foodInfo.setCount(count);
////                        Log.d(TAG, "onClick: "+foodInfo+"--"+count);
//                        adapter.notifyDataSetChanged();
////                        FoodAdapter adapter = new FoodAdapter(x.app(), foods);
////                        lv_right.setAdapter(adapter);
//                        if (add2ShoppingCartListener != null) {
//                            add2ShoppingCartListener.add2ShoppingCart(v, holder.iv_img, foodInfo, count);
//                        }
//                    }
//                };
//                holder.ib_add.setOnClickListener(onClickListener);
//                holder.ib_sub.setOnClickListener(onClickListener);
//            }
//        });
    }

    public void notifyDataSetChanged(){
        adapter.onDataSetChanged(foods);
        Log.d(TAG, "notifyDataSetChanged: ");
    }
    
    public interface Add2ShoppingCartListener {
        void addToCart(FoodInfo foodInfo,ImageView imageView, ImageButton ib_sub, final TextView tv_count);
        void subFromCart(FoodInfo foodInfo, ImageButton ib_sub, TextView tv_count);
    }

    /**
     * 宿主Activity请求到购物车数据之后执行
     *
     * @param carts
     */
    public void initCarts(List<Cart> carts, List<Food> foods) {
        setCount(carts, foods);
        this.carts = carts;
        this.foods = foods;
        Log.d(TAG, "initCarts() called with: " + "carts = [" + carts + "], foods = [" + foods + "]");
//        adapter.onDataSetChanged(this.foods);
        if (lv_left.getAdapter() == null) {
            List<String> types = new ArrayList<>();
            for (Food food : foods) {
                types.add(food.getType());
            }
            lv_left.setAdapter(new CommonAdapter<String>(getActivity(), types, R.layout.list_item_left) {
                @Override
                public void convert(int position, ViewHolder holder, String s) {
                    holder.setText(R.id.tv_title, s);
                }
            });
            setLeft(0);
        }
        adapter = new FoodAdapter(getActivity(), this.foods);
        lv_right.setAdapter(adapter);
        initEvent();
    }


    /**
     * 根据购物车中商品的数量设置到商品属性
     *
     * @param carts
     * @param foods
     */
    private void setCount(List<Cart> carts, List<Food> foods) {
        if (carts != null && carts.size() > 0 && foods != null && foods.size() > 0) {
            for (int i = 0; i < carts.size(); i++) {
                Cart cart = carts.get(i);
                int fid = cart.getFid();
                for (int j = 0; j < foods.size(); j++) {
                    List<FoodInfo> foodInfos = foods.get(j).getFoods();
                    for (int k = 0; k < foodInfos.size(); k++) {
                        FoodInfo foodInfo = foodInfos.get(k);
                        if (foodInfo.getId() == fid) {
                            String name = foodInfo.getName();
                            if (name.contains("?")) {
                                name = name.substring(0, name.lastIndexOf("?"));
                            }
                            foodInfo.setName(name + "?" + cart.getCount());
                        }
                    }
                }
            }
        }
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
