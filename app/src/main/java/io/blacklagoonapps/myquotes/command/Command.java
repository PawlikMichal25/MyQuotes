package io.blacklagoonapps.myquotes.command;


public interface Command {
    void execute();

    Command NO_OPERATION = new Command() {
        @Override
        public void execute() {
        }
    };
}
