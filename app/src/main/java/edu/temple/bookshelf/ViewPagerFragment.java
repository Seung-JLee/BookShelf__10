package edu.temple.bookshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {
    private static final String BOOKLIST_KEY = "booklist";

    ViewPager viewPager;
    private ArrayList<String> bookList;
    BookListFragment.BookSelectedInterface parentActivity;


    public ViewPagerFragment() {}

    public static ViewPagerFragment newInstance(ArrayList<String> bookList) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(BOOKLIST_KEY, bookList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookList = getArguments().getStringArrayList(BOOKLIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);

        viewPager = v.findViewById(R.id.viewPager);

        viewPager.setAdapter(new BookFragmentAdapter(getChildFragmentManager(), bookList));

        return v;
    }

    class BookFragmentAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> bookList;

        public BookFragmentAdapter(FragmentManager fm, ArrayList<String> bookList) {
            super(fm);
            this.bookList = bookList;
        }

        @Override
        public Fragment getItem(int i) {
            return BookDetailsFragment.newInstance(bookList.get(i));
        }

        @Override
        public int getCount() {
            return bookList.size();
        }
    }

}
