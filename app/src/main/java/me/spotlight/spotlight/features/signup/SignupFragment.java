package me.spotlight.spotlight.features.signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.activities.MainActivity;

/**
 * Created by Anatol on 7/14/2016.
 */
public class SignupFragment extends Fragment {

    public static final String TAG = "SignupFragment";
    @Bind(R.id.signup_email) EditText signupEmail;
    @Bind(R.id.signup_username) EditText signupUsername;
    @Bind(R.id.signup_password) EditText signupPassword;

    /*
        Manufacturing singleton
    */
    public static SignupFragment newInstance() {
        Bundle args = new Bundle();
        SignupFragment signupFragment = new SignupFragment();
        signupFragment.setArguments(args);
        return signupFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setup(signupEmail);
        setup(signupPassword);
        setup(signupUsername);
    }

    private void setup(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >=
                            (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                        editText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @OnClick(R.id.signup_submit)
    public void signup() {
        if (validate()) {
            // sign-up
            ParseUser mUser = new ParseUser();
            mUser.setUsername(signupUsername.getText().toString());
            mUser.setPassword(signupPassword.getText().toString());
            mUser.setEmail(signupEmail.getText().toString());
            mUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
                        // success
                        Toast.makeText(getActivity(), "Logged in successfully!", Toast.LENGTH_LONG).show();
                        startActivity(MainActivity.getStartIntent(getActivity()));
                    } else {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                    }
                }
            });
        }
    }



    public boolean validate() {

        boolean valid = true;
        if (TextUtils.isEmpty(signupUsername.getText().toString())) {
            signupUsername.setError("Please enter your username!");
            valid = false;
        }
        if (TextUtils.isEmpty(signupPassword.getText().toString())) {
            signupPassword.setError("Please enter a password!");
            valid = false;
        }
        if (TextUtils.isEmpty(signupEmail.getText().toString())) {
            signupEmail.setError("Please enter an email!");
        }
        if (!signupEmail.getText().toString().contains("@")
                || !signupEmail.getText().toString().contains(".")) {
            signupEmail.setError("Please enter a valid email!");
        }
        return valid ;
    }

}
