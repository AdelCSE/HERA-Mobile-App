package dz.esisba.a2cpi_project.search_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.QuestionsOnItemClickListner;
import dz.esisba.a2cpi_project.interfaces.SearchOnItemClick;

public class TagsFragment extends Fragment implements SearchOnItemClick {

    View parentHolder;
    Chip all,newborn, kid, baby,
            sleeping,healthcare,breastfeeding,needs,circumcision,routine,food,
            fever, influenza,hepatitis,conjunctivitis,
            experience,motherhood,tools,guidance,other;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_tags, container, false);

        InitChips();
        ShowTagResults();

        return parentHolder;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onAnswerClick(int position) {

    }

    public void GettSelectedTag (){




    }

    private void ShowTagResults()
    {
        Toast.makeText(getActivity(), "All", Toast.LENGTH_SHORT).show();

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Toast.makeText(getActivity(), compoundButton.getText().toString(), Toast.LENGTH_SHORT).show();

                }else{

                }

            }
        };
        all.setOnCheckedChangeListener(checkedChangeListener);
        newborn.setOnCheckedChangeListener(checkedChangeListener);
        baby.setOnCheckedChangeListener(checkedChangeListener);
        kid.setOnCheckedChangeListener(checkedChangeListener);
        sleeping.setOnCheckedChangeListener(checkedChangeListener);
        healthcare.setOnCheckedChangeListener(checkedChangeListener);
        breastfeeding.setOnCheckedChangeListener(checkedChangeListener);
        needs.setOnCheckedChangeListener(checkedChangeListener);
        circumcision.setOnCheckedChangeListener(checkedChangeListener);
        routine.setOnCheckedChangeListener(checkedChangeListener);
        fever.setOnCheckedChangeListener(checkedChangeListener);
        influenza.setOnCheckedChangeListener(checkedChangeListener);
        hepatitis.setOnCheckedChangeListener(checkedChangeListener);
        conjunctivitis.setOnCheckedChangeListener(checkedChangeListener);
        experience.setOnCheckedChangeListener(checkedChangeListener);
        motherhood.setOnCheckedChangeListener(checkedChangeListener);
        food.setOnCheckedChangeListener(checkedChangeListener);
        tools.setOnCheckedChangeListener(checkedChangeListener);
        guidance.setOnCheckedChangeListener(checkedChangeListener);
        other.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void InitChips() {
        all = parentHolder.findViewById(R.id.allS);
        newborn = parentHolder.findViewById(R.id.newbornS);
        kid = parentHolder.findViewById(R.id.kidS);
        baby = parentHolder.findViewById(R.id.babyS);
        sleeping = parentHolder.findViewById(R.id.sleepingS);
        healthcare = parentHolder.findViewById(R.id.healthcareS);
        breastfeeding = parentHolder.findViewById(R.id.breastfeedingS);
        needs = parentHolder.findViewById(R.id.needsS);
        circumcision = parentHolder.findViewById(R.id.circumcisionS);
        routine = parentHolder.findViewById(R.id.routineS);
        food = parentHolder.findViewById(R.id.foodS);
        fever = parentHolder.findViewById(R.id.feverS);
        influenza = parentHolder.findViewById(R.id.influenzaS);
        hepatitis = parentHolder.findViewById(R.id.hepatitisS);
        conjunctivitis = parentHolder.findViewById(R.id.conjunctivitisS);
        experience = parentHolder.findViewById(R.id.experienceS);
        motherhood = parentHolder.findViewById(R.id.motherhoodS);
        tools = parentHolder.findViewById(R.id.toolsS);
        guidance = parentHolder.findViewById(R.id.guidanceS);
        other = parentHolder.findViewById(R.id.otherS);
    }
}