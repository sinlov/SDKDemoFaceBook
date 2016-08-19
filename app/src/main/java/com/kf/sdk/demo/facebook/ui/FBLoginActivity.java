package com.kf.sdk.demo.facebook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kf.sdk.demo.facebook.R;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mdl.sinlov.android.log.ALog;
import mdl.sinlov.android.log.ALogPrinter;

public class FBLoginActivity extends MDLTestActivity {

    @BindView(R.id.btn_face_book_login)
    Button btnFaceBookLogin;
    @BindView(R.id.btn_face_book_login_out)
    Button btnFaceBookLoginOut;
    @BindView(R.id.tv_face_book_login_result)
    TextView tvFaceBookLoginResult;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void bindListener() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != callbackManager) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void faceBookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(
                this,
//                Arrays.asList("user_friends"));
                Arrays.asList("email", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                if (null != accessToken) {
                    fetchUserInfo(accessToken);
                }
            }

            @Override
            public void onCancel() {
                ALog.d("Facebook login cancel");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                if (e.getMessage().contains("User logged in as different Facebook user")) {
                    if (e instanceof FacebookAuthorizationException) {
                        ALog.i("facebook 切换账号");
                    }
                } else {
                    ALog.i("facebook 登录异常");
                }
            }
        });
    }

    private void fetchUserInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String json = object.toString();
                        ALog.json(json);
                        if (null != json) {
                            tvFaceBookLoginResult.setText(ALogPrinter.getLogMessage());
                        }
                        ALog.w("facebook fetchUserInfo response:" + response.getError());
                        try {
                            ALog.i("facebook fetchUserInfo getResponseCode:" + response.getConnection().getResponseCode());
                            if (response.getError() != null) {
                            } else {
                                String userId = object.getString("id");
                                String userName = null;
                                if (object.has("email")) {
                                    userName = object.getString("email");
                                }
                                if (userName == null || "".equals(userName)) {
                                    userName = userId + "@facebook.com";
                                }
                                ALog.w("facebook fetchUserInfo userId=" + userId + ",userName=" + userName);
                            }
                        } catch (Exception e) {
                            ALog.w("facebook login fail");
                            e.printStackTrace();
                        }
                    }
                });
        Bundle params = request.getParameters();
        params.putString("fields", "id,email,verified,name,first_name,last_name,locale,timezone,gender,location");
        request.setParameters(params);
        request.executeAsync();
    }

    @OnClick({R.id.btn_face_book_login, R.id.btn_face_book_login_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_face_book_login:
                faceBookLogin();
                break;
            case R.id.btn_face_book_login_out:
                LoginManager.getInstance().logOut();
                break;
        }
    }
}
