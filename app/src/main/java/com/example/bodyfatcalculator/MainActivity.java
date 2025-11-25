package com.example.bodyfatcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rgGender;
    private EditText etAge, etSkinfold1, etSkinfold2, etSkinfold3;
    private TextView tvResult;
    private Button btnCalculate, btnViewHistory;
    private HistoryManager historyManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        // Get the singleton instance of HistoryManager
        historyManager = HistoryManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBodyFat();
            }
        });
        
        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        rgGender = findViewById(R.id.rg_gender);
        etAge = findViewById(R.id.et_age);
        etAge.setText("28");
        etSkinfold1 = findViewById(R.id.et_skinfold1);
        etSkinfold2 = findViewById(R.id.et_skinfold2);
        etSkinfold3 = findViewById(R.id.et_skinfold3);
        tvResult = findViewById(R.id.tv_result);
        btnCalculate = findViewById(R.id.btn_calculate);
        btnViewHistory = findViewById(R.id.btn_view_history);
    }

    private void calculateBodyFat() {
        try {
            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
            String gender = selectedGenderRadioButton.getText().toString();

            String ageString = etAge.getText().toString();
            int age = ageString.isEmpty() ? 28 : Integer.parseInt(ageString);

            double skinfold1 = Double.parseDouble(etSkinfold1.getText().toString());
            double skinfold2 = Double.parseDouble(etSkinfold2.getText().toString());
            double skinfold3 = Double.parseDouble(etSkinfold3.getText().toString());

            double sumSkinfolds = skinfold1 + skinfold2 + skinfold3;
            double bodyDensity;
            
            if (gender.equals("男")) {
                bodyDensity = 1.10938 - (0.0008267 * sumSkinfolds) + (0.0000016 * sumSkinfolds * sumSkinfolds) - (0.0002574 * age);
            } else {
                bodyDensity = 1.0994921 - (0.0009929 * sumSkinfolds) + (0.0000023 * sumSkinfolds * sumSkinfolds) - (0.0001392 * age);
            }
            
            double bodyFatPercentage = (495 / bodyDensity) - 450;
            
            tvResult.setText(String.format("体脂率: %.2f%%", bodyFatPercentage));
            
            // Use HistoryManager to save to file
            executorService.execute(() -> {
                BodyFatRecord record = new BodyFatRecord(null, gender, age, skinfold1, skinfold2, skinfold3, bodyFatPercentage, System.currentTimeMillis());
                historyManager.addRecord(record);
            });
            
        } catch (NumberFormatException e) {
            tvResult.setText("请输入有效数值");
        } catch (Exception e) {
            tvResult.setText("计算出错: " + e.getMessage());
        }
    }
}