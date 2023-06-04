package ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shalevjavaproject.AllTasksActivity;
import com.example.shalevjavaproject.R;
import com.example.shalevjavaproject.TodoListsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.List;

import model.Task;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>{


    private Context context;
    private List<Task> taskList;

    //private String documentId;

    private static Dialog infoDialog;

    public Button edit_btn;
    private CardView delete;
    private ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Task");


    public TaskRecyclerAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.task_row, parent , false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRecyclerAdapter.ViewHolder holder, int position) {

        Task task = taskList.get(position);
        String imageUrl;
        holder.title.setText(task.getTitle());
        holder.details.setText(task.getTask_details());
        holder.name.setText(task.getUserName());
        imageUrl = task.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(task.getTimeAdded().getSeconds() * 1000 );
        holder.dateAdded.setText(timeAgo);

        //picasso use!!
        Picasso.get().load(imageUrl).placeholder(R.drawable.noimage)
                .fit()
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title , details , dateAdded , name;

        public ImageView image;
        //public ImageButton shareButton;


        public ViewHolder(@NonNull View itemView , Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.task_title_list);
            details = itemView.findViewById(R.id.task_details_list);
            dateAdded = itemView.findViewById(R.id.task_timestamp_list);
            image = itemView.findViewById(R.id.task_image_list);
            name = itemView.findViewById(R.id.task_row_username);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            /*shareButton = itemView.findViewById(R.id.task_row_share_button);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //context.startActivity();
                }
            });*/


            //DELETING TASK!
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    infoDialog = new Dialog(context);
                    infoDialog.setContentView(R.layout.popup_delete);
                    infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    infoDialog.show();

                    delete = infoDialog.findViewById(R.id.card_delete);
                    progressBar=infoDialog.findViewById(R.id.progress_delete);


                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            final int position = getAdapterPosition();
                            final Task task = taskList.get(position);
                            final String userId = task.getUserId();
                            final String url=task.getImageUrl();
                            final Timestamp timeAdded = task.getTimeAdded();



                            collectionReference
                                    .whereEqualTo("userId", userId)
                                    .whereEqualTo("timeAdded", timeAdded)
                                    .addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                                                snapshot.getReference().delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(context, "המשימה נמחקה בהצלחה!\nנהדר!!! עכשיו הוסיפו עוד!", Toast.LENGTH_SHORT).show();
                                                                final Intent intent = new Intent(context, AllTasksActivity.class);
                                                                context.startActivity(intent);

                                                            }
                                                        });
                                            }
                                        }
                                    });
                            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                            photoRef.delete();
                        }
                    });

                    return true;
                }
            });


            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    Task taskEdit = taskList.get(position);

                    final Intent intent = new Intent(context , TodoListsActivity.class);
                    intent.putExtra("task", taskEdit);
                    intent.putExtra("postActionId", 1);
                    context.startActivity(intent);

                }
            });


        }
    }
}

























