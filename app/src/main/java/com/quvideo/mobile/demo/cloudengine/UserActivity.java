package com.quvideo.mobile.demo.cloudengine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.quvideo.mobile.external.component.cloudengine.QVCloudEngine;
import com.quvideo.mobile.external.component.cloudengine.model.CloudEngineConfig;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

/**
 * Created by santa on 2020-03-23.
 */
public class UserActivity extends AppCompatActivity {

    private EditText mEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.user_edit);
    }

    public void onFakeLogin(View view) {
        if (mEditText.getText().length() == 0) {
            Toast.makeText(this, "需要填入用户id", Toast.LENGTH_SHORT).show();
            return;
        }
        String country = Locale.getDefault().getCountry();
        String language = Locale.getDefault().getLanguage();
        CloudEngineConfig config = new CloudEngineConfig(App.APP_KEY, App.APP_SECRET, mEditText.getText().toString(),
            country, language, true);
        //CloudEngineConfig config = new CloudEngineConfig(App.APP_KEY, App.APP_SECRET, mEditText.getText().toString(), true);
        QVCloudEngine.initialize(getApplicationContext(), config);
        startActivity(new Intent(this, MainActivity.class));
    }
}
