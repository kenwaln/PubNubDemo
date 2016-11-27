package com.kwaln.pubnubdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

public class MainActivity extends AppCompatActivity implements MessageReceiver {
    public final static String EXTRA_MESSAGE = "com.kwaln.pubnubdemo.MESSAGE";
    private static final String MESSAGE_BODY = "com.kwaln.pubnubdemo.BODY";
    private static final String MESSAGE_POSTER = "com.kwaln.pubnubdemo.POSTER";
    private static final String DEFAULT_POSTER = "Android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PubNubConn.getInstance().addReceiver("room1", this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(MESSAGE_BODY)) {
            String poster = intent.getStringExtra(MESSAGE_POSTER);
            String body = intent.getStringExtra(MESSAGE_BODY);
            TextView messages = (TextView) findViewById(R.id.messages);
            messages.append("\n");
            messages.append(poster + ": " + body);
        }
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        EditText editName = (EditText) findViewById(R.id.edit_name);
        String poster = editName.getText().toString();
        if (poster.isEmpty()) {
            poster = DEFAULT_POSTER;
        }
        PubNubConn.getInstance().post("room1", new ChatMsg(poster, message));
        editText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void processMsg(JsonNode msg) {
        String poster = msg.path("poster").textValue();
        String body = msg.path("body").textValue();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MESSAGE_BODY, body);
        intent.putExtra(MESSAGE_POSTER, poster);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
