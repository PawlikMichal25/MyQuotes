package com.example.quotes.authors;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.quotes.R;
import com.example.quotes.model.Author;
import com.example.quotes.quotes.QuotesFragment;

public class AuthorsActivity extends AppCompatActivity {

    public static final String AUTHOR_ID = "authorId";
    public static final String AUTHOR_NAME = "authorName";

    private QuotesFragment quotesFragment;  // For onActivityResult, alternatively we could add a tag during committing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        getSupportActionBar().setTitle((String)getIntent().getExtras().get(AUTHOR_NAME));

        quotesFragment = new QuotesFragment();

        String [] authorNames = ((String)getIntent().getExtras().get(AUTHOR_NAME)).split(" ", 2);

        quotesFragment.setAuthor(new Author(authorNames[1], authorNames[0]));
        quotesFragment.setAuthorId((long)getIntent().getExtras().get(AUTHOR_ID));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, quotesFragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        quotesFragment.onActivityResult(requestCode, resultCode, data);
        getSupportActionBar().setTitle(quotesFragment.getAuthor().toString());
    }
}
