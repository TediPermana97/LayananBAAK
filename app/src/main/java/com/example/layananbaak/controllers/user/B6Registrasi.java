package com.example.layananbaak.controllers.user;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layananbaak.LoginActivity;
import com.example.layananbaak.R;
import com.example.layananbaak.models.SIKP;
import com.example.layananbaak.models.Sidang;
import com.example.layananbaak.utility.FileUtils;
import com.example.layananbaak.utility.Fungsi;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

public class B6Registrasi extends AppCompatActivity {
    String TAG = "B6";

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
        setContentView(R.layout.pages_b6_registrasi);
        context = B6Registrasi.this;


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
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(B6Registrasi.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_daftar, null);
        mBottomSheetDialog = new BottomSheetDialog(B6Registrasi.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });
        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        AppCompatEditText editKey = findViewById(R.id.editKey);

        TextInputEditText tf_ditujukan_kepada = findViewById(R.id.tf_ditujukan_kepada);
        TextInputEditText tf_istansi_perusahaan = findViewById(R.id.tf_istansi_perusahaan);
        TextInputEditText tf_alamat_istansi = findViewById(R.id.tf_alamat_istansi);
        TextInputEditText tf_alamat_istansi_kota = findViewById(R.id.tf_alamat_istansi_kota);
        TextInputEditText tf_alamat_istansi_kodepos = findViewById(R.id.tf_alamat_istansi_kodepos);
        TextInputEditText tf_alamat_istansi_provinsi = findViewById(R.id.tf_alamat_istansi_provinsi);
        TextInputEditText tf_alamat_istansi_negara = findViewById(R.id.tf_alamat_istansi_negara);
        TextInputEditText tf_tanggal_mulai = findViewById(R.id.tf_tanggal_mulai);
        TextInputEditText tf_tanggal_akhir = findViewById(R.id.tf_tanggal_akhir);
        TextInputEditText tf_keterangan = findViewById(R.id.tf_keterangan);
        TextInputEditText tf_verifikasi_kode = findViewById(R.id.tf_verifikasi_kode);
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


        tf_tanggal_mulai.setText(sdf.format(new Date()));
        tf_tanggal_mulai.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tf_tanggal_mulai.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    B6Registrasi.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });
        tf_tanggal_akhir.setText(sdf.format(new Date()));
        tf_tanggal_akhir.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tf_tanggal_akhir.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    B6Registrasi.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        findViewById(R.id.action_simpan).setOnClickListener(v-> {

            String key = editKey.getText().toString();
            String ditujukan_kepada = tf_ditujukan_kepada.getText().toString();
            String istansi_perusahaan = tf_istansi_perusahaan.getText().toString();
            String alamat_istansi = tf_alamat_istansi.getText().toString();
            String alamat_istansi_kota = tf_alamat_istansi_kota.getText().toString();
            String alamat_istansi_kodepos = tf_alamat_istansi_kodepos.getText().toString();
            String alamat_istansi_provinsi = tf_alamat_istansi_provinsi.getText().toString();
            String alamat_istansi_negara = tf_alamat_istansi_negara.getText().toString();
            String tanggal_mulai = tf_tanggal_mulai.getText().toString();
            String tanggal_akhir = tf_tanggal_akhir.getText().toString();
            String keterangan = tf_keterangan.getText().toString();
            String verifikasi_kode = tf_verifikasi_kode.getText().toString();
            String file_persyaratan = editFilePersyaratan.getText().toString();



            if(TextUtils.isEmpty(istansi_perusahaan)){
                Toast.makeText(v.getContext(),"Istansi belum diisi!",Toast.LENGTH_SHORT).show();
            }else{
                getLoadDialog();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                if(!TextUtils.isEmpty(key)){
                    //update
                    db.collection("sikp").document(key).set(
                            new SIKP(getUserID,ditujukan_kepada,istansi_perusahaan,alamat_istansi,alamat_istansi_kota,alamat_istansi_kodepos,alamat_istansi_provinsi,alamat_istansi_negara,tanggal_mulai,tanggal_akhir,keterangan,file_persyaratan,verifikasi_kode)
                    ).addOnSuccessListener(documentReference -> {
                        sweetAlertDialog.dismissWithAnimation();

                        editKey.setText("");
                        v.setEnabled(true);

                        Toast.makeText(B6Registrasi.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                    });

                }else{
                    //add
                    db.collection("sikp").add(
                            new SIKP(getUserID,ditujukan_kepada,istansi_perusahaan,alamat_istansi,alamat_istansi_kota,alamat_istansi_kodepos,alamat_istansi_provinsi,alamat_istansi_negara,tanggal_mulai,tanggal_akhir,keterangan,file_persyaratan,verifikasi_kode, calendar.getTime())
                    ).addOnSuccessListener(documentReference -> {
                        sweetAlertDialog.dismissWithAnimation();

                        editKey.setText("");
                        v.setEnabled(true);

                        Toast.makeText(B6Registrasi.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                    });

                }

                ((TextInputEditText) findViewById(R.id.tf_ditujukan_kepada)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_istansi_perusahaan)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_alamat_istansi)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_alamat_istansi_kota)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_alamat_istansi_kodepos)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_alamat_istansi_provinsi)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_alamat_istansi_negara)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_tanggal_mulai)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_tanggal_akhir)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_keterangan)).setText("");
                ((TextInputEditText) findViewById(R.id.tf_verifikasi_kode)).setText("");
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


        Query query = db.collection("sikp").whereEqualTo("uid",getUserID);
        query.get().addOnCompleteListener(command -> {
            sweetAlertDialog.dismissWithAnimation();
            adapter.notifyDataSetChanged();
            recycle_view.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        });
        FirestoreRecyclerOptions<SIKP> response = new FirestoreRecyclerOptions.Builder<SIKP>()
                .setQuery(query, SIKP.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<SIKP, AdapterViewHolder>(response) {

            @Override
            public void onBindViewHolder(AdapterViewHolder holder, int position, SIKP model) {
                String docId = getSnapshots().getSnapshot(position).getId();

                holder.tv1.setText(model.keterangan);

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a", Locale.US);
                String time = sdf.format(model.tanggal);

                holder.tv2.setText(time);

                if(!TextUtils.isEmpty(model.file_sikp)){
                    holder.fontTextView.setTextColor(holder.itemView.getResources().getColor(R.color.colorGreen));
                    holder.linearLayout.setBackgroundColor(holder.itemView.getResources().getColor(R.color.colorGreen));
                }

                holder.action_dl.setOnClickListener(view -> {
                    if(!TextUtils.isEmpty(model.file_sikp)){
                        Fungsi.dlFile(view.getContext(),model.file_sikp,model.istansi_perusahaan);
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
                    final String namaFile = "syaratb6_"+new Date().getTime()+"."+ extension;
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
                .setMessage(getString(R.string.text_b6))
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
        }

        return super.onOptionsItemSelected(item);
    }
}
