package me.spotlight.spotlight.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import me.spotlight.spotlight.base.BaseApplication;

/**
 * Created by Anatol on 7/10/2016.
 */
public class DialogUtils {

    public static ProgressDialog showProgressDialog(Context context, int message) {
        return showProgressDialog(context, BaseApplication.getContext().getString(message));
    }

    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public static void showAlertDialog(Context context, int message) {
        showAlertDialog(context, BaseApplication.getContext().getString(message));
    }

    public static void showAlertDialog(Context context, String message) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                //  .setTitle(R.string.confirm)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

}
