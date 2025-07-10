# Spring Boot 複雜控制器實戰專案 ⚡

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 專案介紹

這是一個基於 Spring Boot 3.x 的咖啡店訂單管理系統實戰專案，展示了複雜控制器設計、資料驗證、異常處理和 RESTful API 開發的最佳實踐。

### 核心功能
- **咖啡商品管理**: 支援新增、查詢咖啡商品資訊
- **訂單處理系統**: 完整的訂單建立和狀態管理流程
- **資料驗證機制**: 使用 Jakarta Validation 進行請求參數驗證
- **異常處理**: 統一的錯誤處理和回應格式
- **快取機制**: 整合 Spring Cache 提升查詢效能
- **自定義類型轉換**: 實作 MoneyFormatter 處理貨幣類型轉換
- **檔案上傳處理**: 支援 Multipart 檔案上傳和批次處理

### 解決的問題
- 複雜業務邏輯的控制器設計模式
- 資料驗證錯誤的統一處理
- 多種請求格式的支援（JSON、Form Data、Multipart）
- 貨幣類型的資料庫儲存和轉換
- Spring Boot 自動配置機制下的類型轉換
- 自定義 Formatter 和 Converter 的實作
- 檔案上傳和批次資料處理

### 目標使用者
- Spring Boot 開發者
- 需要學習複雜控制器設計的工程師
- 想要了解 Spring Boot 3.x 新特性的開發者

> 💡 **為什麼選擇此專案？**
> - 展示 Spring Boot 3.x 的最新特性和最佳實踐
> - 提供完整的驗證錯誤處理解決方案
> - 包含實際業務場景的程式碼範例
> - 使用台灣本地化的技術術語和註解

### 🎯 專案特色

- **現代化技術棧**: 使用 Java 21 和 Spring Boot 3.4.5
- **完整的驗證機制**: 整合 Jakarta Validation 和自定義異常處理
- **多格式支援**: 支援 JSON、Form Data、Multipart 等多種請求格式
- **貨幣處理**: 使用 Joda Money 處理貨幣計算和資料庫轉換
- **快取優化**: 整合 Spring Cache 提升查詢效能
- **自定義類型轉換**: 實作 MoneyFormatter 處理貨幣字串轉換
- **Spring Boot 自動配置**: 展示 WebMVC Auto Configuration 機制
- **檔案上傳處理**: 支援批次檔案上傳和資料處理

## 技術棧

### 核心框架
- **Spring Boot 3.4.5** - 現代化的 Java 應用程式框架
- **Spring MVC** - Web 層控制器和請求處理
- **Spring Data JPA** - 資料存取層和 ORM 框架
- **Spring Cache** - 快取機制和效能優化

### 開發工具與輔助
- **Java 21** - 最新的 Java 版本，支援現代化語法
- **Maven** - 專案建置和依賴管理
- **H2 Database** - 內嵌式資料庫，方便開發測試
- **Lombok** - 減少樣板程式碼，提升開發效率
- **Joda Money** - 專業的貨幣處理函式庫
- **Apache Commons Lang3** - 字串處理和數字轉換工具
- **Spring Boot Auto Configuration** - 自動配置機制

## 專案結構

```
more-complex-controller-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tw/fengqing/spring/springbucks/waiter/
│   │   │       ├── controller/
│   │   │       │   ├── CoffeeController.java          # 咖啡商品控制器
│   │   │       │   ├── CoffeeOrderController.java     # 訂單控制器
│   │   │       │   ├── GlobalExceptionHandler.java    # 全域異常處理器
│   │   │       │   └── request/
│   │   │       │       ├── NewCoffeeRequest.java      # 新增咖啡請求物件
│   │   │       │       └── NewOrderRequest.java       # 新增訂單請求物件
│   │   │       ├── model/
│   │   │       │   ├── BaseEntity.java                # 基礎實體類別
│   │   │       │   ├── Coffee.java                    # 咖啡實體
│   │   │       │   ├── CoffeeOrder.java               # 訂單實體
│   │   │       │   ├── MoneyConverter.java            # 貨幣轉換器
│   │   │       │   └── OrderState.java                # 訂單狀態列舉
│   │   │       ├── repository/
│   │   │       │   ├── CoffeeRepository.java          # 咖啡資料存取層
│   │   │       │   └── CoffeeOrderRepository.java     # 訂單資料存取層
│   │   │       ├── service/
│   │   │       │   ├── CoffeeService.java             # 咖啡業務邏輯
│   │   │       │   └── CoffeeOrderService.java        # 訂單業務邏輯
│   │   │       └── support/
│   │   │           └── MoneyFormatter.java            # 貨幣格式化器
│   │   └── resources/
│   │       ├── application.properties                  # 應用程式設定檔
│   │       ├── schema.sql                             # 資料庫結構定義
│   │       ├── data.sql                               # 初始資料
│   │       └── coffee.txt                             # 咖啡資料檔案
│   └── test/
│       └── java/
│           └── tw/fengqing/spring/springbucks/waiter/
│               └── WaiterServiceApplicationTests.java  # 應用程式測試
├── pom.xml                                             # Maven 專案設定
└── README.md                                           # 專案說明文件
```

## 快速開始

### 前置需求
- **Java 21** - 請確保已安裝 Java 21 或更新版本
- **Maven 3.6+** - 用於專案建置和依賴管理
- **IDE 支援** - 建議使用 IntelliJ IDEA 或 Eclipse

### 安裝與執行

1. **克隆此倉庫：**
```bash
git clone https://github.com/SpringMicroservicesCourse/spring-microservices-course.git
```

2. **進入專案目錄：**
```bash
cd more-complex-controller-demo
```

3. **編譯專案：**
```bash
mvn clean compile
```

4. **執行應用程式：**
```bash
mvn spring-boot:run
```

5. **驗證應用程式：**
應用程式啟動後，可以透過以下方式驗證：
- 瀏覽器訪問：`http://localhost:8080`
- 查看所有咖啡：`GET http://localhost:8080/coffee/`

## API 使用範例

### 咖啡商品管理

#### 1. 新增咖啡商品（Form Data）
```bash
# 使用 Form Data 格式，支援自定義貨幣格式
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=美式咖啡&price=100.00"

# 支援帶貨幣代碼的格式
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=拿鐵咖啡&price=TWD 150.00"
```

#### 2. 批次新增咖啡商品（Multipart）
```bash
# 使用 Multipart 格式上傳檔案
# coffee.txt 內容格式：每行一個咖啡，格式為 "名稱 價格"
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: multipart/form-data" \
  -F "file=@coffee.txt"
```

#### 3. 查詢咖啡商品
```bash
# 查詢所有咖啡
curl http://localhost:8080/coffee/

# 依名稱查詢
curl "http://localhost:8080/coffee/?name=espresso"
```

### 訂單管理

#### 1. 建立新訂單
```bash
curl -X POST http://localhost:8080/order/ \
  -H "Content-Type: application/json" \
  -d '{
    "customer": "張三",
    "items": ["espresso", "latte"]
  }'
```

## 進階說明

### Spring Boot 自動配置機制

本專案展示了 Spring Boot 的 WebMVC Auto Configuration 機制：

1. **WebMVC Auto Configuration**: Spring Boot 自動配置 WebMVC 相關元件
2. **類型轉換器註冊**: 自動註冊 Converter、GenericConverter 和 Formatter
3. **Multipart 配置**: 自動配置 MultipartResolver 處理檔案上傳
4. **驗證機制**: 整合 Hibernate Validator 進行資料驗證

### 自定義類型轉換

專案實作了 `MoneyFormatter` 來處理貨幣類型轉換：

```java
@Component
public class MoneyFormatter implements Formatter<Money> {
    // 支援 "TWD 100.00" 或 "100.00" 格式
    // 自動註冊到 Spring 容器中
}
```

### 環境變數
```properties
# 資料庫設定
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver

# JPA 設定
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show_sql=true
spring.jpa.format_sql=true

# 快取設定
spring.cache.type=caffeine

# Multipart 檔案上傳設定
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 設定檔說明
```properties
# application.properties 主要設定
spring.jpa.hibernate.ddl-auto=none          # 不自動建立資料表
spring.jpa.properties.hibernate.show_sql=true    # 顯示 SQL 語句
spring.jpa.properties.hibernate.format_sql=true  # 格式化 SQL 輸出
```

### 重要程式碼區塊說明

#### 1. 自定義貨幣格式化器
```java
@Component
public class MoneyFormatter implements Formatter<Money> {
    /**
     * 處理 TWD 10.00 / 10.00 形式的字串
     * 支援帶貨幣代碼或不帶貨幣代碼的格式
     */
    @Override
    public Money parse(String text, Locale locale) throws ParseException {
        if (NumberUtils.isParsable(text)) {
            return Money.of(CurrencyUnit.of("TWD"), NumberUtils.createBigDecimal(text));
        } else if (StringUtils.isNotEmpty(text)) {
            String[] split = StringUtils.split(text, " ");
            if (split != null && split.length == 2 && NumberUtils.isParsable(split[1])) {
                return Money.of(CurrencyUnit.of(split[0]),
                        NumberUtils.createBigDecimal(split[1]));
            }
        }
        throw new ParseException(text, 0);
    }
}
```

#### 2. 控制器驗證處理
```java
@PostMapping(path = "/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
public Coffee addCoffee(@Valid NewCoffeeRequest newCoffee, BindingResult result) {
    if (result.hasErrors()) {
        // 自定義驗證錯誤處理，不讓 Spring MVC 自動處理
        log.warn("Binding Errors: {}", result);
        return null;
    }
    return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
}
```

#### 3. 檔案上傳處理
```java
@PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
public List<Coffee> batchAddCoffee(@RequestParam("file") MultipartFile file) {
    List<Coffee> coffees = new ArrayList<>();
    if (!file.isEmpty()) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String str;
            while ((str = reader.readLine()) != null) {
                String[] arr = StringUtils.split(str, " ");
                if (arr != null && arr.length == 2) {
                    coffees.add(coffeeService.saveCoffee(arr[0],
                            Money.of(CurrencyUnit.of("TWD"),
                                    NumberUtils.createBigDecimal(arr[1]))));
                }
            }
        } catch (IOException e) {
            log.error("exception", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
    return coffees;
}
```

#### 4. 全域異常處理器
```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 處理驗證錯誤異常
     * 當 @Valid 註解驗證失敗時會被觸發
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        // 收集所有驗證錯誤並回傳詳細資訊
    }
}
```

#### 5. 貨幣轉換器（JPA）
```java
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Long> {
    /**
     * 將 Money 物件轉換為資料庫中的長整數值
     * 將金額轉換為最小貨幣單位（分）
     */
    @Override
    public Long convertToDatabaseColumn(Money attribute) {
        return attribute == null ? null : attribute.getAmountMinorLong();
    }
}
```

## 參考資源

- [Spring Boot 官方文件](https://spring.io/projects/spring-boot)
- [Spring MVC 參考指南](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Jakarta Validation 規範](https://beanvalidation.org/)
- [Joda Money 文件](https://www.joda.org/joda-money/)
- [Spring Boot Auto Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration)
- [Spring Framework Formatter](https://docs.spring.io/spring-framework/reference/core/validation/format.html)
- [Multipart File Upload](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/multipart.html)

## 注意事項與最佳實踐

### ⚠️ 重要提醒

| 項目 | 說明 | 建議做法 |
|------|------|----------|
| 資料驗證 | 請求參數驗證 | 使用 @Valid 和 BindingResult |
| 異常處理 | 統一錯誤回應 | 實作 @ControllerAdvice |
| 貨幣處理 | 避免浮點數精度問題 | 使用 Joda Money |
| 快取機制 | 提升查詢效能 | 使用 @Cacheable 註解 |
| 類型轉換 | 自定義格式化器 | 實作 Formatter 介面 |
| 檔案上傳 | Multipart 處理 | 設定檔案大小限制 |
| 自動配置 | Spring Boot 機制 | 善用 @Component 註解 |

### 🔒 最佳實踐指南

- **控制器設計**: 保持控制器簡潔，業務邏輯放在 Service 層
- **資料驗證**: 在請求物件上使用驗證註解，並處理驗證錯誤
- **異常處理**: 實作全域異常處理器，提供統一的錯誤回應格式
- **貨幣處理**: 使用專業的貨幣函式庫，避免浮點數精度問題
- **程式碼註解**: 在重要的程式碼區塊添加清楚註解，方便團隊成員理解與維護
- **自定義類型轉換**: 實作 Formatter 或 Converter 處理複雜類型轉換
- **檔案上傳**: 使用 MultipartResolver 處理檔案上傳，注意資源釋放
- **Spring Boot 自動配置**: 善用自動配置機制，減少手動配置

### 🚀 效能優化建議

1. **快取策略**: 對頻繁查詢的資料使用快取
2. **資料庫優化**: 適當的索引和查詢優化
3. **非同步處理**: 對於耗時操作考慮使用非同步處理
4. **連線池設定**: 適當的資料庫連線池配置
5. **檔案上傳優化**: 設定適當的檔案大小限制和緩衝區
6. **類型轉換優化**: 避免重複的類型轉換，使用快取機制

## 授權說明

本專案採用 MIT 授權條款，詳見 LICENSE 檔案。

## 關於我們

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。

## 聯繫我們

- **FB 粉絲頁**：[風清雲談 | Facebook](https://www.facebook.com/profile.php?id=61576838896062)
- **LinkedIn**：[linkedin.com/in/chu-kuo-lung](https://www.linkedin.com/in/chu-kuo-lung)
- **YouTube 頻道**：[雲談風清 - YouTube](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- **風清雲談 部落格**：[風清雲談](https://blog.fengqing.tw/)
- **電子郵件**：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**📅 最後更新：2025-07-10**  
**👨‍💻 維護者：風清雲談團隊** 