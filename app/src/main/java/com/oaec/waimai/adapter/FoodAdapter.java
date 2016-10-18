package com.oaec.waimai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private static final String TAG = "FoodAdapter";
    private List<Food> foods;
    private Context context;

    private OnConvertListener onConvertListener;

    public void setOnConvertListener(OnConvertListener onConvertListener) {
        this.onConvertListener = onConvertListener;
    }

    public FoodAdapter(Context context, List<Food> foods) {
        Log.d(TAG, "FoodAdapter() called with: " + "foods = [" + foods + "]");
        this.context = context;
        this.foods = foods;
    }

    @Override
    public Object getItem(int section, int position) {
        return foods.get(section).getFoods().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        if (position == -1){
            return 0;
        }
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_right, null);
            holder = new ViewHolder();
            holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_xl = (TextView) convertView.findViewById(R.id.tv_xl);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.ib_sub = (ImageButton) convertView.findViewById(R.id.ib_sub);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.ib_add = (ImageButton) convertView.findViewById(R.id.ib_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FoodInfo info = foods.get(section).getFoods().get(position);
        ImageOptions options = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(82), DensityUtil.dip2px(82))
                .setUseMemCache(true)
                .build();
        x.image().bind(holder.iv_img, info.getImg(), options);
        String name = info.getName();
        if (name.indexOf("?") > 0) {
            holder.tv_count.setText(name.substring(name.lastIndexOf("?") + 1));
            name = name.substring(0, name.lastIndexOf("?"));
            holder.ib_sub.setVisibility(View.VISIBLE);
            holder.tv_count.setVisibility(View.VISIBLE);
        } else {
            holder.ib_sub.setVisibility(View.INVISIBLE);
            holder.tv_count.setVisibility(View.INVISIBLE);
        }
        holder.tv_name.setText(name);
        holder.tv_xl.setText("月售" + info.getXl() + "份");
        holder.tv_price.setText("￥" + info.getPrice());
//        holder.tv_count.setText(info.getCount()+"");
        final ViewHolder finalHolder = holder;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_add://+
                        if (onConvertListener != null) {
                            onConvertListener.clickAdd(info, finalHolder.iv_img,finalHolder.ib_sub, finalHolder.tv_count);
                        }
                        break;
                    case R.id.ib_sub://-
                        if (onConvertListener != null) {
                            onConvertListener.clickSub(info, finalHolder.ib_sub, finalHolder.tv_count);
                        }
                        break;
                }
            }
        };
        holder.ib_add.setOnClickListener(onClickListener);
        holder.ib_sub.setOnClickListener(onClickListener);
        return convertView;
    }

    public interface OnConvertListener {
        void clickAdd(FoodInfo foodInfo,ImageView imageView,ImageButton ib_sub,TextView tv_count);
        void clickSub(FoodInfo foodInfo, ImageButton ib_sub,TextView tv_count);
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

    public void onDataSetChanged(List<Food> foods) {
        this.foods = foods;
        this.notifyDataSetChanged();
    }

    public class ViewHolder {
        public ImageView iv_img;
        TextView tv_name;
        TextView tv_xl;
        TextView tv_price;
        public ImageButton ib_sub;
        public TextView tv_count;
        public ImageButton ib_add;
    }
}
