package com.example.quotes.command;


import android.content.DialogInterface;

import com.example.quotes.command.Command;

public class CommandWrapper implements DialogInterface.OnClickListener {

    private Command command;

    public CommandWrapper(Command command){
        this.command = command;
    }

    @Override
    public void onClick(DialogInterface dialog, int i) {
        dialog.dismiss();
        command.execute();
    }
}
