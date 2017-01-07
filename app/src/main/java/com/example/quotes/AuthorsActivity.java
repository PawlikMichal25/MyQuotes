package com.example.quotes;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.quotes.model.Author;

public class AuthorsActivity extends AppCompatActivity {

    public static final String AUTHOR_ID = "authorId";
    public static final String AUTHOR_NAME = "authorName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        getSupportActionBar().setTitle((String)getIntent().getExtras().get(AUTHOR_NAME));

        QuotesFragment quotesFragment = new QuotesFragment();

        String [] authorNames = ((String)getIntent().getExtras().get(AUTHOR_NAME)).split(" ", 2);
        quotesFragment.setAuthorData(new Author(authorNames[1], authorNames[0]),
                (long)getIntent().getExtras().get(AUTHOR_ID));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, quotesFragment);
        transaction.commit();
    }
}
