package com.example.personalfinanceplanner;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Expense.class}, version = 11) //change the version with any changes to the schema of the database
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDAO userDao();

    private static volatile AppDatabase dbINSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS); //allows for execution of database tasks on background thread, to avoid UI disruption

    static AppDatabase getDatabase(final Context context) {
        if (dbINSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (dbINSTANCE == null) {
                    dbINSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user_database").addCallback(sRoomDatabaseCallback).allowMainThreadQueries().fallbackToDestructiveMigration().build();
                }
            }
        }
        return dbINSTANCE;
    }

    //used to create the database for the first time
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}

/* Notes about this database implementation:

- You annotate the class to be a Room database with @Database and use the annotation parameters to declare the entities that belong in the database and set the version number. Each entity corresponds to a table that will be created in the database. Database migrations are beyond the scope of this codelab, so we set exportSchema to false here to avoid a build warning. In a real app, you should consider setting a directory for Room to use to export the schema so you can check the current schema into your version control system.

- This defines a singleton structure RoomDatabase, to prevent having multiple instances of the database opened at the same time.

- getDatabase returns the singleton. It'll create the database the first time it's accessed,
using Room's database builder to create a RoomDatabase object in the application context from the AppDatabase class and names it "user_database".

- Uses an ExecutorService with a fixed thread pool to run database operations asynchronously on a background thread.
 */
