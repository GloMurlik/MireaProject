package ru.mirea.gribinichenko.mireaproject.ui.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.io.FileOutputStream;

import ru.mirea.gribinichenko.mireaproject.R;

public class FileDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(getContext());
        fileName.setHint("set filename");
        EditText fileText = new EditText(getContext());
        fileText.setHint("set text");
        layout.addView(fileName);
        layout.addView(fileText);
        View view = new View(getContext());
        builder.setTitle("Create file")
                .setIcon(R.mipmap.ic_launcher_round)
                .setView(layout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string = fileText.getText().toString();
                        FileOutputStream outputStream;
                        try {
                            outputStream = getActivity().openFileOutput(fileName.getText().toString().concat(".txt"), Context.MODE_PRIVATE);
                            outputStream.write(FileFragment.encryptMsg(string, FileFragment.generateKey()));
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        return builder.create();
    }
}
