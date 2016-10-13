package com.oaec.waimai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.alibaba.fastjson.JSON;
import com.oaec.waimai.R;
import com.oaec.waimai.app.WaiMaiApplication;
import com.oaec.waimai.entity.User;
import com.oaec.waimai.util.WaiMaiConfig;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.met_userName)
    private MaterialEditText met_userName;
    @ViewInject(R.id.met_password)
    private MaterialEditText met_password;
    @ViewInject(R.id.btn_login)
    private Button btn_login;
    @ViewInject(R.id.cb_inputTpye)
    private CheckBox cb_inputType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initEvent();
    }

    private void initEvent() {
        btn_login.setOnClickListener(this);
        cb_inputType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    met_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    met_password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                met_password.setSelection(met_password.getText().toString().length());
            }
        });
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = met_userName.getText().toString();
                String s2 = met_password.getText().toString();
                if(s1.length() >= 5 && s2.length() >= 6){
                    btn_login.setEnabled(true);
                }else{
                    btn_login.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged() called with: " + "s = [" + s + "]");
            }
        };
        met_password.addTextChangedListener(watcher);
        met_userName.addTextChangedListener(watcher);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                String userName = met_userName.getText().toString();
                String password = met_password.getText().toString();
                RequestParams params = new RequestParams(WaiMaiConfig.URL_LOGIN);
                params.addParameter("userName",userName);
                params.addParameter("password",password);
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess() called with: " + "result = [" + result + "]");
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String result1 = jsonObject.getString("result");
                            if("success".equals(result1)){
                                String userStr = jsonObject.getString("user");
                                User user = JSON.parseObject(userStr, User.class);
                                WaiMaiApplication app = (WaiMaiApplication) getApplicationContext();
                                app.setUser(user);
                                Intent intent = new Intent();
                                intent.putExtra("nickName",user.getNickName());
                                intent.putExtra("img",user.getImg());
                                setResult(RESULT_OK,intent);
                                finish();
//                                Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                            }else {
                                String reason = jsonObject.getString("reason");
//                                Toast.makeText(LoginActivity.this, reason, Toast.LENGTH_SHORT).show();
                                if(reason.equals("密码错误")){
                                    met_password.setError(reason);
                                }else{
                                    met_userName.setError(reason);
//                                    met_userName.setText("");
                                }
//                                met_password.setText("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d(TAG, "onCancelled: ");
                    }

                    @Override
                    public void onFinished() {
                        Log.d(TAG, "onFinished: ");
                    }
                });
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
