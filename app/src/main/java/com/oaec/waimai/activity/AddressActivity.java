package com.oaec.waimai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.oaec.waimai.R;
import com.oaec.waimai.util.CommonAdapter;
import com.oaec.waimai.util.ViewHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_address)
public class AddressActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener {

    private static final String TAG = "AddressActivity";

    @ViewInject(R.id.lv_address)
    private ListView listView;

    private CommonAdapter<PoiItem> adapter;
    private ArrayList<PoiItem> pois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);
        PoiSearch.Query query = new PoiSearch.Query("", "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施");
        PoiSearch poiSearch = new PoiSearch(x.app(), query);
        poiSearch.setOnPoiSearchListener(this);
        LatLonPoint latLonPoint = new LatLonPoint(latitude,longitude);
        PoiSearch.SearchBound searchBound = new PoiSearch.SearchBound(latLonPoint,800);
        poiSearch.setBound(searchBound);
        poiSearch.searchPOIAsyn();
        pois = new ArrayList<PoiItem>();
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        Log.d(TAG, "onPoiSearched() called with: " + "poiResult = [" + poiResult + "], i = [" + i + "]");
        pois = poiResult.getPois();
        adapter = new CommonAdapter<PoiItem>(x.app(),pois,R.layout.list_item_address) {
            @Override
            public void convert(ViewHolder holder, PoiItem poiItem) {
                holder.setText(R.id.tv_poiTitle,poiItem.getTitle())
                        .setText(R.id.tv_distance,poiItem.getDistance()+"米");
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        Log.d(TAG, "onPoiItemSearched() called with: " + "poiItem = [" + poiItem + "], i = [" + i + "]");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiItem poiItem = pois.get(position);
        Intent intent = new Intent();
        intent.putExtra("address",poiItem.getTitle());
        setResult(RESULT_OK,intent);
        finish();
    }
}
