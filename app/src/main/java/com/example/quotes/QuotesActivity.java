package com.example.quotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class QuotesActivity extends AppCompatActivity {

    public static final String AUTHOR_FIRST_NAME = "AUTHOR_FIRST_NAME";
    public static final String AUTHOR_LAST_NAME = "AUTHOR_LAST_NAME";
    public static final String QUOTE_CONTENT = "QUOTE_CONTENT";

    private String authorFirstName;
    private String authorLastName;
    private String quoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);
        Intent intent = getIntent();
        if(intentHasAllExtras(intent)) {
            authorFirstName = getIntent().getStringExtra(AUTHOR_FIRST_NAME);
            authorLastName = getIntent().getStringExtra(AUTHOR_LAST_NAME);
            quoteContent = getIntent().getStringExtra(QUOTE_CONTENT);
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
                finish();
                break;
            case R.id.save:
                Toast toast = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean intentHasAllExtras(Intent intent){
        return intent.hasExtra(AUTHOR_FIRST_NAME) && intent.hasExtra(AUTHOR_LAST_NAME) && intent.hasExtra(QUOTE_CONTENT);
    }
}
