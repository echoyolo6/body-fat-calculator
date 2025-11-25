package com.example.bodyfatcalculator;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private HistoryAdapter adapter;
    private HistoryManager historyManager;
    private ExecutorService executorService;
    private Button btnClearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.history_list);
        btnClearAll = findViewById(R.id.btn_clear_all);
        
        // Get the singleton instance of HistoryManager
        historyManager = HistoryManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Initialize with a simple, empty list
        adapter = new HistoryAdapter(this, new ArrayList<>());
        listView.setAdapter(adapter);
        
        // 设置删除按钮回调
        adapter.setOnDeleteClickListener(record -> {
            showDeleteConfirmationDialog(record);
        });
        
        // 设置清空所有按钮点击事件
        btnClearAll.setOnClickListener(v -> {
            showClearAllConfirmationDialog();
        });

        loadHistory();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadHistory() {
        executorService.execute(() -> {
            List<BodyFatRecord> records = historyManager.getHistory();
            runOnUiThread(() -> {
                adapter.clear();
                adapter.addAll(records);
            });
        });
    }
    
    /**
     * 显示删除确认对话框
     */
    private void showDeleteConfirmationDialog(BodyFatRecord record) {
        new AlertDialog.Builder(this)
            .setTitle("删除确认")
            .setMessage("确定要删除这条记录吗？\n\n记录信息：\n体脂率: " + 
                       String.format("%.2f%%", record.getResult()) + 
                       "\n时间: " + (record.getTimestamp() != null ? 
                       new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                       .format(new Date(record.getTimestamp())) : "N/A"))
            .setPositiveButton("删除", (dialog, which) -> deleteRecord(record))
            .setNegativeButton("取消", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    /**
     * 执行删除操作
     */
    private void deleteRecord(BodyFatRecord record) {
        executorService.execute(() -> {
            boolean success = historyManager.deleteRecord(record.getId());
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "记录删除成功", Toast.LENGTH_SHORT).show();
                    // 刷新列表
                    loadHistory();
                } else {
                    Toast.makeText(this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    
    /**
     * 显示清空所有记录确认对话框
     */
    private void showClearAllConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("清空所有记录")
            .setMessage("确定要删除所有历史记录吗？\n此操作不可恢复！")
            .setPositiveButton("清空", (dialog, which) -> clearAllRecords())
            .setNegativeButton("取消", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    /**
     * 执行清空所有记录操作
     */
    private void clearAllRecords() {
        executorService.execute(() -> {
            historyManager.clearAllRecords();
            runOnUiThread(() -> {
                Toast.makeText(this, "所有记录已清空", Toast.LENGTH_SHORT).show();
                // 刷新列表
                loadHistory();
            });
        });
    }
}