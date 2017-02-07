package com.example.quotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quotes.model.Quote;


import java.util.List;

import static android.R.attr.author;


public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.MyViewHolder> {

    private List<Quote> quotes;
    private boolean showAuthor;

    public QuotesAdapter(List<Quote> quotes, boolean showAuthor) {
        this.quotes = quotes;
        this.showAuthor = showAuthor;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quote_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Quote quote = quotes.get(position);
        holder.content.setText("\"" + quote.getContent() + "\"");
        if(showAuthor)
            holder.author.setText(quote.getAuthor().getLastName() + " " + quote.getAuthor().getFirstName());
        if(quote.isFavorite())
            holder.favorite.setImageResource(R.drawable.full_star);
        else
            holder.favorite.setImageResource(R.drawable.empty_star);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), QuotesActivity.class);
                intent.putExtra(QuotesActivity.AUTHOR_FIRST_NAME, quote.getAuthor().getFirstName());
                intent.putExtra(QuotesActivity.AUTHOR_LAST_NAME, quote.getAuthor().getLastName());
                intent.putExtra(QuotesActivity.IS_FAVORITE, quote.isFavorite());
                intent.putExtra(QuotesActivity.QUOTE_CONTENT, quote.getContent());

                ((AppCompatActivity)v.getContext()).startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView content;
        public TextView author;
        public ImageView favorite;

        public MyViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.quote_content);
            author = (TextView) itemView.findViewById(R.id.author_of_qoute);
            favorite = (ImageView) itemView.findViewById(R.id.is_favorite);
        }
    }
}
