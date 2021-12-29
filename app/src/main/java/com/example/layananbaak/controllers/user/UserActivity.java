package com.example.layananbaak.controllers.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.layananbaak.LoginActivity;
import com.example.layananbaak.ProfileActivity;
import com.example.layananbaak.R;
import com.example.layananbaak.controllers.admin.B0;
import com.example.layananbaak.controllers.admin.B1;
import com.example.layananbaak.controllers.admin.B2;
import com.example.layananbaak.controllers.admin.B3;
import com.example.layananbaak.controllers.admin.B4;
import com.example.layananbaak.controllers.admin.B5;
import com.example.layananbaak.controllers.admin.B6;
import com.example.layananbaak.controllers.admin.B7;
import com.example.layananbaak.controllers.admin.B8;
import com.example.layananbaak.controllers.admin.B9;
import com.example.layananbaak.models.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {
    String TAG = "MainActivity";


    SweetAlertDialog sweetAlertDialog;
    static SharedPreferences sharedpreferences;
    private String getUserID;



    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() == null){
            //Do anything here which needs to be done after signout is complete
            signOutComplete();
        }
    };

    private FirebaseFirestore db;
    Context context;
    CircleImageView profilePengaturan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        context = UserActivity.this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        getUserID = user.getUid();

        if (getUserID == null) {

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        profilePengaturan = toolbar.findViewById(R.id.profilePengaturan);
        profilePengaturan.setOnClickListener(v->{
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.b1).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B1Registrasi.class);
            startActivity(intent);
        });

        findViewById(R.id.b2).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B2Registrasi.class);
            startActivity(intent);
        });
        findViewById(R.id.b3).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B3Registrasi.class);
            startActivity(intent);
        });
        findViewById(R.id.b4).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B4Registrasi.class);
            startActivity(intent);
        });
        findViewById(R.id.b5).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B5Registrasi.class);
            startActivity(intent);
        });
        findViewById(R.id.b6).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B6Registrasi.class);
            startActivity(intent);
        });
        findViewById(R.id.b7).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B7Registrasi.class);
            startActivity(intent);
        });
        findViewById(R.id.b8).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B8Registrasi.class);
            startActivity(intent);
        });

        findViewById(R.id.b9).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), B9Registrasi.class);
            startActivity(intent);
        });


    }



    public void setDataProfile(){

        db.collection("profile")
                .document(getUserID)
                .addSnapshotListener(this, (documentSnapshot, e) -> {

                    try{

                        Profile p = documentSnapshot.toObject(Profile.class);
                        if( p != null ){

                            p.setKey( documentSnapshot.getId() );

                            if (!TextUtils.isEmpty(p.foto)) {
                                Picasso.get()
                                        .load(p.foto)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .into(profilePengaturan);
                            }
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                });


    }

    @Override
    protected void onStart() {
        super.onStart();


        setDataProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    private void signOutComplete(){

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Sesi akan kami akhiri segera!");
        sweetAlertDialog.setConfirmText("Keluar!");
        sweetAlertDialog.setConfirmClickListener(sDialog -> {
            sDialog.dismissWithAnimation();

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();

        });
        sweetAlertDialog.setCancelText("Batal");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if(item.getItemId() == R.id.navigation_mhs){
            Intent intent = new Intent(getApplicationContext(), B0.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.navigation_logout){
            firebaseAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }
}