package io.blacklagoonapps.myquotes.command;


import android.content.DialogInterface;

public class CommandWrapper implements DialogInterface.OnClickListener {

    private Command command;

    public CommandWrapper(Command command) {
        this.command = command;
    }

    @Override
    public void onClick(DialogInterface dialog, int i) {
        dialog.dismiss();
        command.execute();
    }
}
