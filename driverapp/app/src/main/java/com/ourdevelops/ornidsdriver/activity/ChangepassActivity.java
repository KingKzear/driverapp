package com.ourdevelops.ornidsdriver.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourdevelops.ornidsdriver.R;
import com.ourdevelops.ornidsdriver.constants.BaseApp;
import com.ourdevelops.ornidsdriver.json.ChangePassRequestJson;
import com.ourdevelops.ornidsdriver.json.LoginResponseJson;
import com.ourdevelops.ornidsdriver.utils.api.ServiceGenerator;
import com.ourdevelops.ornidsdriver.utils.api.service.DriverService;
import com.ourdevelops.ornidsdriver.models.FirebaseToken;
import com.ourdevelops.ornidsdriver.models.User;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangepassActivity extends AppCompatActivity {

    ImageView backbtn;
    Button submit;
    EditText pass, passbaru;
    TextView notiftext;
    RelativeLayout rlnotif, rlprogress;
    String disableback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        backbtn = findViewById(R.id.back_btn_verify);
        submit = findViewById(R.id.buttonconfirm);
        pass = findViewById(R.id.password);
        passbaru = findViewById(R.id.newpassword);
        notiftext = findViewById(R.id.textnotif2);
        rlnotif = findViewById(R.id.rlnotif2);
        rlprogress = findViewById(R.id.rlprogress);

        disableback = "false";

        backbtn.setOnClickListener(v -> finish());

        submit.setOnClickListener(v -> {
            if (pass.getText().toString().isEmpty()) {
                notif(getString(R.string.old_pass_toast));
            } else if (passbaru.getText().toString().isEmpty()) {
                notif(getString(R.string.new_pass_toast));
            } else {
                get();
            }

        });

    }

    private void get() {
        progressshow();
        ChangePassRequestJson request = new ChangePassRequestJson();
        User loginuser = BaseApp.getInstance(ChangepassActivity.this).getLoginUser();
        request.setNotelepon(loginuser.getNoTelepon());
        request.setPassword(pass.getText().toString());
        request.setNewPassword(passbaru.getText().toString());

        DriverService service = ServiceGenerator.createService(DriverService.class, request.getNotelepon(), request.getPassword());
        service.changepass(request).enqueue(new Callback<LoginResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseJson> call,@NonNull Response<LoginResponseJson> response) {
                progresshide();
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("found")) {

                        User user = response.body().getData().get(0);
                        saveUser(user);
                        finish();


                    } else {
                        notif(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseJson> call,@NonNull Throwable t) {
                progresshide();
                t.printStackTrace();
                notif(getString(R.string.error));
            }
        });
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
        disableback = "true";
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
        disableback = "false";
    }

    @Override
    public void onBackPressed() {
        if (!disableback.equals("true")) {
            finish();
        }
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        notiftext.setText(text);

        new Handler().postDelayed(() -> rlnotif.setVisibility(View.GONE), 3000);
    }

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();
        BaseApp.getInstance(ChangepassActivity.this).setLoginUser(user);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FirebaseToken response) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FirebaseToken.class);
        realm.copyToRealm(response);
        realm.commitTransaction();
    }


}
