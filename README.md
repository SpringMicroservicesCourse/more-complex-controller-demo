# more-complex-controller-demo

> Advanced Spring MVC controller methods with custom Formatter, Bean Validation, and Multipart file upload

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6.2.5-blue.svg)](https://spring.io/projects/spring-framework)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.1.0-red.svg)](https://jakarta.ee/specifications/bean-validation/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive demonstration of **advanced Spring MVC controller methods** featuring custom `Formatter` implementation, automatic Bean Validation, and Multipart file upload handling.

## Features

- Custom `MoneyFormatter` for Joda Money type conversion
- Automatic Bean Validation with `@Valid`
- Two validation strategies: manual (BindingResult) vs automatic (Spring MVC)
- Multipart file upload for batch coffee creation
- Form data validation (application/x-www-form-urlencoded)
- Multiple currency format support ("125.00" and "TWD 125.00")
- Spring Boot WebMVC Auto Configuration demonstration
- Formatter auto-registration with `@Component`
- Complete CRUD operations for coffee and orders
- Spring Cache integration
- H2 database integration

## Tech Stack

- Spring Boot 3.4.5
- Spring MVC 6.2.5
- Spring Data JPA
- Jakarta Validation (Bean Validation 3.0)
- Java 21
- H2 Database 2.3.232
- Joda Money 2.0.2
- Apache Commons Lang3
- Lombok
- Maven 3.8+

## Getting Started

### Prerequisites

- JDK 21 or higher
- Maven 3.8+ (or use included Maven Wrapper)

### Quick Start

**Run the application:**

```bash
./mvnw spring-boot:run
```

**Test MoneyFormatter:**

```bash
# Format 1: Pure number (default TWD)
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=125.00"

# Format 2: With currency code
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=latte&price=TWD 125.00"
```

## Configuration

### Application Properties

```properties
# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true

# Error response configuration (for development only)
server.error.include-message=always
server.error.include-binding-errors=always
```

**Important:**
- `show_sql=true`: Show SQL statements (development only)
- `include-message=always`: Include error messages in response (development only)
- **Production**: Set to `never` to avoid information leakage

## Custom MoneyFormatter

### Implementation

```java
@Component
public class MoneyFormatter implements Formatter<Money> {
    
    /**
     * Parse string to Money object
     * Supports: "125.00" or "TWD 125.00"
     */
    @Override
    public Money parse(String text, Locale locale) throws ParseException {
        // Case 1: Pure number → default TWD
        if (NumberUtils.isParsable(text)) {
            return Money.of(CurrencyUnit.of("TWD"), NumberUtils.createBigDecimal(text));
        } 
        // Case 2: With currency code "TWD 125.00"
        else if (StringUtils.isNotEmpty(text)) {
            String[] split = StringUtils.split(text, " ");
            if (split != null && split.length == 2 && NumberUtils.isParsable(split[1])) {
                return Money.of(CurrencyUnit.of(split[0]),
                        NumberUtils.createBigDecimal(split[1]));
            } else {
                throw new ParseException(text, 0);
            }
        }
        throw new ParseException(text, 0);
    }

    /**
     * Format Money object to string
     * Output: "TWD 125.00"
     */
    @Override
    public String print(Money money, Locale locale) {
        return money.getCurrencyUnit().getCode() + " " + money.getAmount();
    }
}
```

### How It Works

```
1. @Component annotation → Spring auto-registers Formatter
2. Spring Boot WebMVC Auto Configuration detects it
3. FormatterRegistry automatically registers MoneyFormatter
4. Form data submission → MoneyFormatter.parse() converts string to Money
5. Response serialization → MoneyFormatter.print() converts Money to string
```

### Supported Formats

| Input Format | Description | Example | Result |
|-------------|-------------|---------|--------|
| **Pure number** | Default currency (TWD) | `"125.00"` | `Money.of(TWD, 125.00)` |
| **With currency** | Explicit currency code | `"TWD 125.00"` | `Money.of(TWD, 125.00)` |
| **Other currency** | Any ISO 4217 code | `"USD 10.00"` | `Money.of(USD, 10.00)` |

## Validation Strategies

### Two Validation Approaches

This project demonstrates **two different validation strategies**:

| Strategy | BindingResult | Error Handling | Response | This Project |
|----------|--------------|----------------|----------|-------------|
| **Method 1** | ✅ Has | Manual check, return `null` | 201 (even if validation fails) | ⚠️ Commented out |
| **Method 2** | ❌ No | Spring MVC auto-handles | 400 BAD REQUEST | ✅ In use |

### Method 1: Manual Validation (Commented Out)

```java
/**
 * Method 1: Manual validation handling
 * Validation errors return null
 */
// @PostMapping(path = "/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
// @ResponseBody
// @ResponseStatus(HttpStatus.CREATED)
// public Coffee addCoffee(@Valid NewCoffeeRequest newCoffee, BindingResult result) {
//     if (result.hasErrors()) {
//         log.warn("Binding Errors: {}", result);
//         return null;  // Custom error handling
//     }
//     return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
// }
```

**Characteristics:**
- ✅ Can customize error handling logic
- ✅ Can log detailed validation errors
- ❌ Returns `null` on validation failure (HTTP 201)
- ❌ Not RESTful compliant (wrong status code)

### Method 2: Automatic Validation (In Use) ✅

```java
/**
 * Method 2: Spring MVC automatic validation
 * Validation errors automatically return 400 Bad Request
 */
@PostMapping(path = "/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
public Coffee addCoffeeWithoutBindingResult(@Valid NewCoffeeRequest newCoffee) {
    // Spring MVC auto-handles validation errors
    return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
}
```

**Characteristics:**
- ✅ Simpler code, no manual validation check
- ✅ Spring MVC auto-returns 400 on validation failure
- ✅ RESTful compliant
- ✅ Detailed JSON error response
- ✅ Frontend can directly check HTTP status code

## API Documentation

### Coffee API

#### 1. Add Coffee (Form Data - Success, Pure Number)

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=125.00"
```

**MoneyFormatter Processing:**
- Input: `"125.00"` (string)
- Process: `MoneyFormatter.parse()` converts to `Money.of(TWD, 125.00)`
- Save: Money object saved to database

**Response:** 201 CREATED

```json
{
    "id": 6,
    "createTime": "2025-10-18T00:30:03.005+00:00",
    "updateTime": "2025-10-18T00:30:03.005+00:00",
    "name": "Americano",
    "price": {
        "currencyUnit": {
            "code": "TWD",
            "numericCode": 901,
            "decimalPlaces": 2,
            "symbol": "$"
        },
        "amount": 125.00
    }
}
```

#### 2. Add Coffee (Form Data - Success, With Currency Code)

```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=latte&price=TWD 150.00"
```

**MoneyFormatter Processing:**
- Input: `"TWD 150.00"` (string)
- Process: Parse currency code and amount separately
- Result: `Money.of(TWD, 150.00)`

**Response:** 201 CREATED

#### 3. Add Coffee (Validation Error - Empty Name)

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=&price=125.00"
```

**Spring MVC Automatic Validation:**
- Detects: `@NotEmpty` violation on `name` field
- Action: Auto-return 400 Bad Request
- Error: Detailed JSON error response

**Response:** 400 BAD REQUEST

```json
{
    "timestamp": "2025-10-18T01:06:10.966+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed for object='newCoffeeRequest'. Error count: 1",
    "errors": [
        {
            "objectName": "newCoffeeRequest",
            "field": "price",
            "rejectedValue": null,
            "codes": ["NotNull.newCoffeeRequest.price", "NotNull.price", "NotNull.org.joda.money.Money", "NotNull"],
            "defaultMessage": "不得是空值",
            "bindingFailure": false,
            "code": "NotNull"
        }
    ],
    "path": "/coffee/"
}
```

**Error Details:**
- HTTP Status: 400 Bad Request
- Error Type: Validation failed
- Field: `price` (violates `@NotNull`)
- Rejected Value: `null`
- Default Message: 不得是空值

#### 4. Add Coffee (Type Conversion Error - Invalid Price)

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=XXX"
```

**MoneyFormatter Processing:**
- Input: `"XXX"` (invalid format)
- Parse: MoneyFormatter cannot convert
- Error: Type conversion failed

**Response:** 400 BAD REQUEST

```json
{
    "timestamp": "2025-10-18T00:40:15.627+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed for object='newCoffeeRequest'. Error count: 1",
    "errors": [
        {
            "codes": ["typeMismatch.newCoffeeRequest.price", "typeMismatch.price", "typeMismatch.org.joda.money.Money", "typeMismatch"],
            "defaultMessage": "Failed to convert property value of type 'java.lang.String' to required type 'org.joda.money.Money' for property 'price'; Failed to convert from type [java.lang.String] to type [@jakarta.validation.constraints.NotNull org.joda.money.Money] for value [XXX]",
            "objectName": "newCoffeeRequest",
            "field": "price",
            "rejectedValue": "XXX",
            "bindingFailure": true,
            "code": "typeMismatch"
        }
    ],
    "path": "/coffee/"
}
```

**Error Details:**
- HTTP Status: 400 Bad Request
- Error Type: Type mismatch
- Cause: MoneyFormatter cannot parse "XXX"
- Note: This is a type conversion error, not `@NotNull` validation error

#### 5. Batch Add Coffees (File Upload)

**Create test file (coffee.txt):**

```
Americano 125.0
Italian 150.0
```

**Upload:**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: multipart/form-data" \
  -F "file=@coffee.txt"
```

**Response:** 201 CREATED

```json
[
    {
        "id": 7,
        "createTime": "2025-10-18T00:41:01.915+00:00",
        "updateTime": "2025-10-18T00:41:01.915+00:00",
        "name": "Americano",
        "price": {
            "currencyUnit": {
                "code": "TWD",
                "numericCode": 901
            },
            "amount": 125.00
        }
    },
    {
        "id": 8,
        "createTime": "2025-10-18T00:41:01.947+00:00",
        "updateTime": "2025-10-18T00:41:01.947+00:00",
        "name": "Italian",
        "price": {
            "currencyUnit": {
                "code": "TWD",
                "numericCode": 901
            },
            "amount": 150.00
        }
    }
]
```

#### 6. Get All Coffees

```bash
curl -X GET http://localhost:8080/coffee/
```

**Response:** 200 OK (includes all created coffees)

## Key Components

### MoneyFormatter

```java
@Component
public class MoneyFormatter implements Formatter<Money> {
    
    @Override
    public Money parse(String text, Locale locale) throws ParseException {
        // Case 1: Pure number → default TWD
        if (NumberUtils.isParsable(text)) {
            return Money.of(CurrencyUnit.of("TWD"), NumberUtils.createBigDecimal(text));
        } 
        // Case 2: With currency code "TWD 125.00"
        else if (StringUtils.isNotEmpty(text)) {
            String[] split = StringUtils.split(text, " ");
            if (split != null && split.length == 2 && NumberUtils.isParsable(split[1])) {
                return Money.of(CurrencyUnit.of(split[0]),
                        NumberUtils.createBigDecimal(split[1]));
            } else {
                throw new ParseException(text, 0);
            }
        }
        throw new ParseException(text, 0);
    }

    @Override
    public String print(Money money, Locale locale) {
        return money.getCurrencyUnit().getCode() + " " + money.getAmount();
    }
}
```

**How it works:**
1. `@Component` → Spring auto-registers as a bean
2. Spring Boot WebMVC Auto Configuration detects `Formatter<Money>`
3. Automatically registered to `FormatterRegistry`
4. Applied during form data binding

### CoffeeController

```java
@Controller
@RequestMapping("/coffee")
@Slf4j
public class CoffeeController {
    
    @Autowired
    private CoffeeService coffeeService;

    /**
     * Method 2: Automatic validation (In Use) ✅
     * Spring MVC auto-handles validation errors
     * Returns 400 Bad Request on validation failure
     */
@PostMapping(path = "/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
    public Coffee addCoffeeWithoutBindingResult(@Valid NewCoffeeRequest newCoffee) {
    return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
}

    /**
     * Multipart file upload
     * Batch create coffees from uploaded file
     */
@PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
public List<Coffee> batchAddCoffee(@RequestParam("file") MultipartFile file) {
    List<Coffee> coffees = new ArrayList<>();
    if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream()))) {
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
                log.error("File processing error", e);
            }
        }
        return coffees;
    }
}
```

### NewCoffeeRequest

```java
@Getter
@Setter
@ToString
public class NewCoffeeRequest {
    @NotEmpty
    private String name;
    
    @NotNull
    private Money price;
}
```

**Validation:**
- `@NotEmpty`: Name cannot be null or empty
- `@NotNull`: Price cannot be null

## Validation Strategy Comparison

### Method 1 vs Method 2

| Aspect | Method 1 (Manual) | Method 2 (Automatic) ✅ |
|--------|-------------------|------------------------|
| **BindingResult** | ✅ Used | ❌ Not used |
| **Validation Check** | Manual `if (result.hasErrors())` | Spring MVC auto-handles |
| **Success Response** | HTTP 201, Coffee object | HTTP 201, Coffee object |
| **Failure Response** | HTTP 201, `null` | HTTP 400, detailed error JSON |
| **Error Information** | None (only logged) | Full JSON with field, code, message |
| **Code Simplicity** | More code | Less code |
| **RESTful Compliance** | ❌ No (wrong status code) | ✅ Yes |
| **Frontend Handling** | Check if response is `null` | Check HTTP status code |
| **This Project** | ⚠️ Commented out | ✅ In use |

### Why Method 2?

**Advantages:**
- ✅ Simpler code, no manual validation check
- ✅ Automatic 400 Bad Request on validation failure
- ✅ RESTful API compliant
- ✅ Detailed JSON error response with field, code, message
- ✅ Standard Spring MVC error format
- ✅ Frontend can directly use HTTP status code

**Method 1 Use Cases:**
- Need custom error response format
- Need complex error handling logic
- Need to continue processing even with validation errors
- See Lecture 49 (exception-demo) for advanced custom exception handling

## Testing

### MoneyFormatter Tests

**1. Pure Number Format:**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=125.00"

# MoneyFormatter: "125.00" → Money.of(TWD, 125.00)
# Response: 201 CREATED
```

**2. With Currency Code:**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=latte&price=TWD 150.00"

# MoneyFormatter: "TWD 150.00" → Money.of(TWD, 150.00)
# Response: 201 CREATED
```

### Validation Error Tests

**3. Empty Name (Validation Error):**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=&price=125.00"

# Spring MVC: Detects @NotEmpty violation
# Response: 400 BAD REQUEST with detailed error
```

**4. Invalid Price (Type Conversion Error):**

```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=XXX"

# MoneyFormatter: Cannot parse "XXX"
# Response: 400 BAD REQUEST (typeMismatch)
```

### File Upload Test

**5. Batch Upload:**

```bash
# Create coffee.txt
echo "Americano 125.0" > coffee.txt
echo "Italian 150.0" >> coffee.txt

# Upload
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: multipart/form-data" \
  -F "file=@coffee.txt"

# Response: 201 CREATED (list of coffees)
```

## Spring Boot Auto Configuration

### WebMVC Auto Configuration

**How Formatter is Auto-Registered:**

```
1. @Component on MoneyFormatter
   ↓
2. Spring component scan detects it
   ↓
3. Spring Boot WebMvcAutoConfiguration
   ↓
4. WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter
   ↓
5. Calls addFormatters(FormatterRegistry)
   ↓
6. Auto-discovers Formatter<T> beans
   ↓
7. Registers MoneyFormatter to FormatterRegistry
   ↓
8. Available for form data binding
```

**Key Classes:**
- `org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration`
- `org.springframework.format.support.FormattingConversionService`
- `org.springframework.format.FormatterRegistry`

**Auto-Discovered Types:**
- `Converter<S, T>` - Simple type converter
- `GenericConverter` - Generic type converter
- `Formatter<T>` - Formatter with Locale support (our case)

## Formatter vs Converter

### Comparison

| Feature | Formatter | Converter |
|---------|-----------|-----------|
| **Interface** | `Formatter<T>` | `Converter<S, T>` |
| **Methods** | `parse()`, `print()` | `convert()` |
| **Locale Support** | ✅ Yes | ❌ No |
| **Bidirectional** | ✅ Yes (parse + print) | ❌ No (only one direction) |
| **Use Case** | User input/output | Internal conversions |
| **This Project** | ✅ MoneyFormatter | - |

### When to Use Each

**Use Formatter when:**
- Need to parse user input (e.g., form data)
- Need to format output for display
- Need locale support for internationalization
- Need bidirectional conversion (string ↔ object)

**Use Converter when:**
- Simple type conversion (e.g., String → Integer)
- Internal data transformation
- One-way conversion only
- No locale consideration needed

## Best Practices

### 1. Formatter Implementation

```java
// ✅ Recommended: Implement Formatter with @Component
@Component
public class MoneyFormatter implements Formatter<Money> {
    @Override
    public Money parse(String text, Locale locale) throws ParseException {
        // Parse logic
    }
    
    @Override
    public String print(Money money, Locale locale) {
        // Format logic
    }
}

// ❌ Not recommended: Manual registration
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new MoneyFormatter());  // Manual registration
    }
}
```

### 2. Validation Strategy

```java
// ✅ Recommended: Let Spring MVC auto-handle (Method 2)
@PostMapping("/")
public Coffee add(@Valid NewCoffeeRequest request) {
    return service.save(request);
}

// ⚠️ Use when needed: Manual handling (Method 1)
@PostMapping("/")
public Coffee add(@Valid NewCoffeeRequest request, BindingResult result) {
    if (result.hasErrors()) {
        // Custom error handling
    }
    return service.save(request);
}
```

### 3. Error Messages

```java
// ✅ Recommended: Provide custom messages
public class NewCoffeeRequest {
    @NotEmpty(message = "咖啡名稱不能為空")
    private String name;
    
    @NotNull(message = "價格不能為空")
    private Money price;
}

// ⚠️ Acceptable: Use default messages
public class NewCoffeeRequest {
    @NotEmpty  // Default: "不得是空的"
    private String name;
}
```

### 4. File Upload

```java
// ✅ Recommended: try-with-resources
public List<Coffee> batchAdd(@RequestParam("file") MultipartFile file) {
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream()))) {
        // Process file
    } catch (IOException e) {
        log.error("Error", e);
        throw new RuntimeException("File processing failed", e);
    }
}

// ❌ Not recommended: Manual close
public List<Coffee> batchAdd(@RequestParam("file") MultipartFile file) {
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(...);
        // Process file
    } finally {
        IOUtils.closeQuietly(reader);  // Manual close
    }
}
```

### 5. Production Configuration

```properties
# ✅ Production: Hide sensitive information
server.error.include-message=never
server.error.include-binding-errors=never
server.error.include-stacktrace=never

# ❌ Development only: Show detailed errors
server.error.include-message=always
server.error.include-binding-errors=always
```

## Common Issues

### Issue 1: Formatter Not Working

**Problem:** MoneyFormatter not applied

**Cause:** Missing `@Component` annotation

**Solution:**

```java
@Component  // Required!
public class MoneyFormatter implements Formatter<Money> {
    // ...
}
```

### Issue 2: Validation Not Triggered

**Problem:** Validation errors not caught

**Cause:** Missing `@Valid` annotation

**Solution:**

```java
// ❌ Wrong: No @Valid
@PostMapping("/")
public Coffee add(NewCoffeeRequest request) { }

// ✅ Correct: With @Valid
@PostMapping("/")
public Coffee add(@Valid NewCoffeeRequest request) { }
```

### Issue 3: ParseException on Empty String

**Problem:** MoneyFormatter throws ParseException for empty string

**Cause:** `price` field is empty or invalid

**Expected Behavior:**
- Empty string → Spring MVC returns 400 (type conversion error)
- Invalid format → MoneyFormatter throws ParseException → 400

**No action needed:** This is correct behavior

### Issue 4: File Upload 413 Error

**Problem:** File too large

**Cause:** Exceeds `max-file-size` limit

**Solution:**

```properties
# Increase file size limit
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Formatter Interface

### Interface Hierarchy

```java
// Formatter extends Parser and Printer
public interface Formatter<T> extends Printer<T>, Parser<T> {
    // No additional methods
}

// Parser provides parse method
@FunctionalInterface
public interface Parser<T> {
    T parse(String text, Locale locale) throws ParseException;
}

// Printer provides print method
@FunctionalInterface
public interface Printer<T> {
    String print(T object, Locale locale);
}
```

**Why this design?**
- **Single Responsibility**: Parser for parsing, Printer for formatting
- **Flexibility**: Can implement only Parser or Printer if needed
- **Functional**: Both are `@FunctionalInterface`, can use lambda

## Database Schema

**schema.sql:**

```sql
drop table t_coffee if exists;
drop table t_order if exists;
drop table t_order_coffee if exists;

create table t_coffee (
    id bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    name varchar(255),
    price bigint,
    primary key (id)
);

create table t_order (
    id bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    customer varchar(255),
    state integer not null,
    primary key (id)
);

create table t_order_coffee (
    coffee_order_id bigint not null,
    items_id bigint not null
);
```

**data.sql:**

```sql
insert into t_coffee (name, price, create_time, update_time) 
    values ('espresso', 10000, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('latte', 12500, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('capuccino', 12500, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('mocha', 15000, now(), now());
insert into t_coffee (name, price, create_time, update_time) 
    values ('macchiato', 15000, now(), now());
```

## Best Practices Demonstrated

1. **Custom Formatter**: `MoneyFormatter` with `@Component` auto-registration
2. **Spring Boot Auto Configuration**: Leverage WebMVC Auto Configuration
3. **Automatic Validation**: Use `@Valid` without `BindingResult` for simpler code
4. **Multiple Format Support**: Handle both "125.00" and "TWD 125.00"
5. **RESTful Error Responses**: Standard 400 Bad Request with detailed errors
6. **Bean Validation Annotations**: `@NotEmpty`, `@NotNull` on request objects
7. **Multipart File Upload**: Batch processing from uploaded files
8. **Resource Management**: try-with-resources for auto-closing streams
9. **Locale Support**: Formatter supports internationalization
10. **Type Safety**: Strong typing with Money instead of BigDecimal

## References

- [Spring Boot WebMVC Auto Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.auto-configuration)
- [Spring Framework Formatter](https://docs.spring.io/spring-framework/reference/core/validation/format.html)
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/3.0/)
- [Multipart File Upload](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/multipart.html)
- [Joda Money Documentation](https://www.joda.org/joda-money/)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## About Us

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。近來也積極結合 AI 技術，推動自動化工作流，讓開發與運維更有效率、更智慧。持續學習與分享，希望能一起推動軟體開發的創新和進步。

## Contact

**風清雲談** - 專注於敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。

- 🌐 官方網站：[風清雲談部落格](https://blog.fengqing.tw/)
- 📘 Facebook：[風清雲談粉絲頁](https://www.facebook.com/profile.php?id=61576838896062)
- 💼 LinkedIn：[Chu Kuo-Lung](https://www.linkedin.com/in/chu-kuo-lung)
- 📺 YouTube：[雲談風清頻道](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- 📧 Email：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**⭐ 如果這個專案對您有幫助，歡迎給個 Star！**
