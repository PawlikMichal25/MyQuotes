package com.example.quotes.authors;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quotes.R;
import com.example.quotes.database.DatabaseHelper;
import com.example.quotes.model.Author;

public class AuthorsFragment extends ListFragment {

    private SQLiteDatabase db;
    private Cursor cursor;

    public AuthorsFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authors, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.authors_empty).setVisibility(View.GONE);
        initFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setEmptyView(getView().findViewById(R.id.authors_empty));
    }

    public void initFragment() {
        createCursor();
        setUpAdapter();
    }

    private void createCursor() {
        try {
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(getActivity());
            db = databaseHelper.getReadableDatabase();

            cursor = db.query(Author.TABLE_NAME,
                    new String[]{Author.Columns.ID, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME},
                    null, null, null, null, Author.Columns.LAST_NAME + " || " + Author.Columns.FIRST_NAME + " COLLATE NOCASE");

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setUpAdapter() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME},
                new int[]{android.R.id.text1},
                0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView name = (TextView) view;
                if(cursor.getString(2).isEmpty())
                    name.setText(cursor.getString(1));
                else
                    name.setText(cursor.getString(2) + " " + cursor.getString(1));
                return true;
            }
        });

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id){
        Intent intent = new Intent(getActivity(), AuthorsActivity.class);
        intent.putExtra(AuthorsActivity.AUTHOR_ID, id);

        // Instead of one TextView and split there could be used eg. custom view with 2 TextViews, but I found it less handy nor convenient.
        String [] authorNames = ((TextView)itemView).getText().toString().split(" ", 2);
        intent.putExtra(AuthorsActivity.AUTHOR_FIRST_NAME, authorNames[0]);
        if(authorNames.length == 2)
            intent.putExtra(AuthorsActivity.AUTHOR_LAST_NAME, authorNames[1]);

        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
