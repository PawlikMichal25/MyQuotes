package com.example.quotes.authors;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.example.quotes.BaseActivity;
import com.example.quotes.MainActivity;
import com.example.quotes.R;
import com.example.quotes.model.Author;
import com.example.quotes.quotes.QuotesFragment;

public class AuthorsActivity extends BaseActivity {

    public static final String AUTHOR_ID = "authorId";
    public static final String AUTHOR_NAME = "authorName";

    private QuotesFragment quotesFragment;  // For retrieving current author

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        getSupportActionBar().setTitle((String)getIntent().getExtras().get(AUTHOR_NAME));

        quotesFragment = new QuotesFragment();

        String [] authorNames = ((String)getIntent().getExtras().get(AUTHOR_NAME)).split(" ", 2);

        if(authorNames.length == 1)
            quotesFragment.setAuthor(new Author(authorNames[0], ""));
        else
            quotesFragment.setAuthor(new Author(authorNames[1], authorNames[0]));
        quotesFragment.setAuthorId((long)getIntent().getExtras().get(AUTHOR_ID));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, quotesFragment);
        transaction.commit();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        quotesFragment.initFragment();
        getSupportActionBar().setTitle(quotesFragment.getAuthor().toString());
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.CHANGE_TAB, 1);
        return intent;
    }
}
