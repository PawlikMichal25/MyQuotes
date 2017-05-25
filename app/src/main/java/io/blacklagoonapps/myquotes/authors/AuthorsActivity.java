package io.blacklagoonapps.myquotes.authors;


import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import io.blacklagoonapps.myquotes.ThemedActivity;
import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.model.Author;
import io.blacklagoonapps.myquotes.quotes.QuotesFragment;

public class AuthorsActivity extends ThemedActivity {

    public static final String AUTHOR_ID = "authorId";
    public static final int NEW_AUTHOR_ID = 2510;

    private QuotesFragment quotesFragment;
    private Author author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        quotesFragment = new QuotesFragment();
        quotesFragment.setAuthorId((long) getIntent().getExtras().get(AUTHOR_ID));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_authors, quotesFragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpAuthor();
        setTitle();
    }

    private void setUpAuthor() {
        Author newAuthor = quotesFragment.getAuthorAt(0);
        if (newAuthor != null)
            this.author = newAuthor;
    }

    private void setTitle() {
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(getString(R.string.pref_names_display), true))
            getSupportActionBar().setTitle(author.getFirstName() + " " + author.getLastName());
        else
            getSupportActionBar().setTitle(author.getLastName() + " " + author.getFirstName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == NEW_AUTHOR_ID) {
            setIntent(data);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        quotesFragment.setAuthorId((long) getIntent().getExtras().get(AUTHOR_ID));
        quotesFragment.restart();
    }
}
