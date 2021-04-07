package org.cordova.quasar.corona.app;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.RecViewHolder> implements Filterable {

    private List<Questions> list;
    private List<Questions> listFull;

    public RecAdapter(List<Questions> list) {
        this.list = list;
        listFull = new ArrayList<>(list);
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

    @Override
    public Filter getFilter(){
        return listFilter;
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            List<Questions> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length()==0){
                filteredList.addAll(listFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Questions item: listFull) {
                    if (item.getQuestion().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
