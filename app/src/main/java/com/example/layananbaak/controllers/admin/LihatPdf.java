package com.example.layananbaak.controllers.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.layananbaak.R;
import com.example.layananbaak.utility.Fungsi;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.snackbar.Snackbar;
import com.shockwave.pdfium.PdfDocument;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LihatPdf extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnErrorListener {

    InputStream fromFile;
    PDFView pdfView;
    Integer pageNumber = 0;
    TextView title;
    Integer zoom = 80;
    boolean light = false;

    Context context;

    ProgressDialog progressDialog;
    LinearLayout empty_view;
    String filelink, filejudul;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        context = getApplicationContext();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        progressDialog.setCancelable(false);

        Intent i = getIntent();
        filelink = i.getStringExtra("filelink");
        filejudul = i.getStringExtra("filejudul");


        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        pdfView = findViewById(R.id.pdfView);
        empty_view = findViewById(R.id.empty_view);


        new RetrivePDFStream().execute(filelink);

    }


    @Override
    public void onError(Throwable t) {
        t.printStackTrace();

    }


    class RetrivePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL uri = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
            if(inputStream != null){
                fromFile = inputStream;
                openPDF(inputStream);
            }else{
                empty_view.setVisibility(View.GONE);
                progressDialog.dismiss();

            }
        }
    }


    private void openPDF(InputStream fromFile) {

        pdfView.useBestQuality(true);
        pdfView.fromStream(fromFile)
                //.pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(pageNumber)
                // allows to draw something on the current page, usually visible in the middle of the screen
                //.onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                //.onDrawAll(onDrawListener)
                .onLoad(this) // called after document is loaded and starts to be rendered
                .onPageChange(this)
                //.onPageScroll(onPageScrollListener)
                .onError(this)
                //.onPageError(onPageErrorListener)
                //.onRender(onRenderListener) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                //.onTap(onTapListener)
                //.onLongPress(onLongPressListener)
                .enableAnnotationRendering(true) // render annotations (such as comments, colors or forms)
                //.password(null)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                //.spacing(0)
                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
                //.linkHandler(DefaultLinkHandler)
                .pageFitPolicy(FitPolicy.WIDTH)
                //.pageSnap(true) // snap pages to screen boundaries
                //.pageFling(true) // make a fling change only a single page like ViewPager
                //.nightMode(false) // toggle night mode
                .load();

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }
    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        /**Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());*/

        //pdfView.fitToWidth(0);   // Goto to specific page
        //pdfView.setMinZoom(60);
        //pdfView.setMidZoom(80);
        //pdfView.setMaxZoom(100);
        //progressBar.setVisibility(View.GONE);
        progressDialog.dismiss();
        empty_view.setVisibility(View.GONE);
    }



    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if(item.getItemId() == R.id.navigation_dlfile){
            final File rootPath = new File(Environment.getExternalStorageDirectory(), "BAAK");

            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }
            final File localFile = new File(rootPath, filejudul +".pdf");

            OutputStream out = null;

            try {
                out = new FileOutputStream(localFile);
                byte[] buf = new byte[1024];
                int len;
                while((len= fromFile.read(buf))>0){
                    out.write(buf,0,len);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                // Ensure that the InputStreams are closed even if there's an exception.
                try {
                    if ( out != null ) {
                        out.close();
                    }

                    // If you want to close the "in" InputStream yourself then remove this
                    // from here but ensure that you close it yourself eventually.
                    fromFile.close();


                    Snackbar.make(coordinatorLayout, "Export Data "+localFile.toString()+" success!", Snackbar.LENGTH_LONG)
                            .setAction("Buka File", v -> new Fungsi().openFile(context, localFile))
                            .show();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
