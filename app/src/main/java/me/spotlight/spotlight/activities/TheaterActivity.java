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
import android.widget.ImageView;
import android.widget.Toast;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.jcodec.api.android.SequenceEncoder;

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
import io.vov.vitamio.widget.VideoView;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.utils.ParseConstants;
import me.spotlight.spotlight.utils.PathUtils;

/**
 * Created by Anatol on 7/26/2016.
 */
public class TheaterActivity extends Activity {


    private Uri uri;
    @Bind(R.id.vid)
    VideoView vid;
    @Bind(R.id.vid2)
    android.widget.VideoView vid2;
    @Bind(R.id.image)
    ImageView image;
    public static final int REQUEST_PERMISSION_CAMERA = 52;

    Movie movie, movie2, movie3;
    Movie result;

    String reelPath;
    String musicPath;

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
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))

        getPermission();

//        reelPath = getIntent().getExtras().getString("reelPath");
//        musicPath = getIntent().getExtras().getString("musicPath");

        vid.setVideoPath("/storage/sdcard/1470151215059in.mp4");
        vid.start();
    }






//    @OnClick(R.id.start)
//    public void go() {
//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        startActivityForResult(intent, 11);
//    }
//
//    @OnClick(R.id.start)
//    public void go(Bitmap image) {
//        File file = new File(Environment.getExternalStorageDirectory() + "/err.mp4");
//        List<Bitmap> bitmaps = new ArrayList<>();
//
//        for (int i = 0; i < 120; i++) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
//            bitmaps.add(bitmap);
//        }
//
//        try {
//            SequenceEncoder sequenceEncoder = new SequenceEncoder(file);
//
//            for (int i = 0; i < 120; i++) {
//                sequenceEncoder.encodeImage(bitmaps.get(i));
//            }
//
//            sequenceEncoder.finish();
//
//        } catch (IOException e) {
//            Log.d("jcodec", "jcodec IOExceptio" + e.getMessage());
//        }
//    }

    @OnClick(R.id.start)
    public void go() {
        //
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 || requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                Uri vidUri = data.getData();
                String vidPath = PathUtils.getPath(this, vidUri);
                Log.d("extracom", vidUri.toString());
                Log.d("extracom", vidPath);


            }
        }
    }










    private void thumbFromUri(String path) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        image.setImageBitmap(bitmap);
    }
    private void addAudio(String vidPath) {

        try {
            movie = MovieCreator.build(vidPath);
            List<Track> vidTracks = new ArrayList<>();
            List<Track> audioTracks = new ArrayList<>();

            for (Track track : movie.getTracks()) {
                if (track.getHandler().equals("vide")) {
                    vidTracks.add(track);
                }
            }
            result = new Movie();

            String mp3File = null;
            InputStream inputStream = getResources().openRawResource(R.raw.ready_go);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int size = 0;
            byte[] buffer = new byte[1024];
            while ((size = inputStream.read(buffer, 0, 1024)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, size);
            }
            inputStream.close();
            buffer = byteArrayOutputStream.toByteArray();
            FileOutputStream fileOutputStream = new FileOutputStream(new File("/storage/emulated/0/DCIM/Camera/ready_go2.mp3"));
            fileOutputStream.write(buffer);
            fileOutputStream.close();



            mp3File = "/storage/emulated/0/DCIM/Camera/ready_go2.mp3";
            MP3TrackImpl mp3Track = new MP3TrackImpl(new FileDataSourceImpl(mp3File));
            Log.d("extracom", "mp3 track created");
            audioTracks.add(mp3Track);

            Log.d("extracom", "adding audioTrack");
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            Log.d("extracom", "adding videoTrack");
            result.addTrack(new AppendTrack(vidTracks.toArray(new Track[vidTracks.size()])));

            Container out = new DefaultMp4Builder().build(result);
            File file = new File("/storage/emulated/0/DCIM/Camera/addedSound001.mp4");
            FileChannel fileChannel = new FileOutputStream(file).getChannel();
            out.writeContainer(fileChannel);
            fileChannel.close();


        } catch (IOException e) {
            Log.d("extracom", "mp4parse IOException");
        }


        vid.setVideoPath("/storage/emulated/0/DCIM/Camera/addedSound001.mp4");
        vid.start();
    }
    private void pathToBytes(String vidPath) {

        byte[] bytes;
        ByteArrayOutputStream byteArrayOutputStream;

        try {

            File file = new File(vidPath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            for (int redNum; (redNum = fileInputStream.read(buffer)) != -1;) {
                byteArrayOutputStream.write(buffer, 0, redNum);
            }
            bytes = byteArrayOutputStream.toByteArray();

            Log.d("extracom", String.valueOf(bytes.length));
            uploadVideo(bytes);

        } catch (IOException e) {
            Log.d("extracom", "ioexception");
        }

    }
    private void uploadVideo(byte[] bytes) {
        try {
            final ParseFile parseFile = new ParseFile("video.mov", bytes);
            final ParseObject parseObject = new ParseObject(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
            parseObject.put("isVideo", true);
            parseObject.put("title", "random title");
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
                        Log.d("extracom", "mediaFile saved!");
                        parseObject.put(ParseConstants.FIELD_OBJECT_MEDIA_FILE, parseFile);
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    Log.d("extracom", "SpotlightMedia saved");
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("extracom", e.getMessage());
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Log.d("extracom", "fileSave  " + e.getMessage());
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.d("extracom", "ParseFile must be less than 10,485,760 bytes");
        }

    }
    private void movieCreator(String vidPath) {

        try {

            movie = MovieCreator.build(vidPath);
            Log.d("extracom", "***");
            // CAREFUL: hardcoded test paths - deleted these already
            movie2 = MovieCreator.build("/storage/emulated/0/DCIM/Camera/VID_20160729_132746.mp4");
            Log.d("extracom", "***");
            // CAREFUL: hardcoded test paths - deleted these already
            movie3 = MovieCreator.build("/storage/emulated/0/DCIM/Camera/VID_20160729_122525.mp4");
            Log.d("extracom", "***");
            result = new Movie();

            List<Track> tracks = new ArrayList<>();

            for (Track track : movie.getTracks()) {
                Log.d("extracom", "adding track");
                if (track.getHandler().equals("vide")) {
                    tracks.add(track);
                    Log.d("extracom", "adding video track");
                }
            }
            for (Track track : movie2.getTracks()) {
                Log.d("extracom", "adding track");
                if (track.getHandler().equals("vide")) {
                    tracks.add(track);
                    Log.d("extracom", "adding video track");
                }
            }
            for (Track track : movie3.getTracks()) {
                Log.d("extracom", "adding track");
                if (track.getHandler().equals("vide")) {
                    tracks.add(track);
                    Log.d("extracom", "adding video track");
                }
            }



            result.addTrack(new AppendTrack(tracks.toArray(new Track[tracks.size()])));

            Container mp4file = new DefaultMp4Builder().build(result);
            File file = new File("/storage/emulated/0/DCIM/Camera/trial666.mp4");
            FileChannel fileChannel = new FileOutputStream(file).getChannel();
            mp4file.writeContainer(fileChannel);
            fileChannel.close();



        } catch (IOException e) {}

        vid.setVideoPath("/storage/emulated/0/DCIM/Camera/trial666.mp4");
        vid.start();

        vid2.setVideoPath(vidPath);
        vid2.start();
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
