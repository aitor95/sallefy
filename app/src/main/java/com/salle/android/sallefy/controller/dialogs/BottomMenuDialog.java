package com.salle.android.sallefy.controller.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salle.android.sallefy.R;
import com.salle.android.sallefy.model.Playlist;
import com.salle.android.sallefy.model.TrackViewPack;
import com.salle.android.sallefy.utils.Session;

public class BottomMenuDialog extends BottomSheetDialogFragment {

    private BottomMenuDialogInterf mListener;

    private ImageView likeImage;
    private boolean songLiked;
    private boolean isTrackOwner;
    private boolean isPlaylistOwner;
    private TrackViewPack track;
    private boolean insidePlaylist;

    public BottomMenuDialog(TrackViewPack track, Context context, Playlist playlist) {
        this.track = track;
        this.songLiked = track.getTrack().isLiked();

        int trackUserId = track.getTrack().getUser().getId();
        int localUserId = Session.getInstance(context).getUser().getId();

        this.isTrackOwner = trackUserId == localUserId;

        if (playlist == null || playlist.getUser() == null) {
            isPlaylistOwner = false;
            insidePlaylist = false;
        } else {
            isPlaylistOwner = localUserId == playlist.getUser().getId();
            insidePlaylist = playlist.getUser() != null;
        }
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

                likeImage.setImageResource(songLiked ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
                mListener.onButtonClicked(track,"like");
                dismiss();
            }
        });

        LinearLayout share = v.findViewById(R.id.bottom_menu_a_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Look what I found on Sallefy!  http://sallefy.eu-west-3.elasticbeanstalk.com/track/" + track.getTrack().getId());
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared from Sallefy");
                startActivity(Intent.createChooser(intent, "Share"));
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

        TextView t = v.findViewById(R.id.bottom_menu_a_delete_text);
//        Log.d("TEST", "onCreateView: inside " + insidePlaylist + " isOwner " +isPlaylistOwner + " is OnwerTrac "  + isTrackOwner);

        if(insidePlaylist){
            t.setText(R.string.RemoveFromPlaylistBottomMenu);
            if(!isPlaylistOwner){
                t.setTextAppearance(R.style.primaryTextDisabled);
                ((ImageView) v.findViewById(R.id.bottom_menu_a_delete_img)).setBackgroundResource(R.drawable.ic_delete_grey);
            }
        }else{
            t.setText(R.string.DeleteBottomMenu);
            if(!isTrackOwner) {
                t.setTextAppearance(R.style.primaryTextDisabled);
                ((ImageView) v.findViewById(R.id.bottom_menu_a_delete_img)).setBackgroundResource(R.drawable.ic_delete_grey);
            }
        }

        if (isTrackOwner || (insidePlaylist && isPlaylistOwner)) {
            delete.setOnClickListener(view -> {
                mListener.onButtonClicked(track, "delete");
                dismiss();
            });
        }


        LinearLayout edit = v.findViewById(R.id.bottom_menu_a_editSong);

        if (!isTrackOwner) {
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

    @Override
    public void show(FragmentManager manager, String tag) {

        if (tag != null && tag.equals("FRAGMENT_TAG_MAX_ONE_INSTANCE")) {
            // we do not show it twice
            if (manager.findFragmentByTag(tag) == null) {
                super.show(manager, tag);
            }
        } else {
            super.show(manager, tag);
        }
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