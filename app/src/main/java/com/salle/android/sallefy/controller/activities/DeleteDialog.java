package com.salle.android.sallefy.controller.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeleteDialog extends AppCompatDialogFragment {
    private DeleteDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Account").setMessage("Are you sure you want to Delete your Account?").setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Delete", (dialogInterface, i) -> listener.onYesClicked());
        return builder.create();

    }

    public interface DeleteDialogListener {
        void onYesClicked();
    }


    /* Hacemos que tenga que implementar esta interface para que funcione y poder comunicarse con la
     * activity de dónde proviene
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement DeleteDialogListener");
        }
    }
}
