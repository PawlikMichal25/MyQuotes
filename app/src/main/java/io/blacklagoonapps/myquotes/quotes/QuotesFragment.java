package io.blacklagoonapps.myquotes.quotes;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.blacklagoonapps.myquotes.R;
import io.blacklagoonapps.myquotes.model.Author;

public class QuotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuotesAdapter quotesAdapter;
    private long authorId = -1;

    public QuotesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotes, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview_quotes);

        setUpQuotesAdapter();
        setUpRecyclerView();
        setUpEmptyQuotesView(rootView);

        return rootView;
    }

    private void setUpQuotesAdapter() {
        QuotesAdapter.Preferences preferences = new QuotesAdapter.Preferences(
                authorId == -1,
                PreferenceManager.getDefaultSharedPreferences(getContext()).
                        getBoolean(getContext().getString(R.string.pref_names_display), true),
                true);

        if (authorId == -1)
            quotesAdapter = new QuotesAdapter(getActivity(), preferences);
        else
            quotesAdapter = new QuotesAdapter(getActivity(), preferences, authorId);
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(quotesAdapter);
    }

    private void setUpEmptyQuotesView(View rootView) {
        View quotesEmpty = rootView.findViewById(R.id.linearlayout_quotes_empty);
        int visibility;

        if (quotesAdapter.getItemCount() == 0) {
            visibility = View.VISIBLE;
            setUpAddQuoteText(rootView);
        } else
            visibility = View.INVISIBLE;
        quotesEmpty.setVisibility(visibility);
    }

    private void setUpAddQuoteText(View rootView) {
        TextView addQuote = rootView.findViewById(R.id.textview_quotes_add_quote);
        if (authorId == -1)
            addQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddQuoteActivity.class);
                    startActivity(intent);
                }
            });
        else
            addQuote.setVisibility(View.GONE);
    }

    public void restart() {
        setUpQuotesAdapter();
        setUpRecyclerView();
        setUpEmptyQuotesView(getView());
    }

    public void findQuotesWithAuthorsContainingWords(String searchWords) {
        quotesAdapter.changeDataSet(getActivity(), searchWords);
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    @Nullable
    public Author getAuthorAt(int position) {
        return quotesAdapter.getAuthorAt(position);
    }
}
