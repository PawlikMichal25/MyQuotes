package com.example.quotes.quotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quotes.R;
import com.example.quotes.model.Quote;


import java.util.List;


class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.MyViewHolder> {

    private List<Quote> quotes;
    private boolean showAuthor;

    QuotesAdapter(List<Quote> quotes, boolean showAuthor) {
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
        holder.content.setText(quote.getContent());
        if(showAuthor)
            holder.author.setText(quote.getAuthor().getLastName() + " " + quote.getAuthor().getFirstName());
        else
            holder.author.setVisibility(View.GONE);

        final Context ctx = holder.content.getContext();
        if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(ctx.getString(R.string.pref_show_star), true)){
            if(quote.isFavorite())
                holder.favorite.setImageResource(R.drawable.full_star);
            else
                holder.favorite.setImageResource(R.drawable.empty_star);
        }
        else
            holder.favorite.setVisibility(View.GONE);
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditQuoteActivity.class);
                intent.putExtra(EditQuoteActivity.AUTHOR_FIRST_NAME, quote.getAuthor().getFirstName());
                intent.putExtra(EditQuoteActivity.AUTHOR_LAST_NAME, quote.getAuthor().getLastName());
                intent.putExtra(EditQuoteActivity.IS_FAVORITE, quote.isFavorite());
                intent.putExtra(EditQuoteActivity.QUOTE_CONTENT, quote.getContent());

                v.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(quote.getContent())
                        .setItems(R.array.share_copy, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        sharePlainText(ctx, prepareFullQuote(quote));
                                        break;
                                    case 1:
                                        copyToClipboard(ctx, prepareFullQuote(quote));
                                        break;
                                }
                            }
                        });
                builder.create().show();

                return true;
            }
        });
    }

    private void sharePlainText(Context ctx, String messageText){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, messageText);
        Intent chosenIntent = Intent.createChooser(intent, ctx.getString(R.string.share_quote));
        ctx.startActivity(chosenIntent);
    }

    private void copyToClipboard(Context ctx, String messageText){
        ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(ctx.getString(R.string.clipboard_copied), messageText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(ctx, ctx.getString(R.string.clipboard_copied), Toast.LENGTH_SHORT).show();
    }

    private String prepareFullQuote(Quote quote){
        return new StringBuilder()
                .append("\"")
                .append(quote.getContent())
                .append("\"")
                .append(" ")
                .append(quote.getAuthor().getLastName())
                .append(" ")
                .append(quote.getAuthor().getFirstName())
                .toString();
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView content;
        public TextView author;
        public ImageView favorite;

        MyViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.quote_content);
            author = (TextView) itemView.findViewById(R.id.author_of_qoute);
            favorite = (ImageView) itemView.findViewById(R.id.is_favorite);
        }
    }
}
