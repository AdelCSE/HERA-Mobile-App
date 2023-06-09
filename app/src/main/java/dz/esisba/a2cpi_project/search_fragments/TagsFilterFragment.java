package dz.esisba.a2cpi_project.search_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dz.esisba.a2cpi_project.BottomNavigationActivity;
import dz.esisba.a2cpi_project.QuestionBlocActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.SearchRecommendationAdapter;
import dz.esisba.a2cpi_project.interfaces.SearchOnItemClick;
import dz.esisba.a2cpi_project.models.PostModel;

public class TagsFilterFragment extends Fragment implements SearchOnItemClick {

    private View parentHolder;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<PostModel> QuestionsDataHolder;
    private SearchRecommendationAdapter adapter;
    private FirebaseFirestore fstore;
    private CollectionReference postRef;
    private ImageButton backBtn;
    private Chip all,newborn, kid, baby,
                 sleeping,healthcare,breastfeeding,needs,circumcision,routine,food,
                 fever, influenza,hepatitis,conjunctivitis,
                 experience,motherhood,tools,guidance,other;
    private SwipeRefreshLayout refresh;
    private LinearLayout emptyTagSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_tags, container, false);

        fstore = FirebaseFirestore.getInstance();
        postRef = fstore.collection("Posts");
        QuestionsDataHolder = new ArrayList<>();
        refresh = parentHolder.findViewById(R.id.searchRefresh);
        backBtn = parentHolder.findViewById(R.id.tagsFilterBackBtn);
        progressBar = parentHolder.findViewById(R.id.tagsProgressBar);
        recyclerView = parentHolder.findViewById(R.id.searchTagsRecview);
        emptyTagSearch = parentHolder.findViewById(R.id.emptyTagSearch);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BottomNavigationActivity.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        InitChips();
        progressBar.setVisibility(View.VISIBLE);
        FetchAllQuestions();
        ShowTagFilterResults();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ShowTagFilterResults();
                refresh.setRefreshing(false);
            }
        });
        return parentHolder;
    }

    //***Display questions on recyclerview***//
    private void BuildRecyclerView(){
        recyclerView = parentHolder.findViewById(R.id.searchTagsRecview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchRecommendationAdapter(QuestionsDataHolder,this);
        recyclerView.setAdapter(adapter);
    }

    //***Sort questions according to the number of likes***//
    private void SortDataByAnswers(ArrayList<PostModel> questions){
        Collections.sort(questions, new Comparator<PostModel>() {
            @Override
            public int compare(PostModel question1, PostModel question2) {
                if(question1.getAnswersCount() > question2.getAnswersCount()) {
                    return -1;
                } else if (question1.getAnswersCount() < question2.getAnswersCount()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    //***Start Question Bloc Activity method***//
    private void StartQuestionBlocActivity(int position){
        PostModel Post1 = new PostModel(QuestionsDataHolder.get(position).getAskedBy(), QuestionsDataHolder.get(position).getPublisher()
                , QuestionsDataHolder.get(position).getUsername() , QuestionsDataHolder.get(position).getQuestion() ,
                QuestionsDataHolder.get(position).getBody(),QuestionsDataHolder.get(position).getPostid(),
                QuestionsDataHolder.get(position).getDate(),QuestionsDataHolder.get(position).getPublisherPic(),
                QuestionsDataHolder.get(position).getLikesCount(),QuestionsDataHolder.get(position).getAnswersCount(), QuestionsDataHolder.get(position).getTags());
        Intent intent = new Intent(getActivity(), QuestionBlocActivity.class);
        intent.putExtra("Tag", (Serializable) Post1);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        StartQuestionBlocActivity(position);
    }

    @Override
    public void onAnswerClick(int position) {
        StartQuestionBlocActivity(position);
    }

    //***Fetch All Questions***//
    private void FetchAllQuestions(){
        postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuestionsDataHolder = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        QuestionsDataHolder.add(post);
                    }
                    SortDataByAnswers(QuestionsDataHolder);
                    BuildRecyclerView();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }else{
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //***Filter the questions according to the tag selected and the number of likes***//
    private void ShowTagFilterResults() {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (compoundButton.getText().toString().equals("All")){
                        emptyTagSearch.setVisibility(View.GONE);
                        FetchAllQuestions();
                    }else {
                        postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuestionsDataHolder = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        PostModel post = document.toObject(PostModel.class);
                                        ArrayList<String> tags = post.getTags();
                                        if (tags!=null){
                                            for (String tag : tags){
                                                if (compoundButton.getText().toString().equals(tag)){
                                                    QuestionsDataHolder.add(post);
                                                }
                                            }
                                        }
                                    }
                                    if (QuestionsDataHolder.size() != 0){
                                        emptyTagSearch.setVisibility(View.GONE);
                                        SortDataByAnswers(QuestionsDataHolder);
                                        BuildRecyclerView();
                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }else {
                                        BuildRecyclerView();
                                        progressBar.setVisibility(View.GONE);
                                        emptyTagSearch.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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

    //***Initialize chips***//
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