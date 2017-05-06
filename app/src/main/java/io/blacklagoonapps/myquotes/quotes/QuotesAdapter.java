package io.blacklagoonapps.myquotes.quotes;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.database.DatabaseHelper;
import io.blacklagoonapps.myquotes.model.Author;
import io.blacklagoonapps.myquotes.model.Quote;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.Holder> {

    // Needed for startActivityForResult. Alternatively could be used given context in holder, but the question is, if it is always an instance of activity?
    private Activity mActivity;
    
    private Cursor mCursor;
    private Preferences preferences;

    private int contentColumnIndex;
    private int favoriteColumnIndex;
    private int firstNameColumnIndex;
    private int lastNameColumnIndex;

    public QuotesAdapter(Activity activity, Preferences preferences){
        this.preferences = preferences;
        this.mActivity = activity;
        changeDataSet(activity);
    }

    public QuotesAdapter(Activity activity, Preferences preferences, long authorId){
        this.preferences = preferences;
        this.mActivity = activity;
        changeDataSet(activity, authorId);
    }

    public void changeDataSet(Activity activity){
        this.mActivity = activity;
        DatabaseHelper databaseHelper = new DatabaseHelper(activity);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = databaseHelper.getQuotesWithAuthorsCursor(db);

        updateCursor(cursor);
    }

    public void changeDataSet(Activity activity, long authorId){
        this.mActivity = activity;
        DatabaseHelper databaseHelper = new DatabaseHelper(activity);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = databaseHelper.getSingleAuthorQuotesCursor(db, authorId);

        updateCursor(cursor);
    }

    public void changeDataSet(Activity activity, String searchWords){
        this.mActivity = activity;
        DatabaseHelper databaseHelper = new DatabaseHelper(activity);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = databaseHelper.getQuotesWithAuthorsContainingWords(db, searchWords);

        updateCursor(cursor);
    }

    private void updateCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return;
        }

        if(mCursor != null){
            Cursor old = mCursor;
            mCursor = cursor;
            notifyDataSetChanged();
            old.close();
        }
        else
            mCursor = cursor;

        contentColumnIndex = mCursor.getColumnIndex(Quote.Columns.CONTENT);
        favoriteColumnIndex = mCursor.getColumnIndex(Quote.Columns.FAVORITE);
        firstNameColumnIndex = mCursor.getColumnIndex(Author.Columns.FIRST_NAME);
        lastNameColumnIndex = mCursor.getColumnIndex(Author.Columns.LAST_NAME);
    }

    @Override
    public QuotesAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_quote, parent, false);

        return new QuotesAdapter.Holder(itemView);
    }

    @Override
    public void onBindViewHolder(QuotesAdapter.Holder holder, int position) {
        if(mCursor.moveToPosition(position)){
            final String content = mCursor.getString(contentColumnIndex);
            final String firstName = mCursor.getString(firstNameColumnIndex);
            final String lastName = mCursor.getString(lastNameColumnIndex);
            final boolean favorite = mCursor.getInt(favoriteColumnIndex) != 0;

            holder.content.setText(content);

            if(preferences.showAuthor) {
                if (preferences.showFirstNameFirst)
                    holder.author.setText(firstName + " " + lastName);
                else
                    holder.author.setText(lastName + " " + firstName);
            }
            else
                holder.author.setVisibility(View.GONE);

            if(preferences.showFavoriteStar){
                if(favorite)
                    holder.favorite.setImageResource(R.drawable.full_star);
                else
                    holder.favorite.setImageResource(R.drawable.empty_star);
            }
            else
                holder.favorite.setVisibility(View.GONE);

            if(preferences.listenToClickEvents){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openEditQuoteActivity(content, favorite, firstName, lastName);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v){
                        openShareCopyDialog(content, firstName, lastName);
                        return true;
                    }
                });
            }
        }
    }

    private void openEditQuoteActivity(String content, boolean favorite, String firstName, String lastName){
        Intent intent = new Intent(mActivity, EditQuoteActivity.class);
        intent.putExtra(EditQuoteActivity.QUOTE_CONTENT, content);
        intent.putExtra(EditQuoteActivity.IS_FAVORITE, favorite);
        intent.putExtra(EditQuoteActivity.AUTHOR_FIRST_NAME, firstName);
        intent.putExtra(EditQuoteActivity.AUTHOR_LAST_NAME, lastName);

        mActivity.startActivityForResult(intent, 1);
    }

    private void openShareCopyDialog(String content, String firstName, String lastName){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        final String fullQuote = prepareFullQuote(content, firstName, lastName);

        builder.setTitle(fullQuote)
                .setItems(R.array.share_copy, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                sharePlainText(fullQuote);
                                break;
                            case 1:
                                copyToClipboard(fullQuote);
                                break;
                        }
                    }
                });

        builder.create().show();
    }

    private void sharePlainText(String messageText){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, messageText);
        Intent chosenIntent = Intent.createChooser(intent, mActivity.getString(R.string.share_quote));
        mActivity.startActivity(chosenIntent);
    }

    private void copyToClipboard(String messageText){
        ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(mActivity.getString(R.string.quote_copied), messageText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mActivity, mActivity.getString(R.string.quote_copied), Toast.LENGTH_SHORT).show();
    }

    /*
        "content" firstName lastName
        String concatenation used intentionally
    */
    private String prepareFullQuote(String content, String firstName, String lastName){
        return "\"" +
                content +
                "\"" +
                " " +
                firstName +
                " " +
                lastName;
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Nullable
    public Author getAuthorAt(int position){
        Author author = null;
        if(mCursor.moveToPosition(position)){
            author = new Author(mCursor.getString(firstNameColumnIndex), mCursor.getString(lastNameColumnIndex));
        }
        return author;
    }

    static class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.quote_content) TextView content;
        @BindView(R.id.author_of_quote) TextView author;
        @BindView(R.id.is_favorite) ImageView favorite;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // "Dumb data holder" ;)
    public static class Preferences {

        public Preferences(boolean showAuthor, boolean showFirstNameFirst, boolean showFavoriteStar, boolean listenToClickEvents){
            this.showAuthor = showAuthor;
            this.showFirstNameFirst = showFirstNameFirst;
            this.showFavoriteStar = showFavoriteStar;
            this.listenToClickEvents = listenToClickEvents;
        }

        public boolean showAuthor;
        public boolean showFirstNameFirst;
        public boolean showFavoriteStar;
        public boolean listenToClickEvents;
    }
}
