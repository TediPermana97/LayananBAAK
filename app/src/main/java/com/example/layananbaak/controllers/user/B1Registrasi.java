package com.example.layananbaak.controllers.user;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layananbaak.LoginActivity;
import com.example.layananbaak.R;
import com.example.layananbaak.models.Sidang;
import com.example.layananbaak.utility.Fungsi;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import info.androidhive.fontawesome.FontTextView;

public class B1Registrasi extends AppCompatActivity {
    String TAG = "B1";

    private String getUserID;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    SweetAlertDialog sweetAlertDialog;
    static SharedPreferences sharedpreferences;

    FirebaseStorage storage;
    StorageReference storageReference;
    Context context;

    String xnama, xemail, xnpm;
    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_b1_registrasi);
        context = B1Registrasi.this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        xnpm = sharedpreferences.getString("xnpm","");

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
        getLoadDialog();

        /**
         * BOTTOM SHEET ADD
         */
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(B1Registrasi.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_daftar, null);
        mBottomSheetDialog = new BottomSheetDialog(B1Registrasi.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });
        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());



        AppCompatEditText editKey = findViewById(R.id.editKey);
        TextInputEditText tf_judul = findViewById(R.id.tf_judul);
        TextInputEditText tf_pembimbing_nidn = findViewById(R.id.tf_pembimbing_nidn);
        TextInputEditText tf_bimbingan_nosk = findViewById(R.id.tf_bimbingan_nosk);
        TextInputEditText tf_bimbingan_tanggal_sk = findViewById(R.id.tf_bimbingan_tanggal_sk);
        TextInputEditText tf_sidang_tanggal_acc = findViewById(R.id.tf_sidang_tanggal_acc);
        TextInputEditText tf_bimbingan_isi = findViewById(R.id.tf_bimbingan_isi);

        AutoCompleteTextView tf_pembimbing =  findViewById(R.id.tf_pembimbing);
        AutoCompleteTextView tf_penguji1 =  findViewById(R.id.tf_penguji1);
        AutoCompleteTextView tf_penguji2 =  findViewById(R.id.tf_penguji2);

        tf_pembimbing.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.pembimbing)));
        tf_penguji1.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.pembimbing)));
        tf_penguji2.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.pembimbing)));


        tf_bimbingan_tanggal_sk.setText(sdf.format(new Date()));
        tf_bimbingan_tanggal_sk.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tf_bimbingan_tanggal_sk.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    B1Registrasi.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });
        tf_sidang_tanggal_acc.setText(sdf.format(new Date()));
        tf_sidang_tanggal_acc.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tf_sidang_tanggal_acc.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    B1Registrasi.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });


        findViewById(R.id.action_simpan).setOnClickListener(v-> {

            String key = editKey.getText().toString();
            String judul = tf_judul.getText().toString();

            String pembimbing = tf_pembimbing.getText().toString();
            String penguji1 = tf_penguji1.getText().toString();
            String penguji2 = tf_penguji2.getText().toString();

            String pembimbing_nidn = tf_pembimbing_nidn.getText().toString();
            String bimbingan_nosk = tf_bimbingan_nosk.getText().toString();
            String bimbingan_tanggal_sk = tf_bimbingan_tanggal_sk.getText().toString();
            String sidang_tanggal_acc = tf_sidang_tanggal_acc.getText().toString();
            String bimbingan_isi = tf_bimbingan_isi.getText().toString();


            if(TextUtils.isEmpty(judul)){
                Toast.makeText(v.getContext(),"Judul belum diisi!",Toast.LENGTH_SHORT).show();
            }else{
                getLoadDialog();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                if(!TextUtils.isEmpty(key)){
                    //update
                    db.collection("sidang").document(key).set(
                            new Sidang(getUserID,judul,pembimbing,penguji1,penguji2,pembimbing_nidn,bimbingan_nosk,bimbingan_tanggal_sk,sidang_tanggal_acc,bimbingan_isi)
                    ).addOnSuccessListener(documentReference -> {
                        sweetAlertDialog.dismissWithAnimation();

                        editKey.setText("");
                        v.setEnabled(true);

                        Toast.makeText(B1Registrasi.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                    });

                }else{
                    //add
                    db.collection("sidang").add(
                            new Sidang(getUserID,judul,pembimbing,penguji1,penguji2,pembimbing_nidn,bimbingan_nosk,bimbingan_tanggal_sk,sidang_tanggal_acc,bimbingan_isi, calendar.getTime())
                    ).addOnSuccessListener(documentReference -> {
                        sweetAlertDialog.dismissWithAnimation();

                        editKey.setText("");
                        v.setEnabled(true);

                        Toast.makeText(B1Registrasi.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                    });

                }



                ((TextInputEditText) findViewById(R.id.tf_judul)).setText("");
                ((AutoCompleteTextView) findViewById(R.id.tf_pembimbing)).clearListSelection();
                ((AutoCompleteTextView) findViewById(R.id.tf_penguji1)).clearListSelection();
                ((AutoCompleteTextView) findViewById(R.id.tf_penguji2)).clearListSelection();
                ((TextInputEditText) findViewById(R.id.tf_pembimbing_nidn)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_bimbingan_nosk)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_bimbingan_tanggal_sk)).setText("");
                ((AppCompatEditText) findViewById(R.id.tf_sidang_tanggal_acc)).setText("");
                ((AppCompatEditText) findViewById(R.id.tf_bimbingan_isi)).setText("");
            }
        });

        getList();
    }

    private void getLoadDialog(){
        if(sweetAlertDialog != null) sweetAlertDialog.dismiss();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }


    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recycle_view;
    LinearLayout empty;

    private void getList(){

        empty = bottomSheetLayout.findViewById(R.id.empty_view);
        recycle_view = bottomSheetLayout.findViewById(R.id.recycle_view);
        empty.setVisibility(View.VISIBLE);
        recycle_view.setVisibility(View.GONE);
        recycle_view.setNestedScrollingEnabled(false);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view.setLayoutManager(linearLayoutManager);

        Query query = db.collection("sidang").whereEqualTo("uid", getUserID);
        query.get().addOnCompleteListener(command -> {

            sweetAlertDialog.dismissWithAnimation();
            adapter.notifyDataSetChanged();

            recycle_view.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        });


        FirestoreRecyclerOptions<Sidang> response = new FirestoreRecyclerOptions.Builder<Sidang>()
                .setQuery(query, Sidang.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Sidang, AdapterViewHolder>(response) {

            @Override
            public void onBindViewHolder(AdapterViewHolder holder, int position, Sidang model) {
                String docId = getSnapshots().getSnapshot(position).getId();

                holder.tv1.setText(model.judul);

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a", Locale.US);
                String time = sdf.format(model.tanggal);

                holder.tv2.setText(time);
                if(!TextUtils.isEmpty(model.file_sidang)){
                    holder.fontTextView.setTextColor(holder.itemView.getResources().getColor(R.color.colorGreen));
                    holder.linearLayout.setBackgroundColor(holder.itemView.getResources().getColor(R.color.colorGreen));
                }

                holder.action_dl.setOnClickListener(view -> {
                    if(!TextUtils.isEmpty(model.file_sidang)){
                        Fungsi.dlFile(view.getContext(),model.file_sidang,model.judul);
                    }else{
                        Toast.makeText(view.getContext(),"File tidak tersedia!",Toast.LENGTH_SHORT).show();
                    }
                });



            }

            @Override
            public AdapterViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_dl, group, false);

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

        private FontTextView fontTextView;
        private TextView tv1, tv2, tv3, tv4;
        private ImageButton action_dl;
        private LinearLayout linearLayout;
        public AdapterViewHolder(android.view.View itemView) {
            super(itemView);

            fontTextView = itemView.findViewById(R.id.imageView1);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            action_dl = itemView.findViewById(R.id.action_dl);
            linearLayout = itemView.findViewById(R.id.linearLayout);


        }
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
        inflater.inflate(R.menu.b1, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if(item.getItemId() == R.id.navigation_daftar){
            mBottomSheetDialog.show();
            adapter.startListening();
        }

        return super.onOptionsItemSelected(item);
    }
}
