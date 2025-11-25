package com.example.bodyfatcalculator;

public class BodyFatRecord {

    private Long id; // 添加ID字段
    private String gender;
    private int age;
    private double skinfold1;
    private double skinfold2;
    private double skinfold3;
    private double result;
    private Long timestamp;

    public BodyFatRecord(Long id, String gender, int age, double skinfold1, double skinfold2, double skinfold3, double result, Long timestamp) {
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.skinfold1 = skinfold1;
        this.skinfold2 = skinfold2;
        this.skinfold3 = skinfold3;
        this.result = result;
        this.timestamp = timestamp;
    }

    // Getters
    public Long getId() { return id; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
    public double getSkinfold1() { return skinfold1; }
    public double getSkinfold2() { return skinfold2; }
    public double getSkinfold3() { return skinfold3; }
    public double getResult() { return result; }
    public Long getTimestamp() { return timestamp; }
    
    // Setter for ID (needed when loading from JSON)
    public void setId(Long id) { this.id = id; }
    
    /**
     * 获取体脂率水平描述
     */
    public String getBodyFatLevel() {
        double bodyFat = this.result;
        String gender = this.gender;
        
        if (gender.equals("男")) {
            if (bodyFat < 6) {
                return "运动员";
            } else if (bodyFat < 14) {
                return "健身";
            } else if (bodyFat < 18) {
                return "正常";
            } else if (bodyFat < 25) {
                return "偏高";
            } else {
                return "肥胖";
            }
        } else { // 女性
            if (bodyFat < 14) {
                return "运动员";
            } else if (bodyFat < 21) {
                return "健身";
            } else if (bodyFat < 25) {
                return "正常";
            } else if (bodyFat < 32) {
                return "偏高";
            } else {
                return "肥胖";
            }
        }
    }
    
    /**
     * 获取体脂率水平的颜色（用于界面显示）
     */
    public String getBodyFatLevelColor() {
        String level = getBodyFatLevel();
        switch (level) {
            case "运动员": return "#4CAF50"; // 绿色
            case "健身": return "#8BC34A"; // 浅绿
            case "正常": return "#2196F3"; // 蓝色
            case "偏高": return "#FF9800"; // 橙色
            case "肥胖": return "#F44336"; // 红色
            default: return "#666666"; // 默认灰色
        }
    }
}