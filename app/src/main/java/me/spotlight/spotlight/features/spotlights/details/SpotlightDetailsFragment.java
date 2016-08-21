package me.spotlight.spotlight.features.spotlights.details;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.activities.TheaterActivity;
import me.spotlight.spotlight.features.spotlights.SpotlightsFragment;
import me.spotlight.spotlight.features.spotlights.add.AddSpotlightFragment;
import me.spotlight.spotlight.features.spotlights.add.TitleDialog;
import me.spotlight.spotlight.models.Spotlight;
import me.spotlight.spotlight.models.SpotlightMedia;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ParseConstants;
import me.spotlight.spotlight.utils.PathUtils;

/**
 * Created by Anatol on 7/22/2016.
 */
public class SpotlightDetailsFragment extends Fragment implements SpotPreviewAdapter.ActionListener,
                                                                                TitleDialog.ActionListener {

    public static final String TAG = "SpotDetailFragment";
    @Bind(R.id.recycler_view_details)
    RecyclerView details;
    SpotPreviewAdapter spotPreviewAdapter;
    List<String> urls = new ArrayList<>();
    Transformation round;
    @Bind(R.id.spotlight_avatar)
    ImageView spotlightAvatar;
    @Bind(R.id.spot_details_date)
    TextView spotlightDate;
    @Bind(R.id.spot_details_info)
    TextView spotlightInfo;
    @Bind(R.id.spot_details_name)
    TextView spotlightName;
    List<String> ids = new ArrayList<>();
    public static List<String> movs = new ArrayList<>();
    private String title = "";
    private ParseObject currentSpotlight;
    Uri mImageCaptureUri;

    /*
        Manufacturing singleton
    */
    public static SpotlightDetailsFragment newInstance(Bundle args) {
        SpotlightDetailsFragment spotlightDetailsFragment = new SpotlightDetailsFragment();
        spotlightDetailsFragment.setArguments(args);
        return spotlightDetailsFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        findCurrentSpotlight();
        Log.d(TAG, "onStart");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_spotlight_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        round = new RoundedTransformationBuilder().oval(true).build();
        details.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        spotPreviewAdapter = new SpotPreviewAdapter(getActivity(), urls, this);
        details.setAdapter(spotPreviewAdapter);

        loadSpotDetails();
    }

    @Override
    public void onResume() {
        super.onResume();

        initTeamAvatar();
        populateInfo();
    }

    @Override
    public void onShow(String url) {
        try {
            startActivity(TheaterActivity.getStartIntent(getContext(), url));
        } catch (Exception e) {
            Log.d(TAG, "******************************************");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean ret = true;
        if (menuItem.getItemId() == android.R.id.home) {
            ret = false;
        }
        if (menuItem.getItemId() == R.id.action_add) {
            addMedia();
            ret = true;
        }
        return ret;
    }

    private void addMedia() {

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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Pick a title");
        final EditText editText = new EditText(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(params);
        alertDialogBuilder.setView(editText);
        alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().equals("")) {
                    title = editText.getText().toString();
                    dialogInterface.dismiss();
                    // proceed with actresult
                    onActRes(requestCode, resultCode, data);
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void onActRes(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap pickedPicture;
            switch (requestCode) {
                case Constants.PICTURE_CAMERA_REQUEST:
                    // took photo
                    try {
                        pickedPicture = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        pickedPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        /// upload to parse
                        uploadPhoto(bytes, bytes);
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
                        /// upload to parse
                        uploadPhoto(bytes, bytes);
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
                        /// upload to parse
                        uploadVideo(bytes, thumBytes);
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
                        /// upload to parse
                        uploadVideo(bytes1, thumBytes);
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

    private void findCurrentSpotlight() {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT);
        query.whereEqualTo("objectId", getArguments().getString("objectId"));
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

    @Override
    public void onTitlePicked(String string) {
        title = string;
    }

    private void uploadPhoto(byte[] bytes, byte[] thumBytes) {
        if (null == title) title = String.valueOf(System.currentTimeMillis());
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
                                                Log.d(TAG, "SpotlightMedia saved");
                                                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
                                                preloadSpotlightMedia();
                                            } else {
                                                Log.d(TAG, e.getMessage());
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
        if (null == title) title = String.valueOf(System.currentTimeMillis());
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
                        Log.d(TAG, "mediaFile saved!");

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
                                                Log.d(TAG, "SpotlightMedia saved");
                                                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
                                                preloadSpotlightMedia();
                                            } else {
                                                Log.d(TAG, e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    } else {
                        Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.d("extracom", "ParseFile must be less than 10,485,760 bytes");
        } catch (ParseException e1) {
            Log.d("extracom", e1.getMessage());
        }
    }


    private void preloadSpotlightMedia() {
        if (!SpotlightsFragment.spotlightMedias.isEmpty())
            SpotlightsFragment.spotlightMedias.clear();
        ParseQuery<ParseObject> mediaQ = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
        mediaQ.setLimit(1000);
        mediaQ.orderByDescending("createdAt");
        mediaQ.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        Log.d(TAG, String.valueOf(objects.size()));
                        for (ParseObject spotMedia : objects) {

                            try {
                                spotMedia.fetchIfNeeded();

                                SpotlightMedia spotlightMedia = new SpotlightMedia();
                                spotlightMedia.setObjectId(spotMedia.getObjectId());

                                if (null != spotMedia.getParseFile("mediaFile")) {
                                    spotlightMedia.setFileUrl(spotMedia.getParseFile("mediaFile").getUrl());
                                }
                                if (null != spotMedia.getParseFile("thumbnailImageFile")) {
                                    if (null != spotMedia.getParseFile("thumbnailImageFile").getUrl()) {
                                        spotlightMedia.setThumbnailUrl(spotMedia.getParseFile("thumbnailImageFile").getUrl());
                                    }
                                }
                                if (null != spotMedia.getParseObject("parent")) {
                                    spotlightMedia.setParentId(spotMedia.getParseObject("parent").getObjectId());
                                } else {
                                    spotlightMedia.setParentId("null");
                                }

                                SpotlightsFragment.spotlightMedias.add(spotlightMedia);

                            } catch (Exception e1) {
                                Log.d(TAG, "exception:" + " preloadSpotlightMedia");
                            }
                        }

                        reloadFragment();
                    }
                }
            }
        });
    }

    // **************************************

    @OnClick(R.id.view)
    public void view(View view) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.view_dialog))
                .setItems(getResources().getTextArray(R.array.spot_view), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Toast.makeText(getContext(), "Cool Kids", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(getContext(), "Disney Funk", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(getContext(), "Every single night", Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                Toast.makeText(getContext(), "Ready 2 Go", Toast.LENGTH_LONG).show();
                                break;
                            case 4:
                                Toast.makeText(getContext(), "No music", Toast.LENGTH_LONG).show();
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
        view(view);
//        for (SpotlightMedia spotlightMedia : SpotlightsFragment.spotlightMedias) {
//            if (spotlightMedia.getParentId().equals(getArguments().getString("objectId"))) {
//                ids.add(spotlightMedia.getObjectId());
//            }
//        }
//
//
//        for (String id : ids) {
//            Log.d("downloading", id + " bytes");
//            ParseQuery<ParseObject> mediaQuery = new ParseQuery<>(ParseConstants.OBJECT_SPOTLIGHT_MEDIA);
//            mediaQuery.whereEqualTo("objectId", id);
//            mediaQuery.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> objects, ParseException e) {
//                    try {
//                        ParseObject media = objects.get(0).fetchIfNeeded();
//                        ParseFile parseFile = media.getParseFile("mediaFile");
//                        parseFile.getFile();
//                        byte[] bytes = parseFile.getData();
//                        ByteArrayOutputStream bbw = new ByteArrayOutputStream(bytes.length);
//                        bbw.write(bytes);
//
//                        String fileExtension = media.getBoolean("isVideo") ? ".mp4" : ".png";
//                        String fileName = "in" + fileExtension;
//                        String incoming = writeFile(bbw, fileName);
//                        Log.d("downloading", incoming);
//
//                    } catch (ParseException ee) {
//                        //
//                    } catch (IOException eee) {}
//                }
//            });
//        }
    }

    private void loadSpotDetails() {
        if (!urls.isEmpty())
            urls.clear();
        if (!movs.isEmpty())
            movs.clear();
        for (SpotlightMedia spotlightMedia : SpotlightsFragment.spotlightMedias) {
            if (null != spotlightMedia.getParentId()) {
                if (spotlightMedia.getParentId().equals(getArguments().getString("objectId"))) {
                    urls.add(spotlightMedia.getThumbnailUrl());
                    movs.add(spotlightMedia.getFileUrl());
                }
            }
        }
        spotPreviewAdapter.notifyDataSetChanged();
    }

    private void initTeamAvatar() {
        try {
            if (null != getArguments().getString("teamAvatar")) {
                if (!"".equals(getArguments().getString("teamAvatar"))) {
                    Glide.with(getActivity())
                            .load(getArguments().getString("teamAvatar"))
                            .into(spotlightAvatar);
                } else {
                    Glide.with(getActivity())
                            .load(R.drawable.unknown_user)
                            .into(spotlightAvatar);
                }
            } else {
                Glide.with(getActivity())
                        .load(R.drawable.unknown_user)
                        .into(spotlightAvatar);
            }
        } catch (Exception e) {}
    }

    private void populateInfo() {
        try {
            spotlightInfo.setText("Grade " + getArguments().getString("teamGrade") + " "
                    + getArguments().getString("teamSport"));
            spotlightName.setText(getArguments().getString("teamName"));
            spotlightDate.setText(getArguments().getString("date"));
        } catch (Exception e) {
            Log.d(TAG, "exception");
        }
    }

    // *****************************************************
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

    private void shareIntent() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Spotlight");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing spotlight!");
        try {
            getActivity().startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
        } catch (Exception e) {}
    }

    private void reloadFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
