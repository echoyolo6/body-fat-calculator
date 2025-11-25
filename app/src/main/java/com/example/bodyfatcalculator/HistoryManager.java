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
import java.util.Collections;
import java.util.Comparator;
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
                if (records != null && !records.isEmpty()) {
                    // 确保记录按ID倒序排列
                    Collections.sort(records, new Comparator<BodyFatRecord>() {
                        @Override
                        public int compare(BodyFatRecord r1, BodyFatRecord r2) {
                            return r2.getId().compareTo(r1.getId()); // 倒序排列
                        }
                    });
                }
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
            
            // 计算下一个ID (最大的ID + 1，如果没有记录则为1)
            Long nextId = 1L;
            if (!records.isEmpty()) {
                Long maxId = records.stream()
                    .map(BodyFatRecord::getId)
                    .max(Long::compare)
                    .orElse(0L);
                nextId = maxId + 1;
            }
            
            // 设置记录的ID
            record.setId(nextId);
            
            // 按ID倒序排列 (ID大的在前面)
            records.add(record);
            Collections.sort(records, new Comparator<BodyFatRecord>() {
                @Override
                public int compare(BodyFatRecord r1, BodyFatRecord r2) {
                    return r2.getId().compareTo(r1.getId()); // 倒序排列
                }
            });

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

    /**
     * 删除指定的记录
     */
    public boolean deleteRecord(Long recordId) {
        synchronized (lock) {
            List<BodyFatRecord> records = getHistory();
            
            // 查找要删除的记录
            BodyFatRecord recordToDelete = null;
            for (BodyFatRecord record : records) {
                if (record.getId().equals(recordId)) {
                    recordToDelete = record;
                    break;
                }
            }
            
            if (recordToDelete == null) {
                return false; // 记录不存在
            }
            
            // 移除记录
            records.remove(recordToDelete);
            
            // 重新排序
            Collections.sort(records, new Comparator<BodyFatRecord>() {
                @Override
                public int compare(BodyFatRecord r1, BodyFatRecord r2) {
                    return r2.getId().compareTo(r1.getId());
                }
            });
            
            // 写入文件
            File tempFile = new File(file.getAbsolutePath() + ".tmp");
            try (FileWriter writer = new FileWriter(tempFile)) {
                gson.toJson(records, writer);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            
            if (file.exists()) {
                file.delete();
            }
            if (!tempFile.renameTo(file)) {
                System.err.println("Failed to rename temp file to " + FILE_NAME);
                return false;
            }
            
            return true;
        }
    }

    /**
     * 清空所有记录
     */
    public void clearAllRecords() {
        synchronized (lock) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}