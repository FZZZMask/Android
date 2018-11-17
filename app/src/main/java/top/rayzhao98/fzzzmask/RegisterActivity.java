package top.rayzhao98.fzzzmask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText registerUsernameEditText = findViewById(R.id.registerUsernameEditText);
        final EditText registerEmailEditText = findViewById(R.id.registerEmailEditText);
        final EditText registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
        final EditText registerPasswordAgainEditText = findViewById(R.id.registerPasswordAgainEditText);
        Button registerButton = findViewById(R.id.registerButton);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String registerUsername = registerUsernameEditText.getText().toString();
                String registerEmail = registerEmailEditText.getText().toString();
                String registerPassword = registerPasswordEditText.getText().toString();
                String registerPasswordAgain = registerPasswordAgainEditText.getText().toString();
                OkHttpClient client = new OkHttpClient();
                FormBody formBody = new FormBody.Builder()
                        .add("username", registerUsername)
                        .add("email", registerEmail)
                        .add("password1", registerPassword)
                        .add("password2", registerPasswordAgain).build();
                Request request = new Request.Builder()
                        .url(ApiConfig.API_ROOT + "/auth/registration")
                        .addHeader("Content-Type", "application/json")
                        .post(formBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("error", "onFailure: " + "fail");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        Log.d("res", "onResponse: " + res);
                    }
                });
            }
        });
    }
}
