package com.salle.android.sallefy.controller.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.salle.android.sallefy.R;

public class ErrorDialog {

    private static ErrorDialog sManager;
    private static Object mutex = new Object();

    private Context mContext;
    private Dialog mDialog;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private ImageView ivIcon;
    private ImageView ivLike;
    private ImageView ivFollow;
    private Button btnAccept;

    public static ErrorDialog getInstance(Context context) {
        if (sManager == null) {
            sManager = new ErrorDialog(context);
        }
        return sManager;
    }

    private ErrorDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
    }

    public void showErrorDialog() {
        mDialog.setContentView(R.layout.dialog_error);
        mDialog.setCanceledOnTouchOutside(false);

        tvTitle = (TextView) mDialog.findViewById(R.id.dialog_error_title);
        tvSubtitle = (TextView) mDialog.findViewById(R.id.dialog_error_subtitle);

        ivIcon = (ImageView) mDialog.findViewById(R.id.dialog_error_icon);

        btnAccept = (Button) mDialog.findViewById(R.id.dialog_error_button);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });
        mDialog.show();
    }
}
