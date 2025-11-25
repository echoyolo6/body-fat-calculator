# Android Studio APK打包完整指南

## 方法一：使用Android Studio图形界面（推荐新手）

### 1. 打开项目
- 启动Android Studio
- 选择"Open an existing Android Studio project"
- 选择您的体脂计算器项目文件夹

### 2. 同步项目
- 打开后，Android Studio会自动提示同步项目
- 点击"Sync Now"等待同步完成
- 确保没有构建错误

### 3. 构建Debug版本APK（快速测试用）
1. **菜单方式**：
   - 菜单栏 → Build → Build Bundle(s) / APK(s) → Build APK(s)
   
2. **快捷方式**：
   - 点击工具栏的 `Build` 图标
   - 选择 `Build APK(s)`

3. **Gradle面板方式**：
   - 右侧点击 `Gradle` 面板
   - 展开 `app` → `Tasks` → `build`
   - 双击 `assembleDebug`

### 4. 构建Release版本APK（发布用）
1. **准备签名密钥**（首次发布需要）：
   - 菜单栏 → Build → Generate Signed Bundle / APK
   - 选择 `APK` → Next
   - 点击 `Create new...` 创建新密钥库
   - 填写密钥库信息：
     - Key store path: 选择保存路径
     - Password: 设置密钥库密码
     - Alias: 密钥别名
     - Password: 设置密钥密码
     - Validity: 有效期（建议25年）
     - Certificate: 填写证书信息
   - 保存密钥库文件

2. **构建Release APK**：
   - 菜单栏 → Build → Generate Signed Bundle / APK
   - 选择 `APK` → Next
   - 选择已有的密钥库文件
   - 输入密钥库密码和密钥密码
   - 选择 `release` 构建类型
   - 点击 `Finish`

### 5. 找到生成的APK文件
- APK文件通常保存在：`app/build/outputs/apk/`
- Debug版本：`app/build/outputs/apk/debug/app-debug.apk`
- Release版本：`app/build/outputs/apk/release/app-release.apk`

## 方法二：使用命令行（适合开发者）

### 1. 生成Debug APK
```bash
# 在项目根目录执行
./gradlew assembleDebug
```

### 2. 生成Release APK（需要签名）
```bash
# 创建签名配置后执行
./gradlew assembleRelease
```

### 3. 清理项目
```bash
./gradlew clean
```

## 方法三：Android Studio菜单详细步骤

### Build菜单选项说明
- **Build Bundle(s) / APK(s)**：
  - Build APK(s)：生成未签名的APK
  - Build Bundle(s)：生成AAB格式（Google Play推荐）

- **Clean Project**：清理构建文件
- **Rebuild Project**：重新构建整个项目
- **Make Project**：编译项目
- **Generate Signed Bundle / APK**：生成签名包

### Gradle任务详解
在Gradle面板中，您可以找到这些构建任务：
- `assembleDebug`：构建Debug APK
- `assembleRelease`：构建Release APK
- `build`：构建所有变体
- `clean`：清理构建输出

## 签名配置详解

### 创建密钥库（一次性操作）
1. 在Android Studio中选择：Build → Generate Signed Bundle / APK
2. 选择APK → Create new...
3. 填写信息：
   - Key store path: `~/Desktop/my-app-key.keystore`
   - Password: 你的密钥库密码
   - Alias: `my-app-key`
   - Password: 你的密钥密码
   - Validity: 25
   - Certificate: 填写你的信息

### 配置签名（在build.gradle中）
```gradle
android {
    signingConfigs {
        release {
            storeFile file('../my-app-key.keystore')
            storePassword '你的密钥库密码'
            keyAlias 'my-app-key'
            keyPassword '你的密钥密码'
        }
    }
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }
}
```

## 常见问题解决

### 1. 构建失败
- 检查SDK版本是否正确
- 清理项目：Build → Clean Project
- 重新同步：File → Sync Project with Gradle Files
- 检查依赖是否正确下载

### 2. 签名问题
- 确保密钥库文件路径正确
- 密码输入正确
- 密钥别名正确

### 3. 内存不足
增加Gradle内存：
在 `gradle.properties` 中添加：
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## APK优化建议

### 1. 启用代码混淆
```gradle
buildTypes {
    release {
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

### 2. 移除未使用资源
```gradle
android {
    buildTypes {
        release {
            shrinkResources true
            minifyEnabled true
        }
    }
}
```

### 3. 使用ABI分割
```gradle
android {
    splits {
        abi {
            enable true
            reset()
            include 'x86', 'armeabi-v7a', 'arm64-v8a'
            universalApk true
        }
    }
}
```

## 安装和测试APK

### 1. 启用开发者选项
- 设置 → 关于手机 → 连续点击版本号7次
- 设置 → 开发者选项 → 启用USB调试

### 2. 安装APK
```bash
# 通过ADB安装
adb install app-debug.apk

# 或者直接拖拽APK到模拟器/真机
```

### 3. 检查APK信息
```bash
# 查看APK详细信息
aapt dump badging app-release.apk

# 查看APK大小
ls -lh app-release.apk
```

## 发布到应用商店

### Google Play Store
- 需要AAB格式而不是APK
- 使用Build → Generate Signed Bundle / APK → Android App Bundle

### 其他应用商店
- 通常接受APK格式
- 需要对APK进行签名

---

## GitHub集成和自动化构建

### 1. 从GitHub获取代码
```bash
# 克隆仓库
git clone https://github.com/echoyolo6/body-fat-calculator.git
cd body-fat-calculator

# 在Android Studio中打开项目
open -a "Android Studio" .
```

### 2. GitHub Actions自动化构建
创建 `.github/workflows/android.yml` 实现自动构建：

```yaml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### 3. GitHub Releases发布
可以通过GitHub Releases发布APK文件：

1. 在GitHub仓库页面点击"Releases"
2. 点击"Create a new release"
3. 填写版本信息和说明
4. 上传生成的APK文件
5. 发布Release

## 快速总结

1. **快速测试**：Build → Build APK(s) → 生成Debug版本
2. **正式发布**：需要签名 → Generate Signed APK → 选择Release构建类型
3. **APK位置**：`app/build/outputs/apk/`
4. **GitHub集成**：代码已提交到 https://github.com/echoyolo6/body-fat-calculator
5. **签名重要**：发布到应用商店必须签名

建议您首先使用Debug版本测试应用功能，确认无误后再构建Release版本进行发布。您的代码已成功提交到GitHub，可以实现版本控制和自动化构建。