package com.example.quotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

    private EditText authorFirstNameInput;
    private EditText authorLastNameInput;
    private CheckBox isFavoriteBox;
    private EditText quoteContentInput;

    private TextInputLayout authorFirstNameInputLayout;
    private TextInputLayout quoteContentInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        authorFirstNameInput = (EditText) findViewById(R.id.quotes_author_first_name);
        authorLastNameInput = (EditText) findViewById(R.id.quotes_author_last_name);
        isFavoriteBox = (CheckBox) findViewById(R.id.quotes_is_favorite);
        quoteContentInput = (EditText) findViewById(R.id.quotes_quote);

        authorFirstNameInputLayout = (TextInputLayout) findViewById(R.id.quotes_author_first_name_layout);
        quoteContentInputLayout = (TextInputLayout) findViewById(R.id.quotes_quote_layout);

        authorFirstNameInput.addTextChangedListener(new MyTextWatcher(authorFirstNameInputLayout, authorFirstNameInput));
        quoteContentInput.addTextChangedListener(new MyTextWatcher(quoteContentInputLayout, quoteContentInput));

        Intent intent = getIntent();
        editing = intentHasAllExtras(intent);
        if(editing) {
            authorFirstName = intent.getStringExtra(AUTHOR_FIRST_NAME);
            authorLastName = intent.getStringExtra(AUTHOR_LAST_NAME);
            isFavorite = intent.getBooleanExtra(IS_FAVORITE, false);
            quoteContent = intent.getStringExtra(QUOTE_CONTENT);

            authorFirstNameInput.setText(authorFirstName);
            authorLastNameInput.setText(authorLastName);
            isFavoriteBox.setChecked(isFavorite);
            quoteContentInput.setText(quoteContent);

            getSupportActionBar().setTitle(R.string.title_activity_quotes_edit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(editing)
            getMenuInflater().inflate(R.menu.quotes_edit_menu, menu);
        else
            getMenuInflater().inflate(R.menu.quotes_add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.home:
                finishActivity();
                break;

            case R.id.save:

                // Tmp variables, because if they were both in IF the second one might not be invoked
                boolean fn = validateFieldNotEmpty(authorFirstNameInputLayout, authorFirstNameInput);
                boolean qc = validateFieldNotEmpty(quoteContentInputLayout, quoteContentInput);

                if(fn && qc) {

                    if (editing) {    // Editing existing quote
                        QuotesDatabaseHelper quotesDatabaseHelper = new QuotesDatabaseHelper(this);
                        SQLiteDatabase db = quotesDatabaseHelper.getWritableDatabase();

                        long authorId = quotesDatabaseHelper.findAuthorId(db,
                                authorFirstName,
                                authorLastName);

                        // Author has changed
                        if (!authorFirstName.equals(authorFirstNameInput.getText().toString()) || !authorLastName.equals(authorLastNameInput.getText().toString())) {
                            quotesDatabaseHelper.editAuthor(db,
                                    authorId,
                                    authorFirstNameInput.getText().toString(),
                                    authorLastNameInput.getText().toString());

                            authorId = quotesDatabaseHelper.findAuthorId(db,
                                    authorFirstNameInput.getText().toString(),
                                    authorLastNameInput.getText().toString());
                        }

                        // Quote or Favorite field have changed
                        if (!quoteContent.equals(quoteContentInput.getText().toString()) || isFavorite != isFavoriteBox.isChecked()) {
                            long quoteId = quotesDatabaseHelper.findQuoteId(db,
                                    authorId,
                                    quoteContent,
                                    isFavorite);

                            quotesDatabaseHelper.editQuote(db, quoteId, authorId, quoteContentInput.getText().toString(), isFavoriteBox.isChecked());
                        }

                    } else {          // Adding new quote
                        QuotesDatabaseHelper quotesDatabaseHelper = new QuotesDatabaseHelper(this);
                        SQLiteDatabase db = quotesDatabaseHelper.getWritableDatabase();
                        quotesDatabaseHelper.addQuote(db,
                                authorFirstNameInput.getText().toString(),
                                authorLastNameInput.getText().toString(),
                                quoteContentInput.getText().toString(),
                                isFavoriteBox.isChecked());

                    }
                    Toast toast = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                    toast.show();
                    finishActivity();
                }
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

    private class MyTextWatcher implements TextWatcher {

        private TextInputLayout textInputLayout;
        private EditText editText;

        private MyTextWatcher(TextInputLayout textInputLayout, EditText editText){
            this.textInputLayout = textInputLayout;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            validateFieldNotEmpty(textInputLayout, editText);
        }
    }

    private boolean validateFieldNotEmpty(TextInputLayout textInputLayout, EditText editText){
        if (editText.getText().toString().trim().isEmpty()) {
            textInputLayout.setError(getString(R.string.error_field_empty));
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }
}
