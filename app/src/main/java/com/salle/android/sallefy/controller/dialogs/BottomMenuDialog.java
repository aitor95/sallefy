package com.salle.android.sallefy.controller.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salle.android.sallefy.R;

public class BottomMenuDialog extends BottomSheetDialogFragment {

    private BottomMenuDialogInterf mListener;

    private LinearLayout like;
    private LinearLayout addToPlaylist;
    private LinearLayout showArtist;
    private LinearLayout delete;
    private LinearLayout edit;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_menu, container, false);

        like = v.findViewById(R.id.bottom_menu_a_like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("like");
                dismiss();
            }
        });

        addToPlaylist = v.findViewById(R.id.bottom_menu_a_addtoPlaylist);
        addToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("addToPlaylist");
                dismiss();
            }
        });

        showArtist = v.findViewById(R.id.bottom_menu_a_showArtist);
        showArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("showArtist");
                dismiss();
            }
        });

        delete = v.findViewById(R.id.bottom_menu_a_deleteSong);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("delete");
                dismiss();
            }
        });

        edit = v.findViewById(R.id.bottom_menu_a_editSong);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("edit");
                dismiss();
            }
        });

        return v;
    }

    public interface BottomMenuDialogInterf {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomMenuDialogInterf) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}