package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.QuestionsOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionBlocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<PostModel> AllPostsDataHolder;
    private Context context;
    private final QuestionsOnItemClickListner aListner;

    public QuestionBlocAdapter(ArrayList<PostModel> postsDataHolder , QuestionsOnItemClickListner listner) {
        AllPostsDataHolder = postsDataHolder;
        aListner = listner;
    }

    @Override
    public int getItemViewType(int position) {
        if (AllPostsDataHolder.get(position).getAnswersCount()!= -1 && AllPostsDataHolder.get(position).getLikesCount()!= -1){
            return 1;
        }else if (AllPostsDataHolder.get(position).getAnswersCount()!= -1 && AllPostsDataHolder.get(1).getLikesCount()==-1){
            return 2;
        }else{
            return 3;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1){
            context = parent.getContext();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
            return new ViewHolder1(view,aListner, AllPostsDataHolder);
        }else if (viewType == 2) {
            context = parent.getContext();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_text, parent, false);
            return  new ViewHolder3(view);
        }else{
            context = parent.getContext();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_answer, parent, false);
            return  new ViewHolder2(view,aListner, AllPostsDataHolder, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==1){
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            Glide.with(context).load(AllPostsDataHolder.get(position).getPublisherPic()).into(viewHolder1.img);
            viewHolder1.Question.setText(AllPostsDataHolder.get(position).getQuestion());
            viewHolder1.Name.setText(AllPostsDataHolder.get(position).getAskedBy());
            viewHolder1.Username.setText("@"+AllPostsDataHolder.get(position).getUsername());
            viewHolder1.Details.setText(AllPostsDataHolder.get(position).getBody());
            viewHolder1.Likes.setText(Integer.toString(AllPostsDataHolder.get(position).getLikesCount()));
            viewHolder1.Answers.setText(Integer.toString(AllPostsDataHolder.get(position).getAnswersCount()));
            viewHolder1.Date.setText(AllPostsDataHolder.get(position).ConvertDate());
            RunCheckForLikes(AllPostsDataHolder.get(position), viewHolder1.likeBtn);

        }else if (holder.getItemViewType()==2){
            ViewHolder3 viewHolder3 = (ViewHolder3) holder;

            if (AllPostsDataHolder.get(0).getAnswersCount()==0) {
                viewHolder3.customText.setText("Be the first who answers this question!");
            }else {
                viewHolder3.customText.setText(AllPostsDataHolder.get(0).getAnswersCount() + " answers");
            }
        }else{
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            Glide.with(context).load(AllPostsDataHolder.get(position).getPublisherPic()).into(viewHolder2.img);
            viewHolder2.Username.setText("@"+AllPostsDataHolder.get(position).getUsername());
            viewHolder2.Details.setText(AllPostsDataHolder.get(position).getBody());
            viewHolder2.Likes.setText(Integer.toString(AllPostsDataHolder.get(position).getLikesCount()));
            viewHolder2.Date.setText(AllPostsDataHolder.get(position).ConvertDate());
            RunCheckForLikesAnswerLikes(AllPostsDataHolder.get(position), viewHolder2.likeBtn);
        }
    }

    private void RunCheckForLikes(PostModel post, LottieAnimationView lottieAnimationView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference likesRef = FirebaseFirestore.getInstance().collection("Posts").document(post.getPostid());
        likesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override //check if the document exists, i.e current user likes the post
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<String> arrayList = (ArrayList<String>) doc.get("likes");
                    if (arrayList.contains(user.getUid())) {
                        lottieAnimationView.setSpeed(100);
                        lottieAnimationView.playAnimation();
                        lottieAnimationView.setTag("Liked");
                    }
                    else
                    {
                        lottieAnimationView.setTag("Like");
                    }
                }
            }
        });
    }

    private void RunCheckForLikesAnswerLikes(PostModel post, LottieAnimationView lottieAnimationView) {
        ArrayList<String> likes = post.getLikes();

        if (likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            lottieAnimationView.setSpeed(100);
            lottieAnimationView.playAnimation();
            lottieAnimationView.setTag("Liked");
        }
        else
        {
            lottieAnimationView.setTag("Like");
        };
    }

    @Override
    public int getItemCount() {
        return AllPostsDataHolder.size();
    }


    public class ViewHolder1 extends RecyclerView.ViewHolder {

        //Question Type
        LottieAnimationView likeBtn;
        ImageView img;
        TextView Question,Details,Name,Username,Likes,Answers,Date;

        private FirebaseAuth auth;
        private FirebaseUser user;
        private FirebaseFirestore fstore;

        public ViewHolder1(@NonNull View itemView , QuestionsOnItemClickListner listner, ArrayList<PostModel> postModel) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            Question = itemView.findViewById(R.id.question);
            Name = itemView.findViewById(R.id.name);
            Username = itemView.findViewById(R.id.usernamep);
            Details = itemView.findViewById(R.id.details);
            Likes = itemView.findViewById(R.id.likes);
            Answers = itemView.findViewById(R.id.answers);
            Date = itemView.findViewById(R.id.postDate);
            likeBtn = itemView.findViewById(R.id.lottieLike);

            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = auth.getCurrentUser();

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onPictureClick(position);
                        }
                    }
                }
            });

            Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onNameClick(position);
                        }
                    }
                }
            });

            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onLikeClick(position,likeBtn, Likes, false);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.answerBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onAnswerClick(position);
                        }
                    }
                }
            });
            itemView.findViewById(R.id.questionShareBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onShareClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.questionMenuBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                    if (user.getUid().equals(postModel.get(position).getPublisher()))
                        popupMenu.inflate(R.menu.my_post_menu);
                    else popupMenu.inflate(R.menu.post_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getTitle().equals("Delete")) {
                                Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            else {
                                Toast.makeText(view.getContext(), "Report", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        CircleImageView img;
        LottieAnimationView likeBtn;
        TextView Details,Username,Likes,Date;

        private FirebaseAuth auth;
        private FirebaseUser user;
        private FirebaseFirestore fstore;
        public ViewHolder2(@NonNull View itemView, QuestionsOnItemClickListner listner, ArrayList<PostModel> postHolder, QuestionBlocAdapter adapter) {
            super(itemView);
            img = itemView.findViewById(R.id.imga);
            Username = itemView.findViewById(R.id.usernamea);
            Details = itemView.findViewById(R.id.detailsa);
            Likes = itemView.findViewById(R.id.likesa);
            Date = itemView.findViewById(R.id.postDatea);
            likeBtn = itemView.findViewById(R.id.lottieLike);

            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = auth.getCurrentUser();

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onPictureClick(position);
                        }
                    }
                }
            });

            Username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onNameClick(position);
                        }
                    }
                }
            });

            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onLikeClick(position,likeBtn, Likes, true);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.answerShareBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onShareClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.answerMenuBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                    if (user.getUid().equals(postHolder.get(position).getPublisher()))
                        popupMenu.inflate(R.menu.my_post_menu);
                    else popupMenu.inflate(R.menu.post_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            boolean b = false;
                            PostModel postModel = postHolder.get(position);
                            PostModel parentPost = AllPostsDataHolder.get(0);
                            if (menuItem.getTitle().equals("Delete")) {
                                postHolder.remove(position);
                                adapter.notifyItemRemoved(position);
                         //       DeleteLikes(postModel, "AnswerLikes");

                                DocumentReference dr = fstore.collection("Posts").document(parentPost.getPostid());
                                dr.update("answersCount", FieldValue.increment(-1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        parentPost.setAnswersCount(parentPost.getAnswersCount()-1);
                                        notifyItemChanged(0);
                                    }
                                });
                                DocumentReference answerRef = dr.collection("Answers").document(postModel.getPostid());
                                answerRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
                                                .document(user.getUid());
                                        userRef.update("answers", FieldValue.arrayRemove(postModel.getPostid()));
                                        Toast.makeText(view.getContext(), "Answer deleted", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Some error occurred try again later", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                b = true;
                            }
                            else {
                                int reportCount = postModel.getReportsCount();
                                reportCount++;
                                DocumentReference answerRef = FirebaseFirestore.getInstance().collection("Posts").
                                        document(parentPost.getPostid()).collection("Answers").document(postModel.getPostid());
                                HashMap<String, Object> hm = new HashMap<>();
                                hm.put("reportsCount", reportCount);
                                answerRef.update(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Your report has been sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                b = true;
                            }
                            return b;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
        /*private void DeleteLikes(PostModel postModel, String collection)
        {
            if (collection.equals("Likes"))
            {
                CollectionReference userRef = fstore.collection("Users");
                DocumentReference postRef = fstore.collection("Posts")
                        .document(postModel.getPostid());
                postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        ArrayList<String> likes = (ArrayList<String>) doc.get("likes");
                        for (String id: likes) {
                            Task<QuerySnapshot> cr = userRef.document(id).collection(collection).
                                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
            else
            {
                CollectionReference userRef = fstore.collection("Users");
                DocumentReference postRef = fstore.collection("Posts")
                        .document(AllPostsDataHolder.get(0).getPostid()).
                                collection("Answers").document(postModel.getPostid());
                postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        ArrayList<String> likes = (ArrayList<String>) doc.get("likes");
                        for (String id: likes) {
                            Task<QuerySnapshot> cr = userRef.document(id).collection(collection).
                                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }*/
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {
        TextView customText;

        public ViewHolder3(@NonNull View itemView) {
            super(itemView);
            customText = itemView.findViewById(R.id.customText);
            customText.setTextSize(16f);
        }
    }
}
