package com.example.soring.bandccare_banduser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.soring.bandccare_banduser.Retrofit.Model.Response_Login;
import com.example.soring.bandccare_banduser.Retrofit.RetroCallback;
import com.example.soring.bandccare_banduser.Retrofit.RetroClient;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static LoginActivity instance;
    SharedPreferences.Editor editor;
    Intent intent;
    EditText editText_id;
    EditText editText_pw;
    Button login_btn;

    RetroClient retroClient;

    HashMap<String, Object> parameter;
    ImageView username_img;
    ImageView password_img;

    public static LoginActivity getInstance() {
        if (instance == null)
            instance = new LoginActivity();
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        retroClient = RetroClient.getInstance().createBaseApi();
        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        editor = pref.edit();

        parameter = new HashMap<>();
        editText_id = findViewById(R.id.login_id_et);
        editText_pw = findViewById(R.id.login_pw_et);

        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 서버 안통할때 용 코드 (지우지마세용)
///*
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
              //  */
/*
                parameter.put("AppUserInfo_id", editText_id.getText().toString());
                parameter.put("AppUserInfo_password", editText_pw.getText().toString());

                Log.e("input id", "id->" + editText_id.getText().toString());
                Log.e("input password", "password->" + editText_pw.getText().toString());
                retroClient.Insert_Band_Member(parameter, new RetroCallback() {
                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onSuccess(int code, Object receivedData) {
                        Response_Login data = (Response_Login) receivedData;
                        Log.e("login get data", "data->" + data.getSuccess());
                        if (data.getSuccess().equals("success")) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                            editor.putString("id", editText_id.getText().toString());
                            editor.putString("pw", editText_pw.getText().toString());
                            editor.commit();
                        } else if (data.getSuccess().equals("nomatch")) {
                            Toast.makeText(LoginActivity.this, "비밀번호 혹은 아이디를 확인하세요", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "해당아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int code) {

                    }
                });*/
            }

        });
    }
}
