package ua.opensvit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import ua.opensvit.data.authorization.AuthorizationInfo;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.PassLoginStorage;

@SuppressLint({"NewApi"})
public class LevTv extends Activity implements View.OnClickListener {
    public static final String AUTHORIZATION_INFO_TAG = "authorizationInfo";

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
            OpenWorldApi api = new OpenWorldApi();
            VideoStreamApplication.getInstance().setDbApi(api);
            try {
                AuthorizationInfo authorizationInfo = api.getAuthorizationInfo(login, password);
                if (authorizationInfo.getError() != null) {
                    Toast.makeText(this, authorizationInfo.getError(), Toast.LENGTH_SHORT).show();
                }
                if (authorizationInfo.isActive() && authorizationInfo
                        .isAuthenticated()) {

                    Intent localIntent = new Intent(this, MainMenu.class);
                    localIntent.putExtra(AUTHORIZATION_INFO_TAG, authorizationInfo);
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
