package me.spotlight.spotlight.features.spotlights.add;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.jcodec.api.android.FrameGrab;
import org.jcodec.api.android.SequenceEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.activities.TheaterActivity;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.DialogUtils;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;
import me.spotlight.spotlight.utils.PathUtils;

/**
 * Created by Anatol on 7/30/2016.
 */
public class NewSpotlightFragment extends Fragment implements TitleDialog.ActionListener {

    @Bind(R.id.recycler_view_create)
    RecyclerView create;
    SpotCreateAdapter spotCreateAdapter;
    List<Bitmap> thumbnails = new ArrayList<>();
    Transformation round;
    @Bind(R.id.spotlight_avatar)
    ImageView spotlightAvatar;
    @Bind(R.id.spot_details_date)
    TextView spotlightDate;
    @Bind(R.id.spot_details_info)
    TextView spotlightInfo;
    @Bind(R.id.spot_details_name)
    TextView spotlightName;
    Uri mImageCaptureUri;
    ParseObject currentSpotlight;
    String spotMediaTitle;
    String title;
    String musicPath = "";
    List<String> spotFiles = new ArrayList<>();

    /*
        Manufacturing singleton
     */
    public static NewSpotlightFragment newInstance(Bundle bundle) {
        NewSpotlightFragment newSpotlightFragment = new NewSpotlightFragment();
        newSpotlightFragment.setArguments(bundle);
        return newSpotlightFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_new_spotlight, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        round = new RoundedTransformationBuilder().oval(true).build();
        create.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        spotCreateAdapter = new SpotCreateAdapter(getActivity(), thumbnails);
        create.setAdapter(spotCreateAdapter);
        findCurrentSpotlight();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getArguments().getString("teamName"));
        initTeamAvatar();
        populateInfo();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
//        final MenuItem item = menu.findItem(R.id.action_add);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean ret = true;
        if (menuItem.getItemId() == android.R.id.home) {
            ret = false;
        }
        if (menuItem.getItemId() == R.id.action_add) {
//            menuItem.setVisible(false);
//            FragmentUtils.changeFragment(getActivity(), R.id.content, AddSpotlightFragment.newInstance(), true);
//            FragmentUtils.addFragment(getActivity(), R.id.content, this, AddSpotlightFragment.newInstance(), true);
            addMedia();
            ret = true;
        }
        return ret;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap pickedPicture;
            Log.d("reel", Arrays.toString(spotFiles.toArray()));
//            pickTitle();
            switch (requestCode) {
                case Constants.PICTURE_CAMERA_REQUEST:
                    // took photo
                    try {
                        pickedPicture = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        pickedPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        /// add bitmap to grid
                        updateThumbnails(pickedPicture);
                        /// upload to parse
                        uploadPhoto(bytes, bytes);
                        /// TODO: write to internal file so that theater activity can access it
                        String out = writeFile(byteArrayOutputStream, "spot_img.png");
                        spotFiles.add(out);
                    } catch (IOException e) {
                        Log.d("capture", e.getMessage());
                    }
                    break;
                case Constants.PICTURE_GALLERY_REQUEST:
                    // picked photo
                    mImageCaptureUri = data.getData();
                    try {
                        pickedPicture = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        pickedPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        /// add bitmap to grid
                        updateThumbnails(pickedPicture);
                        /// upload to parse
                        uploadPhoto(bytes, bytes);
                        /// TODO: write to internal file so that theater activity can access it
                        String out = writeFile(byteArrayOutputStream, "spot_img.png");
                        spotFiles.add(out);
                    } catch (IOException e) {
                        Log.d("capture", e.getMessage());
                    }
                    break;
                case Constants.VIDEO_CAMERA_REQUEST:
                    // took video
                    Uri videoCaptureUri = data.getData();
                    String vidPath = PathUtils.getPath(getActivity(), videoCaptureUri);

                    byte[] bytes;
                    ByteArrayOutputStream byteArrayOutputStream;
                    ByteArrayOutputStream byteArrayOutputStream1;

                    try {

                        File file = new File(vidPath);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];

                        for (int redNum; (redNum = fileInputStream.read(buffer)) != -1;) {
                            byteArrayOutputStream.write(buffer, 0, redNum);
                        }
                        bytes = byteArrayOutputStream.toByteArray();

                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(vidPath, MediaStore.Images.Thumbnails.MINI_KIND);
                        byteArrayOutputStream1 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream1);
                        byte[] thumBytes = byteArrayOutputStream1.toByteArray();

                        /// add bitmap to grid
                        updateThumbnails(bitmap);
                        /// upload to parse
                        uploadVideo(bytes, thumBytes);
                        /// TODO: write to internal file so that theater activity can access it
                        String out = writeFile(byteArrayOutputStream, "spot_vid.mp4");
                        spotFiles.add(out);

                    } catch (IOException e) {
                        Log.d("extracom", "ioexception");
                    }
                    break;
                case Constants.VIDEO_GALLERY_REQUEST:
                    // picked video
                    Uri videoPickUri = data.getData();
                    String vidPathPick = PathUtils.getPath(getActivity(), videoPickUri);

                    byte[] bytes1;
                    ByteArrayOutputStream byteArrayOutputStream2;
                    ByteArrayOutputStream byteArrayOutputStream3;

                    try {

                        File file = new File(vidPathPick);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byteArrayOutputStream2 = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];

                        for (int redNum; (redNum = fileInputStream.read(buffer)) != -1;) {
                            byteArrayOutputStream2.write(buffer, 0, redNum);
                        }
                        bytes1 = byteArrayOutputStream2.toByteArray();

                        Bitmap thumNail = ThumbnailUtils.createVideoThumbnail(vidPathPick, MediaStore.Images.Thumbnails.MINI_KIND);
                        byteArrayOutputStream3 = new ByteArrayOutputStream();
                        thumNail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream3);
                        byte[] thumBytes = byteArrayOutputStream3.toByteArray();

                        /// add bitmap to grid
                        updateThumbnails(thumNail);
                        /// upload to parse
                        uploadVideo(bytes1, thumBytes);
                        /// TODO: write to internal file so that theater activity can access it
                        String out = writeFile(byteArrayOutputStream2, "spot_vid.mp4");
                        spotFiles.add(out);

                    } catch (IOException e) {
                        Log.d("extracom", "ioexception");
                    }
                    ///
                    break;
                default:
                    break;
            }
        }
    }

    private void updateThumbnails(Bitmap bitmap) {
        thumbnails.add(bitmap);
        spotCreateAdapter.notifyDataSetChanged();
    }






    @OnClick(R.id.fab_add_media)
    public void addMedia() {

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setItems(getResources().getTextArray(R.array.add_media), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                photoPick();
                                break;
                            case 1:
                                videoPick();
                                break;
                            case 2:
                                photoTake();
                                break;
                            case 3:
                                videoTake();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();

    }

    @OnClick(R.id.share)
    public void share(View view) {
        shareIntent();
    }

    @OnClick(R.id.view)
    public void spotReel(View view) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.view_dialog))
                .setItems(getResources().getTextArray(R.array.spot_view), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Toast.makeText(getContext(), "Cool Kids", Toast.LENGTH_LONG).show();
                                musicPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.cool_kids;
                                createReel(spotFiles);
                                break;
                            case 1:
                                Toast.makeText(getContext(), "Disney Funk", Toast.LENGTH_LONG).show();
                                musicPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.disney_funk;
                                createReel(spotFiles);
                                break;
                            case 2:
                                Toast.makeText(getContext(), "Every single night", Toast.LENGTH_LONG).show();
                                musicPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.every_night;
                                createReel(spotFiles);
                                break;
                            case 3:
                                Toast.makeText(getContext(), "Ready 2 Go", Toast.LENGTH_LONG).show();
                                musicPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.ready_go;
                                createReel(spotFiles);
                                break;
                            case 4:
                                Toast.makeText(getContext(), "No music", Toast.LENGTH_LONG).show();
                                createReel(spotFiles);
                                musicPath = "none";
                                break;
                            default:
                                musicPath = "none";
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void shareIntent() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Jobularity");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing spotlight!");
        getActivity().startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }

    private void initTeamAvatar() {
        if (null != getArguments().getString("teamAvatar")) {
            if (!"".equals(getArguments().getString("teamAvatar"))) {
                Picasso.with(getContext())
                        .load(getArguments().getString("teamAvatar"))
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightAvatar);
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.unknown_user)
                        .fit().centerCrop()
                        .transform(round)
                        .into(spotlightAvatar);
            }
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.unknown_user)
                    .fit().centerCrop()
                    .transform(round)
                    .into(spotlightAvatar);
        }
    }

    private void populateInfo() {
        spotlightInfo.setText("Grade " + getArguments().getString("teamGrade") + " "
                + getArguments().getString("teamSport"));
        spotlightName.setText(getArguments().getString("teamName"));
    }


    /*
        Add media intents below
     */

    private void photoTake() {
        mImageCaptureUri = Uri.fromFile(new File(
                getActivity().getExternalCacheDir(), String.valueOf(System.currentTimeMillis()
                + ".jpg")));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);

        try {
            startActivityForResult(intent, Constants.PICTURE_CAMERA_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void photoPick() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.PICTURE_GALLERY_REQUEST);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.PICTURE_GALLERY_REQUEST);
        }
    }

    private void videoTake() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, Constants.VIDEO_CAMERA_REQUEST);
    }

    private void videoPick() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, Constants.VIDEO_GALLERY_REQUEST);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            startActivityForResult(intent, Constants.VIDEO_GALLERY_REQUEST);
        }
    }

    /*
        Upload media to parse methods below
     */

    @Override
    public void onTitlePicked(String string) {
        title = string;
        Toast.makeText(getActivity(), title, Toast.LENGTH_LONG).show();
    }

    private void pickTitle() {
        TitleDialog titleDialog = TitleDialog.newInstance();
        titleDialog.setActionListener(this);
        titleDialog.show(getChildFragmentManager(), "title");
    }

    private void uploadPhoto(byte[] bytes, byte[] thumBytes) {
        title = String.valueOf(System.currentTimeMillis());
        try {
            final ParseFile parseFile = new ParseFile("image.png", bytes);
            final ParseFile parseFile1 = new ParseFile("thumb.jpg", thumBytes);
            final ParseObject spotlightMedia = new ParseObject(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
            spotlightMedia.put("isVideo", false);
            spotlightMedia.put("title", title);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
                        Log.d("extracom", "mediaFile saved!");

                        parseFile1.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    spotlightMedia.put("parent", currentSpotlight);
                                    spotlightMedia.put(ParseConstants.FIELD_OBJECT_MEDIA_FILE, parseFile);
                                    spotlightMedia.put(ParseConstants.FIELD_OBJECT_THUMB_FILE, parseFile1);
                                    spotlightMedia.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (null == e) {
                                                Log.d("extracom", "SpotlightMedia saved");
                                                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.d("extracom", e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    } else {
                        Log.d("extracom", e.getMessage());
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.d("extracom", "ParseFile must be less than 10,485,760 bytes");
        }
    }

    private void uploadVideo(byte[] bytes, byte[] thumBytes) {
        title = String.valueOf(System.currentTimeMillis());
        try {
            final ParseFile parseFile = new ParseFile("movie.mov", bytes);
            final ParseFile parseFile1 = new ParseFile("thumb.jpg", thumBytes);
            parseFile1.save();
            final ParseObject spotlightMedia = new ParseObject(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
            spotlightMedia.put("isVideo", true);
            spotlightMedia.put("title", title);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
                        Log.d("extracom", "mediaFile saved!");

                        parseFile1.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    spotlightMedia.put("parent", currentSpotlight);
                                    spotlightMedia.put(ParseConstants.FIELD_OBJECT_MEDIA_FILE, parseFile);
                                    spotlightMedia.put(ParseConstants.FIELD_OBJECT_THUMB_FILE, parseFile1);
                                    spotlightMedia.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (null == e) {
                                                Log.d("extracom", "SpotlightMedia saved");
                                                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.d("extracom", e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    } else {
                        Log.d("extracom", e.getMessage());
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.d("extracom", "ParseFile must be less than 10,485,760 bytes");
        } catch (ParseException e1) {
            Log.d("extracom", e1.getMessage());
        }
    }

    /*
        Finding which spotlight we're referring to
     */
    private void findCurrentSpotlight() {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT);
        query.whereEqualTo("title", getArguments().getString("title"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    currentSpotlight = objects.get(0);
//                    Toast.makeText(getActivity(), currentSpotlight.getObjectId(), Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String writeFile(ByteArrayOutputStream baos, String filename) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + timeStamp + filename);
            FileOutputStream fos = new FileOutputStream(file);
            baos.writeTo(fos);
        } catch (FileNotFoundException e) {
            Log.d("extracom", "file not found exception");
        } catch (IOException e1) {
            Log.d("extracom", "io exception");
        }

        return Environment.getExternalStorageDirectory() + timeStamp + filename;
    }

    /*
        Creates reel from list of saved mp4 or png files
     */

    private void createReel(List<String> files) {
        Movie reel = new Movie();
        if (!files.isEmpty()) {
            for (String file : files) {
                if (file.endsWith(".mp4")) {

                    // deal with video file
                    reel = appendVideo(reel, file);
                }

                if (file.endsWith(".png")) {

                    // deal with photo file
                    reel = appendSlideshow(reel, file);
                }
            }

            // after all this appending write the reel out to internal storage
            try {

                String timeStamp = String.valueOf(System.currentTimeMillis());
                String reelPath = Environment.getExternalStorageDirectory() + "/" + "reel" + timeStamp + ".mp4";
                Container mp4file = new DefaultMp4Builder().build(reel);
                File file = new File(reelPath);
                FileChannel fileChannel = new FileOutputStream(file).getChannel();
                mp4file.writeContainer(fileChannel);
                fileChannel.close();

                // all done - now pass the reel path to the theater activity
                startTheater(reelPath);

            } catch (IOException e) {}

        } else {
            // no data source for reel
            DialogUtils.showAlertDialog(getActivity(), "Please add media first!");
        }
    }

    private Movie appendVideo(Movie movie, String file) {
        try {
            Movie temp = MovieCreator.build(file);
            List<Track> tracks = new ArrayList<>();
            for (Track track : temp.getTracks()) {
                if (track.getHandler().equals("vide")) {
                    tracks.add(track);
                }
            }
            movie.addTrack(new AppendTrack(tracks.toArray(new Track[tracks.size()])));

        } catch (IOException e) {}
        //
        return movie;
    }

    private Movie appendSlideshow(Movie movie, String file) {
        String outputPath = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + "slide.mp4";
        File output = new File(outputPath);
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(file);
            bitmaps.add(bitmap);
        }
        try {
            SequenceEncoder sequenceEncoder = new SequenceEncoder(output);
            for (int i = 0; i < 120; i++) {
                sequenceEncoder.encodeImage(bitmaps.get(i));
            }
            sequenceEncoder.finish();

            // got the slideshow mp4 - now add it
            appendVideo(movie, outputPath);

        } catch (IOException e) {
            Log.d("jcodec", "jcodec IOExceptio" + e.getMessage());
        }


        return movie;
    }

    private void startTheater(String reelPath) {
        startActivity(TheaterActivity.getStartIntent(getActivity(), reelPath, musicPath));
    }
}
