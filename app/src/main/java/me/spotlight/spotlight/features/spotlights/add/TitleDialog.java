package me.spotlight.spotlight.features.spotlights.add;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;

/**
 * Created by Anatol on 8/2/2016.
 */
public class TitleDialog extends DialogFragment {

    @Bind(R.id.title_dialog_edit)
    EditText edit;
    @Bind(R.id.title_dialog_submit)
    Button submit;

    public interface ActionListener {
        void onTitlePicked(String title);
    }

    ActionListener actionListener;

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public static TitleDialog newInstance() {
        Bundle bundle = new Bundle();
        TitleDialog titleDialog = new TitleDialog();
        titleDialog.setArguments(bundle);
        return titleDialog;
    }

    @OnClick(R.id.title_dialog_submit)
    public void submit() {
        if (!"".equals(edit.getText().toString())) {
            actionListener.onTitlePicked(edit.getText().toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_choose_title, null);
        ButterKnife.bind(getActivity(), view);
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        return dialog;
    }
}
