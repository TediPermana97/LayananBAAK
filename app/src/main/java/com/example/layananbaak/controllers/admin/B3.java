package com.example.layananbaak.controllers.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layananbaak.LoginActivity;
import com.example.layananbaak.R;
import com.example.layananbaak.models.CutiAkademik;
import com.example.layananbaak.controllers.user.B3Registrasi;
import com.example.layananbaak.models.Profile;
import com.example.layananbaak.models.Sidang;
import com.example.layananbaak.utility.FileUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import info.androidhive.fontawesome.FontTextView;

public class B3 extends AppCompatActivity {
    String TAG = "B3";

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

    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBehavior;

    List<Profile> profileList = new ArrayList<>();
    int FILE_SELECT = 111;
    AppCompatEditText editKey,editKeyUID,editKeyJudul;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_b3);
        context = B3.this;


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

        /**
         * BOTTOM SHEET ADD
         */
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(B3.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_b3, null);
        mBottomSheetDialog = new BottomSheetDialog(B3.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        editKeyUID = bottomSheetLayout.findViewById(R.id.editKeyUID);
        editKeyJudul = bottomSheetLayout.findViewById(R.id.editKeyJudul);
        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());
        bottomSheetLayout.findViewById(R.id.fab_upload).setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT);
            } catch (Exception ex) {
                System.out.println("browseClick :"+ex);
            }

        });



        getListProfile();
        getList();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            File file = new File(FileUtils.getPath(context, data.getData()));

            if (!file.exists()) {
                Toast.makeText(context, "File tidak tersedia!", Toast.LENGTH_SHORT).show();
            } else {
                if (requestCode == FILE_SELECT) {

                    String docId = editKey.getText().toString();
                    String uid = editKeyUID.getText().toString();

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    Uri uri = Uri.fromFile(file);

                    String extension = FilenameUtils.getExtension(file.getAbsolutePath());
                    final String namaFile = docId + "." + extension;
                    StorageReference ref = storageReference.child(uid+"/files/cutiakademik_"+namaFile);
                    ref.putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(B3.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                ref.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                    Log.d(TAG, "onSuccess: uri= " + uri1.toString());
                                    //editKeyFoto.setText(uri.toString());

                                    db.collection("cutiakademik")
                                            .document(docId)
                                            .set(new CutiAkademik(uri1.toString()), SetOptions.mergeFields("file_cutiakademik")
                                            )
                                            .addOnSuccessListener(documentReference -> {

                                                FloatingActionButton  fup = bottomSheetLayout.findViewById(R.id.fab_upload);
                                                fup.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));

                                                //editKeyFoto.setText("");
                                                Toast.makeText(B3.this, "File telah tersimpan", Toast.LENGTH_SHORT).show();
                                            });
                                });

                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(B3.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getListProfile(){
        db.collection("profile")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    ArrayList<Profile> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : documentSnapshot) {
                        Profile p = doc.toObject(Profile.class);
                        p.setKey( doc.getId() );
                        items.add( p );
                    }
                    profileList.addAll(items);
                    if(profileList.size() > 0){
                        adapter.startListening();
                    }
                });
    }


    private void getList(){
        sweetAlertDialog.show();

        empty = findViewById(R.id.empty_view);
        recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setVisibility(View.GONE);
        recycle_view.setNestedScrollingEnabled(false);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view.setLayoutManager(linearLayoutManager);


        Query query = db.collection("cutiakademik").orderBy("tanggal", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(command -> {
            sweetAlertDialog.dismissWithAnimation();
            adapter.notifyDataSetChanged();
            recycle_view.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        });
        FirestoreRecyclerOptions<CutiAkademik> response = new FirestoreRecyclerOptions.Builder<CutiAkademik>()
                .setQuery(query, CutiAkademik.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CutiAkademik, AdapterViewHolder>(response) {

            @Override
            public void onBindViewHolder(AdapterViewHolder holder, int position, CutiAkademik model) {
                String docId = getSnapshots().getSnapshot(position).getId();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a", Locale.US);
                String time = sdf.format(model.tanggal);

                holder.tv3.setText(time);

                if(!TextUtils.isEmpty(model.file_cutiakademik)){
                    holder.borderLeft.setBackgroundColor(holder.itemView.getResources().getColor(R.color.colorGreen));
                }

                for(Profile p:profileList){
                    if(p.key.equalsIgnoreCase(model.uid)){
                        if (!TextUtils.isEmpty(p.foto)) {
                            Picasso.get()
                                    .load(p.foto)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(holder.imageView1);
                        }

                        holder.tv1.setText(p.nama);
                        holder.tv2.setText(p.npm);
                    }
                }

                holder.action_del.setOnClickListener(view -> {

                    sweetAlertDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Anda yakin?")
                            .setContentText("Yakin mau hapus data?");
                    sweetAlertDialog.setConfirmText("Yakin!");
                    sweetAlertDialog.setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();

                        db.collection("cutiakademik" ).document(docId).delete();
                    });
                    sweetAlertDialog.setCancelText("Batal");
                    sweetAlertDialog.showCancelButton(true);
                    sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();

                });

                holder.itemView.setOnClickListener(view -> {
                    mBottomSheetDialog.show();

                    editKey.setText(docId);
                    editKeyUID.setText(model.uid);
                    editKeyJudul.setText(model.alasan_cuti);

                    FloatingActionButton fup = bottomSheetLayout.findViewById(R.id.fab_upload);
                    if(!TextUtils.isEmpty(model.file_cutiakademik)){
                        fup.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
                    }

                    CircleImageView foto;
                    TextView nama, npm, email, nomor_telp;
                    TextView xtv1, xtv2, xtv3, xtv4, xtv5, xtv6, xtv7, xtv8, xtv9, xtv10, xtv11, xtv12;

                    foto = bottomSheetLayout.findViewById(R.id.foto);
                    nama = bottomSheetLayout.findViewById(R.id.nama);
                    npm = bottomSheetLayout.findViewById(R.id.npm);
                    email = bottomSheetLayout.findViewById(R.id.email);
                    nomor_telp = bottomSheetLayout.findViewById(R.id.nomor_telp);



                    for(Profile p:profileList){
                        if(p.key.equalsIgnoreCase(model.uid)){
                            if (!TextUtils.isEmpty(p.foto)) {
                                Picasso.get()
                                        .load(p.foto)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .into(foto);
                            }

                            nama.setText(p.nama);
                            npm.setText(p.npm);
                            email.setText(p.email);
                            nomor_telp.setText(p.nomor_telp);
                        }
                    }



                    xtv1 = bottomSheetLayout.findViewById(R.id.tv1);
                    xtv2 = bottomSheetLayout.findViewById(R.id.tv2);
                    xtv3 = bottomSheetLayout.findViewById(R.id.tv3);

                    xtv1.setText(model.semester);
                    xtv2.setText(model.tahun_akademik);
                    xtv3.setText(model.alasan_cuti);


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
        private TextView tv1, tv2, tv3, tv4, tv5;
        private ImageButton action_del,action_syarat;
        public AdapterViewHolder(android.view.View itemView) {
            super(itemView);

            borderLeft = itemView.findViewById(R.id.borderLeft);
            imageView1 = itemView.findViewById(R.id.imageView1);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);
            action_del = itemView.findViewById(R.id.action_del);
            action_syarat = itemView.findViewById(R.id.action_syarat);


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
