package com.example.layananbaak;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.layananbaak.controllers.admin.AdminActivity;
import com.example.layananbaak.controllers.user.UserActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    String email, password, fullname;

    private FirebaseFirestore db;

    SweetAlertDialog sweetAlertDialog;

    public static final String MyPREFERENCES = "MyPrefs" ;
    static SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        requestStoragePermission();

        final TextInputEditText txtEmail = findViewById(R.id.editEmail);
        final TextInputEditText editPassword = findViewById(R.id.editPassword);
        final TextInputEditText editFullName = findViewById(R.id.editFullName);
        final TextInputLayout editFullNameLayout = findViewById(R.id.editFullNameLayout);

        final MaterialButton actSignIn = findViewById(R.id.actSignIn);
        final MaterialButton actSignUp = findViewById(R.id.actSignUp);
        final MaterialButton actSignInConfirm = findViewById(R.id.actSignInConfirm);
        final MaterialButton actSignUpConfirm = findViewById(R.id.actSignUpConfirm);


        actSignIn.setOnClickListener(v -> {
            email = txtEmail.getText().toString();
            password = editPassword.getText().toString();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(v.getContext(),"Email atau Sandi belum diisi!", Toast.LENGTH_LONG).show();
            }else{
                getLoadDialog("Tunggu sebentar","Sedang memperbaharui data",SweetAlertDialog.PROGRESS_TYPE);

                getSignIn();

            }
        });

        actSignUp.setOnClickListener(v -> {
            email = txtEmail.getText().toString();
            password = editPassword.getText().toString();
            fullname = editFullName.getText().toString();

            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(fullname)){
                Toast.makeText(v.getContext(),"Email atau Sandi belum diisi!", Toast.LENGTH_LONG).show();
            }else{
                getLoadDialog("Tunggu sebentar","Sedang memperbaharui data",SweetAlertDialog.PROGRESS_TYPE);

                getSignUp();
            }
        });

        actSignUpConfirm.setOnClickListener(v -> {

            actSignIn.setVisibility(View.GONE);
            actSignUp.setVisibility(View.VISIBLE);
            actSignInConfirm.setVisibility(View.VISIBLE);
            actSignUpConfirm.setVisibility(View.GONE);
            editFullNameLayout.setVisibility(View.VISIBLE);

        });

        actSignInConfirm.setOnClickListener(v -> {

            actSignIn.setVisibility(View.VISIBLE);
            actSignUp.setVisibility(View.GONE);
            actSignInConfirm.setVisibility(View.GONE);
            actSignUpConfirm.setVisibility(View.VISIBLE);
            editFullNameLayout.setVisibility(View.GONE);

        });

    }


    private void getLoadDialog(String title, String message, int type){
        if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
            sweetAlertDialog.dismiss();
        }

        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, type);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    private void getSignIn(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Gagal masuk, periksa kembali akun.",
                                Toast.LENGTH_LONG).show();

                        getLoadDialog("Oops","Gagal masuk, periksa kembali akun",SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.showCancelButton(true);
                        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());

                        updateUI(null);
                    }

                    // ...
                });

    }

    private void getSignUp(){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        sweetAlertDialog.dismiss();
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullname)
                                .build();

                        user.updateProfile(profileUpdates);


                        Toast.makeText(LoginActivity.this, "Sign up success, now you can login.",
                                Toast.LENGTH_SHORT).show();

                        updateUI(null);

                    } else {
                        sweetAlertDialog.dismiss();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        getLoadDialog("Oops","Gagal daftar, periksa kembali field dan coba lagi",SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.showCancelButton(true);
                        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());

                        updateUI(null);
                    }

                    // ...
                });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
            sweetAlertDialog.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
            sweetAlertDialog.dismiss();
        }
    }

    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            Intent intent = null;
            if (!TextUtils.isEmpty(uid) && getAdminEmail(email)) {

                intent = new Intent(LoginActivity.this, AdminActivity.class);

            }else {

                intent = new Intent(LoginActivity.this, UserActivity.class);

            }
            startActivity(intent);
            finish();
        }
    }

    private boolean getAdminEmail(String email){
        String email_admin[] = getResources().getStringArray(R.array.email_admin);
        for(int i = 0; i<email_admin.length; i++){
            if(email_admin[i].equalsIgnoreCase(email)) return true;
        }

        return false;
    }



    private void requestStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Log.i("izin", "Semua izin telah disetujui!");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Membutuhkan Izin");
        builder.setMessage("Beberapa fitur diperlukan untuk aplikasi ini. Kamu Setujui di pengaturan.");
        builder.setPositiveButton("KE PENGATURAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    @Override
    public void onBackPressed() {

        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Ingin keluar dari aplikasi!");
        sweetAlertDialog.setConfirmText("Ok!");
        sweetAlertDialog.setConfirmClickListener(sDialog -> {
            sDialog.dismissWithAnimation();

            finish();
            System.exit(0);
        });
        sweetAlertDialog.setCancelText("Batal");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

    }
}