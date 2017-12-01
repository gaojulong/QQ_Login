package cn.lovelqq.julong.qqlogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public String mAppid = "1106429423";
    private Button bt_login ;
    private Tencent mTencent;
    private UserInfo mInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_login=(Button) findViewById(R.id.bt_login);
        // 实例化
        mTencent = Tencent.createInstance(mAppid, this);
        bt_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // "all": 所有权限，listener: 回调的实例
                mTencent.login(MainActivity.this, "all", listener);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Tencent.onActivityResultData(requestCode,resultCode,data,listener);
        super.onActivityResult(requestCode, resultCode, data);
    }
    // 实例化回调接口
    IUiListener listener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            Log.e("登陆信息", "登陆信息"+values.toString());
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };
    /**
     * 调用SDK封装好的借口，需要传入回调的实例 会返回服务器的消息
     */
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            JSONObject jsonResponse=(JSONObject)response;

            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Log.e("wang", e.toString());
        }

        @Override
        public void onCancel() {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                    // TODO Auto-generated method stub
                    Log.e("wang", "userInfo 错误");
                }

                @Override
                public void onComplete(final Object response) {
                    JSONObject jsonObject=(JSONObject)response;
                    Log.e("用户信息", "用户信息"+jsonObject.toString());
                }

                @Override
                public void onCancel() {
                    // TODO Auto-generated method stub

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
        }
    }

    //初始化OPENID和TOKEN值（为了得了用户信息）
    public  void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }
}
