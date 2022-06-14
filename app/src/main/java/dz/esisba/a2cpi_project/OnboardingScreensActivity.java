package dz.esisba.a2cpi_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import dz.esisba.a2cpi_project.adapter.OnboardingAdapter;
import dz.esisba.a2cpi_project.models.OnboardingItemModel;

public class OnboardingScreensActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton buttonOnboardingAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screens);

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonOnboardingAction = findViewById(R.id.buttonOnboardingAction);
        setupOnboardingItems();

        ViewPager2 onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()){
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
                }else{
                    startActivity(new Intent(getApplicationContext(),BottomNavigationActivity.class));
                    finish();
                }
            }
        });

    }

    private void setupOnboardingItems(){

        List<OnboardingItemModel> onboardingItemModels = new ArrayList<OnboardingItemModel>();

        OnboardingItemModel item1 = new OnboardingItemModel();
        item1.setTitle("Whoah, Welcome!");
        item1.setDescription("Hey there, we are so happy you decided to join Hera! Hats off on making an excellent decision.");
        item1.setImage(R.drawable.welcomepage1);

        OnboardingItemModel item2 = new OnboardingItemModel();
        item2.setTitle("Ask Mothers");
        item2.setDescription("You can always post new questions related to your child, and you will get answers from experienced mothers!");
        item2.setImage(R.drawable.welcomepage2);

        OnboardingItemModel item3 = new OnboardingItemModel();
        item3.setTitle("Smart Room ?!");
        item3.setDescription("Discover our SmartRoom technology and be always close to your child");
        item3.setImage(R.drawable.welcomepage3);

        onboardingItemModels.add(item1);
        onboardingItemModels.add(item2);
        onboardingItemModels.add(item3);

        onboardingAdapter= new OnboardingAdapter(onboardingItemModels);

    }

    private void setupOnboardingIndicators(){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        );
        LayoutParams.setMargins(8,0,8,0);
        for(int i = 0; i<indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(LayoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index){
        int childCount = layoutOnboardingIndicators.getChildCount();
        for(int i = 0; i<childCount;i++){
            ImageView imageView = (ImageView)layoutOnboardingIndicators.getChildAt(i);
            if(i == index){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_active)
                );
            }else{
               imageView.setImageDrawable(
                       ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive)
               );
            }
        }
        if (index == onboardingAdapter.getItemCount() - 1){
            buttonOnboardingAction.setText("Start");
        }else{
            buttonOnboardingAction.setText("Next");
        }
    }
}