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
import com.example.quotes.authors.AuthorsActivity;
import com.example.quotes.database.DatabaseHelper;

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
        initDataSet();
    }

    private void initDataSet() {
        createCursor();
        setUpAdapter();
    }

    private void createCursor() {
        try {
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(getActivity());
            db = databaseHelper.getReadableDatabase();

            cursor = db.query("Authors",
                    new String[]{"_id", "FirstName", "LastName"},
                    null, null, null, null, null);

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setUpAdapter() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{"FirstName", "LastName"},
                new int[]{android.R.id.text1},
                0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView name = (TextView) view;
                name.setText(cursor.getString(2) + " " + cursor.getString(1));
                return true;
            }
        });

        setListAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initDataSet();
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id){
        Intent intent = new Intent(getActivity(), AuthorsActivity.class);
        // TODO Split author's first name and last name in the view.
        intent.putExtra(AuthorsActivity.AUTHOR_ID, id);
        intent.putExtra(AuthorsActivity.AUTHOR_NAME, ((TextView)itemView).getText());
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
