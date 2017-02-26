package com.example.quotes.quotes;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.quotes.BaseActivity;
import com.example.quotes.R;

public class QuotesActivity extends BaseActivity {

    EditText authorFirstNameInput;
    EditText authorLastNameInput;
    CheckBox isFavoriteBox;
    EditText quoteContentInput;

    TextInputLayout authorFirstNameInputLayout;
    TextInputLayout authorLastNameInputLayout;
    TextInputLayout quoteContentInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        authorFirstNameInput = (EditText) findViewById(R.id.quotes_author_first_name);
        authorLastNameInput = (EditText) findViewById(R.id.quotes_author_last_name);
        isFavoriteBox = (CheckBox) findViewById(R.id.quotes_is_favorite);
        quoteContentInput = (EditText) findViewById(R.id.quotes_quote);

        authorFirstNameInputLayout = (TextInputLayout) findViewById(R.id.quotes_author_first_name_layout);
        authorLastNameInputLayout = (TextInputLayout) findViewById(R.id.quotes_author_last_name_layout);
        quoteContentInputLayout = (TextInputLayout) findViewById(R.id.quotes_quote_layout);

        authorFirstNameInput.addTextChangedListener(new MyTextWatcher(authorFirstNameInputLayout, authorFirstNameInput));
        quoteContentInput.addTextChangedListener(new MyTextWatcher(quoteContentInputLayout, quoteContentInput));
    }

    boolean validateFieldNotEmpty(TextInputLayout textInputLayout, EditText editText){
        if (editText.getText().toString().trim().isEmpty()) {
            textInputLayout.setError(getString(R.string.error_field_empty));
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
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
}
