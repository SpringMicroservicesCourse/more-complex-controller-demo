# more-complex-controller-demo

> Advanced Spring MVC controller with custom Formatter, automatic Bean Validation, and Multipart file upload

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6.2.5-blue.svg)](https://spring.io/projects/spring-framework)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.1.0-red.svg)](https://jakarta.ee/specifications/bean-validation/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A comprehensive demonstration of **advanced Spring MVC controller methods** featuring custom `Formatter` implementation for Money type conversion, automatic Bean Validation, and Multipart file upload handling. This project showcases two validation strategies and Spring Boot's powerful auto-configuration capabilities.

## Features

- Custom `MoneyFormatter` for Joda Money type conversion (NOT Jackson serializer)
- Automatic Bean Validation with `@Valid` annotation
- Two validation strategies demonstration: manual vs automatic
- Multipart file upload for batch coffee creation
- Form data validation (application/x-www-form-urlencoded)
- Multiple currency format support ("125.00" and "TWD 125.00")
- Spring Boot WebMVC Auto Configuration demonstration
- Formatter auto-registration with `@Component`
- Complete CRUD operations for coffee and orders
- Spring Cache integration
- H2 in-memory database

## Tech Stack

### Core Frameworks
- **Spring Boot 3.4.5** - Microservices framework
- **Spring MVC 6.2.5** - Web application framework
- **Spring Data JPA** - Data persistence layer
- **Hibernate 6.x** - ORM framework

### Validation & Conversion
- **Jakarta Validation 3.1** - Bean Validation specification
- **Hibernate Validator** - Validation implementation
- **Custom Formatter** - MoneyFormatter for Money type

### Database & Tools
- **H2 Database 2.3.232** - In-memory database
- **Joda Money 2.0.2** - Money handling
- **Apache Commons Lang3** - Utility library

### Development Tools
- **Lombok** - Reduce boilerplate code
- **Maven 3.8+** - Build tool
- **Java 21** - Development environment

## Project Structure

```
more-complex-controller-demo/
├── src/
│   ├── main/
│   │   ├── java/tw/fengqing/spring/springbucks/waiter/
│   │   │   ├── WaiterServiceApplication.java      # Main application
│   │   │   ├── controller/          # Controller layer
│   │   │   │   ├── CoffeeController.java          # Coffee API (KEY)
│   │   │   │   ├── CoffeeOrderController.java     # Order API
│   │   │   │   └── request/         # Request objects
│   │   │   │       ├── NewCoffeeRequest.java      # Coffee request with validation
│   │   │   │       └── NewOrderRequest.java       # Order request
│   │   │   ├── service/             # Service layer
│   │   │   │   ├── CoffeeService.java
│   │   │   │   └── CoffeeOrderService.java
│   │   │   ├── repository/          # Data access layer
│   │   │   │   ├── CoffeeRepository.java
│   │   │   │   └── CoffeeOrderRepository.java
│   │   │   ├── model/               # Entity models
│   │   │   │   ├── Coffee.java
│   │   │   │   ├── CoffeeOrder.java
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── OrderState.java
│   │   │   │   └── MoneyConverter.java          # JPA Money converter
│   │   │   └── support/             # Support utilities
│   │   │       └── MoneyFormatter.java          # Custom Formatter (KEY)
│   │   └── resources/
│   │       ├── application.properties            # Application config
│   │       ├── schema.sql                        # Database schema
│   │       ├── data.sql                          # Initial data
│   │       └── coffee.txt                        # Coffee data file
│   └── test/
└── pom.xml
```

## Getting Started

### Prerequisites

- **Java 21** - Development environment
- **Maven 3.8+** - Build tool
- **IDE** - IntelliJ IDEA or Eclipse (optional)

### Installation & Execution

**Step 1: Compile the project**

```bash
mvn clean compile
```

**Step 2: Run the application**

```bash
mvn spring-boot:run
```

**Step 3: Verify service**

```bash
# Test coffee endpoint
curl http://localhost:8080/coffee/1

# Expected: Coffee JSON response
```

## Configuration

### Application Properties

```properties
# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true

# Error response configuration (development environment only)
server.error.include-message=always
server.error.include-binding-errors=always
```

> [!warning] Production Configuration
> For production environments, disable detailed error messages to prevent information leakage:
> ```properties
> server.error.include-message=never
> server.error.include-binding-errors=never
> server.error.include-stacktrace=never
> ```

## Custom MoneyFormatter Deep Dive

### What is Formatter?

`Formatter<T>` is a Spring framework interface for bidirectional type conversion between String and custom types, with Locale support.

**Interface Definition:**
```java
public interface Formatter<T> extends Printer<T>, Parser<T> {
    // Combines parsing (String → Object) and printing (Object → String)
}

@FunctionalInterface
public interface Parser<T> {
    T parse(String text, Locale locale) throws ParseException;
}

@FunctionalInterface
public interface Printer<T> {
    String print(T object, Locale locale);
}
```

### MoneyFormatter Implementation

```java
@Component
public class MoneyFormatter implements Formatter<Money> {
    
    /**
     * Parse string to Money object
     * Supports two formats:
     * 1. Pure number: "125.00" → Money.of(TWD, 125.00)
     * 2. With currency: "TWD 125.00" → Money.of(TWD, 125.00)
     */
    @Override
    public Money parse(String text, Locale locale) throws ParseException {
        // Case 1: Pure number → default TWD
        if (NumberUtils.isParsable(text)) {
            return Money.of(CurrencyUnit.of("TWD"), 
                          NumberUtils.createBigDecimal(text));
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
     * Output format: "TWD 125.00"
     */
    @Override
    public String print(Money money, Locale locale) {
        return money.getCurrencyUnit().getCode() + " " + money.getAmount();
    }
}
```

### Auto-Registration Mechanism

```
1. @Component annotation on MoneyFormatter
   ↓
2. Spring component scan detects it during startup
   ↓
3. Spring Boot WebMvcAutoConfiguration activated
   ↓
4. WebMvcAutoConfigurationAdapter.addFormatters() called
   ↓
5. Auto-discovers all Formatter<T> beans
   ↓
6. Registers MoneyFormatter to FormatterRegistry
   ↓
7. Available for form data binding and output formatting
```

**Key Classes:**
- `org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration`
- `org.springframework.format.support.FormattingConversionService`
- `org.springframework.format.FormatterRegistry`

### Supported Formats

| Input Format | Description | Example | Parsed Result |
|-------------|-------------|---------|--------------|
| **Pure number** | Default currency (TWD) | `"125.00"` | `Money.of(TWD, 125.00)` |
| **With currency** | Explicit currency code | `"TWD 125.00"` | `Money.of(TWD, 125.00)` |
| **USD** | US Dollar | `"USD 10.00"` | `Money.of(USD, 10.00)` |
| **EUR** | Euro | `"EUR 8.50"` | `Money.of(EUR, 8.50)` |

### Conversion Flow

**Request → Money:**
```
Form Data: name=Americano&price=125.00
    ↓
Spring MVC binds parameters
    ↓
Detects target type: Money
    ↓
Looks up MoneyFormatter in FormatterRegistry
    ↓
Calls MoneyFormatter.parse("125.00", locale)
    ↓
Returns Money.of(TWD, 125.00)
    ↓
Binds to NewCoffeeRequest.price
```

**Money → String:**
```
Money.of(TWD, 125.00)
    ↓
MoneyFormatter.print(money, locale)
    ↓
Returns "TWD 125.00"
    ↓
Used in view rendering or logging
```

## Bean Validation Strategies

### Two Validation Approaches

This project demonstrates **two different validation strategies** using Bean Validation:

| Strategy | Has BindingResult | Error Handling | Response on Error | This Project |
|----------|------------------|----------------|-------------------|-------------|
| **Method 1: Manual** | ✅ Yes | Manual check with `if (result.hasErrors())` | 201 + `null` | ⚠️ Commented out |
| **Method 2: Automatic** | ❌ No | Spring MVC auto-handles | 400 Bad Request + JSON | ✅ Active |

### Method 1: Manual Validation Handling (Commented Out)

```java
/**
 * Method 1: Manual validation handling
 * Developer manually checks BindingResult and handles errors
 * 
 * ⚠️ This method is COMMENTED OUT in the project
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

| Aspect | Behavior |
|--------|----------|
| **Validation Check** | Manual `if (result.hasErrors())` |
| **Success Response** | HTTP 201 Created + Coffee object |
| **Failure Response** | HTTP 201 Created + `null` |
| **Error Information** | Only in logs, not returned to client |
| **HTTP Status** | Always 201 (even on validation failure) |
| **RESTful Compliance** | ❌ No (wrong status code) |

**Advantages:**
- ✅ Can customize error handling logic
- ✅ Can log detailed validation errors
- ✅ Can perform additional processing

**Disadvantages:**
- ❌ Returns `null` on validation failure (HTTP 201)
- ❌ Not RESTful compliant (wrong status code)
- ❌ Client cannot easily detect validation errors
- ❌ Frontend must check if response is `null`

### Method 2: Automatic Validation Handling (Active) ✅

```java
/**
 * Method 2: Spring MVC automatic validation
 * Spring MVC automatically handles validation errors
 * No BindingResult parameter needed
 * 
 * ✅ This method is ACTIVE in the project
 */
@PostMapping(path = "/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
public Coffee addCoffeeWithoutBindingResult(@Valid NewCoffeeRequest newCoffee) {
    // Spring MVC auto-handles validation errors
    // No need to check BindingResult
    return coffeeService.saveCoffee(newCoffee.getName(), newCoffee.getPrice());
}
```

**Characteristics:**

| Aspect | Behavior |
|--------|----------|
| **Validation Check** | Automatic (no manual check) |
| **Success Response** | HTTP 201 Created + Coffee object |
| **Failure Response** | HTTP 400 Bad Request + detailed error JSON |
| **Error Information** | Complete JSON with field, code, message |
| **HTTP Status** | 201 on success, 400 on validation failure |
| **RESTful Compliance** | ✅ Yes (correct status codes) |

**Advantages:**
- ✅ Simpler code, no manual validation check
- ✅ Spring MVC auto-returns 400 on validation failure
- ✅ RESTful API compliant
- ✅ Detailed JSON error response
- ✅ Standard Spring MVC error format
- ✅ Frontend can directly check HTTP status code

**When to Use Each:**
- **Method 1**: Need custom error response format or complex error handling logic (see Lecture 49: exception-demo)
- **Method 2**: Standard RESTful API (recommended for most cases) ✅

## API Usage Guide

### Coffee Management

#### 1. Add Coffee (Form Data - Success, Pure Number)

**Request:**
```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=125.00"
```

**MoneyFormatter Processing:**
- Input: `"125.00"` (string from form data)
- Process: `MoneyFormatter.parse("125.00", locale)`
- Detection: Pure number format (no currency code)
- Result: `Money.of(CurrencyUnit.of("TWD"), 125.00)`

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

**Request:**
```bash
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=latte&price=TWD 150.00"
```

**MoneyFormatter Processing:**
- Input: `"TWD 150.00"` (string with currency code)
- Process: Split by space → `["TWD", "150.00"]`
- Parse: `Money.of(CurrencyUnit.of("TWD"), 150.00)`

**Response:** 201 CREATED (similar structure)

#### 3. Add Coffee (Validation Error - Empty Name)

**Request:**
```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=&price=125.00"
```

**Spring MVC Automatic Validation:**
- Detects: `@NotEmpty` violation on `name` field
- Action: Automatically returns 400 Bad Request
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
            "field": "name",
            "rejectedValue": "",
            "codes": ["NotEmpty.newCoffeeRequest.name", "NotEmpty.name", ...],
            "defaultMessage": "不得是空的",
            "bindingFailure": false,
            "code": "NotEmpty"
        }
    ],
    "path": "/coffee/"
}
```

**Error Details:**
- **HTTP Status**: 400 Bad Request
- **Error Type**: Bean Validation error
- **Field**: `name` (violates `@NotEmpty`)
- **Rejected Value**: Empty string `""`
- **Message**: "不得是空的" (default validation message)

#### 4. Add Coffee (Type Conversion Error - Invalid Price)

**Request:**
```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Americano&price=XXX"
```

**MoneyFormatter Processing:**
- Input: `"XXX"` (invalid format)
- Parse: Fails in `MoneyFormatter.parse()`
- Error: `ParseException` thrown
- Spring MVC: Catches and converts to type mismatch error

**Response:** 400 BAD REQUEST
```json
{
    "timestamp": "2025-10-18T00:40:15.627+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed for object='newCoffeeRequest'. Error count: 1",
    "errors": [
        {
            "codes": ["typeMismatch.newCoffeeRequest.price", "typeMismatch.price", ...],
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
- **HTTP Status**: 400 Bad Request
- **Error Type**: Type conversion error (NOT Bean Validation)
- **Field**: `price`
- **Rejected Value**: `"XXX"`
- **Cause**: `MoneyFormatter.parse()` cannot convert invalid format
- **Note**: This occurs BEFORE Bean Validation

#### 5. Batch Add Coffees (File Upload)

**Create test file (coffee.txt):**
```
Americano 125.0
Italian 150.0
Cappuccino 130.0
```

**Upload:**
```bash
curl -v -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: multipart/form-data" \
  -F "file=@coffee.txt"
```

**File Processing Logic:**
```java
@PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
```

**Response:** 201 CREATED
```json
[
    {
        "id": 7,
        "name": "Americano",
        "price": { "amount": 125.00, "currencyUnit": { "code": "TWD" } }
    },
    {
        "id": 8,
        "name": "Italian",
        "price": { "amount": 150.00, "currencyUnit": { "code": "TWD" } }
    },
    {
        "id": 9,
        "name": "Cappuccino",
        "price": { "amount": 130.00, "currencyUnit": { "code": "TWD" } }
    }
]
```

#### 6. Get All Coffees

**Request:**
```bash
curl http://localhost:8080/coffee/
```

**Response:** 200 OK (includes all coffees, including uploaded ones)

## Formatter vs Converter vs Serializer

### Comparison Table

| Feature | Formatter | Converter | Jackson Serializer |
|---------|-----------|-----------|-------------------|
| **Purpose** | Form data binding | Internal type conversion | JSON serialization |
| **Interface** | `Formatter<T>` | `Converter<S, T>` | `JsonSerializer<T>` |
| **Bidirectional** | ✅ Yes (parse + print) | ❌ No (one-way) | ❌ No (one-way) |
| **Locale Support** | ✅ Yes | ❌ No | ❌ No |
| **Use Case** | Form input/output | Internal conversions | REST API JSON |
| **Auto-Registration** | `@Component` | `@Component` | `@JsonComponent` |
| **This Project** | ✅ MoneyFormatter | - | json-view-demo |

### When to Use Each

**Use Formatter when:**
- ✅ Parsing user input from HTML forms (`application/x-www-form-urlencoded`)
- ✅ Need to format output for view rendering
- ✅ Need locale support for internationalization
- ✅ Need bidirectional conversion (String ↔ Object)
- ✅ **This project's use case** ⭐

**Use Converter when:**
- ✅ Simple internal type conversion (e.g., String → Integer)
- ✅ One-way conversion only
- ✅ No locale consideration needed
- ✅ Database value conversion (but use JPA AttributeConverter instead)

**Use Jackson Serializer when:**
- ✅ JSON API requests/responses (`application/json`)
- ✅ Custom JSON format for REST API
- ✅ Complex object serialization
- ✅ See json-view-demo project for examples

## Validation Error Flow

### Request Processing Pipeline

```
HTTP Request (Form Data)
    ↓
1. Spring MVC receives request
    ↓
2. Parameter binding (uses MoneyFormatter if needed)
    ↓ (If parse fails)
    ├─→ Type conversion error → 400 Bad Request (typeMismatch)
    ↓ (If parse succeeds)
3. Bean Validation (@Valid triggered)
    ↓ (If validation fails)
    ├─→ Validation error → 400 Bad Request (validation errors)
    ↓ (If validation succeeds)
4. Controller method execution
    ↓
5. Service layer processing
    ↓
6. Response serialization
    ↓
HTTP Response
```

### Error Types

| Error Type | Occurs When | Example | HTTP Status |
|-----------|-------------|---------|-------------|
| **Type Conversion Error** | Formatter/Converter fails | `price=XXX` | 400 Bad Request |
| **Validation Error** | `@Valid` annotation check fails | `@NotEmpty` on empty name | 400 Bad Request |
| **Business Error** | Service layer throws exception | Duplicate coffee name | 500 or custom |

## Testing

### Unit Test Example

```java
@SpringBootTest
class WaiterServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verify application context loads successfully
    }
}
```

### Manual Testing Checklist

| Test Case | Command | Expected Result |
|-----------|---------|-----------------|
| ✅ Add coffee (pure number) | `price=125.00` | 201 Created |
| ✅ Add coffee (with currency) | `price=TWD 150.00` | 201 Created |
| ✅ Validation error (empty name) | `name=` | 400 Bad Request |
| ✅ Type error (invalid price) | `price=XXX` | 400 Bad Request |
| ✅ File upload | Upload coffee.txt | 201 Created (list) |
| ✅ Get all coffees | GET /coffee/ | 200 OK (all coffees) |

## Common Issues

### Issue 1: Formatter Not Working

**Problem:** MoneyFormatter not being applied

**Cause:** Missing `@Component` annotation

**Solution:**
```java
@Component  // Required for auto-registration!
public class MoneyFormatter implements Formatter<Money> {
    // ...
}
```

### Issue 2: Validation Not Triggered

**Problem:** Validation errors not caught

**Cause:** Missing `@Valid` annotation on controller parameter

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

**Problem:** MoneyFormatter throws ParseException for empty price

**Expected Behavior:**
- Empty string → MoneyFormatter throws ParseException
- Spring MVC catches it → Returns 400 Bad Request (type conversion error)
- This is **correct behavior**, no fix needed

### Issue 4: File Upload 413 Error

**Problem:** Uploaded file too large

**Cause:** Exceeds `max-file-size` limit

**Solution:**
```properties
# Increase file size limit
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Issue 5: Wrong Content-Type

**Problem:** Form data not parsed correctly

**Cause:** Incorrect Content-Type header

**Solution:**
```bash
# ✅ Correct
curl -H "Content-Type: application/x-www-form-urlencoded" ...

# ❌ Wrong
curl -H "Content-Type: application/json" ...  # Will not trigger Formatter
```

## Best Practices

### 1. Formatter Implementation

```java
// ✅ Recommended: Implement with @Component
@Component
public class MoneyFormatter implements Formatter<Money> {
    @Override
    public Money parse(String text, Locale locale) throws ParseException {
        // Robust parsing with multiple format support
        if (NumberUtils.isParsable(text)) {
            return Money.of(CurrencyUnit.of("TWD"), NumberUtils.createBigDecimal(text));
        }
        // Handle other formats...
    }
}

// ❌ Not recommended: Manual registration in WebMvcConfigurer
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new MoneyFormatter());  // Manual, not necessary
    }
}
```

### 2. Validation Strategy Selection

```java
// ✅ Recommended for most cases: Auto-validation (Method 2)
@PostMapping("/")
public Coffee add(@Valid NewCoffeeRequest request) {
    return service.save(request);
}

// ⚠️ Use when needed: Manual handling (Method 1)
@PostMapping("/")
public Coffee add(@Valid NewCoffeeRequest request, BindingResult result) {
    if (result.hasErrors()) {
        // Custom error handling logic
        throw new CustomValidationException(result);
    }
    return service.save(request);
}
```

### 3. Custom Error Messages

```java
// ✅ Recommended: Provide meaningful messages
public class NewCoffeeRequest {
    @NotEmpty(message = "咖啡名稱不能為空")
    private String name;
    
    @NotNull(message = "價格不能為空")
    @Min(value = 0, message = "價格必須大於等於 0")
    private Money price;
}

// ⚠️ Acceptable: Use default messages
public class NewCoffeeRequest {
    @NotEmpty  // Default: "不得是空的"
    private String name;
}
```

### 4. File Upload Handling

```java
// ✅ Recommended: try-with-resources
public List<Coffee> batchAdd(@RequestParam("file") MultipartFile file) {
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream()))) {
        // Process file
    } catch (IOException e) {
        log.error("File processing error", e);
        throw new FileProcessingException("Failed to process uploaded file", e);
    }
}

// ❌ Not recommended: Manual close
public List<Coffee> batchAdd(@RequestParam("file") MultipartFile file) {
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(...);
        // Process
    } finally {
        IOUtils.closeQuietly(reader);  // Deprecated, use try-with-resources
    }
}
```

### 5. Production vs Development Config

**Development (`application-dev.properties`):**
```properties
# Show detailed errors for debugging
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=always

# Show SQL for debugging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Production (`application-prod.properties`):**
```properties
# Hide sensitive information
server.error.include-message=never
server.error.include-binding-errors=never
server.error.include-stacktrace=never

# Disable SQL logging
spring.jpa.show-sql=false
```

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
    price bigint,              -- Stored in cents (10000 = TWD 100.00)
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
-- Initial coffee data (price in cents)
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

## Dependencies

```xml
<properties>
    <java.version>21</java.version>
    <joda-money.version>2.0.2</joda-money.version>
</properties>

<dependencies>
    <!-- Spring Boot Web (includes Spring MVC) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Bean Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Spring Cache -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
    
    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Joda Money -->
    <dependency>
        <groupId>org.joda</groupId>
        <artifactId>joda-money</artifactId>
        <version>${joda-money.version}</version>
    </dependency>
    
    <!-- Apache Commons Lang3 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## Best Practices Demonstrated

1. **Custom Formatter**: `MoneyFormatter` with `@Component` auto-registration
2. **Spring Boot Auto Configuration**: Leverage WebMVC Auto Configuration
3. **Automatic Validation**: Use `@Valid` without `BindingResult` for cleaner code
4. **Multiple Format Support**: Handle both "125.00" and "TWD 125.00" formats
5. **RESTful Error Responses**: Standard 400 Bad Request with detailed JSON errors
6. **Bean Validation Annotations**: `@NotEmpty`, `@NotNull` on request objects
7. **Multipart File Upload**: Batch processing from uploaded files
8. **Resource Management**: try-with-resources for automatic stream closing
9. **Locale Support**: Formatter implements internationalization-ready pattern
10. **Type Safety**: Strong typing with Money instead of double/BigDecimal

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Framework Formatter](https://docs.spring.io/spring-framework/reference/core/validation/format.html)
- [Spring Boot WebMVC Auto Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.auto-configuration)
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
