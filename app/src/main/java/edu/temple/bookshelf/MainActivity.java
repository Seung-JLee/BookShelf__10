package edu.temple.bookshelf;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    FragmentManager fm;
    BookDetailsFragment bookDetailsFragment;
    boolean onePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();

        onePane = findViewById(R.id.container_2) == null;

        ArrayList<String> bookList = new ArrayList<>();
        Collections.addAll(bookList, getResources().getStringArray(R.array.book_titles));

        // Check for fragments in both containers

        Fragment current1 = fm.findFragmentById(R.id.container_1);
        Fragment current2 = fm.findFragmentById(R.id.container_2);


        // If there are no fragments at all (first time starting activity)
        if (current1 == null) {
            if (onePane) {
                fm.beginTransaction()
                        .add(R.id.container_1, ViewPagerFragment.newInstance(bookList))
                        .commit();
            } else {
                bookDetailsFragment = new BookDetailsFragment();
                fm.beginTransaction()
                        .add(R.id.container_1, BookListFragment.newInstance(bookList))
                        .add(R.id.container_2, bookDetailsFragment)
                        .commit();
            }
        } else {
            // Fragments already exist (activity was restarted)
            if (onePane) {
                if (current1 instanceof BookListFragment) {
                    // If we have the wrong fragment for this configuration, remove it and add the correct one
                    fm.beginTransaction()
                            .remove(current1)
                            .add(R.id.container_1, ViewPagerFragment.newInstance(bookList))
                            .commit();
                }
            } else {
                if (current1 instanceof ViewPagerFragment) {
                    fm.beginTransaction()
                            .remove(current1)
                            .add(R.id.container_1, BookListFragment.newInstance(bookList))
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
    }

    @Override
    public void bookSelected(String title) {
        if (bookDetailsFragment != null)
            bookDetailsFragment.changeBook(title);
    }
}
