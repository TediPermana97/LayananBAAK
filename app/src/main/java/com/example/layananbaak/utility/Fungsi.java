package com.example.layananbaak.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.layananbaak.BuildConfig;
import com.example.layananbaak.controllers.user.B1Registrasi;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Fungsi {


    static int BUFFER_SIZE = 1024;


    public static void dlFile(Context context, String ref, String judul) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(ref);

        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Downloading Please Wait!");
        pd.setIndeterminate(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();


        final File rootPath = new File(Environment.getExternalStorageDirectory(), "BAAK");

        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }


        storageRef.getMetadata().addOnSuccessListener(storageMetadata -> {
            String filename = storageMetadata.getName();

            String extension = FilenameUtils.getExtension(filename);
            final File localFile = new File(rootPath, judul +"."+extension);
            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());

                if (localFile.canRead()){

                    pd.dismiss();
                }

                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(exception -> {
                Toast.makeText(context, "Download Incompleted", Toast.LENGTH_LONG).show();
            });
        });

    }


    public void openFile(Context c,File fileName){
        Uri uri = FileProvider.getUriForFile(c, BuildConfig.APPLICATION_ID + ".provider", fileName);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");

        // FLAG_GRANT_READ_URI_PERMISSION is needed on API 24+ so the activity opening the file can read it
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(c.getPackageManager()) == null) {
            // Show an error
        } else {
            c.startActivity(intent);
        }

    }


    public static String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "0000-00-00";
        }
    }


    public static String getFormatDate(Date time){
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
        Date newDate = null;
        try {
            newDate = format.parse(time.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(newDate);

        return date;
    }


    public static Date getDate(Timestamp time){
        return time.toDate();
    }

    public static Timestamp getTimestamp(String time){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null;
        try{
            date = s.parse(time);
        }catch (Exception e){
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new Timestamp(date);
    }

    public Date getDateFromString(String datetoSaved){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(datetoSaved);
            return date ;
        } catch (ParseException e){
            return null ;
        }

    }



    public static String to(String time, String format) {

        if(format == null){
            format = "yyyy-MM-dd";
        }

        Date date_sekarang = new Date();

        SimpleDateFormat s = new SimpleDateFormat(format);
        Date date = null;
        try{
            date = s.parse(time);
        }catch (Exception e){
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);

        //String sekarang = now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+"-"+now.get(Calendar.DAY_OF_MONTH);

        //SimpleDateFormat formatter = new SimpleDateFormat("E");
        //String days = formatter.format(date);
        //int dayofweek = Integer.valueOf(days);

        //now.set(tahun,bulan,hari);


        String[] yyyymd = time.split("-");

        int tahun = 0;
        int bulan = 0;
        int hari = 0;
        String timeago = "";
        String hasil = null;


        SimpleDateFormat f1 = new SimpleDateFormat(format);
        if(time.equals(f1.format(date_sekarang))){
            timeago = "Hari ini ";
        }

        if(yyyymd.length == 3) {
            tahun = Integer.parseInt(yyyymd[0]);
            bulan = Integer.parseInt(yyyymd[1]);
            hari = Integer.parseInt(yyyymd[2]);
        }

        //String[] timeago = new String[]{"Hari ini","Kemarin","Minggu lalu","Bulan lalu","Tahun lalu"};
        String[] nama_hari = new String[]{"Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};
        String[] nama_bulan = new String[]{
                "Januari",
                "Februari",
                "Maret",
                "April",
                "Mei",
                "Juni",
                "Juli",
                "Agustus",
                "September",
                "Oktober",
                "November",
                "Desember"
        };

        /**Format: Hari ini Rabu, 28 Oktober 1988
        Format: Kemarin Rabu, 28 Oktober 1988
        Format: Minggu lalu Rabu, 28 Oktober 1988*/
        //hasil = hari+" "+nama_bulan[bulan]+" "+tahun;

        //int day = now.get(Calendar.DAY_OF_WEEK);

        hasil = timeago + nama_hari[day_of_week-1]+", " +hari+ " " +nama_bulan[bulan-1]+" "+tahun;

        return hasil;
    }

    public String to2(String tanggal){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try{
            date = s.parse(tanggal);
        }catch (Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String format = formatter.format(date);

        return format;

    }

    public String toConvert(String tanggal){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-d");
        Date date = null;
        try{
            date = s.parse(tanggal);
        }catch (Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(date);

        return String.valueOf(format);

    }

    public String now(){
        Date date = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-MM-dd");

        return to( simpleDateformat.format(date), null );
    }

}
