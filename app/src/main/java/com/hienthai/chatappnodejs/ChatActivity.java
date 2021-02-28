package com.hienthai.chatappnodejs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity implements TextWatcher {

    private final int REQUEST_CODE_IMAGE = 1;

    private String name;
    private WebSocket webSocket;
    private static final String SERVER_PATH = "ws://192.168.1.118:3000";
    private EditText editText;
    private ImageView imgPhoto;
    private TextView txtSend;
    private RecyclerView rcvMessage;

    private MessageAdapter messageAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = getIntent().getStringExtra("name");

        initiateSocketConnection();

    }

    private void initiateSocketConnection() {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(SERVER_PATH).build();

        webSocket = okHttpClient.newWebSocket(request, new SocketListener());

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString().trim();
        if (str.isEmpty()) {

            resetMessageEdittext();
        } else {
            txtSend.setVisibility(View.VISIBLE);
            imgPhoto.setVisibility(View.VISIBLE);
        }
    }

    private void resetMessageEdittext() {

        editText.removeTextChangedListener(this);
        editText.setText("");



        editText.addTextChangedListener(this);
    }


    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);

            runOnUiThread(() -> {
                Toast.makeText(ChatActivity.this, "Socket Connection Successful !", Toast.LENGTH_SHORT).show();

                initializeView();
            });

        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);

            runOnUiThread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    jsonObject.put("isSent", false);

                    messageAdapter.addItem(jsonObject);

                    rcvMessage.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void initializeView() {
        editText = findViewById(R.id.edtMessage);
        txtSend = findViewById(R.id.txtSendMessage);

        imgPhoto = findViewById(R.id.imgPhoto);

        rcvMessage = findViewById(R.id.rcvMessage);

        messageAdapter = new MessageAdapter(getLayoutInflater());
        rcvMessage.setAdapter(messageAdapter);
        rcvMessage.setLayoutManager(new LinearLayoutManager(this));


        editText.addTextChangedListener(this);

        txtSend.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("name", name);
                jsonObject.put("message", editText.getText().toString().trim());

                webSocket.send(jsonObject.toString());

                jsonObject.put("isSent", true);

                messageAdapter.addItem(jsonObject);

                rcvMessage.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                resetMessageEdittext();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        imgPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), REQUEST_CODE_IMAGE);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                sendImage(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private void sendImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        String base64String = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("name", name);
            jsonObject.put("image", base64String);

            webSocket.send(jsonObject.toString());

            jsonObject.put("isSent", true);

            messageAdapter.addItem(jsonObject);
            rcvMessage.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}