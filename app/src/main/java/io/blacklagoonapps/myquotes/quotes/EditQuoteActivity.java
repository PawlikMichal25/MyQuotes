package io.blacklagoonapps.myquotes.quotes;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.authors.AuthorsActivity;
import io.blacklagoonapps.myquotes.database.DatabaseHelper;
import io.blacklagoonapps.myquotes.command.Command;
import io.blacklagoonapps.myquotes.command.CommandWrapper;

public class EditQuoteActivity extends QuotesActivity {

    public static final String QUOTE_CONTENT = "QUOTE_CONTENT";
    public static final String AUTHOR_FIRST_NAME = "AUTHOR_FIRST_NAME";
    public static final String AUTHOR_LAST_NAME = "AUTHOR_LAST_NAME";

    private String authorFirstName;
    private String authorLastName;
    private String quoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        authorFirstName = intent.getStringExtra(AUTHOR_FIRST_NAME);
        authorLastName = intent.getStringExtra(AUTHOR_LAST_NAME);
        quoteContent = intent.getStringExtra(QUOTE_CONTENT);

        authorFirstNameInputLayout.setHintAnimationEnabled(false);
        authorFirstNameInput.setText(authorFirstName);

        authorLastNameInputLayout.setHintAnimationEnabled(false);   //Heh https://code.google.com/p/android/issues/detail?id=178168&thanks=178168&ts=1435254358
        authorLastNameInput.setText(authorLastName);
        authorLastNameInputLayout.setHintAnimationEnabled(true);

        quoteContentInputLayout.setHintAnimationEnabled(false);
        quoteContentInput.setText(quoteContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotes_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        final long authorId = databaseHelper.findAuthorId(db, authorFirstName, authorLastName);
        final long quoteId = databaseHelper.findQuoteId(db, authorId, quoteContent);

        switch (item.getItemId()) {
            case R.id.item_editquotes_save:

                final String firstNameInput = authorFirstNameInput.getText().toString().trim();
                final String lastNameInput = authorLastNameInput.getText().toString().trim();
                final String contentInput = quoteContentInput.getText().toString().trim();

                if (firstNameInput.equals(authorFirstName) && lastNameInput.equals(authorLastName) && contentInput.equals(quoteContent)) {
                    finish();
                    return true;
                }

                // Tmp variables, because if they were both in IF the second one might not be invoked
                boolean fn = validateFieldNotEmpty(authorFirstNameInputLayout, authorFirstNameInput);
                boolean qc = validateFieldNotEmpty(quoteContentInputLayout, quoteContentInput);
                if (fn && qc) {

                    // Quote's content have changed
                    if (!quoteContent.equals(contentInput)) {
                        databaseHelper.editQuote(db, quoteId, authorId, contentInput);
                    }

                    long newAuthorId = -1;
                    // Author has changed
                    if (!authorFirstName.equals(firstNameInput) || !authorLastName.equals(lastNameInput)) {
                        if (databaseHelper.countQuotes(db, authorId) > 1)
                            createAndShowDialog(databaseHelper, db, firstNameInput, lastNameInput, authorId, quoteId);
                        else
                            newAuthorId = changeAllQuotes(databaseHelper, db, firstNameInput, lastNameInput, authorId);
                    }

                    if (newAuthorId != -1)
                        finishWithToastAndResult(getString(R.string.saved), newAuthorId);
                }
                break;

            case R.id.item_editquotes_delete:
                databaseHelper.deleteQuote(db, quoteId);
                finishWithToast(getString(R.string.deleted));
                break;

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAndShowDialog(final DatabaseHelper databaseHelper, final SQLiteDatabase db,
                                     final String firstNameInput, final String lastNameInput, final long authorId,
                                     final long quoteId) {
        Command changeSingleQuoteCommand = new Command() {
            @Override
            public void execute() {
                long newAuthorId = changeSingleQuote(databaseHelper, db, firstNameInput, lastNameInput, authorId, quoteId);
                finishWithToastAndResult(getString(R.string.saved), newAuthorId);
            }
        };
        Command changeAllQuotesCommand = new Command() {
            @Override
            public void execute() {
                long newAuthorId = changeAllQuotes(databaseHelper, db, firstNameInput, lastNameInput, authorId);
                finishWithToastAndResult(getString(R.string.saved), newAuthorId);
            }
        };
        Command emptyCommand = Command.NO_OPERATION;
        AlertDialog dialog = createEditAuthorDialog(changeAllQuotesCommand, emptyCommand, changeSingleQuoteCommand);
        dialog.show();
        centerDialogButtons(dialog);
    }

    private void finishWithToastAndResult(String message, long authorId) {
        Intent intent = new Intent();
        intent.putExtra(AuthorsActivity.AUTHOR_ID, authorId);
        setResult(AuthorsActivity.NEW_AUTHOR_ID, intent);
        finishWithToast(message);
    }

    private void finishWithToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private long changeSingleQuote(DatabaseHelper databaseHelper, SQLiteDatabase db, String firstNameInput,
                                   String lastNameInput, long authorId, long quoteId) {
        long newAuthorId = databaseHelper.findAuthorId(db, firstNameInput, lastNameInput);
        if (newAuthorId == -1) {   // quote's new author doesn't exist in database
            newAuthorId = databaseHelper.addAuthor(db, firstNameInput, lastNameInput);
        }
        databaseHelper.updateQuote(db, quoteId, newAuthorId);
        if (databaseHelper.countQuotes(db, authorId) == 0) {  // delete old author if there are no quotes by him/her
            databaseHelper.deleteAuthor(db, authorId);
        }

        return newAuthorId;
    }

    private long changeAllQuotes(DatabaseHelper databaseHelper, SQLiteDatabase db, String firstNameInput,
                                 String lastNameInput, long authorId) {
        long newAuthorId = databaseHelper.findAuthorId(db, firstNameInput, lastNameInput);
        if (newAuthorId == -1) { // if new author doesn't exist in database, edit existing author's first name and last name
            databaseHelper.editAuthor(db, authorId, firstNameInput, lastNameInput);
            return authorId;
        } else {
            databaseHelper.editQuotesAuthor(db, authorId, newAuthorId);
            return newAuthorId;
        }
    }

    private AlertDialog createEditAuthorDialog(Command allQuotesCommand, Command cancelCommand,
                                               Command singleQuoteCommand) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_author_dialog_title)
                .setMessage(R.string.edit_author_dialog_message)
                .setPositiveButton(R.string.all_quotes, new CommandWrapper(allQuotesCommand))
                .setNeutralButton(R.string.cancel, new CommandWrapper(cancelCommand))
                .setNegativeButton(R.string.only_this_quote, new CommandWrapper(singleQuoteCommand));
        return builder.create();
    }

    private void centerDialogButtons(AlertDialog dialog) {
        Button[] buttons = {dialog.getButton(AlertDialog.BUTTON_POSITIVE), dialog.getButton(AlertDialog.BUTTON_NEUTRAL),
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)};

        for (Button button : buttons) {
            centerButton(button);
        }
    }

    private void centerButton(Button button) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        button.setLayoutParams(layoutParams);
    }
}
