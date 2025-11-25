package com.example.bodyfatcalculator;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {

    private static final String FILE_NAME = "history.json";
    private static volatile HistoryManager INSTANCE;
    private final File file;
    private final Gson gson = new Gson();
    private final Object lock = new Object();

    private HistoryManager(Context context) {
        file = new File(context.getApplicationContext().getFilesDir(), FILE_NAME);
    }

    public static HistoryManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HistoryManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HistoryManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public List<BodyFatRecord> getHistory() {
        synchronized (lock) {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<BodyFatRecord>>() {}.getType();
                List<BodyFatRecord> records = gson.fromJson(reader, listType);
                return records != null ? records : new ArrayList<>();
            } catch (Exception e) {
                // If reading fails, the file is likely corrupt. Delete it and start fresh.
                file.delete();
                return new ArrayList<>();
            }
        }
    }

    public void addRecord(BodyFatRecord record) {
        synchronized (lock) {
            List<BodyFatRecord> records = getHistory();
            records.add(0, record);

            // --- ATOMIC WRITE OPERATION to prevent file corruption ---
            File tempFile = new File(file.getAbsolutePath() + ".tmp");
            try (FileWriter writer = new FileWriter(tempFile)) {
                gson.toJson(records, writer);
            } catch (IOException e) {
                e.printStackTrace();
                // If writing to temp file fails, we abort and the original file is safe.
                return;
            }

            // If temp file write is successful, delete the original and rename the temp file.
            if (file.exists()) {
                file.delete();
            }
            if (!tempFile.renameTo(file)) {
                System.err.println("Failed to rename temp file to " + FILE_NAME);
            }
        }
    }
}