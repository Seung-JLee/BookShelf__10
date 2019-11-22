package edu.temple.bookshelf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    FragmentManager fm;
    BookDetailsFragment bookDetailsFragment;
    boolean onePane;
    Library library;
    Fragment current1, current2;

    private final String SEARCH_URL = "https://kamorris.com/lab/audlib/booksearch.php?search=";

    Handler bookHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try {
                library.clear();
                JSONArray booksArray = new JSONArray((String) message.obj);
                for (int i = 0; i < booksArray.length(); i++) {
                    library.addBook(new Book(booksArray.getJSONObject(i)));
                }

                if(fm.findFragmentById(R.id.container_1) == null)
                    setUpDisplay();
                else
                    updateBooks();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        library = new Library();

        // Check for fragments in both containers
        current1 = fm.findFragmentById(R.id.container_1);
        current2 = fm.findFragmentById(R.id.container_2);

        onePane = findViewById(R.id.container_2) == null;

        if (current1 == null) {
            fetchBooks(null);
        } else {
            updateDisplay();
        }

        findViewById(R.id.searchButton).setOnClickListener(v -> fetchBooks(((EditText) findViewById(R.id.searchBox)).getText().toString()));
    }

    private void setUpDisplay() {
        // If there are no fragments at all (first time starting activity)

        if (onePane) {
            current1 = ViewPagerFragment.newInstance(library);
            fm.beginTransaction()
                    .add(R.id.container_1, current1)
                    .commit();
        } else {
            current1 = BookListFragment.newInstance(library);
            bookDetailsFragment = new BookDetailsFragment();
            fm.beginTransaction()
                    .add(R.id.container_1, current1)
                    .add(R.id.container_2, bookDetailsFragment)
                    .commit();
        }

    }

    private void updateDisplay () {
        Fragment tmpFragment = current1;;
        library = ((Displayable) current1).getBooks();
        if (onePane) {
            if (current1 instanceof BookListFragment) {
                current1 = ViewPagerFragment.newInstance(library);
                // If we have the wrong fragment for this configuration, remove it and add the correct one
                fm.beginTransaction()
                        .remove(tmpFragment)
                        .add(R.id.container_1, current1)
                        .commit();
            }
        } else {
            if (current1 instanceof ViewPagerFragment) {
                current1 = BookListFragment.newInstance(library);
                fm.beginTransaction()
                        .remove(tmpFragment)
                        .add(R.id.container_1, current1)
                        .commit();
            }
            if (current2 instanceof BookDetailsFragment)
                bookDetailsFragment = (BookDetailsFragment) current2;
            else {
                bookDetailsFragment = new BookDetailsFragment();
                fm
                        .beginTransaction()
                        .add(R.id.container_2, bookDetailsFragment)
                        .commit();
            }
        }

        bookDetailsFragment = (BookDetailsFragment) current2;
    }

    private void updateBooks() {
        ((Displayable) current1).setBooks(library);
    }

    @Override
    public void bookSelected(Book book) {
        if (bookDetailsFragment != null)
            bookDetailsFragment.changeBook(book);
    }

    private boolean isNetworkActive() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void fetchBooks(final String searchString) {
        new Thread() {
            @Override
            public void run() {
                if (isNetworkActive()) {

                    URL url;

                    try {
                        url = new URL(SEARCH_URL + (searchString != null ? searchString : ""));
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        url.openStream()));

                        StringBuilder response = new StringBuilder();
                        String tmpResponse;

                        while ((tmpResponse = reader.readLine()) != null) {
                            response.append(tmpResponse);
                        }

                        Message msg = Message.obtain();

                        msg.obj = response.toString();

                        Log.d("Books RECEIVED", response.toString());

                        bookHandler.sendMessage(msg);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("Network Error", "Cannot download books");
                }
            }
        }.start();
    }

}
