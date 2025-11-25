package com.example.bodyfatcalculator;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private HistoryAdapter adapter;
    private HistoryManager historyManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.history_list);
        // Get the singleton instance of HistoryManager
        historyManager = HistoryManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Initialize with a simple, empty list
        adapter = new HistoryAdapter(this, new ArrayList<>());
        listView.setAdapter(adapter);

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
}