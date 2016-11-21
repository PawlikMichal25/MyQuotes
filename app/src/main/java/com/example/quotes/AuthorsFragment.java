package com.example.quotes;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class AuthorsFragment extends ListFragment {

    public AuthorsFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authors, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO Change data source to query
        String[] datasource={"Piotr", "Michal", "Ela", "Ola", "Ula", "Ala", "Adam", "Juliusz", "Julian", "Tomasz", "Henryk"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, datasource);

        setListAdapter(adapter);
    }
}
