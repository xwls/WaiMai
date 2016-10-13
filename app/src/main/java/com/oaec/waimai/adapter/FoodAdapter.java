package com.oaec.waimai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oaec.waimai.R;
import com.oaec.waimai.entity.Food;
import com.oaec.waimai.entity.FoodInfo;
import com.oaec.waimai.widget.SectionedBaseAdapter;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Kevin on 2016/10/12.
 * Description：
 */
public class FoodAdapter extends SectionedBaseAdapter {
    private List<Food> foods;
    private Context context;

    public FoodAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @Override
    public Object getItem(int section, int position) {
        return foods.get(section).getFoods().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return foods.get(section).getFoods().get(position).getId();
    }

    @Override
    public int getSectionCount() {
        return foods.size();
    }

    @Override
    public int getCountForSection(int section) {
        return foods.get(section).getFoods().size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_right,null);
            holder = new ViewHolder();
            holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_xl = (TextView) convertView.findViewById(R.id.tv_xl);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        FoodInfo foodInfo = foods.get(section).getFoods().get(position);
        ImageOptions options = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(82), DensityUtil.dip2px(82))
                .setUseMemCache(true)
                .build();
        x.image().bind(holder.iv_img,foodInfo.getImg(),options);
        holder.tv_name.setText(foodInfo.getName());
        holder.tv_xl.setText("月售"+foodInfo.getXl()+"份");
        holder.tv_price.setText("￥"+foodInfo.getPrice());
        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        Food food = foods.get(section);
        ((TextView) layout.findViewById(R.id.textItem)).setText(food.getType());
        return layout;
    }
    private class ViewHolder{
        ImageView iv_img;
        TextView tv_name;
        TextView tv_xl;
        TextView tv_price;
    }
}
