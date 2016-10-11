package com.oaec.waimai.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.oaec.waimai.R;

/**
 * Created by Kevin on 2016/9/30.
 * Description：
 */
public class LoadListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "LoadListView";
    private View footer;//底部布局
    private int firstVisibleItem;//第一个可见的item
    private int visibleItemCount;//可见item的总数量
    private int totalItemCount;//item的总数量
    private OnLoadListener onLoadListener;//接口变量
    private boolean isLoading = false;

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //为接口提供set方法
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    private void initView(Context context){
        footer = LayoutInflater.from(context).inflate(R.layout.footer_layout,null);
        //添加ListView底部布局
        this.addFooterView(footer);
        //开始的时候，底部布局隐藏
        footer.setVisibility(View.GONE);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        Log.d(TAG, "onScrollStateChanged: "+scrollState);
//        if((firstVisibleItem + visibleItemCount == totalItemCount)&&(scrollState == SCROLL_STATE_IDLE)){
//            //显示底部布局
//            footer.setVisibility(View.VISIBLE);
//            if (onLoadListener != null) {
//                if(!isLoading){
//                    isLoading = true;
//                    onLoadListener.onLoad();
//                }
//            }
//        }
    }

    /**
     * 开始加载更多
     */
    public void startLoad(){
        footer.setVisibility(View.VISIBLE);
        if (onLoadListener != null) {
            if(!isLoading){
                isLoading = true;
                onLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        Log.d(TAG, "onScroll() called with: " + "view = [" + view + "], firstVisibleItem = [" + firstVisibleItem + "], visibleItemCount = [" + visibleItemCount + "], totalItemCount = [" + totalItemCount + "]");
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
        if(onLoadListener != null){
            onLoadListener.onScroll(view,firstVisibleItem);
        }
    }

    public void loadComplete(){
        footer.setVisibility(View.GONE);
        isLoading = false;
    }

    //加载时回调接口
    public interface OnLoadListener{
        void onLoad();
        void onScroll(AbsListView view, int firstVisibleItem);
    }
}
