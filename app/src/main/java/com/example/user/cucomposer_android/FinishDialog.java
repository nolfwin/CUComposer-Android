package com.example.user.cucomposer_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Wongse on 2/4/2558.
 */
public class FinishDialog extends DialogFragment {
    public FinishDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Poptime")
                .setMessage("Do you want to save your song?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast toast = Toast.makeText(getActivity(),"Please select a type of song you want to save.",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent nextIntent = new Intent(getActivity(), Home.class);
                        startActivity(nextIntent);
                    }
                })
                .create();
    }
}
