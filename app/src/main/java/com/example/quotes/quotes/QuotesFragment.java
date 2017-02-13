package com.example.quotes.quotes;


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

import com.example.quotes.R;
import com.example.quotes.database.DatabaseHelper;
import com.example.quotes.model.Author;
import com.example.quotes.model.Quote;

import java.util.ArrayList;
import java.util.List;


public class QuotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuotesAdapter quotesAdapter;
    private List<Quote> quotes;
    private long authorId;
    private Author author;
    private final String allQuotesQuery = "SELECT Author_id, Content, Favorite FROM Quotes ORDER BY Favorite DESC";

    public QuotesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(author != null)
            initSpecificAuthorDataSet();
        else
            initDataSet(allQuotesQuery);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initFragment(allQuotesQuery);
    }

    public void initFragment(){
        initFragment(allQuotesQuery);
    }

    private void initFragment(String query) {
        quotes.clear();
        if(author != null)
            initSpecificAuthorDataSet();
        else
            initDataSet(query);
        refreshQuotesAdapter();
        setUpEmptyQuotesText(getView());
    }

    private void setUpEmptyQuotesText(View view) {
        TextView textView = (TextView) view.findViewById(R.id.quotes_empty_text);
        int visibility = quotes.isEmpty() ? View.VISIBLE : View.INVISIBLE;
        textView.setVisibility(visibility);
    }

    private void refreshQuotesAdapter() {
        quotesAdapter = new QuotesAdapter(quotes, author == null);
        recyclerView.setAdapter(quotesAdapter);
        quotesAdapter.notifyDataSetChanged();
    }

    private void initDataSet(String query) {
        quotes = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DatabaseHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor quotesCursor = db.rawQuery(query, null);
          
            if (quotesCursor.moveToFirst()){
                do{
                    authorId = quotesCursor.getInt(0);
                    Cursor authorCursor = db.query("Authors", new String[]{"FirstName", "LastName"},
                            "_id = ?", new String[]{String.valueOf(authorId)}, null, null, null);
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
            SQLiteOpenHelper dbHelper = new DatabaseHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor authorCursor = db.rawQuery("SELECT FirstName, LastName FROM Authors WHERE _id = ?",
                    new String[]{String.valueOf(authorId)});
            if(authorCursor.moveToFirst()){
                author = new Author(authorCursor.getString(0), authorCursor.getString(1));
            }

            Cursor quotesCursor = db.rawQuery(
                    "SELECT Author_id, Content, Favorite FROM Quotes WHERE Author_id = ? ORDER BY Favorite DESC",
                    new String[]{String.valueOf(authorId)});
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
            SQLiteOpenHelper dbHelper = new DatabaseHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT FirstName, LastName, Content, Favorite " +
                    "FROM Quotes INNER JOIN Authors ON Quotes.Author_id == Authors._id " +
                    "WHERE Content LIKE '%" + searchQuery + "%' OR FirstName LIKE '%" +
                    searchQuery + "%' OR LastName LIKE '%" + searchQuery + "%' " +
                    "ORDER BY Favorite DESC", null);
            if (cursor.moveToFirst()){
                do{
                    Author author = new Author(cursor.getString(0), cursor.getString(1));
                    Quote quote = new Quote(author, cursor.getString(2), cursor.getInt(3) == 1);
                    quotes.add(quote);
                } while(cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e){
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        refreshQuotesAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotes, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        quotesAdapter = new QuotesAdapter(quotes, author == null);
        setUpRecyclerView();
        setUpEmptyQuotesText(rootView);
        return rootView;
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(quotesAdapter);
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
