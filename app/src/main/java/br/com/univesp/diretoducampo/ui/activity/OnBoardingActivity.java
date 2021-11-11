package br.com.univesp.diretoducampo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import br.com.univesp.diretoducampo.R;
import br.com.univesp.diretoducampo.adapter.OnBoardingAdapter;
import br.com.univesp.diretoducampo.item.OnBoardingItem;

public class OnBoardingActivity extends AppCompatActivity {

    public static final String COMPLETED_ONBOARDING = "";
    private OnBoardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicator;
    private MaterialButton buttonOnboardingAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        layoutOnboardingIndicator = findViewById(R.id.layoutOnBoardingIndicators);
        buttonOnboardingAction = findViewById(R.id.buttonOnBoardingAction);

        setOnboardingItem();

        ViewPager2 onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setOnboadingIndicator();
        setCurrentOnBoardingIndicators(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicators(position);
            }
        });

        buttonOnboardingAction.setOnClickListener(view -> {
            if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(getApplicationContext(), ListProductsActivity.class));
                finish();
            }
        });
    }

    private void setOnboadingIndicator() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicator.addView(indicators[i]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentOnBoardingIndicators(int index) {
        int childCount = layoutOnboardingIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicator.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            }
        }

        buttonOnboardingAction.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (index == onboardingAdapter.getItemCount() - 1){
            buttonOnboardingAction.setText(R.string.button_start_label);
        } else {
            buttonOnboardingAction.setText(R.string.button_next_label);
        }
    }

    private void setOnboardingItem() {
        List<OnBoardingItem> onBoardingItems = new ArrayList<>();

        OnBoardingItem itemFastFood = new OnBoardingItem();
        itemFastFood.setTitle(getResources().getString(R.string.fast_home_delivery_label));
        itemFastFood.setDescription(getResources().getString(R.string.fast_home_delivery_description_label));
        itemFastFood.setImage(R.drawable.on_the_way);

        OnBoardingItem itemDiscoverCampProducts = new OnBoardingItem();
        itemDiscoverCampProducts.setTitle(getResources().getString(R.string.discover_camp_products_label));
        itemDiscoverCampProducts.setDescription(getResources().getString(R.string.discover_camp_products_description_label));
        itemDiscoverCampProducts.setImage(R.drawable.fruits_cartoon);

        OnBoardingItem itemSocialize = new OnBoardingItem();
        itemSocialize.setTitle(getResources().getString(R.string.socialize_with_friends_label));
        itemSocialize.setDescription(getResources().getString(R.string.socialize_with_friends_description_label));
        itemSocialize.setImage(R.drawable.eat_together);

        onBoardingItems.add(itemDiscoverCampProducts);
        onBoardingItems.add(itemSocialize);
        onBoardingItems.add(itemFastFood);

        onboardingAdapter = new OnBoardingAdapter(onBoardingItems);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(this.getResources().getString(R.string.exit_question_label))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        OnBoardingActivity.this.finish();
                    }
                })
                .setNegativeButton(this.getResources().getString(R.string.no), null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // User has seen OnboardingSupportFragment, so mark our SharedPreferences
        // flag as completed so that we don't show our OnboardingSupportFragment
        // the next time the user launches the app.
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        sharedPreferencesEditor.putBoolean(
                COMPLETED_ONBOARDING, true);
        sharedPreferencesEditor.apply();
    }
}