package com.example.asus.sip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonEncryption;
    private Button buttonDecryption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonEncryption = findViewById(R.id.select_encryption);
        buttonDecryption = findViewById(R.id.select_decryption);
    }

    public void selectEncryption(View view) {
        Intent intent = new Intent(this, EncryptionActivity.class);
        startActivity(intent);
    }

    public void selectDecryption(View view) {
        Intent intent = new Intent(this, DecryptionActivity.class);
        startActivity(intent);
    }
}
