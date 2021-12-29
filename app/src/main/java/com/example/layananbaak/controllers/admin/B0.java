package com.example.layananbaak.controllers.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layananbaak.LoginActivity;
import com.example.layananbaak.R;
import com.example.layananbaak.controllers.user.UserActivity;
import com.example.layananbaak.models.Profile;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class B0 extends AppCompatActivity {
    String TAG = "B1";

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recycle_view;
    LinearLayout empty;


    SweetAlertDialog sweetAlertDialog;
    static SharedPreferences sharedpreferences;

    FirebaseStorage storage;
    StorageReference storageReference;
    Context context;

    private String getUserID;
    String xnama, xemail, xnpm;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_b0mahasiswa);
        context = B0.this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();


        getUserID = user.getUid();
        xnama = user.getDisplayName();
        xemail = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);


        getList();


    }



    private void getList(){
        sweetAlertDialog.show();

        empty = findViewById(R.id.empty_view);
        recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setVisibility(View.GONE);
        recycle_view.setNestedScrollingEnabled(false);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view.setLayoutManager(linearLayoutManager);


        Query query = db.collection("profile");
        query.get().addOnCompleteListener(command -> {
            sweetAlertDialog.dismissWithAnimation();
            adapter.notifyDataSetChanged();
            recycle_view.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        });
        FirestoreRecyclerOptions<Profile> response = new FirestoreRecyclerOptions.Builder<Profile>()
                .setQuery(query, Profile.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Profile, AdapterViewHolder>(response) {

            @Override
            public void onBindViewHolder(AdapterViewHolder holder, int position, Profile model) {
                String docId = getSnapshots().getSnapshot(position).getId();

                holder.tv1.setText(model.nama);
                holder.tv2.setText(model.npm);
                holder.tv3.setVisibility(View.GONE);
                holder.borderLeft.setVisibility(View.GONE);
                if(TextUtils.isEmpty(model.nama)) holder.itemView.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(model.foto)) {
                    Picasso.get()
                            .load(model.foto)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(holder.imageView1);
                }

                holder.action_del.setOnClickListener(view -> {

                    sweetAlertDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Anda yakin?")
                            .setContentText("Yakin mau hapus data?");
                    sweetAlertDialog.setConfirmText("Yakin!");
                    sweetAlertDialog.setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();

                        db.collection("profile" ).document(docId).delete();
                    });
                    sweetAlertDialog.setCancelText("Batal");
                    sweetAlertDialog.showCancelButton(true);
                    sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();

                });


            }

            @Override
            public AdapterViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_list, group, false);

                return new AdapterViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }

        };

        adapter.notifyDataSetChanged();

        recycle_view.setAdapter(adapter);
    }


    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private View borderLeft;
        private CircleImageView imageView1;
        private TextView tv1, tv2, tv3, tv4;
        private ImageButton action_del;
        public AdapterViewHolder(View itemView) {
            super(itemView);

            borderLeft = itemView.findViewById(R.id.borderLeft);
            imageView1 = itemView.findViewById(R.id.imageView1);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);
            action_del = itemView.findViewById(R.id.action_del);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.b, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if(item.getItemId() == R.id.navigation_cari){
        }

        return super.onOptionsItemSelected(item);
    }
}
