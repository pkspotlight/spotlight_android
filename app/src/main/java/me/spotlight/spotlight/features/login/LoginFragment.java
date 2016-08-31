package me.spotlight.spotlight.features.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
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
public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    @Bind(R.id.login_username) EditText loginUsername;
    @Bind(R.id.login_password) EditText loginPassword;

    /*
        Manufacturing singleton
    */
    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(args);
        return loginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_login, container, false);
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
        setup(loginPassword);
        setup(loginUsername);
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

    @OnClick(R.id.login_submit)
    public void login() {
        if (validate()) {
            // sign-in
            ParseUser mUser = new ParseUser();
            mUser.setUsername(loginUsername.getText().toString());
            mUser.setPassword(loginPassword.getText().toString());
            mUser.logInInBackground(loginUsername.getText().toString(),
                    loginPassword.getText().toString(),
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (null != user) {
                                hideKeyboard();
                                Toast.makeText(getActivity(), "Logged in successfully!", Toast.LENGTH_LONG).show();
                                startActivity(MainActivity.getStartIntent(getActivity()));
                            } else {
                                hideKeyboard();
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                            }
                        }
                    });
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.login_password).getWindowToken(), 0);
    }

    public boolean validate() {

        boolean valid = true;
        if (TextUtils.isEmpty(loginUsername.getText().toString())) {
            loginUsername.setError("Please enter your username!");
            valid = false;
        }
        if (TextUtils.isEmpty(loginPassword.getText().toString())) {
            loginPassword.setError("Please enter a password!");
            valid = false;
        }
        return valid ;
    }

}
