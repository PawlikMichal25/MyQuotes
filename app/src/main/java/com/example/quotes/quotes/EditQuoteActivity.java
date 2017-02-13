package com.example.quotes.quotes;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.quotes.R;
import com.example.quotes.database.DatabaseHelper;
import com.example.quotes.command.Command;
import com.example.quotes.command.CommandWrapper;

public class EditQuoteActivity extends QuotesActivity {

    public static final String AUTHOR_FIRST_NAME = "AUTHOR_FIRST_NAME";
    public static final String AUTHOR_LAST_NAME = "AUTHOR_LAST_NAME";
    public static final String IS_FAVORITE = "IS_FAVORITE";
    public static final String QUOTE_CONTENT = "QUOTE_CONTENT";

    private String authorFirstName;
    private String authorLastName;
    private boolean isFavorite;
    private String quoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        authorFirstName = intent.getStringExtra(AUTHOR_FIRST_NAME);
        authorLastName = intent.getStringExtra(AUTHOR_LAST_NAME);
        isFavorite = intent.getBooleanExtra(IS_FAVORITE, false);
        quoteContent = intent.getStringExtra(QUOTE_CONTENT);

        authorFirstNameInput.setText(authorFirstName);
        authorLastNameInput.setText(authorLastName);
        isFavoriteBox.setChecked(isFavorite);
        quoteContentInput.setText(quoteContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotes_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        final long authorId = databaseHelper.findAuthorId(db, authorFirstName, authorLastName);
        final long quoteId = databaseHelper.findQuoteId(db, authorId, quoteContent, isFavorite);

        switch(item.getItemId()){
            case R.id.save:

                // Tmp variables, because if they were both in IF the second one might not be invoked
                boolean fn = validateFieldNotEmpty(authorFirstNameInputLayout, authorFirstNameInput);
                boolean qc = validateFieldNotEmpty(quoteContentInputLayout, quoteContentInput);
                if(fn && qc) {
                    final String firstNameInput = authorFirstNameInput.getText().toString();
                    final String lastNameInput = authorLastNameInput.getText().toString();
                    final String contentInput = quoteContentInput.getText().toString();

                    // Quote's content or Favorite field have changed
                    if (!quoteContent.equals(contentInput) || isFavorite != isFavoriteBox.isChecked()) {
                        databaseHelper.editQuote(db, quoteId, authorId, contentInput, isFavoriteBox.isChecked());
                    }

                    boolean finishActivity = true;
                    // Author has changed
                    if (!authorFirstName.equals(firstNameInput) || !authorLastName.equals(lastNameInput)) {
                        if (databaseHelper.countQuotes(db, authorId) > 1){
                            finishActivity = false;
                            createAndShowDialog(databaseHelper, db, firstNameInput, lastNameInput, authorId, quoteId);
                        }
                        else changeAllQuotes(databaseHelper, db, firstNameInput, lastNameInput, authorId);
                    }

                    if(finishActivity) finishEditing("Saved!");
                }
                break;

            case R.id.delete:
                databaseHelper.deleteQuote(db, quoteId);
                finishEditing("Deleted!");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAndShowDialog(final DatabaseHelper databaseHelper, final SQLiteDatabase db,
                                     final String firstNameInput, final String lastNameInput, final long authorId,
                                     final long quoteId) {
        Command changeSingleQuoteCommand = new Command() {
            @Override
            public void execute() {
                changeSingleQuote(databaseHelper, db, firstNameInput, lastNameInput, authorId, quoteId);
                finishEditing("Saved!");
            }
        };
        Command changeAllQuotesCommand = new Command() {
            @Override
            public void execute() {
                changeAllQuotes(databaseHelper, db, firstNameInput, lastNameInput, authorId);
                finishEditing("Saved!");
            }
        };
        Command emptyCommand = Command.NO_OPERATION;
        AlertDialog dialog = createEditAuthorDialog(changeAllQuotesCommand, emptyCommand, changeSingleQuoteCommand);
        dialog.show();
    }

    private void finishEditing(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        finishActivityWithResult();
    }

    private void changeSingleQuote(DatabaseHelper databaseHelper, SQLiteDatabase db, String firstNameInput,
                                   String lastNameInput, long authorId, long quoteId) {
        long newAuthorId = databaseHelper.findAuthorId(db, firstNameInput, lastNameInput);
        if (newAuthorId == -1){   // quote's new author doesn't exist in database
            newAuthorId = databaseHelper.insertAuthor(db, firstNameInput, lastNameInput);
        }
        databaseHelper.editSingleQuotesAuthor(db, quoteId, newAuthorId);
        if (databaseHelper.countQuotes(db, authorId) == 0){  // delete old author if there are no quotes by him/her
            databaseHelper.deleteAuthor(db, authorId);
        }
    }

    private void changeAllQuotes(DatabaseHelper databaseHelper, SQLiteDatabase db, String firstNameInput,
                                 String lastNameInput, long authorId) {
        long newAuthorId = databaseHelper.findAuthorId(db, firstNameInput, lastNameInput);
        if (newAuthorId == -1)  // if new author doesn't exist in database, edit existing author's first name and last name
            databaseHelper.editAuthor(db, authorId, firstNameInput, lastNameInput);
        else {
            databaseHelper.editQuotesAuthor(db, authorId, newAuthorId);
        }
    }

    private AlertDialog createEditAuthorDialog(Command allQuotesCommand, Command cancelCommand,
                                               Command singleQuoteCommand){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_author_dialog_title)
                .setMessage(R.string.edit_author_dialog_message)
                .setPositiveButton(R.string.all_quotes, new CommandWrapper(allQuotesCommand))
                .setNeutralButton(R.string.cancel, new CommandWrapper(cancelCommand))
                .setNegativeButton(R.string.only_this_quote, new CommandWrapper(singleQuoteCommand));
        return builder.create();
    }
}
