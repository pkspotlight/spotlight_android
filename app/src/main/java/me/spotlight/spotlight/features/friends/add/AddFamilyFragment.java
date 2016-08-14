package me.spotlight.spotlight.features.friends.add;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
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
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.utils.Constants;
import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 7/11/2016.
 */
public class AddFamilyFragment extends Fragment {

    byte[] pickedBitmap = null;
    Transformation round;
    Uri mImageCaptureUri;
    @Bind(R.id.fam_add_avatar)
    ImageView famAddAvatar;
    @Bind(R.id.fam_add_first)
    EditText famAddFirst;
    @Bind(R.id.fam_add_last)
    EditText famAddLast;
    @Bind(R.id.fam_add_hometown)
    EditText famAddHometown;

    /*
        Manufacturing singleton
    */
    public static AddFamilyFragment newInstance() {
        Bundle args = new Bundle();
        AddFamilyFragment addFamilyFragment = new AddFamilyFragment();
        addFamilyFragment.setArguments(args);
        return addFamilyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_add_family, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        round = new RoundedTransformationBuilder().oval(true).build();
        initEmptyAvatar();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.add_family));
        getPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    @OnClick(R.id.fam_add_avatar)
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
        try {
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
        } catch (Exception e) {}
    }


    private void initEmptyAvatar() {
        Picasso.with(getActivity())
                .load(R.drawable.unknown_user)
                .fit().centerCrop()
                .transform(round)
                .into(famAddAvatar);
    }

    private void initAvatar(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);

        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

        famAddAvatar.setImageBitmap(circleBitmap);
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
                        pickedBitmap = byteArrayOutputStream.toByteArray();
                        initAvatar(pickedPicture);
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
                        pickedBitmap = byteArrayOutputStream.toByteArray();
                        initAvatar(pickedPicture);
                    } catch (IOException e) {
                        Log.d("capture", e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick(R.id.fam_add_submit)
    public void submitFam() {
        if (validate()) {
            final ParseObject mChild = new ParseObject(ParseConstants.OBJECT_CHILD);
            mChild.put("lastName", famAddLast.getText().toString());
            mChild.put("firstName", famAddFirst.getText().toString());
            mChild.put("homeTown", famAddHometown.getText().toString());

            if (null != pickedBitmap) {
                // get bytes here
                ParseFile childPicFile = new ParseFile("image.png", pickedBitmap);
                ParseObject childPicObject = new ParseObject("ProfilePictureMedia");
                childPicObject.put("mediaFile", childPicFile);
                mChild.put("profilePic", childPicObject);
            }


            mChild.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
//                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_LONG).show();
//                        getActivity().onBackPressed();
                        ParseRelation<ParseObject> famRel = ParseUser.getCurrentUser().getRelation("children");
                        famRel.add(mChild);
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (null == e) {
                                    getActivity().onBackPressed();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (TextUtils.isEmpty(famAddFirst.getText().toString())) {
            valid = false;
            famAddFirst.setError("Please enter a first name!");
        }
        if (TextUtils.isEmpty(famAddLast.getText().toString())) {
            valid = false;
            famAddLast.setError("Please enter a last name!");
        }
        if (TextUtils.isEmpty(famAddHometown.getText().toString())) {
            valid = false;
            famAddHometown.setError("Please enter a hometown!");
        }
        return valid;
    }
}
