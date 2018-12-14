package com.jsti.pile.collector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jsti.pile.collector.common.AppProfiles;
import com.jsti.pile.collector.json.BaseResult;
import com.jsti.pile.collector.json.LoginResult;
import com.jsti.pile.collector.model.User;
import com.jsti.pile.collector.requestbean.Login;
import com.jsti.pile.collector.utils.GsonCallback;
import com.jsti.pile.collector.utils.UrlUtils;
import com.jsti.pile.collector.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import static com.jsti.pile.collector.common.AppProfiles.KEY_LAST_LOGIN_USER;
import static com.jsti.pile.collector.common.AppProfiles.KEY_LAST_LOGIN_USER_ACOUNT;
import static com.jsti.pile.collector.common.AppProfiles.KEY_LAST_LOGIN_USER_PWD;

public class LoginActivity extends BaseActivity implements OnClickListener {


    private EditText usernameEd;
    private EditText passwordEd;
    private AppProfiles profiles;
    private EditText edtIpAddress;
    private AlertDialog.Builder alertDialog;
    private SharedPreferences pref;
    private LinearLayout view;

    private LoginActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        profiles = AppProfiles.getInstance(this);
        setContentView(R.layout.activity_login);

        usernameEd = (EditText) this.findViewById(R.id.username_edit);
        passwordEd = (EditText) this.findViewById(R.id.password_edit);
        pref = this.getSharedPreferences("data", MODE_PRIVATE);
        this.findViewById(R.id.tv_login_param_config)
                .setOnClickListener(this);
        usernameEd.setText(profiles.getLoginAcount());
        passwordEd.setText(profiles.getLoginPassword());
        findViewById(R.id.login).setOnClickListener(this);
        User user = App.SP_System.getBeanFromSp(KEY_LAST_LOGIN_USER);
        if (user != null) {
            goMainPage();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (null == pref.getString("ipAddress", "")) {
                    Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show();
                } else {
                    postLogin();
                }
                break;
            case R.id.tv_login_param_config:
                alertDialog = new AlertDialog.Builder(this);
                LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_input_ip_address, null, false);
                edtIpAddress = (EditText) view.findViewById(R.id.tv_input_ip_address);
                alertDialog.setView(view);
                alertDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.SP_Data.putString("ipAddress", edtIpAddress.getText().toString());
                    }
                });
                String ipAddress = App.SP_Data.getString("ipAddress");
                edtIpAddress.setText(ipAddress);
                alertDialog.show();
                break;
        }
    }

    private String safeGetText(EditText e, String def) {
        if (e == null) {
            return def;
        }
        Editable eb = e.getText();
        if (eb == null) {
            return def;
        }
        return eb.toString();
    }


    private void postLogin() {
        final String username = safeGetText(usernameEd, null);
        final String password = safeGetText(passwordEd, null);
        if (username == null || username.length() <= 0) {
            Utils.toast(this, R.string.hint_login_empty_username);
            return;
        }
        if (password == null || password.length() <= 0) {
            Utils.toast(this, R.string.hint_login_empty_passwor);
            return;
        }
        //

        Login login = new Login(username, password);
        String loginjson = new Gson().toJson(login);
        showProgressDialog(R.string.hint_user_login_waiting_msg, false);
        OkGo.<LoginResult>post(UrlUtils.getBaseUrl() + "/intf/collector/login.intf")
                .tag(this)
                .upJson(loginjson)
                .execute(new GsonCallback<LoginResult>() {
                    @Override
                    public void onSuccess(Response<LoginResult> response) {
                        dismissProgressDialog();
                        App.SP_System.putString(KEY_LAST_LOGIN_USER_ACOUNT, username);
                        App.SP_System.putString(KEY_LAST_LOGIN_USER_PWD, password);
                        LoginResult result = response.body();
                        BaseResult.Result result1 = result.getResult();
                        if (result1.getCode() == -1) {
                            Utils.toast(LoginActivity.this, "登录失败");
                            return;
                        } else {
                            if (result == null) {
                                Utils.toast(LoginActivity.this, "登录失败");
                                return;
                            } else if (result.isSuccess() && result.getUser() != null) {
                                User user = result.getUser();
                                App.SP_System.saveBean(user, KEY_LAST_LOGIN_USER);
                                goMainPage();
                            }


                        }
                    }

                    @Override
                    public void onError(Response<LoginResult> response) {
                        super.onError(response);
                        dismissProgressDialog();
                        Utils.toast(LoginActivity.this, "登录失败,请确保网络通常");
                    }
                });
    }

    private void goMainPage() {
        Intent intent = new Intent(this, CreateCollectActivity.class);
        startActivity(intent);
        finish();
    }

}