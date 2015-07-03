package ua.ic.levtv_ott;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.PrintStream;

import ua.ic.levtv.library.LevtvDbApi;
import ua.ic.levtv.library.LevtvStruct;
import ua.ic.levtv.library.LevtvStruct.AuthStruct;
import ua.ic.levtv.library.LevtvStruct.AuthStruct.profile;
import ua.ic.levtv.library.LevtvStruct.AuthStruct.user;

@SuppressLint({"NewApi"})
public class LevTv extends Activity implements View.OnClickListener {
    CheckBox demoUserBox;
    EditText editLogin;
    EditText editPass;
    PassLoginStorage loginStorage = new PassLoginStorage();
    Button mButton;
    CheckBox rememberMe;

    public LevTv() {
    }

    public void onClick(View paramView) {
        NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        this.loginStorage.init(getApplicationContext());
        this.loginStorage.addString("Login", "");
        this.loginStorage.addString("Pass", "");
        this.loginStorage.addBooleanState("ChBoxState", false);

        if ((info != null) && (info.isConnected())) {
            String login = this.editLogin.getText().toString();
            String password = this.editPass.getText().toString();
            if (this.rememberMe.isChecked()) {
                this.loginStorage.init(getApplicationContext());
                this.loginStorage.addString("Login", login);
                this.loginStorage.addString("Pass", password);
                this.loginStorage.addBooleanState("ChBoxState", true);
            }
            if (this.demoUserBox.isChecked()) {
                login = "310807";
                password = "123321";
            }
            LevtvDbApi api = new LevtvDbApi();
            try {
                LevtvStruct authStruct = api.getAuth(login, password);
                if (authStruct.Auth_str.error != null) {
                    Toast.makeText(this, authStruct.Auth_str.error, Toast.LENGTH_SHORT)
                            .show();
                }
                if (authStruct.Auth_str.isActive && authStruct.Auth_str
                        .isAuthenticated) {
                    Intent localIntent = new Intent();
                    localIntent.setClass(this, MainMenu.class);
                    localIntent.putExtra("user_balacse", authStruct.Auth_str.user_s.balance);
                    localIntent.putExtra("user_name", authStruct.Auth_str.user_s.name);
                    localIntent.putExtra("user_prof_id", authStruct.Auth_str.user_prof.id);
                    localIntent.putExtra("user_prof_transparency", authStruct.Auth_str.user_prof.transparency);
                    localIntent.putExtra("user_prof_reminder", authStruct.Auth_str.user_prof.reminder);
                    localIntent.putExtra("user_prof_ratio", authStruct.Auth_str.user_prof.ratio);
                    localIntent.putExtra("user_prof_volume", authStruct.Auth_str.user_prof.volume);
                    localIntent.putExtra("user_prof_resolution", authStruct.Auth_str.user_prof.resolution);
                    localIntent.putExtra("user_prof_language", authStruct.Auth_str.user_prof.language);
                    localIntent.putExtra("user_prof_startPage", authStruct.Auth_str.user_prof.startPage);
                    localIntent.putExtra("user_prof_type", authStruct.Auth_str.user_prof.type);
                    localIntent.putExtra("user_prof_skin", authStruct.Auth_str.user_prof.skin);
                    localIntent.putExtra("user_password", password);
                    localIntent.putExtra("user_login", login);
                    startActivity(localIntent);
                    //finish();
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            Toast.makeText(this, "Please check your network connection", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint({"NewApi"})
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        System.out.println("android.os.Build.VERSION.SDK_INT" + Build.VERSION.SDK_INT);
        setContentView(R.layout.activity_lev_tv);
        this.mButton = ((Button) findViewById(R.id.Button01));
        this.mButton.setOnClickListener(this);
        this.editLogin = ((EditText) findViewById(R.id.username));
        this.demoUserBox = ((CheckBox) findViewById(R.id.DemoCheckBox));
        this.editPass = ((EditText) findViewById(R.id.password));
        this.rememberMe = ((CheckBox) findViewById(R.id.checkRemember));
        this.loginStorage.init(getApplicationContext());
        if (this.loginStorage.getBooleanState("ChBoxState")) {
            this.editPass.setText(this.loginStorage.getString("Pass"));
            this.editLogin.setText(this.loginStorage.getString("Login"));
            this.rememberMe.setChecked(true);
        }
    }
}
