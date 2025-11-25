package com.example.bodyfatcalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends ArrayAdapter<BodyFatRecord> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    
    // 删除按钮点击回调接口
    public interface OnDeleteClickListener {
        void onDeleteClick(BodyFatRecord record);
    }
    
    private OnDeleteClickListener deleteListener;

    public HistoryAdapter(@NonNull Context context, @NonNull List<BodyFatRecord> records) {
        super(context, 0, records);
    }
    
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
            holder = new ViewHolder();
            holder.tvId = convertView.findViewById(R.id.tv_id);
            holder.tvGender = convertView.findViewById(R.id.tv_gender);
            holder.tvAge = convertView.findViewById(R.id.tv_age);
            holder.tvSkinfolds = convertView.findViewById(R.id.tv_skinfolds);
            holder.tvResult = convertView.findViewById(R.id.tv_result);
            holder.tvTimestamp = convertView.findViewById(R.id.tv_timestamp);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BodyFatRecord record = getItem(position);

        if (record != null) {
            holder.tvId.setText("ID: " + record.getId()); // 显示记录的真实ID
            holder.tvGender.setText("性别: " + record.getGender());
            holder.tvAge.setText("年龄: " + record.getAge());
            holder.tvSkinfolds.setText(String.format("皮褶厚度: %.2f, %.2f, %.2f mm",
                    record.getSkinfold1(), record.getSkinfold2(), record.getSkinfold3()));
            holder.tvResult.setText(String.format("体脂率: %.2f%%", record.getResult()));

            if (record.getTimestamp() != null) {
                holder.tvTimestamp.setText("时间: " + dateFormat.format(new Date(record.getTimestamp())));
            } else {
                holder.tvTimestamp.setText("时间: N/A");
            }
            
            // 设置删除按钮点击事件
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(record);
                }
            });
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvGender;
        TextView tvAge;
        TextView tvSkinfolds;
        TextView tvResult;
        TextView tvTimestamp;
        Button btnDelete;
    }
}