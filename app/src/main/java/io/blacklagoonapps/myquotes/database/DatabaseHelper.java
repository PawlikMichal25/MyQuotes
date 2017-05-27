package io.blacklagoonapps.myquotes.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.blacklagoonapps.myquotes.model.Author;
import io.blacklagoonapps.myquotes.model.Quote;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Quotes";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAuthorTable(db);
        createQuoteTable(db);
        setUpSampleData(db);
    }

    private void createAuthorTable(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL);",
                Author.TABLE_NAME, Author.Columns.ID, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME));
    }

    private void createQuoteTable(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s));",
                Quote.TABLE_NAME, Quote.Columns.ID, Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT,
                Quote.Columns.AUTHOR_ID, Author.TABLE_NAME, Author.Columns.ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // With this update favorite column was removed
            // SQLite cannot drop columns :C
            String BACKUP_TABLE = "QuoteBackup";

            // Create backup table
            db.execSQL(String.format("CREATE TEMPORARY TABLE %s(" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s INTEGER NOT NULL, " +
                            "%s TEXT NOT NULL, " +
                            "FOREIGN KEY(%s) REFERENCES %s(%s));",
                    BACKUP_TABLE, Quote.Columns.ID, Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT,
                    Quote.Columns.AUTHOR_ID, Author.TABLE_NAME, Author.Columns.ID));

            // Copy data
            db.execSQL(String.format("INSERT INTO %s SELECT %s, %s, %s FROM %s",
                    BACKUP_TABLE, Quote.Columns.ID, Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT, Quote.TABLE_NAME));

            // Drop table Quote
            db.execSQL(String.format("DROP TABLE %s", Quote.TABLE_NAME));

            // Create table Quote
            createQuoteTable(db);

            // Copy back the data
            db.execSQL(String.format("INSERT INTO %s SELECT %s, %s, %s FROM %s",
                    Quote.TABLE_NAME, Quote.Columns.ID, Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT, BACKUP_TABLE));

            // Drop temporary table
            db.execSQL(String.format("DROP TABLE %s", BACKUP_TABLE));
        }
    }

    private void setUpSampleData(SQLiteDatabase db) {

        long id8 = insertAuthor(db, "Janis", "Joplin");
        insertQuote(db, id8, "I\'d trade all my tomorrows for a single yesterday.");

        long id2 = insertAuthor(db, "Albert", "Einstein");
        insertQuote(db, id2, "A person who never made a mistake never tried anything new.");

        long id11 = insertAuthor(db, "Mark", "Twain");
        insertQuote(db, id11, "The man who does not read has no advantage over the man who cannot read.");

        long id4 = insertAuthor(db, "Abraham", "Lincoln");
        insertQuote(db, id4, "I\'m a slow walker, but I never walk back.");

        long id1 = insertAuthor(db, "Bob", "Marley");
        insertQuote(db, id1, "The truth is, everyone is going to hurt you. You just got to find the ones worth suffering for.");

        long id6 = insertAuthor(db, "Oprah", "Winfrey");
        insertQuote(db, id6, "The biggest adventure you can ever take is to live the life of your dreams.");

        insertQuote(db, id11, "Whenever you find yourself on the side of the majority, it is the time to pause and reflect.");
        insertQuote(db, id11, "Do the thing you fear most and the death of fear is certain.");
        insertQuote(db, id11, "There are lies, damned lies and statistics.");

        insertQuote(db, id2, "Intellectuals solve problems, geniuses prevent them.");
        insertQuote(db, id2, "Imagination is everything. It is the preview of life\'s coming attractions.");

        long id3 = insertAuthor(db, "Jimi", "Hendrix");
        insertQuote(db, id3, "Knowledge speaks, but wisdom listens.");

        long id7 = insertAuthor(db, "Audrey", "Hepburn");
        insertQuote(db, id7, "If I get married, I want to be very married.");

        long id5 = insertAuthor(db, "John", "Lennon");
        insertQuote(db, id5, "Time you enjoy wasting, was not wasted.");

        long id9 = insertAuthor(db, "Alan", "Watts");
        insertQuote(db, id9, "The only way to make sense out of change is to plunge into it, move with it, and join the dance.");

        long id10 = insertAuthor(db, "Buddha", "");
        insertQuote(db, id10, "You yourself, as much as anybody in the entire universe, deserve your love and affection.");

    }

    private long insertQuote(SQLiteDatabase db, long authorId, String content) {
        ContentValues values = new ContentValues();
        values.put(Quote.Columns.AUTHOR_ID, authorId);
        values.put(Quote.Columns.CONTENT, content);
        return db.insert(Quote.TABLE_NAME, null, values);
    }

    private long insertAuthor(SQLiteDatabase db, String firstName, String lastName) {
        ContentValues values = new ContentValues();
        values.put(Author.Columns.FIRST_NAME, firstName);
        values.put(Author.Columns.LAST_NAME, lastName);
        return db.insert(Author.TABLE_NAME, null, values);
    }

    public long findQuoteId(SQLiteDatabase db, long authorId, String content) {
        String query = String.format("SELECT %s, %s FROM %s WHERE %s = ? AND %s = ?",
                Quote.Columns.ID, Quote.Columns.CONTENT, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID,
                Quote.Columns.CONTENT);
        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(authorId), content});
        if (cursor.moveToFirst()) {
            long result = cursor.getLong(0);
            cursor.close();
            return result;
        }
        return -1;
    }

    public long findAuthorId(SQLiteDatabase db, String firstName, String lastName) {
        String query = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?",
                Author.Columns.ID, Author.TABLE_NAME, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME);
        Cursor cursor = db.rawQuery(query, new String[]{firstName, lastName});
        if (cursor.moveToFirst()) {
            long result = cursor.getLong(0);
            cursor.close();
            return result;
        }
        return -1;
    }

    public long addQuote(SQLiteDatabase db, String authorFirstName, String authorLastName, String quoteContent) {
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

        if (cursor != null)
            cursor.close();

        return insertQuote(db, authorId, quoteContent);
    }

    public int editQuote(SQLiteDatabase db, long quoteId, long authorId, String content) {
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, authorId);
        cv.put(Quote.Columns.CONTENT, content);
        return db.update(Quote.TABLE_NAME, cv, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    public int deleteQuote(SQLiteDatabase db, long quoteId) {
        String query = String.format("SELECT %s FROM %s WHERE %s = ?", Quote.Columns.AUTHOR_ID, Quote.TABLE_NAME,
                Quote.Columns.ID);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(quoteId)});

        // Check if this author has other quotes
        if (cursor.moveToFirst()) {
            long authorId = cursor.getLong(0);
            String countQuery = String.format("SELECT COUNT(%s) AS Ids FROM %s WHERE %s = ?",
                    Quote.Columns.ID, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID);
            Cursor othersCursor = db.rawQuery(countQuery, new String[]{String.valueOf(authorId)});

            if (othersCursor.moveToFirst() && othersCursor.getInt(0) < 2)
                db.delete(Author.TABLE_NAME, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
            othersCursor.close();
        }
        cursor.close();
        return db.delete(Quote.TABLE_NAME, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    public long addAuthor(SQLiteDatabase db, String firstName, String lastName) {
        return insertAuthor(db, firstName, lastName);
    }

    public int editAuthor(SQLiteDatabase db, long authorId, String firstName, String lastName) {
        ContentValues cv = new ContentValues();
        cv.put(Author.Columns.FIRST_NAME, firstName);
        cv.put(Author.Columns.LAST_NAME, lastName);
        return db.update(Author.TABLE_NAME, cv, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
    }

    public int deleteAuthor(SQLiteDatabase db, long authorId) {
        return db.delete(Author.TABLE_NAME, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
    }

    /**
     * Returns number of quotes with given Author_id
     */
    public int countQuotes(SQLiteDatabase db, long authorId) {
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?",
                Quote.Columns.ID, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(authorId)});
        if (cursor.moveToFirst()) {
            int result = cursor.getInt(0);
            cursor.close();
            return result;
        }
        return 0;
    }

    /**
     * Changes Author_id of quote with given quoteId
     */
    public void updateQuote(SQLiteDatabase db, long quoteId, long authorId) {
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, authorId);
        db.update(Quote.TABLE_NAME, cv, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes Author_id of quotes with given oldAuthorId to newAuthorId and deletes author with oldAuthorId if there
     * are no remaining quotes by him/her.
     */
    public void editQuotesAuthor(SQLiteDatabase db, long oldAuthorId, long newAuthorId) {
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, newAuthorId);
        db.update(Quote.TABLE_NAME, cv, Quote.Columns.AUTHOR_ID + "=?", new String[]{String.valueOf(oldAuthorId)});
        if (countQuotes(db, oldAuthorId) == 0) deleteAuthor(db, oldAuthorId);
    }

    public Cursor getQuotesWithAuthorsCursor(SQLiteDatabase db) {
        String query = String.format("SELECT %s, %s, %s FROM %s AS Q INNER JOIN %s as A ON Q.%s = A.%s",
                Quote.Columns.CONTENT, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME,
                Quote.TABLE_NAME, Author.TABLE_NAME,
                Quote.Columns.AUTHOR_ID, Author.Columns.ID);
        return db.rawQuery(query, null);
    }

    public Cursor getSingleAuthorQuotesCursor(SQLiteDatabase db, long authorId) {
        String query = String.format("SELECT %s, %s, %s FROM %s as Q INNER JOIN %s as A ON Q.%s = A.%s WHERE %s = %s",
                Quote.Columns.CONTENT, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME,
                Quote.TABLE_NAME, Author.TABLE_NAME,
                Quote.Columns.AUTHOR_ID, Author.Columns.ID,
                Quote.Columns.AUTHOR_ID, authorId);
        return db.rawQuery(query, null);
    }

    /*
       Returns cursor with quotes (or their authors) comprising any of the given words (or their parts)
       It was hard ("impossible"?) to do it with regex due to conflicts of escape characters:
       ' in SQL would end up statement, but replacing it with '' would make regex search for '' not '
       I am using many LIKE statements to support searching words in different order.
     */
    public Cursor getQuotesWithAuthorsContainingWords(SQLiteDatabase db, String searchWords) {
        final String ALL_COLUMNS = "ALL_COLUMNS";
        final String AND = " AND ";

        searchWords = DatabaseUtils.sqlEscapeString(searchWords.trim());        // trim and escape special chars eg. ' ?

        searchWords = searchWords.substring(1, searchWords.length() - 1);

        String [] words = searchWords.split(" ");
        // ALL_COLUMNS will be the name of concatenated column in query which we want to process.
        // Example: ALL_COLUMNS LIKE '%Albert%' AND ALL_COLUMNS LIKE '%Eins%' AND
        StringBuilder like = new StringBuilder();
        for(String word: words)
            like.append(ALL_COLUMNS).append(" LIKE '%").append(word).append("%'").append(AND);


        like.delete(like.length() - AND.length(), like.length());   // Remove last AND from the end of statement

        String query = String.format("SELECT %s, %s, %s, " +
                        "%s || ' ' || %s || ' ' || %s as %s " +
                        "FROM %s as Q INNER JOIN %s as A ON Q.%s == A.%s " +
                        "WHERE %s",
                Quote.Columns.CONTENT, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME,
                Quote.Columns.CONTENT, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME, ALL_COLUMNS,
                Quote.TABLE_NAME, Author.TABLE_NAME,
                Quote.Columns.AUTHOR_ID, Author.Columns.ID,
                like);

        return db.rawQuery(query, null);
    }
}