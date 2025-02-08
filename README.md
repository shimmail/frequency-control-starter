### 项目名称：`frequency-control-spring-boot-starter`

### 项目概述：

该项目是一个基于 Spring Boot 的限流功能实现，封装为自定义的 Spring Boot Starter。通过使用该 Starter，用户可以在应用程序中轻松实现基于 Redis 的频率控制（限流），并且可以根据需求自定义频率控制的规则，例如限制访问次数、时间范围和时间单位。

### 主要功能：

1. **频率控制注解（`FrequencyAnnotation`）**：
   - 提供了一个注解，允许开发者在方法上配置限流规则。用户可以通过注解指定：
     - `key`：用于区分不同的限流规则。
     - `maxCount`：单位时间范围内最大访问次数。
     - `timeRange`：频控时间范围（单位：秒）。
     - `timeUnit`：时间单位（默认是秒）。
2. **切面（`FrequencyControlAspect`）**：
   - 使用 AOP 切面技术，在请求方法执行前后进行频率控制的检查。
   - 通过 Redis 存储请求次数，并在每次请求时进行计数，超过限制的请求会抛出异常。
3. **自动配置（`FrequencyControlAutoConfiguration`）**：
   - 提供自动配置，允许用户在 Spring Boot 应用中自动启用频率控制功能。
4. **Redis 集成**：
   - 使用 `RedisTemplate` 与 Redis 进行交互，存储请求次数并对其进行递增和过期设置，确保限流逻辑的有效性。

### 项目结构：

1. **`org.example.frequencycontrolstarter`**：
   - **`annotation`**：定义了频率控制的注解 `FrequencyAnnotation`。
   - **`aspect`**：实现了频率控制的切面 `FrequencyControlAspect`，负责拦截方法并执行限流逻辑。
   - **`config`**：配置类 `FrequencyControlAutoConfiguration`，用于自动装配 `FrequencyControlAspect`。
   - **`FrequencyControlStarterApplication`**：Spring Boot 启动类，启动应用程序。
2. **`resources`**：
   - **`META-INF/spring.factories`**：Spring Boot 自动配置所需的配置文件，允许将该 starter 集成到其他项目中。
3. **`application.properties`**：配置文件，用于定义 Redis 配置等其他相关配置。

### 配置示例：

用户可以在 Spring Boot 项目中通过以下方式使用该 Starter：

1. **添加 Maven 依赖**：

   ```xml
   <dependency>
       <groupId>org.example</groupId>
       <artifactId>frequency-control-spring-boot-starter</artifactId>
       <version>0.0.1-SNAPSHOT</version>
   </dependency>
   ```

2. **配置 Redis**： 在 `application.properties` 或 `application.yml` 中配置 Redis 连接信息：

   ```properties
   spring.redis.host=localhost
   spring.redis.port=6379
   spring.redis.password=
   ```

3. **使用 `@FrequencyAnnotation` 注解进行限流**： 在需要进行频率控制的方法上使用注解：

   ```java
   @FrequencyAnnotation(key = "register", maxCount = 5, timeRange = 10, timeUnit = "SECONDS")
   @PostMapping("/user/register")
   public ResponseEntity<String> registerUser(@RequestBody User user) {
       return ResponseEntity.ok("User registered successfully");
   }
   ```