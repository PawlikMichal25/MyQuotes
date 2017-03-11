package io.blacklagoonapps.myquotes.authors;


import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import io.blacklagoonapps.myquotes.ThemedActivity;
import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.model.Author;
import io.blacklagoonapps.myquotes.quotes.QuotesFragment;

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

        setTitle();

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
        setTitle();  // Author could have changed
    }

    private void setTitle(){
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(getString(R.string.pref_names_display), true))
            getSupportActionBar().setTitle(author.getFirstName() + " " + author.getLastName());
        else
            getSupportActionBar().setTitle(author.getLastName() + " " + author.getFirstName());
    }
}
