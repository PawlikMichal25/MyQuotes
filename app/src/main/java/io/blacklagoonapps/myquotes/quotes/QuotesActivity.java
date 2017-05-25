package io.blacklagoonapps.myquotes.quotes;

import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;

import io.blacklagoonapps.myquotes.ThemedActivity;
import io.blacklagoonapps.myquotes.R;

public class QuotesActivity extends ThemedActivity {

    EditText authorFirstNameInput;
    EditText authorLastNameInput;
    EditText quoteContentInput;

    TextInputLayout authorFirstNameInputLayout;
    TextInputLayout authorLastNameInputLayout;
    TextInputLayout quoteContentInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        authorFirstNameInput = (EditText) findViewById(R.id.textinputedittext_quotes_first_name);
        authorLastNameInput = (EditText) findViewById(R.id.textinputedittext_quotes_last_name);
        quoteContentInput = (EditText) findViewById(R.id.textinputedittext_quotes_quote);

        authorFirstNameInputLayout = (TextInputLayout) findViewById(R.id.textinputlayout_quotes_first_name);
        authorLastNameInputLayout = (TextInputLayout) findViewById(R.id.textinputlayout_quotes_last_name);
        quoteContentInputLayout = (TextInputLayout) findViewById(R.id.textinputlayout_quotes_quote);

        authorFirstNameInput.addTextChangedListener(new MyTextWatcher(authorFirstNameInputLayout, authorFirstNameInput));
        quoteContentInput.addTextChangedListener(new MyTextWatcher(quoteContentInputLayout, quoteContentInput));
    }

    boolean validateFieldNotEmpty(TextInputLayout textInputLayout, EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            textInputLayout.setError(getString(R.string.field_empty_error));
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private TextInputLayout textInputLayout;
        private EditText editText;

        private MyTextWatcher(TextInputLayout textInputLayout, EditText editText) {
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
