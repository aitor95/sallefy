package com.salle.android.sallefy.controller.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.model.TrackViewPack;
import com.salle.android.sallefy.utils.Session;

public class BottomMenuDialog extends BottomSheetDialogFragment {

    private BottomMenuDialogInterf mListener;

    private ImageView likeImage;
    private boolean songLiked;
    private boolean isOwned;
    private TrackViewPack track;

    public BottomMenuDialog(TrackViewPack track, Context context){
        this.track = track;
        this.songLiked = track.getTrack().isLiked();
        this.isOwned = track.getTrack().getUserLogin().equals(Session.getInstance(context).getUser().getLogin());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_menu, container, false);

        likeImage = v.findViewById(R.id.bottom_menu_like_image);

        likeImage.setImageResource(songLiked ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);

        LinearLayout like = v.findViewById(R.id.bottom_menu_a_like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songLiked = !songLiked;
                //TODO: Revisar
                likeImage.setImageResource(songLiked ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
                mListener.onButtonClicked(track,"like");
                dismiss();
            }
        });

        LinearLayout addToPlaylist = v.findViewById(R.id.bottom_menu_a_addtoPlaylist);
        addToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked(track, "addToPlaylist");
                dismiss();
            }
        });

        LinearLayout showArtist = v.findViewById(R.id.bottom_menu_a_showArtist);
        showArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked(track, "showArtist");
                dismiss();
            }
        });

        LinearLayout delete = v.findViewById(R.id.bottom_menu_a_deleteSong);

        if (!isOwned) {
            ((TextView) v.findViewById(R.id.bottom_menu_a_delete_text)).setTextAppearance(R.style.primaryTextDisabled);
            ((ImageView) v.findViewById(R.id.bottom_menu_a_delete_img)).setBackgroundResource(R.drawable.ic_delete_grey);
        } else {
            delete.setOnClickListener(view -> {
                mListener.onButtonClicked(track, "delete");
                dismiss();
            });
        }

        LinearLayout edit = v.findViewById(R.id.bottom_menu_a_editSong);

        if (!isOwned) {
            ((TextView) v.findViewById(R.id.bottom_menu_a_editSong_text)).setTextAppearance(R.style.primaryTextDisabled);
            ((ImageView) v.findViewById(R.id.bottom_menu_a_editSong_img)).setBackgroundResource(R.drawable.ic_edit_grey);
        } else {
            edit.setOnClickListener(view ->  {
                mListener.onButtonClicked(track, "edit");
                dismiss();
            });
        }

        return v;
    }

    public interface BottomMenuDialogInterf {
        void onButtonClicked(TrackViewPack track, String text);
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