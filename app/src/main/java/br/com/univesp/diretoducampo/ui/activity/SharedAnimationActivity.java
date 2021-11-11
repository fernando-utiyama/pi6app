package br.com.univesp.diretoducampo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import br.com.univesp.diretoducampo.R;

public class SharedAnimationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_animation);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        final TextView txtView = findViewById(R.id.textView);
        txtView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_up));

        // Check if we need to display our OnboardingSupportFragment
        if (!sharedPreferences.getBoolean(
                OnBoardingActivity.COMPLETED_ONBOARDING, false)) {
            // The user hasn't seen the OnboardingSupportFragment yet, so show it
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SharedAnimationActivity.this, OnBoardingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        } else {
            Intent intent = new Intent(SharedAnimationActivity.this, ListProductsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(this.getResources().getString(R.string.exit_question_label))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedAnimationActivity.this.finish();
                    }
                })
                .setNegativeButton(this.getResources().getString(R.string.no), null)
                .show();
    }
}