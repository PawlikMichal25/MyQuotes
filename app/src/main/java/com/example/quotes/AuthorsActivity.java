package com.example.quotes;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AuthorsActivity extends AppCompatActivity {

    public static final String AUTHOR_ID = "authorId";
    public static final String AUTHOR_NAME = "authorName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        getSupportActionBar().setTitle((String)getIntent().getExtras().get(AUTHOR_NAME));

        QuotesFragment quotesFragment = new QuotesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, quotesFragment);
        transaction.commit();
    }
}
