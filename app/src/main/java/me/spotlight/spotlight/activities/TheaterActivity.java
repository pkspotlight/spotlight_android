package me.spotlight.spotlight.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

//import com.coremedia.iso.IsoFile;
//import com.coremedia.iso.boxes.Container;
//import com.googlecode.mp4parser.FileDataSourceImpl;
//import com.googlecode.mp4parser.authoring.Movie;
//import com.googlecode.mp4parser.authoring.Track;
//import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
//import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
//import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
//import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
//import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

//import org.jcodec.api.android.SequenceEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.utils.ParseConstants;
import me.spotlight.spotlight.utils.PathUtils;

/**
 * Created by Anatol on 7/26/2016.
 */
public class TheaterActivity extends Activity {

    public static final String TAG = "TheaterActivity";
    public static final int REQUEST_PERMISSION_CAMERA = 52;
    @Bind(R.id.web_view)
    WebView webView;

    /*
        Intent manufacturing
     */
    public static Intent getStartIntent(Context context, String reelPath, String musicPath) {
        Intent intent = new Intent(context, TheaterActivity.class);
        intent.putExtra("reel", reelPath);
        intent.putExtra("music", musicPath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);
        ButterKnife.bind(this);
//        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))

        webView.getSettings().setBuiltInZoomControls(true);


        getPermission();
    }




    private void getPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if (PackageManager.PERMISSION_GRANTED == permissionCheck) {
            return;
        } else if (PackageManager.PERMISSION_DENIED == permissionCheck){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "All good!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Sorry, can't make videos then!", Toast.LENGTH_LONG).show();
                    ///
                }
                return;
            }
        }
    }
}
