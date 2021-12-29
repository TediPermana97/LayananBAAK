package com.example.layananbaak.controllers.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layananbaak.LoginActivity;
import com.example.layananbaak.R;
import com.example.layananbaak.controllers.admin.B1;
import com.example.layananbaak.models.SKM;
import com.example.layananbaak.models.Sidang;
import com.example.layananbaak.utility.FileUtils;
import com.example.layananbaak.utility.Fungsi;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import info.androidhive.fontawesome.FontTextView;

public class B2Registrasi extends AppCompatActivity {
    String TAG = "B2";

    private String getUserID;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    SweetAlertDialog sweetAlertDialog;
    static SharedPreferences sharedpreferences;

    FirebaseStorage storage;
    StorageReference storageReference;
    Context context;

    String xnama, xemail, xnpm;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_b2_registrasi);
        context = B2Registrasi.this;


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
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(B2Registrasi.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_daftar, null);
        mBottomSheetDialog = new BottomSheetDialog(B2Registrasi.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });
        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        AppCompatEditText editKey = findViewById(R.id.editKey);
        TextInputEditText tf_nik_ortu = findViewById(R.id.tf_nik_ortu);
        TextInputEditText tf_nama_ortu = findViewById(R.id.tf_nama_ortu);
        TextInputEditText tf_pekerjaan_ortu = findViewById(R.id.tf_pekerjaan_ortu);
        TextInputEditText tf_keperluan_surat = findViewById(R.id.tf_keperluan_surat);
        AppCompatEditText editFilePersyaratan = findViewById(R.id.editFilePersyaratan);
        findViewById(R.id.tf_file_persyaratan).setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT);
            } catch (Exception ex) {
                System.out.println("browseClick :"+ex);
            }

        });

        tf_nik_ortu.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        tf_nama_ortu.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        tf_pekerjaan_ortu.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        tf_keperluan_surat.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        findViewById(R.id.action_simpan).setOnClickListener(v-> {

            String key = editKey.getText().toString();
            String nik_ortu = tf_nik_ortu.getText().toString();
            String nama_ortu = tf_nama_ortu.getText().toString();
            String pekerjaan_ortu = tf_pekerjaan_ortu.getText().toString();
            String keperluan_surat = tf_keperluan_surat.getText().toString();
            String file_persyaratan = editFilePersyaratan.getText().toString();


            if(TextUtils.isEmpty(file_persyaratan)){
                Toast.makeText(v.getContext(),"File Syarat belum diisi!",Toast.LENGTH_SHORT).show();
            }else{
                getLoadDialog();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                if(!TextUtils.isEmpty(key)){
                    //update
                    db.collection("skm").document(key).set(
                            new SKM(getUserID,nik_ortu,nama_ortu,pekerjaan_ortu,keperluan_surat,file_persyaratan)
                    ).addOnSuccessListener(documentReference -> {
                        sweetAlertDialog.dismissWithAnimation();

                        editKey.setText("");
                        v.setEnabled(true);

                        Toast.makeText(B2Registrasi.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                    });

                }else{
                    //add
                    db.collection("skm").add(
                            new SKM(getUserID,nik_ortu,nama_ortu,pekerjaan_ortu,keperluan_surat,file_persyaratan,calendar.getTime())
                    ).addOnSuccessListener(documentReference -> {
                        sweetAlertDialog.dismissWithAnimation();

                        editKey.setText("");
                        v.setEnabled(true);

                        Toast.makeText(B2Registrasi.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                    });

                }

                ((TextInputEditText) findViewById(R.id.tf_nik_ortu)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_nama_ortu)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_pekerjaan_ortu)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_keperluan_surat)).setText("");
                ((AppCompatEditText) findViewById(R.id.editFilePersyaratan)).setText("");
                ((MaterialButton) findViewById(R.id.tf_file_persyaratan)).setText("File Syarat");


            }
        });



        getList();
        getDialog();
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

        Log.e("uid",getUserID);

        Query query = db.collection("skm").whereEqualTo("uid",getUserID);
        query.get().addOnCompleteListener(command -> {
            sweetAlertDialog.dismissWithAnimation();
            adapter.notifyDataSetChanged();
            recycle_view.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        });
        FirestoreRecyclerOptions<SKM> response = new FirestoreRecyclerOptions.Builder<SKM>()
                .setQuery(query, SKM.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<SKM, AdapterViewHolder>(response) {

            @Override
            public void onBindViewHolder(AdapterViewHolder holder, int position, SKM model) {
                String docId = getSnapshots().getSnapshot(position).getId();

                holder.tv1.setText(model.keperluan_surat);

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a", Locale.US);
                String time = sdf.format(model.tanggal);

                holder.tv2.setText(time);

                if(!TextUtils.isEmpty(model.file_skm)){
                    holder.fontTextView.setTextColor(holder.itemView.getResources().getColor(R.color.colorGreen));
                    holder.linearLayout.setBackgroundColor(holder.itemView.getResources().getColor(R.color.colorGreen));
                }

                holder.action_dl.setOnClickListener(view -> {
                    if(!TextUtils.isEmpty(model.file_skm)){
                        Fungsi.dlFile(view.getContext(),model.file_skm,model.keperluan_surat);
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


    int FILE_SELECT = 112;
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            File file = new File(FileUtils.getPath(context, data.getData()));

            if (!file.exists()) {
                Toast.makeText(context, "File tidak tersedia!", Toast.LENGTH_SHORT).show();
            } else {
                if (requestCode == FILE_SELECT) {

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    Uri uri = Uri.fromFile(file);

                    String extension = FilenameUtils.getExtension(file.getAbsolutePath());
                    final String namaFile = "syaratb2_"+new Date().getTime()+"."+ extension;
                    StorageReference ref = storageReference.child(getUserID+"/files/"+namaFile);
                    ref.putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                                ref.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                    Log.d(TAG, "onSuccess: uri= " + uri1.toString());
                                    //editKeyFoto.setText(uri.toString());


                                    MaterialButton tf_file_persyaratan = findViewById(R.id.tf_file_persyaratan);
                                    tf_file_persyaratan.setText(namaFile);


                                    AppCompatEditText editFilePersyaratan = findViewById(R.id.editFilePersyaratan);
                                    editFilePersyaratan.setText(uri1.toString());

                                });

                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnProgressListener(taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });
                }
            }
        }
    }

    private void getDialog(){
        new AlertDialog.Builder(context)
                .setTitle("Syarat")
                .setMessage(getString(R.string.text_b2))
                .setPositiveButton("Siap", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Batal", (dialog, which) -> onBackPressed())
                .show();
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
