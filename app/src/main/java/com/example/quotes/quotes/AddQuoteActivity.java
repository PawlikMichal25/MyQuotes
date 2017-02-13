package com.example.quotes.quotes;

import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.quotes.R;
import com.example.quotes.database.DatabaseHelper;

public class AddQuoteActivity extends QuotesActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotes_add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                // Tmp variables, because if they were both in IF the second one might not be invoked
                boolean fn = validateFieldNotEmpty(authorFirstNameInputLayout, authorFirstNameInput);
                boolean qc = validateFieldNotEmpty(quoteContentInputLayout, quoteContentInput);
                if (fn && qc) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    databaseHelper.addQuote(db,
                            authorFirstNameInput.getText().toString(),
                            authorLastNameInput.getText().toString(),
                            quoteContentInput.getText().toString(),
                            isFavoriteBox.isChecked());

                    Toast toast = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                    toast.show();
                    finishActivityWithResult();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}