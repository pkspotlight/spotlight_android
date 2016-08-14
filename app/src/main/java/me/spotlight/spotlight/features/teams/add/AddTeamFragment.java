package me.spotlight.spotlight.features.teams.add;

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
import android.view.inputmethod.InputMethodManager;
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
 * Created by Anatol on 7/10/2016.
 */
public class AddTeamFragment extends Fragment {

    byte[] pickedBitmap = null;
    Transformation round;
    Uri mImageCaptureUri;
    @Bind(R.id.team_add_avatar)
    ImageView teamAddAvatar;
    @Bind(R.id.team_add_name)
    EditText teamAddName;
    @Bind(R.id.team_add_sport)
    EditText teamAddSport;
    @Bind(R.id.team_add_grade)
    EditText teamAddGrade;
    @Bind(R.id.team_add_season)
    EditText teamAddSeason;
    @Bind(R.id.team_add_town)
    EditText teamAddTown;
    @Bind(R.id.team_add_coach)
    EditText teamAddCoach;
    @Bind(R.id.team_add_year)
    EditText teamAddYear;

    /*
        Manufacturing singleton
    */
    public static AddTeamFragment newInstance() {
        Bundle args = new Bundle();
        AddTeamFragment addTeamFragment = new AddTeamFragment();
        addTeamFragment.setArguments(args);
        return addTeamFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_add_teams, container, false);
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
        getActivity().setTitle(getString(R.string.add_teams));
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

    @OnClick(R.id.team_add_avatar)
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
                .into(teamAddAvatar);
    }

    private void initAvatar(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);

        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

        teamAddAvatar.setImageBitmap(circleBitmap);
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

    @OnClick(R.id.team_add_submit)
    public void submitTeam() {
        if (validate()) {
            final ParseObject mTeam = new ParseObject(ParseConstants.OBJECT_TEAM);
            mTeam.put("teamName", teamAddName.getText().toString());
            mTeam.put("coach", teamAddCoach.getText().toString());
            mTeam.put("grade", teamAddGrade.getText().toString());
            mTeam.put("season", teamAddSeason.getText().toString());
            mTeam.put("sport", teamAddSport.getText().toString());
            mTeam.put("year", teamAddYear.getText().toString());
            mTeam.put("town", teamAddTown.getText().toString());

            if (null != pickedBitmap) {
                // get bytes here
                ParseFile teamLogo = new ParseFile("image.png", pickedBitmap);
                ParseObject teamLogoMedia = new ParseObject(ParseConstants.OBJECT_TEAM_LOGO_MEDIA);
                teamLogoMedia.put(ParseConstants.FIELD_OBJECT_MEDIA_FILE, teamLogo);
                mTeam.put("teamLogoMedia", teamLogoMedia);
            }

            mTeam.getRelation("moderators").add(ParseUser.getCurrentUser());




            mTeam.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_LONG).show();
                        updateRelation(mTeam);
                    }
                }
            });
        }
    }

    private void updateRelation(final ParseObject mTeam) {
        final ParseRelation<ParseObject> myteamsRelation = ParseUser.getCurrentUser().getRelation("teams");
        myteamsRelation.add(mTeam);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (null == e) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private boolean validate() {
        boolean valid = true;
        if (TextUtils.isEmpty(teamAddCoach.getText().toString())) {
            valid = false;
            teamAddCoach.setError("Please enter a coach!");
        }
        if (TextUtils.isEmpty(teamAddName.getText().toString())) {
            valid = false;
            teamAddName.setError("Please enter a name!");
        }
        if (TextUtils.isEmpty(teamAddGrade.getText().toString())) {
            valid = false;
            teamAddGrade.setError("Please enter a grade!");
        }
        if (TextUtils.isEmpty(teamAddSeason.getText().toString())) {
            valid = false;
            teamAddSeason.setError("Please enter a season!");
        }
        if (TextUtils.isEmpty(teamAddSport.getText().toString())) {
            valid = false;
            teamAddSport.setError("Please enter a sport!");
        }
        if (TextUtils.isEmpty(teamAddYear.getText().toString())) {
            valid = false;
            teamAddYear.setError("Please enter a year!");
        }
        if (TextUtils.isEmpty(teamAddTown.getText().toString())) {
            valid = false;
            teamAddTown.setError("Please enter a town!");
        }
        return valid;
    }
}
