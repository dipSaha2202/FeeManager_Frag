package com.diptution.fee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RecordListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Integer> months;
    private ArrayList<String> payDates;

    public RecordListAdapter(Context context, LinkedHashMap<Integer, String> mData) {
        if (mData == null) {
            mData = new LinkedHashMap<>();
        }

        months = new ArrayList<>();
        payDates = new ArrayList<>();
        this.context = context;

        for (Integer month : mData.keySet()) {
            months.add(month);
            payDates.add(mData.get(month));
        }

        this.mInflater =  LayoutInflater.from(context);
        /*(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
    }

    @Override
    public int getCount() {
        return months.size();
    }

    @Override
    public Integer getItem(int position) {
        return months.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_row_record_list, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.txtMonth = convertView.findViewById(R.id.txtMonth_List);
            viewHolder.txtDate = convertView.findViewById(R.id.txtDate_List);

            convertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        if (months.get(position) > 0 && months.get(position) < 13) {
            holder.txtMonth.setText(VariableMethods.NAME_OF_MONTHS[months.get(position) - 1]);
            holder.txtDate.setText(context.getString(R.string.paid_date_list, payDates.get(position)));
        } else {
            holder.txtMonth.setText(context.getString(R.string.error));
            holder.txtDate.setText(context.getString(R.string.error));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView txtMonth, txtDate;
    }

    public void updateList(LinkedHashMap<Integer, String> records){
        if (records == null) {
            records = new LinkedHashMap<>();
        }
        months.clear();
        payDates.clear();

        for (Integer month : records.keySet()) {
            months.add(month);
            payDates.add(records.get(month));
        }
        notifyDataSetChanged();
    }
}
