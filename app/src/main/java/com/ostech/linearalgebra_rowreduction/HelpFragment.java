package com.ostech.linearalgebra_rowreduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HelpFragment extends Fragment {
    private static final String TAG = "RowReductionActivity";

    private RecyclerView helpRecyclerView;

    private ArrayList<String> helpTextList;

    private HelpTextAdapter helpAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        helpRecyclerView = view.findViewById(R.id.help_recycler_view);
        helpRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        helpTextList = new ArrayList<>();

        helpTextList.add(getString(R.string.help_text_1));
        helpTextList.add(getString(R.string.help_text_2));
        helpTextList.add(getString(R.string.help_text_3));
        helpTextList.add(getString(R.string.help_text_4));
        helpTextList.add(getString(R.string.help_text_5));
        helpTextList.add(getString(R.string.help_text_6));
        helpTextList.add(getString(R.string.help_text_7));

        if (helpAdapter == null) {
            helpAdapter = new HelpTextAdapter(helpTextList);
            helpRecyclerView.setAdapter(helpAdapter);
        } else {
            helpAdapter.setHelpTextList(helpTextList);
            helpAdapter.notifyDataSetChanged();
        }

        return view;
    }   //  end of onCreateView()

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.help_menu_item_title);
    }   //  end of onViewCreated()

    private class HelpTextHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView helpTextView;

        private String helpText;

        public HelpTextHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.holder_help, parent, false));

            helpTextView = itemView.findViewById(R.id.help_text_view);
        }

        @Override
        public void onClick(View view) {
        }

        public void bind(String helpText) {
            this.helpText = helpText;
            helpTextView.setText(this.helpText);
        }
    }   //  end of HelpTextHolder class

    private class HelpTextAdapter extends RecyclerView.Adapter<HelpTextHolder> {
        private ArrayList<String> helpTextList;

        public HelpTextAdapter(ArrayList<String> helpTextList) {
            this.helpTextList = helpTextList;
        }

        @Override
        public HelpTextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new HelpTextHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(HelpTextHolder holder, int position) {
            String helpText = helpTextList.get(position);
            holder.bind(helpText);
        }

        @Override
        public int getItemCount() {
            return helpTextList.size();
        }

        public void setHelpTextList(ArrayList<String> helpTextList) {
            this.helpTextList = helpTextList;
        }
    }
}
