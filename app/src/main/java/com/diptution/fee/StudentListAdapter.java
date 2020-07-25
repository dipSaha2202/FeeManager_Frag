package com.diptution.fee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Student> studentArrayList;

    public StudentListAdapter(Context context, ArrayList<Student> studentArrayList) {
        this.context = context;
        this.studentArrayList = studentArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return studentArrayList.size();
    }

    @Override
    public Student getItem(int position) {
        return studentArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.student_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.studentName = convertView.findViewById(R.id.txtName_singleRow);
            viewHolder.studentClass = convertView.findViewById(R.id.txtClass_singleRow);
            viewHolder.paidUpToMonth = convertView.findViewById(R.id.txtLastPaid_singleRow);
            viewHolder.fee = convertView.findViewById(R.id.txtFee_singleRow);
            viewHolder.phone = convertView.findViewById(R.id.txtPhone_singleRow);

            convertView.setTag(viewHolder);
        }

        Student student = getItem(position);
        int paidUpTo = student.getLastPaidMonth();

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.studentName.setText(context.getString(R.string.student_name, student.getName()));
        holder.studentClass.setText(context.getString(R.string.student_class, student.getClassOfTheStudent()));

        if (paidUpTo >= 1 && paidUpTo < 13) {
            holder.paidUpToMonth.setText(context.getString(R.string.student_paid_month,
                    VariableMethods.NAME_OF_MONTHS[paidUpTo - 1]));
        } else {
            holder.paidUpToMonth.setText(context.getString(R.string.student_paid_month, "No Payment"));
        }

        holder.fee.setText(context.getString(R.string.student_fee, student.getFee()));

        String phone = student.getPhoneNumber();

        if (phone.length() > 5) {
            holder.phone.setText(context.getString(R.string.student_phone, phone));
        } else {
            holder.phone.setText(context.getString(R.string.student_phone, "No Contact"));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView studentClass, studentName, paidUpToMonth, fee, phone;
    }
}
