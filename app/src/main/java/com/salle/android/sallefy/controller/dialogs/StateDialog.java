package com.salle.android.sallefy.controller.dialogs;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.salle.android.sallefy.R;

import java.util.ArrayList;
import java.util.List;


public class StateDialog {

    public static final String TAG = StateDialog.class.getName();

    private static StateDialog sManager;
    private static Object mutex = new Object();

    private Context mContext;
    private Dialog mDialog;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private ImageView ivIcon;
    private ImageView ivLike;
    private ImageView ivFollow;
    private Button btnAccept;

    public static StateDialog getInstance(Context context) {
        if (sManager == null) {
            sManager = new StateDialog(context);
        }
        return sManager;
    }

    private StateDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
    }

    public void showStateDialog(boolean completed) {
        mDialog.setContentView(R.layout.dialog_state);
        mDialog.setCanceledOnTouchOutside(false);

        tvTitle = (TextView) mDialog.findViewById(R.id.dialog_state_title);
        tvSubtitle = (TextView) mDialog.findViewById(R.id.dialog_state_subtitle);
        ivIcon = (ImageView) mDialog.findViewById(R.id.dialog_state_icon);
        btnAccept = (Button) mDialog.findViewById(R.id.dialog_state_button);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        if ((completed)) {
            completedTask();
        } else {
            inProgressTask();
        }
        ArrayList<String> runningactivities = new ArrayList<String>();

        ActivityManager activityManager = (ActivityManager)mContext.getSystemService (Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i1 = 0; i1 < services.size(); i1++) {
            runningactivities.add(0,services.get(i1).topActivity.toString());
        }

        if(runningactivities.contains("ComponentInfo{com.salle.sallefy/com.salle.android.sallefy.controller.activities.EditSongActivity}") ||
                runningactivities.contains("ComponentInfo{com.salle.sallefy/com.salle.android.sallefy.controller.activities.UploadSongActivity}") ||
                runningactivities.contains("ComponentInfo{com.salle.sallefy/com.salle.android.sallefy.controller.download.Downloader}")){
            Log.d(TAG, "showStateDialog: Showing state dialog.");
            mDialog.show();
        }
    }


    private void inProgressTask() {
        tvTitle.setText(R.string.state_wait);
        tvSubtitle.setText(R.string.state_task_in_progress);
        ivIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_sallefy));
        btnAccept.setVisibility(View.GONE);
    }

    private void completedTask() {
        tvTitle.setText(R.string.state_success_title);
        tvSubtitle.setText(R.string.state_task_completed);
        //btnAccept.setBackground(mContext.getDrawable(R.drawable.backgr_blue_radius));
        btnAccept.setVisibility(View.VISIBLE);
    }

    public boolean isDialogShown() {
        return mDialog.isShowing();
    }

    public void close() {

        mDialog.cancel();
        sManager = null;
    }
}
