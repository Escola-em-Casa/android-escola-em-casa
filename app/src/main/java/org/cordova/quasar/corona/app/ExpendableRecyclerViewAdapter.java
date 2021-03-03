package org.cordova.quasar.corona.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.cordova.quasar.corona.app.databinding.ItemExpandBinding;

import java.util.List;

public class ExpendableRecyclerViewAdapter extends RecyclerView.Adapter<ExpendableRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<Questions> questionsList;

    public ExpendableRecyclerViewAdapter(Context context, List<Questions> list) {

        this.context = context;
        this.questionsList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_expand, null);
        ItemExpandBinding bi = DataBindingUtil.bind(view);
        return new ViewHolder(bi);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        holder.bi.question.setText(questionsList.get(i).getQuestion());

        holder.bi.viewMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemExpandBinding bi;

        public ViewHolder(@NonNull ItemExpandBinding itemView) {
            super(itemView.getRoot());

            bi = itemView;

        }
    }
}
