package com.example.quotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class QuotesActivity extends AppCompatActivity {

    public static final String AUTHOR_FIRST_NAME = "AUTHOR_FIRST_NAME";
    public static final String AUTHOR_LAST_NAME = "AUTHOR_LAST_NAME";
    public static final String IS_FAVORITE = "IS_FAVORITE";
    public static final String QUOTE_CONTENT = "QUOTE_CONTENT";

    private boolean editing;

    private String authorFirstName;
    private String authorLastName;
    private boolean isFavorite;
    private String quoteContent;

    private EditText authorFirstNameView;
    private EditText authorLastNameView;
    private CheckBox isFavoriteView;
    private EditText quoteContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        authorFirstNameView = (EditText) findViewById(R.id.quotes_author_first_name);
        authorLastNameView = (EditText) findViewById(R.id.quotes_author_last_name);
        isFavoriteView = (CheckBox) findViewById(R.id.quotes_is_favorite);
        quoteContentView = (EditText) findViewById(R.id.quotes_quote);

        Intent intent = getIntent();
        editing = intentHasAllExtras(intent);
        if(editing) {
            authorFirstName = intent.getStringExtra(AUTHOR_FIRST_NAME);
            authorLastName = intent.getStringExtra(AUTHOR_LAST_NAME);
            isFavorite = intent.getBooleanExtra(IS_FAVORITE, false);
            quoteContent = intent.getStringExtra(QUOTE_CONTENT);

            authorFirstNameView.setText(authorFirstName);
            authorLastNameView.setText(authorLastName);
            isFavoriteView.setChecked(isFavorite);
            quoteContentView.setText(quoteContent);

            getSupportActionBar().setTitle(R.string.title_activity_quotes_edit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.home:
                finishActivity();
                break;

            case R.id.save:
                if(editing){    // Editing existing quote
                    QuotesDatabaseHelper quotesDatabaseHelper = new QuotesDatabaseHelper(this);
                    SQLiteDatabase db = quotesDatabaseHelper.getWritableDatabase();

                    long authorId = quotesDatabaseHelper.findAuthorId(db,
                            authorFirstName,
                            authorLastName);

                    // Author has changed
                    if(!authorFirstName.equals(authorFirstNameView.getText().toString()) || !authorLastName.equals(authorLastNameView.getText().toString())){
                        quotesDatabaseHelper.editAuthor(db,
                                authorId,
                                authorFirstNameView.getText().toString(),
                                authorLastNameView.getText().toString());

                        authorId = quotesDatabaseHelper.findAuthorId(db,
                                authorFirstNameView.getText().toString(),
                                authorLastNameView.getText().toString());
                    }

                    // Quote or Favorite field have changed
                    if(!quoteContent.equals(quoteContentView.getText().toString()) || isFavorite != isFavoriteView.isChecked()) {
                        long quoteId = quotesDatabaseHelper.findQuoteId(db,
                                authorId,
                                quoteContent,
                                isFavorite);

                        quotesDatabaseHelper.editQuote(db, quoteId, authorId, quoteContentView.getText().toString(), isFavoriteView.isChecked());
                    }

                }
                else {          // Adding new quote
                    QuotesDatabaseHelper quotesDatabaseHelper = new QuotesDatabaseHelper(this);
                    SQLiteDatabase db = quotesDatabaseHelper.getWritableDatabase();
                    quotesDatabaseHelper.addQuote(db,
                            authorFirstNameView.getText().toString(),
                            authorLastNameView.getText().toString(),
                            quoteContentView.getText().toString(),
                            isFavoriteView.isChecked());

                }
                Toast toast = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                toast.show();
                finishActivity();
                break;

            case R.id.delete:
                QuotesDatabaseHelper quotesDatabaseHelper = new QuotesDatabaseHelper(this);
                SQLiteDatabase db = quotesDatabaseHelper.getWritableDatabase();

                long authorId = quotesDatabaseHelper.findAuthorId(db,
                        authorFirstName,
                        authorLastName);

                long quoteId = quotesDatabaseHelper.findQuoteId(db,
                        authorId,
                        quoteContent,
                        isFavorite);
                quotesDatabaseHelper.deleteQuote(db, quoteId);
                finishActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean intentHasAllExtras(Intent intent){
        return intent.hasExtra(AUTHOR_FIRST_NAME) && intent.hasExtra(AUTHOR_LAST_NAME) && intent.hasExtra(QUOTE_CONTENT);
    }

    private void finishActivity(){
        setResult(AppCompatActivity.RESULT_OK);
        finish();
    }
}
