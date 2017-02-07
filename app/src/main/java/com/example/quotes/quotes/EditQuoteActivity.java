package com.example.quotes.quotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.quotes.R;
import com.example.quotes.database.DatabaseHelper;

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
        switch(item.getItemId()){
            case R.id.save:

                // Tmp variables, because if they were both in IF the second one might not be invoked
                boolean fn = validateFieldNotEmpty(authorFirstNameInputLayout, authorFirstNameInput);
                boolean qc = validateFieldNotEmpty(quoteContentInputLayout, quoteContentInput);
                if(fn && qc) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();

                    long authorId = databaseHelper.findAuthorId(db,
                            authorFirstName,
                            authorLastName);

                    // Author has changed
                    if (!authorFirstName.equals(authorFirstNameInput.getText().toString()) || !authorLastName.equals(authorLastNameInput.getText().toString())) {
                        databaseHelper.editAuthor(db,
                                authorId,
                                authorFirstNameInput.getText().toString(),
                                authorLastNameInput.getText().toString());

                        authorId = databaseHelper.findAuthorId(db,
                                authorFirstNameInput.getText().toString(),
                                authorLastNameInput.getText().toString());
                    }

                    // Quote or Favorite field have changed
                    if (!quoteContent.equals(quoteContentInput.getText().toString()) || isFavorite != isFavoriteBox.isChecked()) {
                        long quoteId = databaseHelper.findQuoteId(db,
                                authorId,
                                quoteContent,
                                isFavorite);

                        databaseHelper.editQuote(db, quoteId, authorId, quoteContentInput.getText().toString(), isFavoriteBox.isChecked());
                    }
                    Toast toast = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                    toast.show();
                    finishActivityWithResult();
                }
                break;

            case R.id.delete:
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                long authorId = databaseHelper.findAuthorId(db,
                        authorFirstName,
                        authorLastName);

                long quoteId = databaseHelper.findQuoteId(db,
                        authorId,
                        quoteContent,
                        isFavorite);
                databaseHelper.deleteQuote(db, quoteId);
                finishActivityWithResult();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
