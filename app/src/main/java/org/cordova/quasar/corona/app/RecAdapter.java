package org.cordova.quasar.corona.app;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.RecViewHolder> {

    private List<Questions> list;

    public RecAdapter(List<Questions> list) {
        this.list = list;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_expand, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, int position) {
        Questions questions = list.get(position);

        holder.bind(questions);

        holder.itemView.setOnClickListener(v -> {
            boolean expanded = questions.isExpanded();
            questions.setExpanded(!expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView question;
        private TextView answer;
        private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            subItem = itemView.findViewById(R.id.sub_item);
        }

        private void bind(Questions questions1) {
            boolean expanded = questions1.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            question.setText(questions1.getQuestion());
            answer.setText(questions1.getAnswer());
        }
    }
}
