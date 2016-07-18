package me.spotlight.spotlight.features.profile;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.activities.LoginActivity;
import me.spotlight.spotlight.base.BaseFragment;
import me.spotlight.spotlight.models.User;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class ProfileFragment extends Fragment {

    String profilePicUrl;
    ParseUser currentUser;
    Transformation round;
    Uri mImageCaptureUri;
    @Bind(R.id.profile_avatar)
    ImageView profileAvatar;
    @Bind(R.id.profile_name)
    TextView profileName;
    @Bind(R.id.profile_username)
    TextView profileUsername;
    @Bind(R.id.profile_first)
    TextView profileFirst;
    @Bind(R.id.profile_last)
    TextView profileLast;
    @Bind(R.id.profile_hometown)
    TextView profileHometown;
    @Bind(R.id.profile_family)
    TextView profileFamily;

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
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentUser = ParseUser.getCurrentUser();

        round = new RoundedTransformationBuilder().oval(true).build();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadAvatar();

        getPermission();

        populateFields();
    }



    private void parse() {

        Drawable drawable = getResources().getDrawable(R.drawable.test);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        ParseFile profilePic = new ParseFile("image.png", bytes);
        ParseObject parseObject = new ParseObject("ProfilePictureMedia");
        parseObject.put("mediaFile", profilePic);

        currentUser.put("profilePic", parseObject);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (null == e)
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void populateFamily() {}

    private void populateFields() {
        if (null != currentUser.getUsername()) {
            if (!TextUtils.isEmpty(currentUser.getUsername())) {
                profileUsername.setText(currentUser.getUsername());
            }
        }

        if (null != currentUser.getString("firstName") && null != currentUser.getString("lastName")) {
            if (!TextUtils.isEmpty(currentUser.getString("firstName"))
                    && !TextUtils.isEmpty(currentUser.getString("lastName"))) {
                profileName.setText(currentUser.getString("firstName") + " " + currentUser.getString("lastName"));
                profileFirst.setText(currentUser.getString("firstName"));
                profileLast.setText(currentUser.getString("lastName"));
            }
        }

        if (null != currentUser.getString("homeTown")) {
            if (!TextUtils.isEmpty(currentUser.getString("homeTown"))) {
                profileHometown.setText(currentUser.getString("homeTown"));
            }
        }
    }

    private void initAvatar(String url) {
        Picasso.with(getActivity())
                .load(url)
                .fit().centerCrop()
                .transform(round)
                .into(profileAvatar);
    }

    private void initEmptyAvatar() {
        Picasso.with(getActivity())
                .load(R.drawable.unknown_user)
                .fit().centerCrop()
                .transform(round)
                .into(profileAvatar);
    }

    private void loadAvatar() {
        ParseObject profilePic =  currentUser.getParseObject(ParseConstants.FIELD_USER_PIC);
        if (null != profilePic) {
            String profilePicId = profilePic.getObjectId();
            ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.OBJECT_PROFILE_PIC);
            query.whereEqualTo(ParseConstants.FIELD_OBJECT_ID, profilePicId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (null == e) {
                        if (!objects.isEmpty()) {
                            profilePicUrl = objects.get(0)
                                    .getParseFile(ParseConstants.FIELD_OBJECT_MEDIA_FILE).getUrl();
                            initAvatar(profilePicUrl);
                        } else {
                            initEmptyAvatar();
                        }
                    } else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            initEmptyAvatar();
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
                        takePicture();
                        break;
                    case 1:
                        selectPicture();
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
                        uploadAvatar(bytes);
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
                        uploadAvatar(bytes);
                    } catch (IOException e) {
                        Log.d("capture", e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void uploadAvatar(byte[] bytes) {
        ParseFile profilePic = new ParseFile("image.png", bytes);
        ParseObject parseObject = new ParseObject(ParseConstants.OBJECT_PROFILE_PIC);
        parseObject.put(ParseConstants.FIELD_OBJECT_MEDIA_FILE, profilePic);

        currentUser.put(ParseConstants.FIELD_USER_PIC, parseObject);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (null == e) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    loadAvatar();
                } else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadAvatar();
                }
            }
        });
    }


    @OnClick(R.id.profile_logout)
    public void logout() {
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
}
