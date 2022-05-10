package dz.esisba.a2cpi_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.models.OnboardingItemModel;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingItemModel> onboardingItemModels;

    public OnboardingAdapter(List<OnboardingItemModel> onboardingItemModels) {
        this.onboardingItemModels = onboardingItemModels;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_onboarding,parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingItemModels.get(position));

    }

    @Override
    public int getItemCount() {
        return onboardingItemModels.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder{

        private TextView textTitle;
        private TextView textDescription;
        private ImageView imageOnboarding;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageOnboarding = itemView.findViewById(R.id.imageOnboarding);
        }

        void setOnboardingData(OnboardingItemModel onboardingItemModel){
            textTitle.setText(onboardingItemModel.getTitle());
            textDescription.setText(onboardingItemModel.getDescription());
            imageOnboarding.setImageResource(onboardingItemModel.getImage());


        }
    }
}
