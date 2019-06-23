package io.blacklagoonapps.myquotes.authors;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.ListFragment;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.database.DatabaseHelper;
import io.blacklagoonapps.myquotes.model.Author;

public class AuthorsFragment extends ListFragment {

    private SQLiteDatabase db;
    private Cursor cursor;
    private boolean firstNameFirst;

    public AuthorsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.firstNameFirst = PreferenceManager.getDefaultSharedPreferences(getContext()).
                getBoolean(getString(R.string.pref_names_display), true);
        return inflater.inflate(R.layout.fragment_authors, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.linearlayout_authors_empty).setVisibility(View.GONE);
        initFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setEmptyView(getView().findViewById(R.id.linearlayout_authors_empty));
    }

    public void initFragment() {
        this.firstNameFirst = PreferenceManager.getDefaultSharedPreferences(getContext()).
                getBoolean(getContext().getString(R.string.pref_names_display), true);
        createCursor();
        setUpAdapter();
    }

    private void createCursor() {
        try {
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(getActivity());
            db = databaseHelper.getReadableDatabase();
            String orderBy;
            if (firstNameFirst)
                orderBy = Author.Columns.FIRST_NAME + " || " + Author.Columns.LAST_NAME + " COLLATE NOCASE";
            else
                orderBy = Author.Columns.LAST_NAME + " || " + Author.Columns.FIRST_NAME + " COLLATE NOCASE";

            cursor = db.query(Author.TABLE_NAME,
                    new String[]{Author.Columns.ID, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME},
                    null, null, null, null, orderBy);

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
                if (cursor.getString(2).isEmpty())
                    name.setText(cursor.getString(1));
                else {
                    if (firstNameFirst)
                        name.setText(cursor.getString(1) + " " + cursor.getString(2));
                    else
                        name.setText(cursor.getString(2) + " " + cursor.getString(1));
                }
                return true;
            }
        });

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id) {
        Intent intent = new Intent(getActivity(), AuthorsActivity.class);
        intent.putExtra(AuthorsActivity.AUTHOR_ID, id);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
