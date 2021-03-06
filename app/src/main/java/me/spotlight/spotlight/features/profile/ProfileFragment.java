package me.spotlight.spotlight.features.profile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.activities.LoginActivity;
import me.spotlight.spotlight.features.friends.add.AddFamilyFragment;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.DialogUtils;
import me.spotlight.spotlight.utils.FragmentUtils;
import me.spotlight.spotlight.utils.ImageUtils;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class ProfileFragment extends Fragment implements ProfileContract {

    public static final String TAG = "ProfileFragment";
    private ProfilePresenter presenter;

    Uri mImageCaptureUri;
    @Bind(R.id.profile_avatar) CircleImageView profileAvatar;
    @Bind(R.id.profile_name) TextView profileName;
    @Bind(R.id.profile_username) TextView profileUsername;
    @Bind(R.id.profile_first) EditText profileFirst;
    @Bind(R.id.profile_last) EditText profileLast;
    @Bind(R.id.profile_hometown) EditText profileHometown;
    @Bind(R.id.profile_family) TextView profileFamily;
    @Bind(R.id.progress) ProgressBar progressBar;

    /*
        Manufacturing singleton
     */
    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter = new ProfilePresenter(this);
        Log.d(TAG, "onStart");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = layoutInflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        getActivity().setTitle("Profile");
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.btn_tab_profile).getWindowToken(), 0);

        presenter.fetchAvatar();

        getPermission();

        populateFields();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        menuInflater.inflate(R.menu.profile, menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Log.d(TAG, "onOptionsItemSelected");
        boolean ret = true;
        if (menuItem.getItemId() == android.R.id.home) {
            ret = false;
        }
        if (menuItem.getItemId() == R.id.action_save) {
            menuItem.setVisible(false);
            update();
            ret = true;
        }
        return ret;
    }

    private void update() {
        ParseUser.getCurrentUser().put("firstName", profileFirst.getText().toString());
        ParseUser.getCurrentUser().put("lastName", profileLast.getText().toString());
        ParseUser.getCurrentUser().put("homeTown", profileHometown.getText().toString());

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (null == e) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.btn_tab_profile).getWindowToken(), 0);
                    refresh();
                } else {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void refresh() {
        Log.d(TAG, "refreshing fragment");
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void loadFamily() {
        final StringBuilder stringBuilder = new StringBuilder();
        final ParseRelation<ParseObject> familyRelation = ParseUser.getCurrentUser().getRelation("children");
        ParseQuery<ParseObject> familyQuery = familyRelation.getQuery();
        familyQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {
                            if (null != parseObject.getString("firstName")) {
                                stringBuilder.append(parseObject.getString("firstName"));
                                stringBuilder.append(" ");
                            }
                            if (null != parseObject.getString("lastName")) {
                                stringBuilder.append(parseObject.getString("lastName"));
                                stringBuilder.append(", ");
                            }
                        }

                        profileFamily.setText(stringBuilder.toString());
                    }
                } else {
                    //
                }
            }
        });
    }

    private void populateFields() {
        if (null != ParseUser.getCurrentUser().getUsername()) {
            if (!TextUtils.isEmpty(ParseUser.getCurrentUser().getUsername())) {
                profileUsername.setText(ParseUser.getCurrentUser().getUsername());
            }
        }

        if (null != ParseUser.getCurrentUser().getString("firstName") && null != ParseUser.getCurrentUser().getString("lastName")) {
            if (!TextUtils.isEmpty(ParseUser.getCurrentUser().getString("firstName"))
                    && !TextUtils.isEmpty(ParseUser.getCurrentUser().getString("lastName"))) {
                profileName.setText(ParseUser.getCurrentUser().getString("firstName") + " " + ParseUser.getCurrentUser().getString("lastName"));
                profileFirst.setText(ParseUser.getCurrentUser().getString("firstName"));
                profileLast.setText(ParseUser.getCurrentUser().getString("lastName"));
            }
        }

        if (null != ParseUser.getCurrentUser().getString("homeTown")) {
            if (!TextUtils.isEmpty(ParseUser.getCurrentUser().getString("homeTown"))) {
                profileHometown.setText(ParseUser.getCurrentUser().getString("homeTown"));
            }
        }

        loadFamily();
    }


    public void onAvatarUpdated(boolean ok) {
        if (ok) presenter.fetchAvatar();
        else DialogUtils.showAlertDialog(getContext(), "There was an error. Please try again.");
    }

    public void onAvatarFetched(String url) {
        ImageUtils.into(getContext(), profileAvatar, url);
    }

    public void onProfileUpdated(boolean ok) {
        //
    }

    public void onProfileFetched(boolean ok) {
        //
    }

    public void showProgress(boolean show) {
        try {
            if (show) {
                Log.d(TAG, "showing progress");
                progressBar.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "dismissing progress");
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            if (null != progressBar) progressBar.setVisibility(View.GONE);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }


    private void getPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (PackageManager.PERMISSION_GRANTED == permissionCheck) {
            return;
        } else if (PackageManager.PERMISSION_DENIED == permissionCheck){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.CAMERA}, Constants.REQUEST_PERMISSION_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                } else {
                    Toast.makeText(getContext(), "Sorry, can't take photos then!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @OnClick(R.id.profile_avatar)
    public void onAvatarCLick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String[] items = new String[]{getString(R.string.picture_camera), getString(R.string.picture_gallery)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        try {
                            takePicture();
                        } catch (Exception e) { }
                        break;
                    case 1:
                        try {
                            selectPicture();
                        } catch (Exception e) { }
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void takePicture() {
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

    private void selectPicture() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap pickedPicture;
            switch (requestCode) {
                case Constants.PICTURE_CAMERA_REQUEST:
                    try {
                        pickedPicture = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        pickedPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        presenter.updateAvatar(bytes);
                    } catch (IOException e) {
                        Log.d("capture", e.getMessage());
                    }
                    break;
                case Constants.PICTURE_GALLERY_REQUEST:
                    mImageCaptureUri = data.getData();
                    try {
                        pickedPicture = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        pickedPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        presenter.updateAvatar(bytes);
                    } catch (IOException e) {
                        Log.d("capture", e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick(R.id.profile_logout)
    public void logout() {
        LoginManager.getInstance().logOut();
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (null == e) {
                    startActivity(LoginActivity.getStartIntent(getActivity().getApplicationContext()));
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.profile_send_feedback)
    public void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"ryan@spotlight.me"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Spotlight user feedback");
        startActivity(Intent.createChooser(intent, "Send feedback"));
    }

    @OnClick(R.id.profile_family)
    public void addFamily() {
        FragmentUtils.addFragment(getActivity(), R.id.content, this, AddFamilyFragment.newInstance(), true);
    }
}
