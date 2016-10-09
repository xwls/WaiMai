package com.oaec.waimai.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oaec.waimai.R;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Kevin on 2016/9/26.
 */
public class ViewHolder {
    private static final String TAG = "ViewHolder";
    //用来存放View的容器
    private SparseArray<View> mViews;
    //Item的View
    private View convertView;
    private int position;

    private ViewHolder(Context context, int layoutId, int position) {
        mViews = new SparseArray<View>();
        this.position = position;
        convertView = LayoutInflater.from(context).inflate(layoutId, null);
        convertView.setTag(this);
    }

    public static ViewHolder getInstance(Context context, View convertView, int layoutId, int position) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder(context, layoutId, position);
            convertView = holder.convertView;
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.position = position;
        }
        return holder;
    }

    public <T extends View> T getView(int viewId, Class<T> c) {
        View v = mViews.get(viewId);
        if (v == null) {
            v = convertView.findViewById(viewId);
            mViews.put(viewId, v);
        }
        return (T) v;
    }

    public View getConvertView() {
        return convertView;
    }

    /**
     * 设置TextView显示的文字
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId, TextView.class);
        view.setText(text);
        return this;
    }

    public void setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId, ImageView.class);
        view.setImageResource(resId);
    }

    public ViewHolder setImageUrl(int viewId,String url){
        ImageView view = getView(viewId, ImageView.class);
        ImageOptions options = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120),DensityUtil.dip2px(80))//设置宽高
                .setRadius(DensityUtil.dip2px(5))//设置圆角
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)//图片拉伸显示
                .setUseMemCache(true)//使用缓存
                .setLoadingDrawableId(R.mipmap.ic_launcher)//加载时显示的图片
                .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败显示的图片
                .build();
        x.image().bind(view,url,options);
        return this;
    }
}
