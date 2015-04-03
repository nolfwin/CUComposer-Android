package com.example.user.cucomposer_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Wongse on 2/4/2558.
 */
public class BackFromEditDialog extends DialogFragment {
    public BackFromEditDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Poptime")
                .setMessage("Which version of note do you want to save?")
                .setPositiveButton("Latest version", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast toast = Toast.makeText(getActivity(),"Latest version",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setNegativeButton("Current default version", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast toast = Toast.makeText(getActivity(),"Current default version",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .create();
    }
}
