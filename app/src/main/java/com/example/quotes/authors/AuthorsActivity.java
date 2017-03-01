package com.example.quotes.authors;


import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.quotes.ThemedActivity;
import com.example.quotes.R;
import com.example.quotes.model.Author;
import com.example.quotes.quotes.QuotesFragment;

public class AuthorsActivity extends ThemedActivity {

    public static final String AUTHOR_ID = "authorId";
    public static final String AUTHOR_FIRST_NAME = "authorFirstName";
    public static final String AUTHOR_LAST_NAME = "authorLastName";

    private QuotesFragment quotesFragment;
    private Author author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        if(getIntent().hasExtra(AUTHOR_LAST_NAME))
            author = new Author((String)getIntent().getExtras().get(AUTHOR_FIRST_NAME),
                    (String)getIntent().getExtras().get(AUTHOR_LAST_NAME));
        else
            author = new Author((String)getIntent().getExtras().get(AUTHOR_FIRST_NAME));

        getSupportActionBar().setTitle(author.toString());

        quotesFragment = new QuotesFragment();
        quotesFragment.setAuthor(author);
        quotesFragment.setAuthorId((long)getIntent().getExtras().get(AUTHOR_ID));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, quotesFragment);
        transaction.commit();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        quotesFragment.initFragment();
        author = quotesFragment.getAuthor();
        getSupportActionBar().setTitle(author.toString());  // Author could have changed
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }
}
