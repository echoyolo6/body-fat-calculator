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
}