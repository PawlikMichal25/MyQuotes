package io.blacklagoonapps.myquotes.quotes;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.blacklagoonapps.myquotes.MainActivity;
import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.database.DatabaseHelper;
import io.blacklagoonapps.myquotes.model.Author;
import io.blacklagoonapps.myquotes.model.Quote;

import java.util.ArrayList;
import java.util.List;


public class QuotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuotesAdapter quotesAdapter;
    private List<Quote> quotes;
    private long authorId;
    private Author author;
    private final String allQuotesQuery = String.format("SELECT %s, %s, %s FROM %s ORDER BY %s DESC",
            Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT, Quote.Columns.FAVORITE, Quote.TABLE_NAME,
            Quote.Columns.FAVORITE);

    public QuotesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(allQuotesQuery);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotes, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        quotesAdapter = new QuotesAdapter(getContext(), quotes, author == null);

        setUpRecyclerView();
        setUpEmptyQuotesText(rootView);
        return rootView;
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(quotesAdapter);
    }

    private void setUpEmptyQuotesText(View rootView) {
        View quotesEmpty = rootView.findViewById(R.id.quotes_empty);
        int visibility;

        if(quotes.isEmpty()){
            visibility = View.VISIBLE;
            setUpAddQuote(rootView);
        }
        else
            visibility = View.INVISIBLE;
        quotesEmpty.setVisibility(visibility);
    }

    private void setUpAddQuote(View rootView){
        TextView addQuote = (TextView)rootView.findViewById(R.id.quotes_empty_add_quote);
        if(author == null)
            addQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddQuoteActivity.class);
                    startActivity(intent);
                }
            });
        else
            addQuote.setVisibility(View.GONE);
    }

    private void initData(String query) {
        if(author != null)
            initSpecificAuthorDataSet();
        else
            initDataSet(query);
    }

    private void initFragment(String query) {
        if(quotes != null)
            quotes.clear();
        initData(query);
        refreshQuotesAdapter();
        setUpEmptyQuotesText(getView());
    }

    public void initFragment(){
        initFragment(allQuotesQuery);
    }

    private void refreshQuotesAdapter() {
        quotesAdapter = new QuotesAdapter(getContext(), quotes, author == null);
        recyclerView.setAdapter(quotesAdapter);
        quotesAdapter.notifyDataSetChanged();
    }

    private void initDataSet(String query) {
        quotes = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DatabaseHelper(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor quotesCursor = db.rawQuery(query, null);

            if (quotesCursor.moveToFirst()){
                do{
                    authorId = quotesCursor.getInt(0);
                    Cursor authorCursor = db.query(Author.TABLE_NAME,
                            new String[]{Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME},
                            Author.Columns.ID + " = ?",
                            new String[]{String.valueOf(authorId)},
                            null, null, null);
                    if (authorCursor.moveToFirst()){
                        Author author = new Author(authorCursor.getString(0),
                                authorCursor.getString(1));
                        Quote quote = new Quote(author, quotesCursor.getString(1),
                                quotesCursor.getInt(2) == 1);
                        quotes.add(quote);
                    }
                    authorCursor.close();
                } while(quotesCursor.moveToNext());
            }

            quotesCursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void initSpecificAuthorDataSet() {
        quotes = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DatabaseHelper(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String authorsQuery = String.format("SELECT %s, %s FROM %s WHERE %s = ?",
                    Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME, Author.TABLE_NAME, Author.Columns.ID);
            Cursor authorCursor = db.rawQuery(authorsQuery, new String[]{String.valueOf(authorId)});
            if(authorCursor.moveToFirst()){
                author = new Author(authorCursor.getString(0), authorCursor.getString(1));
            }

            String quotesQuery = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ? ORDER BY %s DESC",
                    Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT, Quote.Columns.FAVORITE, Quote.TABLE_NAME,
                    Quote.Columns.AUTHOR_ID, Quote.Columns.FAVORITE);
            Cursor quotesCursor = db.rawQuery(quotesQuery, new String[]{String.valueOf(authorId)});
            if (quotesCursor.moveToFirst()) {
                do {
                    Quote quote = new Quote(author, quotesCursor.getString(1), quotesCursor.getInt(2) == 1);
                    quotes.add(quote);
                } while (quotesCursor.moveToNext());
            }

            quotesCursor.close();
            authorCursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void findQuotesAndAuthorsFromQuery(String searchQuery){
        quotes = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DatabaseHelper(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = String.format("SELECT %s, %s, %s, %s " +
                    "FROM %s as Q INNER JOIN %s as A ON Q.%s == A.%s " +
                    "WHERE %s LIKE '%%%s%%' OR %s LIKE '%%%s%%' OR %s LIKE '%%%s%%' ORDER BY %s DESC",
                    Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME, Quote.Columns.CONTENT, Quote.Columns.FAVORITE,
                    Quote.TABLE_NAME, Author.TABLE_NAME, Quote.Columns.AUTHOR_ID, Author.Columns.ID,
                    Quote.Columns.CONTENT, searchQuery, Author.Columns.FIRST_NAME, searchQuery,
                    Author.Columns.LAST_NAME, searchQuery, Quote.Columns.FAVORITE);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
                do{
                    Author author = new Author(cursor.getString(0), cursor.getString(1));
                    Quote quote = new Quote(author, cursor.getString(2), cursor.getInt(3) == 1);
                    quotes.add(quote);
                } while(cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        refreshQuotesAdapter();
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }
}
