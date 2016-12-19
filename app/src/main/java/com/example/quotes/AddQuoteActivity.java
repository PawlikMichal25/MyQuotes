package com.example.quotes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddQuoteActivity extends AppCompatActivity {

    public static final int NEW_QUOTE_ADDED = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);
    }

    public void onAddQuote(View view){
        EditText addQuoteContent = (EditText)findViewById(R.id.addQuoteContent);
        EditText addQuoteAuthor = (EditText)findViewById(R.id.addQuoteAuthor);
        CheckBox addQuoteFavorite = (CheckBox)findViewById(R.id.addQuoteFavorite);

        // TODO Distinguish firstName and lastName (initial?)
        String [] author = addQuoteAuthor.getText().toString().split(" ", 2);

        try {
            QuotesDatabaseHelper quotesDatabaseHelper = new QuotesDatabaseHelper(this);
            SQLiteDatabase db = quotesDatabaseHelper.getWritableDatabase();

            // Check if author already exists:
            Cursor cursor = db.query("Authors",
                    new String[]{"_id"},
                    "FirstName = ? and LastName = ?",
                    new String[]{author[0], author[1]},
                    null, null, null);

            long authorId;

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())                // Author exists in the database
                authorId = cursor.getLong(0);
            else
                authorId = quotesDatabaseHelper.insertAuthor(db, author[0], author[1]);         // Create new author

            // Add new quote
            quotesDatabaseHelper.insertQuote(db, authorId, addQuoteContent.getText().toString(), addQuoteFavorite.isChecked());

            cursor.close();
            db.close();
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        setResult(AppCompatActivity.RESULT_OK);
        finish();
    }
}
