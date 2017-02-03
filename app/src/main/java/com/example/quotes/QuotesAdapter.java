package com.example.quotes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quotes.model.Quote;


import java.util.List;



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
        Quote quote = quotes.get(position);
        holder.content.setText("\"" + quote.getContent() + "\"");
        if(showAuthor)
            holder.author.setText(quote.getAuthor().getLastName() + " " + quote.getAuthor().getFirstName());
        if(quote.isFavorite())
            holder.favorite.setImageResource(R.drawable.full_star);
        else
            holder.favorite.setImageResource(R.drawable.empty_star);
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
