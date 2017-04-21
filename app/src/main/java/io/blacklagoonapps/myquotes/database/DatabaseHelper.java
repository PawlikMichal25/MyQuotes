package io.blacklagoonapps.myquotes.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.blacklagoonapps.myquotes.model.Author;
import io.blacklagoonapps.myquotes.model.Quote;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Quotes";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL);",
                Author.TABLE_NAME, Author.Columns.ID, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME));
        db.execSQL(String.format("CREATE TABLE %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s INTEGER NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s BOOLEAN NOT NULL," +
                "FOREIGN KEY(%s) REFERENCES %s(%s));",
                Quote.TABLE_NAME, Quote.Columns.ID, Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT,
                Quote.Columns.FAVORITE, Quote.Columns.AUTHOR_ID, Author.TABLE_NAME, Author.Columns.ID));

        setUpSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void setUpSampleData(SQLiteDatabase db) {

        long id8 = insertAuthor(db, "Janis", "Joplin");
        insertQuote(db, id8, "I\'d trade all my tomorrows for a single yesterday.", true);

        long id2 = insertAuthor(db, "Albert", "Einstein");
        insertQuote(db, id2, "A person who never made a mistake never tried anything new.", true);

        long id11 = insertAuthor(db, "Mark", "Twain");
        insertQuote(db, id11, "The man who does not read has no advantage over the man who cannot read.", false);

        long id4 = insertAuthor(db, "Abraham", "Lincoln");
        insertQuote(db, id4, "I\'m a slow walker, but I never walk back.", false);

        long id1 = insertAuthor(db, "Bob", "Marley");
        insertQuote(db, id1, "The truth is, everyone is going to hurt you. You just got to find the ones worth suffering for.", false);

        long id6 = insertAuthor(db, "Oprah", "Winfrey");
        insertQuote(db, id6, "The biggest adventure you can ever take is to live the life of your dreams.", false);

        insertQuote(db, id11, "Whenever you find yourself on the side of the majority, it is the time to pause and reflect.", false);
        insertQuote(db, id11, "Do the thing you fear most and the death of fear is certain.", false);
        insertQuote(db, id11, "There are lies, damned lies and statistics.", false);

        insertQuote(db, id2, "Intellectuals solve problems, geniuses prevent them.", false);
        insertQuote(db, id2, "Imagination is everything. It is the preview of life\'s coming attractions.", false);

        long id3 = insertAuthor(db, "Jimi", "Hendrix");
        insertQuote(db, id3, "Knowledge speaks, but wisdom listens.", false);

        long id7 = insertAuthor(db, "Audrey", "Hepburn");
        insertQuote(db, id7, "If I get married, I want to be very married.", false);

        long id5 = insertAuthor(db, "John", "Lennon");
        insertQuote(db, id5, "Time you enjoy wasting, was not wasted.", false);

        long id9 = insertAuthor(db, "Alan", "Watts");
        insertQuote(db, id9, "The only way to make sense out of change is to plunge into it, move with it, and join the dance.", false);

        long id10 = insertAuthor(db, "Buddha", "");
        insertQuote(db, id10, "You yourself, as much as anybody in the entire universe, deserve your love and affection.", false);

    }

    public long insertAuthor(SQLiteDatabase db, String firstName, String lastName){
        ContentValues values = new ContentValues();
        values.put(Author.Columns.FIRST_NAME, firstName);
        values.put(Author.Columns.LAST_NAME, lastName);
        return db.insert(Author.TABLE_NAME, null, values);
    }

    private long insertQuote(SQLiteDatabase db, long authorId, String content, boolean isFavorite){
        ContentValues values = new ContentValues();
        values.put(Quote.Columns.AUTHOR_ID, authorId);
        values.put(Quote.Columns.CONTENT, content);
        values.put(Quote.Columns.FAVORITE, isFavorite);
        return db.insert(Quote.TABLE_NAME, null, values);
    }

    public long findAuthorId(SQLiteDatabase db, String authorFirstName, String authorLastName){
        String query = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?",
                Author.Columns.ID, Author.TABLE_NAME, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME);
        Cursor cursor = db.rawQuery(query, new String[]{authorFirstName, authorLastName});
        if(cursor.moveToFirst()) {
            long result =  cursor.getLong(0);
            cursor.close();
            return result;
        }
        return -1;
    }

    public long findQuoteId(SQLiteDatabase db, long authorId, String content, boolean favorite){
        String query = String.format("SELECT %s, %s FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                Quote.Columns.ID, Quote.Columns.CONTENT, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID,
                Quote.Columns.CONTENT, Quote.Columns.FAVORITE);
        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(authorId), content, String.valueOf(favorite ? 1 : 0)});
        if(cursor.moveToFirst()){
            long result =  cursor.getLong(0);
            cursor.close();
            return result;
        }
        return -1;
    }

    public Author findAuthorById(SQLiteDatabase db, long authorId){
        String query = String.format("SELECT %s, %s FROM %s WHERE %s = %s",
                Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME,
                Author.TABLE_NAME,
                Author.Columns.ID, authorId);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            Author author = new Author(cursor.getString(0), cursor.getString(1));
            cursor.close();
            return author;
        }
        else
            return null;
    }

    /**
     * Returns number of quotes with given Author_id
     */
    public int countQuotes(SQLiteDatabase db, long authorId){
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?",
                Quote.Columns.ID, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(authorId)});
        if(cursor.moveToFirst()){
            int result =  cursor.getInt(0);
            cursor.close();
            return result;
        }
        return 0;
    }

    public void addQuote(SQLiteDatabase db, String authorFirstName, String authorLastName, String quoteContent,
                         boolean isFavorite){
        long authorId;
        // Check if author already exists:
        Cursor cursor = db.query(Author.TABLE_NAME,
                new String[]{Author.Columns.ID},
                String.format("%s = ? AND %s = ?", Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME),
                new String[]{authorFirstName, authorLastName},
                null, null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())        // Author exists in the database
            authorId = cursor.getLong(0);
        else
            authorId = insertAuthor(db, authorFirstName, authorLastName);           // Create new author
        insertQuote(db, authorId, quoteContent, isFavorite);

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    public int deleteQuote(SQLiteDatabase db, long quoteId){
        String query = String.format("SELECT %s FROM %s WHERE %s = ?", Quote.Columns.AUTHOR_ID, Quote.TABLE_NAME,
                Quote.Columns.ID);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(quoteId)});

        // Check if this author has other quotes
        if(cursor.moveToFirst()){
            long authorId =  cursor.getLong(0);
            String countQuery = String.format("SELECT COUNT(%s) AS Ids FROM %s WHERE %s = ?",
                    Quote.Columns.ID, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID);
            Cursor othersCursor = db.rawQuery(countQuery, new String[]{String.valueOf(authorId)});

            if(othersCursor.moveToFirst() && othersCursor.getInt(0) < 2)
                db.delete(Author.TABLE_NAME, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
            othersCursor.close();
        }
        cursor.close();
        return db.delete(Quote.TABLE_NAME, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes existing quote, identified by quoteId with given values.
     */
    public int editQuote(SQLiteDatabase db, long quoteId, long authorId, String content, boolean favorite){
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, authorId);
        cv.put(Quote.Columns.CONTENT, content);
        cv.put(Quote.Columns.FAVORITE, favorite);
        return db.update(Quote.TABLE_NAME, cv, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes Author_id of quote with given quoteId
     */
    public void editSingleQuotesAuthor(SQLiteDatabase db, long quoteId, long authorId){
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, authorId);
        db.update(Quote.TABLE_NAME, cv, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes Author_id of quotes with given oldAuthorId to newAuthorId and deletes author with oldAuthorId if there
     * are no remaining quotes by him/her.
     */
    public void editQuotesAuthor(SQLiteDatabase db, long oldAuthorId, long newAuthorId){
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, newAuthorId);
        db.update(Quote.TABLE_NAME, cv, Quote.Columns.AUTHOR_ID + "=?", new String[]{String.valueOf(oldAuthorId)});
        if (countQuotes(db, oldAuthorId) == 0) deleteAuthor(db, oldAuthorId);
    }

    /**
     * Changes existing author, identified by authorId with given values.
     */
    public int editAuthor(SQLiteDatabase db, long authorId, String firstName, String lastName){
        ContentValues cv = new ContentValues();
        cv.put(Author.Columns.FIRST_NAME, firstName);
        cv.put(Author.Columns.LAST_NAME, lastName);
        return db.update(Author.TABLE_NAME, cv, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
    }

    public int deleteAuthor(SQLiteDatabase db, long authorId){
        return db.delete(Author.TABLE_NAME, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
    }

    public Cursor getQuotesWithAuthorsCursor(SQLiteDatabase db){
        String query = String.format("SELECT %s, %s, %s, %s FROM %s AS Q INNER JOIN %s as A ON Q.%s = A.%s ORDER BY %s DESC",
                Quote.Columns.CONTENT, Quote.Columns.FAVORITE, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME,
                Quote.TABLE_NAME, Author.TABLE_NAME,
                Quote.Columns.AUTHOR_ID, Author.Columns.ID,
                Quote.Columns.FAVORITE);
        return db.rawQuery(query, null);
    }

    public Cursor getSingleAuthorQuotesCursor(SQLiteDatabase db, long authorId){
        String query = String.format("SELECT %s, %s, %s, %s FROM %s as Q INNER JOIN %s as A ON Q.%s = A.%s WHERE %s = %s ORDER BY %s DESC",
                Quote.Columns.CONTENT, Quote.Columns.FAVORITE, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME,
                Quote.TABLE_NAME, Author.TABLE_NAME,
                Quote.Columns.AUTHOR_ID, Author.Columns.ID,
                Quote.Columns.AUTHOR_ID, authorId,
                Quote.Columns.FAVORITE);
        return db.rawQuery(query, null);
    }

    /*
       Returns cursor with quotes (or their authors) comprising any of the given words
     */
    public Cursor getQuotesWithAuthorsContainingWords(SQLiteDatabase db, String searchWords){

        // Returns anything (.*) which consists of (w|w|w) words (\b - word boundaries); regex is case insensitive (?i)
        String regex = String.format("(?i).*\\b(%s)\\b.*", searchWords.trim().replaceAll("\\s", "|"));

        String query = String.format("SELECT %s, %s, %s, %s " +
                "FROM %s as Q INNER JOIN %s as A ON Q.%s == A.%s " +
                "WHERE %s REGEXP '%s' OR %s REGEXP '%s' OR %s REGEXP '%s' OR " +                   // Regex to match any words at any position
                "%s LIKE '%%%s%%' OR %s LIKE '%%%s%%' OR %s LIKE '%%%s%%' ORDER BY %s DESC",       // LIKE to match part of words
                Quote.Columns.CONTENT, Quote.Columns.FAVORITE, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME,
                Quote.TABLE_NAME, Author.TABLE_NAME,
                Quote.Columns.AUTHOR_ID, Author.Columns.ID,
                Quote.Columns.CONTENT, regex,
                Author.Columns.FIRST_NAME, regex,
                Author.Columns.LAST_NAME, regex,
                Quote.Columns.CONTENT, searchWords,
                Author.Columns.FIRST_NAME, searchWords,
                Author.Columns.LAST_NAME, searchWords,
                Quote.Columns.FAVORITE);

        return db.rawQuery(query, null);
    }
}