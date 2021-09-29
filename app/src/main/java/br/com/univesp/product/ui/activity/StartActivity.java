package br.com.univesp.product.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import br.com.univesp.product.R;

public class StartActivity extends AppCompatActivity implements Runnable {

    Thread thread;
    Handler handler;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        handler = new Handler();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            handler.post(() -> i++);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, ListaProdutosActivity.class));
    }
}