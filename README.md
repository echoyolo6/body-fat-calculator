# 体脂钳计算器

这是一个简单的安卓应用，可以通过体脂钳测量的数值来计算体脂率。

## 功能

- 输入性别、年龄和三个皮褶厚度值
- 使用 Jackson-Pollock 3-site 公式计算体脂率
- 保存测量历史记录
- 查看历史数据

## 系统要求

- Android Studio Flamingo | 2022.2.1 或更高版本
- Android SDK 版本 34
- 最低支持 Android API 24 (Android 7.0)

## 如何运行

1. 打开 Android Studio
2. 选择 "Open an existing project" 或 "Open"
3. 选择项目根目录 `/Users/hhyoyo/Desktop/cal/body-fat-calculator`
4. 等待 Android Studio 同步项目依赖
5. 连接安卓设备或启动模拟器
6. 点击 "Run" 按钮 (绿色三角形) 运行应用

## 源码结构

- `MainActivity.java` - 主界面逻辑，包含输入字段和计算功能
- `HistoryActivity.java` - 历史记录查看界面
- `BodyFatDatabaseHelper.java` - SQLite 数据库操作类
- `BodyFatRecord.java` - 数据模型类
- `HistoryAdapter.java` - 历史记录列表适配器
- `activity_main.xml` - 主界面布局
- `activity_history.xml` - 历史记录界面布局
- `history_item.xml` - 历史记录列表项布局

## 计算公式

应用使用 Jackson-Pollock 3-site 公式进行计算，根据性别和年龄调整参数：

男性:

- 身体密度 = 1.10938 - (0.0008267 × 皮褶总和) + (0.0000016 × 皮褶总和 ²) - (0.0002574 × 年龄)
- 体脂率(%) = (495 / 身体密度) - 450

女性:

- 身体密度 = 1.0994921 - (0.0009929 × 皮褶总和) + (0.0000023 × 皮褶总和 ²) - (0.0001392 × 年龄)
- 体脂率(%) = (495 / 身体密度) - 450
