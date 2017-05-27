package io.blacklagoonapps.myquotes.quotes;

import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.database.DatabaseHelper;

public class AddQuoteActivity extends QuotesActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotes_add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_addquotes_save:

                final String firstNameInput = authorFirstNameInput.getText().toString().trim();
                final String lastNameInput = authorLastNameInput.getText().toString().trim();
                final String contentInput = quoteContentInput.getText().toString().trim();
                if (firstNameInput.isEmpty() && lastNameInput.isEmpty() && contentInput.isEmpty()) {
                    finish();
                    return true;
                }

                // Tmp variables, because if they were both in IF the second one might not be invoked
                boolean fn = validateFieldNotEmpty(authorFirstNameInputLayout, authorFirstNameInput);
                boolean qc = validateFieldNotEmpty(quoteContentInputLayout, quoteContentInput);
                if (fn && qc) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    databaseHelper.addQuote(db,
                            firstNameInput,
                            lastNameInput,
                            contentInput);

                    db.close();
                    Toast toast = Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}