package com.example.dunqian;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.dunqian.DunqianApp.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MAIN";

    private String deviceToken = "";

    private View view;

    private TextView textCode, textToken, textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.but_scan);

        textCode = findViewById(R.id.text_code);
        textToken = findViewById(R.id.text_token);
        textResult = findViewById(R.id.text_result);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textCode.setText("");

                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {

                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                deviceToken = task.getResult().getToken();

                textToken.post(new Runnable() {
                    @Override
                    public void run() {
                        textToken.setText("token:" + deviceToken);
                    }
                });
                textToken.setText("token:" + deviceToken);

//                Log.i("JSON", deviceToken);

                // Toast.makeText(getBaseContext(), deviceToken, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        textCode.setText("");
        textResult.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                // SHOW content
                // Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();


                textCode.setText("code: " + result.getContents());

                postData(result.getContents());
            }
        }
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final String url = "https://store.mastripms.com/api/app_exam";

    private void postData(String content) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("device_token", deviceToken);
            jsonObject.put("qrcode", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON", jsonObject.toString());

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseString = response.body().string();

                textResult.post(new Runnable() {
                    @Override
                    public void run() {
                        textResult.setText("result: " + responseString);
                    }
                });
            }
        });
    }
}
