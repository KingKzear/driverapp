package com.ourdevelops.ornidsdriver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.PhoneAuthProvider;
import com.ourdevelops.ornidsdriver.R;
import com.ourdevelops.ornidsdriver.constants.BaseApp;
import com.ourdevelops.ornidsdriver.json.LoginRequestJson;
import com.ourdevelops.ornidsdriver.json.TokenResponseJson;
import com.ourdevelops.ornidsdriver.json.PincodeRequestJson;
import com.ourdevelops.ornidsdriver.json.PincodeResponseJson;
import com.ourdevelops.ornidsdriver.models.User;
import com.ourdevelops.ornidsdriver.utils.SettingPreference;
import com.ourdevelops.ornidsdriver.utils.api.ServiceGenerator;
import com.ourdevelops.ornidsdriver.utils.api.service.DriverService;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
public class Tokengenerator extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    EditText phoneText, password, numOne, numTwo, numThree, numFour, numFive, numSix, pinId;
    TextView token, code, privacypolicy, textnotif, daftar, textnotif2, lupapass;
    String phoneNumber, disableback,countryCode,token_price, valid_token;

    private TextView balance, nameuser,token_info;

    private Float cur_balance;
    Button tokenButton, testPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_tokengenerator);
        setContentView(R.layout.activity_tokengenerator);
        tokenButton = findViewById(R.id.tokenButton);
        testPin = findViewById(R.id.testPin);
        token = findViewById(R.id.token);
        code = findViewById(R.id.code);
        pinId = findViewById(R.id.pinId);
        balance = findViewById(R.id.balance);
        nameuser = findViewById(R.id.namapengguna);
        token_info = findViewById(R.id.token_info);
        balance.setText(extras.getString("currency"));
        nameuser.setText(extras.getString("customer_fullname"));
        token_info.setText(extras.getString("$"+"token_price"));
        tokenButton.setOnClickListener(v -> {
            this.getToken();
        });
        testPin.setOnClickListener(v -> {
            this.sendPincode();
        });
    }

    protected void sendPincode() {
        Dialog dialog = new Dialog(Tokengenerator.this);
        dialog.setContentView(R.layout.custom3);
        Bundle extras = getIntent().getExtras();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button closeBtn = dialog.findViewById(R.id.buttonOk);
        Button closeBtn1 = dialog.findViewById(R.id.cancel);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Bundle extras = getIntent().getExtras();
                PincodeRequestJson request = new PincodeRequestJson();
                if(pinId.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "input the pincode",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(extras.getFloat("cur_balance") < 1)
                {
                    Toast.makeText(getApplicationContext(), "you have to pay!",
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Tokengenerator.this, TopupSaldoActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return;
                }
                else{

                    if (extras != null) {
                        request.setNotelepon(extras.getString("countryCode").replace("+", "") + extras.getString("phoneText") + ":" + extras.getString("token_price"));
                    }
                    request.setPinid(pinId.getText().toString());
                    request.setToken_price(extras.getString("token_price"));
                    DriverService service = ServiceGenerator.createService(DriverService.class, request.getPinid(),request.getNotelepon());
                    service.testPin(request).enqueue(new Callback<PincodeResponseJson>() {
                        @Override
                        public void onResponse(@NonNull Call<PincodeResponseJson> call, @NonNull Response<PincodeResponseJson> response) {
                            if (response.isSuccessful()) {
                                User user = response.body().getData().get(0);
                                savePin(user);
                                Toast.makeText(getApplicationContext(), "Success",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Tokengenerator.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PincodeResponseJson> call, @NonNull Throwable t) {
                            Toast.makeText(getApplicationContext(), "Error",
                                    Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });

                }
            }
        });
        closeBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    protected void getToken() {
        Bundle extras = getIntent().getExtras();
        LoginRequestJson request = new LoginRequestJson();
        if(extras.getFloat("cur_balance") < 1)
        {
            Toast.makeText(getApplicationContext(), "you have to pay!",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(Tokengenerator.this, TopupSaldoActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return;
        }
        if (extras != null) {
            request.setNotelepon(extras.getString("countryCode").replace("+", "") + extras.getString("phoneText"));
        }
        request.setPassword("");
        DriverService service = ServiceGenerator.createService(DriverService.class, request.getNotelepon(), request.getPassword());
        service.tokengenerator(request).enqueue(new Callback<TokenResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<TokenResponseJson> call, @NonNull Response<TokenResponseJson> response) {
                if (response.isSuccessful()) {
                    SettingPreference sp = new SettingPreference(Tokengenerator.this);
                    String strToken = response.body().getToken();
                    token.setText(strToken.substring(0, 10) + "...." + strToken.substring(strToken.length() - 10, strToken.length()));
                    code.setText(response.body().getPing());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TokenResponseJson> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void savePin(User Pin) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(Pin);
        realm.commitTransaction();
        BaseApp.getInstance(Tokengenerator.this).setLoginUser(Pin);
    }
}
