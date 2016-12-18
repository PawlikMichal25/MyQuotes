package com.example.quotes;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.quotes.model.Author;
import com.example.quotes.model.Quote;

import java.util.ArrayList;
import java.util.List;


public class QuotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuotesAdapter quotesAdapter;
    private List<Quote> quotes;

    public QuotesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataSet();
    }

    private void initDataSet() {
        quotes = new ArrayList<Quote>();
        try {
            SQLiteOpenHelper dbHelper = new QuotesDatabaseHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT author_id, content, favorite FROM Quotes", null);
            if (cursor.moveToFirst()){
                do{
                    int authorID = cursor.getInt(0);
                    Cursor authorCursor = db.query("Authors", new String[]{"name", "surname"},
                            "_id = ?", new String[]{String.valueOf(authorID)}, null, null, null);
                    if (authorCursor.moveToFirst()){
                        Author author = new Author(authorCursor.getString(0),
                                authorCursor.getString(1));
                        Quote quote = new Quote(author, cursor.getString(1), cursor.getInt(2) == 1);
                        quotes.add(quote);
                    }
                    authorCursor.close();
                } while(cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotes, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        quotesAdapter = new QuotesAdapter(quotes);
        setUpRecyclerView();
        return rootView;
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(quotesAdapter);
    }

}
