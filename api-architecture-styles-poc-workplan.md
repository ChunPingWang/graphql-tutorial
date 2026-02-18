# API Architecture Styles PoC — 工作計劃與執行清單

> **Tech Stack**: Java 23 + Spring Boot 4 + Jakarta EE 11  
> **Architecture**: Hexagonal (Ports & Adapters) + DDD  
> **Testing**: Testcontainers + JUnit 5 + ArchUnit + Contract Testing  
> **Domain**: Order Management System  
> **Duration**: 6 Weeks  
> **Reference**: [API Architecture Styles Made Simple](https://blog.levelupcoding.com/p/api-architecture-styles) — Nikki Siapno, Dec 2025

---

## 1. PoC 目標

| # | 目標 | 驗證方式 |
|---|------|----------|
| O-1 | 驗證五種 API style 在相同業務場景下的性能差異 | Load test 數據 (p50/p95/p99) |
| O-2 | 量化 Developer Experience 差異 | Time-to-first-endpoint、tooling 評估 |
| O-3 | 評估 Operational Complexity | Infrastructure footprint、monitoring 難度 |
| O-4 | 產出 Evidence-based Decision Framework | 加權評分矩陣 + 決策樹 |
| O-5 | 驗證 Hybrid Architecture 可行性 | API Gateway 整合 5 種 style 的端對端 demo |
| O-6 | 建立完整自動化測試策略 | 每層 test coverage ≥ 80%, CI green gate |

---

## 2. Technology Stack

| Layer | Technology | Notes |
|-------|-----------|-------|
| **Runtime** | Java 23 (LTS candidate) | Virtual Threads (Project Loom) 預設啟用 |
| **Framework** | Spring Boot 4.0 | Jakarta EE 11, baseline Java 17+ |
| **REST** | Spring Web (Functional Router + `@RestController`) | OpenAPI 3.1 via springdoc 2.x |
| **GraphQL** | Spring for GraphQL 2.x | graphql-java 22+, DataLoader |
| **gRPC** | grpc-spring-boot-starter 4.x | Protobuf 3, HTTP/2 |
| **WebSocket** | Spring WebSocket + STOMP | SockJS fallback, Redis session |
| **SOAP** | Spring WS 5.x + JAXB 4.0 | WSDL-first, Jakarta XML Binding |
| **Persistence** | Spring Data JPA 4.x + Hibernate 7 | Jakarta Persistence 3.2 |
| **Database** | PostgreSQL 17 | JSONB for flexible attributes |
| **Cache** | Redis 7.4 + Spring Cache abstraction | Lettuce client |
| **Observability** | Micrometer 2.x + OpenTelemetry | Prometheus + Grafana + Tempo |
| **Load Testing** | Gatling 3.12 + k6 | Java 23 virtual thread aware |
| **Build** | Gradle 8.x + Kotlin DSL | Multi-module project |
| **Container** | Docker Compose (local) / K8s (extended) | GraalVM native-image optional |
| **CI/CD** | GitHub Actions | Build → Test → Report pipeline |

### Testing Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Container Runtime** | Testcontainers 1.20+ | PostgreSQL, Redis, Kafka 等真實容器 |
| **Unit Test** | JUnit 5.11 + Mockito 5 + AssertJ 3 | Domain & Application layer |
| **Architecture Test** | ArchUnit 1.3 | Hexagonal architecture 守護 |
| **REST Integration** | `MockMvc` + `WebTestClient` + Testcontainers | Controller + full HTTP stack |
| **GraphQL Integration** | `HttpGraphQlTester` + `GraphQlTester` | Schema validation + resolver tests |
| **gRPC Integration** | `grpc-testing` + in-process server | Proto contract + streaming tests |
| **WebSocket Integration** | `WebSocketStompClient` + TC Redis | STOMP pub/sub + session failover |
| **SOAP Integration** | `MockWebServiceClient` + `WebServiceTemplate` | WSDL contract + WS-Security |
| **Contract Testing** | Spring Cloud Contract 5.x | Consumer-driven contracts across styles |
| **Database Test** | `@DataJpaTest` + TC PostgreSQL | Repository layer isolation |
| **Cache Test** | Testcontainers Redis | Cache hit/miss/eviction 驗證 |
| **E2E Test** | Testcontainers Compose | Full stack multi-container E2E |
| **Mutation Testing** | PIT (pitest) 1.17 | Test quality validation |
| **Coverage** | JaCoCo 0.8.12 | Per-module + aggregate coverage |

### Java 23 + Spring Boot 4 關鍵特性利用

- **Virtual Threads**: 所有 API adapter 預設使用 virtual threads，特別有利於 WebSocket 高連線數場景
- **Pattern Matching & Record Patterns**: Domain model 使用 sealed interface + record 建模
- **Structured Concurrency (Preview)**: 用於 Dashboard UC-2 的並行資料組裝
- **Spring Boot 4 AOT**: GraalVM native-image 測試啟動速度對比
- **Jakarta EE 11 namespace**: 全面遷移至 `jakarta.*`

---

## 3. Domain Model

```
┌─────────────┐     1:N     ┌─────────────┐     1:N     ┌─────────────┐
│  Customer    │────────────▶│    Order     │────────────▶│  OrderItem  │
│ id, name    │             │ id, status  │             │ id, qty     │
│ email, tier │             │ customerId  │             │ productId   │
└─────────────┘             │ total       │             │ unitPrice   │
                            └──────┬──────┘             └──────┬──────┘
                                   │ 1:N                       │ N:1
                            ┌──────▼──────┐             ┌──────▼──────┐
                            │ Notification│             │   Product   │
                            │ id, type    │             │ id, name    │
                            │ message     │             │ price, stock│
                            └─────────────┘             └─────────────┘
```

---

## 4. Use Case Matrix

| UC# | Use Case | 描述 | 核心挑戰 | 最佳候選 |
|-----|----------|------|----------|----------|
| UC-1 | Simple CRUD | Customer CRUD | Baseline 簡單性 | REST |
| UC-2 | Complex Query (Dashboard) | 一次取得 customer + orders + products | Over/under-fetching | GraphQL |
| UC-3 | High-Throughput Internal | Inventory 高頻呼叫 Product service | 延遲 & payload | gRPC |
| UC-4 | Real-Time Notification | 推送 order status 變更 | Connection 管理 | WebSocket |
| UC-5 | Transactional Operation | 下單 (payment + inventory check) | 原子性 & 可靠性 | SOAP |
| UC-6 | Batch Processing | 批次匯入 10K products | Streaming & 效率 | gRPC |

---

## 5. Project Structure (含完整測試目錄)

```
api-styles-poc/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/libs.versions.toml              # Version catalog
├── docker-compose.yml                     # Full stack (dev)
├── docker-compose-test.yml                # Lightweight (PG + Redis only)
│
├── domain/                                # 🔵 Domain Core (zero dependencies)
│   ├── src/main/java/.../domain/
│   │   ├── model/                         # Entities, VOs, Aggregates
│   │   ├── port/inbound/                  # Use Case ports
│   │   ├── port/outbound/                 # Repository ports
│   │   └── service/                       # Domain Services
│   └── src/test/java/.../domain/
│       ├── model/
│       │   ├── CustomerTest.java          # ✅ Invariants, equality
│       │   ├── OrderTest.java             # ✅ State machine
│       │   └── ProductTest.java           # ✅ Stock rules
│       └── service/
│           └── OrderDomainServiceTest.java # ✅ Cross-aggregate rules
│
├── application/                           # 🟢 Application Layer
│   ├── src/main/java/.../application/
│   └── src/test/java/.../application/
│       ├── CustomerServiceTest.java       # ✅ Mock ports
│       ├── OrderServiceTest.java          # ✅ Transactional
│       ├── DashboardServiceTest.java      # ✅ Structured Concurrency
│       └── ProductBatchServiceTest.java   # ✅ Batch edge cases
│
├── adapter-rest/                          # 🟠 REST
│   ├── src/main/java/
│   └── src/test/java/
│       ├── CustomerControllerTest.java              # ✅ @WebMvcTest
│       ├── CustomerControllerIntegrationTest.java   # ✅ @IntegrationTest (TC)
│       ├── DashboardControllerIntegrationTest.java  # ✅ TC
│       ├── OrderControllerIntegrationTest.java      # ✅ TC
│       ├── RestCachingIntegrationTest.java           # ✅ TC Redis
│       └── RestContractTest.java                    # ✅ Contract
│
├── adapter-graphql/                       # 🟣 GraphQL
│   ├── src/main/
│   └── src/test/java/
│       ├── CustomerResolverTest.java                # ✅ @GraphQlTest
│       ├── DashboardResolverIntegrationTest.java    # ✅ TC + N+1
│       ├── GraphQlGovernanceTest.java               # ✅ Depth/complexity
│       ├── DataLoaderBatchingTest.java              # ✅ Query count
│       └── GraphQlContractTest.java                 # ✅ Schema compat
│
├── adapter-grpc/                          # 🔴 gRPC
│   ├── src/main/
│   └── src/test/java/
│       ├── CustomerGrpcServiceTest.java             # ✅ In-process
│       ├── OrderGrpcServiceIntegrationTest.java     # ✅ TC
│       ├── BulkImportStreamingTest.java             # ✅ Client-stream 10K
│       ├── OrderStatusStreamingTest.java            # ✅ Server-stream
│       ├── GrpcDeadlineTest.java                    # ✅ Deadline
│       └── GrpcProtoCompatibilityTest.java          # ✅ Proto compat
│
├── adapter-websocket/                     # 🟡 WebSocket
│   ├── src/main/java/
│   └── src/test/java/
│       ├── OrderStatusHandlerTest.java              # ✅ STOMP client
│       ├── WebSocketIntegrationTest.java            # ✅ TC Redis
│       ├── WebSocketReconnectionTest.java           # ✅ Reconnect
│       ├── WebSocketSessionFailoverTest.java        # ✅ Redis failover
│       └── WebSocketBackpressureTest.java           # ✅ Slow consumer
│
├── adapter-soap/                          # ⚪ SOAP
│   ├── src/main/
│   └── src/test/java/
│       ├── CustomerSoapEndpointTest.java            # ✅ MockWebServiceClient
│       ├── SoapIntegrationTest.java                 # ✅ TC
│       ├── WsSecurityTest.java                      # ✅ Auth
│       └── SoapWsdlContractTest.java                # ✅ WSDL compat
│
├── infrastructure/                        # 🔧 Outbound Adapters
│   ├── src/main/java/
│   └── src/test/java/
│       ├── persistence/
│       │   ├── JpaCustomerRepositoryTest.java       # ✅ @DatabaseTest TC PG
│       │   ├── JpaOrderRepositoryTest.java          # ✅ Complex queries
│       │   ├── JpaProductRepositoryTest.java        # ✅ Bulk ops
│       │   └── FlywayMigrationTest.java             # ✅ Idempotency
│       └── cache/
│           ├── RedisCacheIntegrationTest.java       # ✅ @CacheTest TC Redis
│           └── CacheConsistencyTest.java            # ✅ Write-through
│
├── test-support/                          # 🧪 Shared Test Infrastructure
│   └── src/main/java/.../test/
│       ├── containers/
│       │   ├── PostgresContainerConfig.java         # @ServiceConnection
│       │   ├── RedisContainerConfig.java            # @ServiceConnection
│       │   └── ComposeContainerConfig.java          # E2E compose
│       ├── fixtures/
│       │   ├── CustomerFixture.java                 # Builder-pattern
│       │   ├── OrderFixture.java
│       │   └── ProductFixture.java
│       ├── annotations/
│       │   ├── IntegrationTest.java                 # TC PG + Redis
│       │   ├── DatabaseTest.java                    # TC PG only
│       │   ├── CacheTest.java                       # TC Redis only
│       │   └── E2eTest.java                         # TC Compose
│       └── assertions/
│           ├── RestAssertions.java
│           ├── GraphQlAssertions.java
│           └── GrpcAssertions.java
│
├── architecture-tests/                    # 🏛️ ArchUnit Guards
│   └── src/test/java/
│       ├── HexagonalArchitectureTest.java           # Dependency rules
│       ├── LayerDependencyTest.java                 # No domain→infra
│       ├── NamingConventionTest.java
│       └── CodingRulesTest.java                     # No field injection
│
├── e2e-tests/                             # 🔄 End-to-End
│   └── src/test/java/
│       ├── OrderLifecycleE2eTest.java               # Cross-style flow
│       ├── CrossStyleConsistencyE2eTest.java        # Same data, 5 styles
│       ├── SecurityE2eTest.java                     # Auth all styles
│       └── ObservabilityE2eTest.java                # Traces emitted
│
├── load-test/                             # 📊 Performance
│   ├── gatling/ ├── k6/ └── results/
│
└── docs/
    ├── architecture-decision-records/
    ├── test-strategy.md
    └── grafana-dashboards/
```

---

## 6. 完整測試策略 (Testcontainers-Centric)

### 6.1 測試金字塔

```
                      ┌─────────┐
                      │  E2E    │  TC Compose (全 5 styles)
                     ─┤ ~10min  ├─
                    / └─────────┘ \
                   /  ┌───────────┐ \
                  │   │Integration│  │  TC PostgreSQL + Redis
                  │   │  ~5min    │  │  Per adapter, per UC
                 ─┤   └───────────┘  ├─
                / └───────────────────┘ \
               /    ┌─────────────┐      \
              │     │    Unit     │       │  Pure JUnit 5 + Mockito
              │     │   ~30s     │       │  No container, no Spring
             ─┤     └─────────────┘      ├─
            / └───────────────────────────┘ \
           /      ┌───────────────┐          \
          │       │  ArchUnit     │           │  Static analysis, ~5s
          │       │  Guards       │           │
          └───────┴───────────────┴───────────┘
```

### 6.2 測試分層規格

| Layer | Scope | Container | Spring Context | Coverage | Time |
|-------|-------|-----------|---------------|----------|------|
| **ArchUnit** | Dependency rules | None | None | 100% rules | < 5s |
| **Unit** | Domain + App | None | None | ≥ 90% | < 30s |
| **Slice** | Single adapter | None | `@WebMvcTest` etc. | ≥ 80% | < 1min |
| **Integration** | Adapter + infra + real DB | TC PG + Redis | `@SpringBootTest` | ≥ 80%/UC | < 5min |
| **Contract** | API schema compat | Varies | Varies | All APIs | < 2min |
| **E2E** | Full stack cross-style | TC Compose | Full | Critical paths | < 10min |

### 6.3 Testcontainers 配置

#### Shared Container Definitions (`test-support` module)

```java
// PostgresContainerConfig.java — @ServiceConnection auto-configures datasource
@TestConfiguration
public class PostgresContainerConfig {
    @ServiceConnection
    @Bean
    static PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
            .withDatabaseName("apistyles_test")
            .withUsername("test").withPassword("test")
            .withInitScript("db/init-test-schema.sql")
            .withReuse(true);  // 跨測試重用，CI 加速 ~60%
    }
}
```

```java
// RedisContainerConfig.java
@TestConfiguration
public class RedisContainerConfig {
    @ServiceConnection
    @Bean
    static GenericContainer<?> redis() {
        return new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);
    }
}
```

```java
// ComposeContainerConfig.java — E2E full stack
@TestConfiguration
public class ComposeContainerConfig {
    @Bean
    static ComposeContainer compose() {
        return new ComposeContainer(new File("docker-compose-test.yml"))
            .withExposedService("postgres", 5432, Wait.forListeningPort())
            .withExposedService("redis", 6379, Wait.forListeningPort())
            .withLocalCompose(true);
    }
}
```

#### Custom Meta-Annotations

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import({PostgresContainerConfig.class, RedisContainerConfig.class})
@ActiveProfiles("integration-test")
@Tag("integration")
public @interface IntegrationTest {}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
@Tag("database")
public @interface DatabaseTest {}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@Import(RedisContainerConfig.class)
@ActiveProfiles("cache-test")
@Tag("cache")
public @interface CacheTest {}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(ComposeContainerConfig.class)
@ActiveProfiles("e2e")
@Tag("e2e")
public @interface E2eTest {}
```

#### Test Data Fixtures (Builder Pattern)

```java
public class CustomerFixture {
    public static Customer.Builder aCustomer() {
        return Customer.builder()
            .id(UUID.randomUUID())
            .name("Alice Johnson").email("alice@example.com")
            .tier(CustomerTier.GOLD);
    }
    public static Customer goldCustomer()   { return aCustomer().build(); }
    public static Customer silverCustomer() { return aCustomer().tier(SILVER).build(); }
    public static List<Customer> bulk(int n) {
        return IntStream.range(0, n)
            .mapToObj(i -> aCustomer().name("C-" + i).email("c" + i + "@test.com").build())
            .toList();
    }
}
```

### 6.4 各層測試詳細規格

#### 6.4.1 Domain Layer (Pure Unit — No Container)

| Test Class | What It Tests | Key Assertions |
|-----------|---------------|---------------|
| `CustomerTest` | Entity invariants, value object equality | Email format, tier transition rules |
| `OrderTest` | State machine (CREATED→CONFIRMED→SHIPPED→DELIVERED→CANCELLED) | Invalid transitions throw `DomainException` |
| `ProductTest` | Stock management (reserve, release) | Negative stock rejected |
| `OrderDomainServiceTest` | Cross-aggregate business rules | Total calculation, tier discount |

```java
class OrderTest {
    @ParameterizedTest
    @CsvSource({"CREATED,CANCELLED,true", "CONFIRMED,CANCELLED,true",
                "SHIPPED,CANCELLED,false", "DELIVERED,CANCELLED,false"})
    void shouldEnforceCancellationRules(OrderStatus from, OrderStatus to, boolean allowed) {
        var order = OrderFixture.anOrder().status(from).build();
        if (allowed) assertThatCode(order::cancel).doesNotThrowAnyException();
        else assertThatThrownBy(order::cancel).isInstanceOf(InvalidStateTransitionException.class);
    }
}
```

#### 6.4.2 Application Layer (Unit — Mock Ports)

```java
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    @Mock CustomerRepository customerRepo;
    @Mock OrderRepository orderRepo;
    @Mock ProductRepository productRepo;
    @InjectMocks DashboardService sut;

    @Test
    void shouldAssembleDashboardFromMultipleSources() {
        var id = UUID.randomUUID();
        when(customerRepo.findById(id)).thenReturn(Optional.of(CustomerFixture.goldCustomer()));
        when(orderRepo.findRecentByCustomerId(id, 10)).thenReturn(OrderFixture.recentOrders(5));
        when(productRepo.findTopByOrderFrequency(id, 5)).thenReturn(ProductFixture.topProducts(5));

        var dashboard = sut.getDashboard(id);
        assertThat(dashboard.recentOrders()).hasSize(5);
        assertThat(dashboard.topProducts()).hasSize(5);
    }

    @Test
    void shouldHandlePartialFailure_StructuredConcurrency() {
        when(customerRepo.findById(any())).thenReturn(Optional.of(CustomerFixture.goldCustomer()));
        when(orderRepo.findRecentByCustomerId(any(), anyInt()))
            .thenThrow(new RuntimeException("DB timeout"));
        assertThatThrownBy(() -> sut.getDashboard(UUID.randomUUID()))
            .isInstanceOf(DashboardAssemblyException.class);
    }
}
```

#### 6.4.3 Infrastructure Layer (TC PostgreSQL + Redis)

```java
@DatabaseTest
class JpaCustomerRepositoryTest {
    @Autowired JpaCustomerRepository repo;

    @Test
    void shouldHandleOptimisticLocking() {
        var entity = repo.save(CustomerEntityFixture.gold());
        var c1 = repo.findById(entity.getId()).orElseThrow();
        var c2 = repo.findById(entity.getId()).orElseThrow();
        c1.setName("A"); repo.saveAndFlush(c1);
        c2.setName("B");
        assertThatThrownBy(() -> repo.saveAndFlush(c2))
            .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void shouldPageCustomersByTier() {
        repo.saveAll(CustomerEntityFixture.mixed(50));
        var page = repo.findByTier("GOLD", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSizeLessThanOrEqualTo(10)
            .allSatisfy(c -> assertThat(c.getTier()).isEqualTo("GOLD"));
    }
}
```

```java
@CacheTest
class RedisCacheIntegrationTest {
    @Autowired CustomerService svc;
    @Autowired CacheManager cache;

    @Test
    void shouldEvictCacheOnUpdate() {
        var id = seedCustomer();
        svc.findById(id);                // cache populated
        svc.updateName(id, "New Name");   // should evict
        assertThat(cache.getCache("customers").get(id)).isNull();
    }

    @Test
    void shouldRespectTtlExpiry() throws Exception {
        var id = seedCustomer();
        svc.findById(id);
        Thread.sleep(Duration.ofSeconds(65)); // TTL = 60s
        assertThat(cache.getCache("customers").get(id)).isNull();
    }
}
```

#### 6.4.4 REST Adapter Tests

```java
@IntegrationTest
class CustomerControllerIntegrationTest {
    @Autowired WebTestClient web;

    @Test
    void shouldCreateAndReturn201WithLocation() {
        web.post().uri("/api/v1/customers")
            .contentType(APPLICATION_JSON)
            .bodyValue("""{"name":"Alice","email":"alice@test.com","tier":"GOLD"}""")
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists("Location")
            .expectBody().jsonPath("$.tier").isEqualTo("GOLD")
                         .jsonPath("$._links.self.href").isNotEmpty();
    }

    @Test
    void shouldReturn304WhenETagMatches() {
        var result = web.post().uri("/api/v1/customers")
            .bodyValue("""{"name":"Bob","email":"bob@test.com","tier":"SILVER"}""")
            .exchange().returnResult(String.class);
        var etag = result.getResponseHeaders().getETag();
        var location = result.getResponseHeaders().getLocation().getPath();

        web.get().uri(location).header("If-None-Match", etag)
            .exchange().expectStatus().isNotModified();
    }

    @Test
    void shouldReturnRfc9457ProblemDetails() {
        web.post().uri("/api/v1/customers")
            .bodyValue("""{"name":"","email":"invalid"}""")
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
            .expectBody().jsonPath("$.type").isNotEmpty()
                         .jsonPath("$.violations").isArray();
    }
}
```

#### 6.4.5 GraphQL Adapter Tests

```java
@IntegrationTest
class DataLoaderBatchingTest {
    @Autowired HttpGraphQlTester gql;

    @Test
    void shouldBatchPreventingNPlusOne() {
        seedCustomerWith20Orders(); // 20 orders × 3 items = 60 product refs
        var counter = new QueryCounter(dataSource);
        counter.start();

        gql.document("""
            query { dashboard(customerId: "$id") {
              recentOrders { items { product { name price } } }
            }}""").execute().errors().verify();

        // Without DataLoader: 81 queries → With: ≤5
        assertThat(counter.count()).isLessThanOrEqualTo(5);
    }
}

@IntegrationTest
class GraphQlGovernanceTest {
    @Autowired HttpGraphQlTester gql;

    @Test
    void shouldRejectExcessiveDepth() {
        gql.document("""
            query { customer(id:"1") { orders { items { product {
              category { subcategory { name } } } } } } }""")
            .execute().errors()
            .expect(e -> e.getMessage().contains("maximum depth"));
    }

    @Test
    void shouldRejectExcessiveComplexity() {
        gql.document("""
            query { allCustomers(first:100) { orders(first:100) {
              items(first:100) { product { name } } } } }""")
            .execute().errors()
            .expect(e -> e.getMessage().contains("complexity"));
    }
}
```

#### 6.4.6 gRPC Adapter Tests

```java
@IntegrationTest
class BulkImportStreamingTest {
    private ProductServiceGrpc.ProductServiceStub asyncStub;

    @Test
    void shouldStream10KProductsSuccessfully() throws Exception {
        var latch = new CountDownLatch(1);
        var result = new AtomicReference<ImportResult>();

        var req = asyncStub.bulkImportProducts(new StreamObserver<>() {
            public void onNext(ImportResult r) { result.set(r); }
            public void onError(Throwable t)   { fail("error: " + t); }
            public void onCompleted()           { latch.countDown(); }
        });

        for (int i = 0; i < 10_000; i++) {
            req.onNext(ProductRecord.newBuilder()
                .setName("P-" + i).setPrice(randomPrice())
                .setCategory("CAT-" + (i % 20)).setStock(random.nextInt(1000))
                .build());
        }
        req.onCompleted();

        assertThat(latch.await(30, SECONDS)).isTrue();
        assertThat(result.get().getSuccessCount()).isEqualTo(10_000);
    }

    @Test
    void shouldHandlePartialFailure() { /* invalid records → failures reported */ }

    @Test
    void shouldRespectDeadline() {
        var stub = asyncStub.withDeadlineAfter(1, MILLISECONDS);
        // expect DEADLINE_EXCEEDED
    }
}

@IntegrationTest
class OrderStatusStreamingTest {
    @Test
    void shouldStreamStatusUpdatesAndHandleCancellation() {
        var orderId = seedOrder(CREATED);
        var messages = new CopyOnWriteArrayList<StatusUpdate>();

        var call = asyncStub.streamOrderStatus(
            OrderStatusRequest.newBuilder().setOrderId(orderId.toString()).build(),
            new StreamObserver<>() {
                public void onNext(StatusUpdate u) { messages.add(u); }
                public void onError(Throwable t) {}
                public void onCompleted() {}
            });

        // trigger 3 status changes
        orderService.confirm(orderId);
        orderService.ship(orderId);
        orderService.deliver(orderId);

        await().atMost(10, SECONDS).until(() -> messages.size() >= 3);
        assertThat(messages).extracting(StatusUpdate::getStatus)
            .containsExactly("CONFIRMED", "SHIPPED", "DELIVERED");

        call.cancel("client done", null); // graceful cancel
    }
}
```

#### 6.4.7 WebSocket Adapter Tests

```java
@IntegrationTest
class WebSocketIntegrationTest {
    @LocalServerPort int port;

    @Test
    void shouldReceiveOrderStatusViaSTOMP() throws Exception {
        var orderId = seedOrder(CREATED);
        var received = new LinkedBlockingQueue<OrderStatusMessage>();

        var session = new WebSocketStompClient(new StandardWebSocketClient())
            .connectAsync("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter(){})
            .get(5, SECONDS);

        session.subscribe("/topic/orders/" + orderId + "/status", new StompFrameHandler() {
            public Type getPayloadType(StompHeaders h) { return OrderStatusMessage.class; }
            public void handleFrame(StompHeaders h, Object p) { received.add((OrderStatusMessage)p); }
        });

        orderService.updateStatus(orderId, CONFIRMED);

        var msg = received.poll(5, SECONDS);
        assertThat(msg).isNotNull();
        assertThat(msg.newStatus()).isEqualTo("CONFIRMED");
        session.disconnect();
    }

    @Test
    void shouldDeliverOnlyToSubscribedUser() { /* user-specific queue isolation */ }

    @Test
    void shouldBroadcastToMultipleSubscribers() {
        var orderId = seedOrder(CREATED);
        var q1 = new LinkedBlockingQueue<>();
        var q2 = new LinkedBlockingQueue<>();
        var s1 = connectAndSubscribe(orderId, q1);
        var s2 = connectAndSubscribe(orderId, q2);

        orderService.updateStatus(orderId, CONFIRMED);

        assertThat(q1.poll(5, SECONDS)).isNotNull();
        assertThat(q2.poll(5, SECONDS)).isNotNull();
    }
}

@IntegrationTest
class WebSocketSessionFailoverTest {
    @Test
    void shouldRecoverAfterRedisRestart() {
        // 1. Connect + subscribe
        // 2. Stop Redis container
        // 3. Restart Redis container
        // 4. Reconnect client
        // 5. Verify subscription still works
    }
}

@IntegrationTest
class WebSocketBackpressureTest {
    @Test
    void shouldHandleSlowConsumer() {
        // 1. Subscribe with artificial delay in handler
        // 2. Blast 1000 rapid status updates
        // 3. Verify no OOM, messages buffered or dropped per policy
    }
}
```

#### 6.4.8 SOAP Adapter Tests

```java
@IntegrationTest
class WsSecurityTest {
    @Autowired WebServiceTemplate ws;

    @Test
    void shouldRejectUnauthenticatedRequest() {
        var req = createGetCustomerRequest("123");
        assertThatThrownBy(() -> ws.marshalSendAndReceive(req))
            .isInstanceOf(SoapFaultClientException.class)
            .hasMessageContaining("Security");
    }

    @Test
    void shouldAcceptValidUsernameToken() {
        var req = createGetCustomerRequest("123");
        addWsSecurity(req, "validUser", "validPass");
        var resp = ws.marshalSendAndReceive(req);
        assertThat(resp).isNotNull();
    }
}
```

#### 6.4.9 Architecture Guard Tests (ArchUnit)

```java
class HexagonalArchitectureTest {
    static final JavaClasses classes = new ClassFileImporter()
        .importPackages("com.poc.apistyles");

    @Test
    void domainMustNotDependOnAdaptersOrInfra() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..adapter..", "..infrastructure..", "..application..")
            .check(classes);
    }

    @Test
    void domainMustNotDependOnFrameworks() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "jakarta.persistence..")
            .check(classes);
    }

    @Test
    void adaptersMustNotDependOnEachOther() {
        slices().matching("..adapter.(*)..")
            .should().notDependOnEachOther().check(classes);
    }

    @Test
    void portsMustBeInterfaces() {
        classes().that().resideInAPackage("..port..").should().beInterfaces().check(classes);
    }
}

class CodingRulesTest {
    @Test
    void noFieldInjection() {
        noFields().should().beAnnotatedWith(Autowired.class).check(classes);
    }

    @Test
    void controllersNeverAccessRepositories() {
        noClasses().that().haveSimpleNameEndingWith("Controller")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
            .check(classes);
    }
}
```

#### 6.4.10 E2E Tests (TC Compose)

```java
@E2eTest
class CrossStyleConsistencyE2eTest {
    @Autowired WebTestClient rest;
    @Autowired HttpGraphQlTester gql;
    @Autowired ProductServiceGrpc.ProductServiceBlockingStub grpc;
    @Autowired WebServiceTemplate soap;

    @Test
    void shouldReturnIdenticalCustomerAcrossAllStyles() {
        var id = seedCustomer("Alice", "alice@test.com", "GOLD");

        var restName  = rest.get().uri("/api/v1/customers/" + id).exchange()
            .expectBody().jsonPath("$.name").returnResult().toString();

        var gqlName = gql.document("query{customer(id:\"" + id + "\"){name}}")
            .execute().path("customer.name").entity(String.class).get();

        var grpcName = grpc.getCustomer(GetCustomerRequest.newBuilder()
            .setId(id.toString()).build()).getName();

        assertThat(restName).isEqualTo(gqlName).isEqualTo(grpcName).isEqualTo("Alice");
    }

    @Test
    void shouldMaintainTransactionalConsistency() {
        // Place same order via REST, GraphQL, gRPC, SOAP
        // Verify: inventory decremented exactly once per style
    }
}

@E2eTest
class OrderLifecycleE2eTest {
    @Test
    void shouldFlowAcrossStyles() {
        // 1. REST:      POST /orders → create order
        // 2. GraphQL:   query dashboard → see new order
        // 3. gRPC:      updateStatus(CONFIRMED)
        // 4. WebSocket:  receive status push
        // 5. SOAP:      audit trail query
    }
}
```

---

## 7. CI Pipeline Test Stages

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌───────────┐   ┌────────┐
│ Compile  │──▶│ ArchUnit │──▶│  Unit    │──▶│Integration│──▶│  E2E   │
│ + Lint   │   │ Guards   │   │  Tests   │   │  Tests    │   │ Tests  │
│          │   │  ~5s     │   │  ~30s    │   │ TC PG+Redis│  │TC Comp.│
└──────────┘   └──────────┘   └──────────┘   │  ~5min    │   │ ~10min │
                                              └───────────┘   └────────┘
                                                     │              │
                                                     ▼              ▼
                                         ┌────────────────────────────────┐
                                         │  JaCoCo Aggregate Report       │
                                         │  PIT Mutation Report            │
                                         │  Gate: line≥85%, mutation≥65%   │
                                         └────────────────────────────────┘
```

**Gradle Tag-Based Execution:**

```kotlin
// build.gradle.kts
tasks.test {
    useJUnitPlatform { excludeTags("integration", "database", "cache", "e2e") }
}
tasks.register<Test>("integrationTest") {
    useJUnitPlatform { includeTags("integration", "database", "cache") }
    shouldRunAfter(tasks.test)
}
tasks.register<Test>("e2eTest") {
    useJUnitPlatform { includeTags("e2e") }
    shouldRunAfter(tasks.named("integrationTest"))
}
```

---

## 8. Test Coverage Targets

| Module | Line Coverage | Mutation Score | Key Metrics |
|--------|-------------|---------------|-------------|
| `domain` | ≥ 95% | ≥ 75% | Entity invariants, state machine |
| `application` | ≥ 90% | ≥ 70% | Use case orchestration |
| `adapter-rest` | ≥ 85% | ≥ 60% | All endpoints + error paths + caching |
| `adapter-graphql` | ≥ 85% | ≥ 60% | Resolvers + governance + DataLoader |
| `adapter-grpc` | ≥ 80% | ≥ 55% | Unary + streaming + deadlines |
| `adapter-websocket` | ≥ 80% | ≥ 55% | Pub/sub + reconnection + failover |
| `adapter-soap` | ≥ 80% | ≥ 55% | Endpoints + WS-Security |
| `infrastructure` | ≥ 85% | ≥ 60% | Queries + cache behavior |
| **Aggregate** | **≥ 85%** | **≥ 65%** | **CI gate: fail build if below** |

---

## 9. 六週執行計劃

### Week 1: Foundation & Setup

#### Project Setup
- [ ] **W1.1** Gradle multi-module 骨架 (Java 23 + Spring Boot 4)
- [ ] **W1.2** `gradle/libs.versions.toml` — version catalog
- [ ] **W1.3** `docker-compose.yml` + `docker-compose-test.yml`
- [ ] **W1.4** Virtual Threads 啟用 (`spring.threads.virtual.enabled=true`)
- [ ] **W1.5** CI pipeline (GitHub Actions: build → test → coverage)

#### Domain & Application
- [ ] **W1.6** Domain module — entities (sealed interface + record)
- [ ] **W1.7** Domain module — port interfaces (inbound + outbound)
- [ ] **W1.8** Domain module — domain services
- [ ] **W1.9** Application module — use case handlers
- [ ] **W1.10** Infrastructure module — JPA entities + repos + mappers
- [ ] **W1.11** Flyway migrations + test data seeding

#### Testing Infrastructure
- [ ] **W1.12** `test-support` module — TC 共用配置
- [ ] **W1.13** `PostgresContainerConfig` + `RedisContainerConfig` (with `@ServiceConnection`)
- [ ] **W1.14** Meta-annotations: `@IntegrationTest`, `@DatabaseTest`, `@CacheTest`, `@E2eTest`
- [ ] **W1.15** Test Fixtures: `CustomerFixture`, `OrderFixture`, `ProductFixture`
- [ ] **W1.16** Domain unit tests — entity invariants, state machine (coverage ≥ 90%)
- [ ] **W1.17** Application unit tests — mock ports, verify orchestration
- [ ] **W1.18** `@DatabaseTest` — JPA repos with TC PG (CRUD, locking, queries)
- [ ] **W1.19** `FlywayMigrationTest` — migration idempotency
- [ ] **W1.20** ArchUnit tests — `HexagonalArchitectureTest`, `CodingRulesTest`
- [ ] **W1.21** JaCoCo + PIT 配置 — CI gate (line ≥ 85%, mutation ≥ 65%)

**Deliverable**: Running skeleton + domain core + DB + CI + test infrastructure

---

### Week 2: REST + GraphQL Adapters

#### REST (W2.1–W2.6)
- [ ] **W2.1** `CustomerController` — CRUD + HATEOAS
- [ ] **W2.2** `DashboardController` — aggregate endpoint (UC-2)
- [ ] **W2.3** `OrderController` — place order (UC-5)
- [ ] **W2.4** `ProductBatchController` — chunked upload (UC-6)
- [ ] **W2.5** Caching — `Cache-Control`, `ETag`, Redis
- [ ] **W2.6** OpenAPI 3.1 + RFC 9457 error handling

#### REST Tests (W2.7–W2.12)
- [ ] **W2.7** `CustomerControllerTest` — `@WebMvcTest` slice (no container)
- [ ] **W2.8** `CustomerControllerIntegrationTest` — `@IntegrationTest` TC PG+Redis
- [ ] **W2.9** `DashboardControllerIntegrationTest` — aggregate response
- [ ] **W2.10** `OrderControllerIntegrationTest` — rollback on failure
- [ ] **W2.11** `RestCachingIntegrationTest` — TC Redis: ETag 304, TTL
- [ ] **W2.12** `RestContractTest` — Spring Cloud Contract

#### GraphQL (W2.13–W2.18)
- [ ] **W2.13** Schema (`schema.graphqls`) — Query, Mutation, types
- [ ] **W2.14** Resolvers (Customer, Dashboard)
- [ ] **W2.15** DataLoader — N+1 prevention
- [ ] **W2.16** Governance — depth 5, complexity 100
- [ ] **W2.17** Mutations (PlaceOrder, BatchImport)
- [ ] **W2.18** Subscription 探索 (UC-4)

#### GraphQL Tests (W2.19–W2.24)
- [ ] **W2.19** `CustomerResolverTest` — `@GraphQlTest` slice
- [ ] **W2.20** `DashboardResolverIntegrationTest` — TC PG, N+1 detection
- [ ] **W2.21** `DataLoaderBatchingTest` — query count ≤ 5
- [ ] **W2.22** `GraphQlGovernanceTest` — depth/complexity rejection
- [ ] **W2.23** `GraphQlContractTest` — schema backward compat
- [ ] **W2.24** `RedisCacheIntegrationTest` — cache hit/miss/eviction

**Deliverable**: REST + GraphQL with full slice + integration + contract + cache tests

---

### Week 3: gRPC + WebSocket Adapters

#### gRPC (W3.1–W3.6)
- [ ] **W3.1** Proto files — customer, order, product
- [ ] **W3.2** Unary RPCs (UC-1, UC-2, UC-5)
- [ ] **W3.3** Client-streaming (UC-6)
- [ ] **W3.4** Server-streaming (UC-4)
- [ ] **W3.5** Tuning — keep-alive, deadlines, pooling
- [ ] **W3.6** Health check + reflection

#### gRPC Tests (W3.7–W3.12)
- [ ] **W3.7** `CustomerGrpcServiceTest` — in-process server, mock service
- [ ] **W3.8** `OrderGrpcServiceIntegrationTest` — TC PG full stack
- [ ] **W3.9** `BulkImportStreamingTest` — 10K stream, backpressure, partial failure
- [ ] **W3.10** `OrderStatusStreamingTest` — lifecycle, client cancellation
- [ ] **W3.11** `GrpcDeadlineTest` — DEADLINE_EXCEEDED
- [ ] **W3.12** `GrpcProtoCompatibilityTest` — backward compat

#### WebSocket (W3.13–W3.17)
- [ ] **W3.13** STOMP config + SockJS fallback
- [ ] **W3.14** Order status pub/sub (UC-4)
- [ ] **W3.15** User-specific notifications
- [ ] **W3.16** Redis session store
- [ ] **W3.17** Heartbeat + reconnection

#### WebSocket Tests (W3.18–W3.23)
- [ ] **W3.18** `OrderStatusHandlerTest` — STOMP subscribe + receive
- [ ] **W3.19** `WebSocketIntegrationTest` — TC Redis: full lifecycle
- [ ] **W3.20** `WebSocketReconnectionTest` — disconnect → reconnect → no loss
- [ ] **W3.21** `WebSocketSessionFailoverTest` — TC Redis stop/restart
- [ ] **W3.22** `WebSocketBackpressureTest` — slow consumer buffering
- [ ] **W3.23** Virtual Threads stress — 1K concurrent WS connections

**Deliverable**: gRPC + WebSocket with streaming, failover, resilience tests

---

### Week 4: SOAP + Security + E2E

#### SOAP (W4.1–W4.5)
- [ ] **W4.1** WSDL (contract-first)
- [ ] **W4.2** JAXB 4.0 binding
- [ ] **W4.3** Endpoints (UC-1, UC-5)
- [ ] **W4.4** WS-Security
- [ ] **W4.5** MTOM (UC-6)

#### SOAP Tests (W4.6–W4.9)
- [ ] **W4.6** `CustomerSoapEndpointTest` — MockWebServiceClient
- [ ] **W4.7** `SoapIntegrationTest` — TC PG
- [ ] **W4.8** `WsSecurityTest` — reject/accept auth
- [ ] **W4.9** `SoapWsdlContractTest` — WSDL compat

#### Security (W4.10–W4.13)
- [ ] **W4.10** REST: OAuth 2.0 Resource Server (JWT)
- [ ] **W4.11** GraphQL: JWT + query-level auth
- [ ] **W4.12** gRPC: mTLS + JWT interceptor
- [ ] **W4.13** WebSocket: STOMP CONNECT auth

#### Security Tests (W4.14–W4.17)
- [ ] **W4.14** REST: 401/403/200 matrix test
- [ ] **W4.15** GraphQL: mutation blocked without auth
- [ ] **W4.16** gRPC: mTLS failure, JWT rejection
- [ ] **W4.17** WebSocket: CONNECT rejected without credentials

#### E2E Tests (W4.18–W4.22)
- [ ] **W4.18** `ComposeContainerConfig` — TC Compose full stack
- [ ] **W4.19** `CrossStyleConsistencyE2eTest` — same data across all styles
- [ ] **W4.20** `OrderLifecycleE2eTest` — REST→GraphQL→gRPC→WS→SOAP flow
- [ ] **W4.21** `SecurityE2eTest` — auth enforcement all 5 styles
- [ ] **W4.22** `ObservabilityE2eTest` — traces + metrics emitted

#### Observability (W4.23–W4.25)
- [ ] **W4.23** Micrometer per-style metrics
- [ ] **W4.24** OpenTelemetry distributed tracing
- [ ] **W4.25** Grafana dashboards (per-style + comparison)

**Deliverable**: All 5 APIs secured + E2E validated + observable

---

### Week 5: Load Testing

- [ ] **W5.1** S-1: Simple CRUD — 5 styles × 3 levels
- [ ] **W5.2** S-2: Dashboard — REST vs GraphQL vs gRPC
- [ ] **W5.3** S-3: High-Throughput — 10K concurrent
- [ ] **W5.4** S-4: Real-Time — WS vs gRPC stream vs GQL subscription
- [ ] **W5.5** S-5: Bulk Import — gRPC stream vs REST vs SOAP MTOM
- [ ] **W5.6** S-6: Mixed Workload — 2K users
- [ ] **W5.7** Gatling HTML reports
- [ ] **W5.8** Prometheus/Grafana snapshots
- [ ] **W5.9** Payload size data (tcpdump)
- [ ] **W5.10** Resource utilization (CPU, mem, GC, threads)
- [ ] **W5.11** Validate H-1~H-5
- [ ] **W5.12** Analyze surprises
- [ ] **W5.13** Re-run outliers (3× average)

**Deliverable**: Complete performance data

---

### Week 6: Analysis & Reporting

- [ ] **W6.1** Comparison Matrix — fill actual scores
- [ ] **W6.2** Weighted totals (C-1~C-8)
- [ ] **W6.3** Hypothesis Validation Report
- [ ] **W6.4** Decision Framework
- [ ] **W6.5** Hybrid Architecture diagram
- [ ] **W6.6** Per-scenario recommendations
- [ ] **W6.7** ADRs
- [ ] **W6.8** Test Strategy Report — TC config, coverage, lessons learned
- [ ] **W6.9** Executive summary + charts
- [ ] **W6.10** Code cleanup + README
- [ ] **W6.11** Peer review + delivery

**Deliverable**: Final Report + Decision Framework + Test Strategy

---

## 10. Hypotheses (待驗證)

| H# | Style | Hypothesis | Scenario |
|----|-------|-----------|----------|
| H-1 | REST | 最佳 cacheability + 最簡 ops；UC-2 需多次 round trip | S-1, S-2 |
| H-2 | GraphQL | UC-2 最佳；ops 最複雜 (governance)；cache 最差 | S-2 |
| H-3 | gRPC | 最低 latency + 最高 throughput (UC-3/6)；browser 差 | S-3, S-5 |
| H-4 | WebSocket | UC-4 最佳；infra 最貴；CRUD 殺雞用牛刀 | S-4 |
| H-5 | SOAP | 最強 contracts (UC-5)；payload 最大；DX 最差 | S-1, S-5 |

---

## 11. Comparison Matrix Template

| Dimension | Weight | REST | GraphQL | gRPC | WebSocket | SOAP |
|-----------|--------|------|---------|------|-----------|------|
| Latency (p95) | 20% | ___ | ___ | ___ | ___ | ___ |
| Throughput | 15% | ___ | ___ | ___ | ___ | ___ |
| Payload Efficiency | 10% | ___ | ___ | ___ | ___ | ___ |
| Developer Experience | 15% | ___ | ___ | ___ | ___ | ___ |
| Cacheability | 10% | ___ | ___ | ___ | ___ | ___ |
| Ops Complexity | 10% | ___ | ___ | ___ | ___ | ___ |
| Security Model | 10% | ___ | ___ | ___ | ___ | ___ |
| Contract Evolution | 10% | ___ | ___ | ___ | ___ | ___ |
| **Weighted Total** | **100%** | **___** | **___** | **___** | **___** | **___** |

---

## 12. Decision Framework

```
  ┌─ Browser/mobile with complex UI?  → GraphQL (BFF)
  ├─ Internal microservice, high throughput?  → gRPC
  ├─ Real-time bidirectional?  → WebSocket
  ├─ Legacy B2B, WS-* required?  → SOAP
  └─ Default  → REST
```

> **Principle**: 沒有明確需求驅動時從 REST 開始。只有可量測的需求證明必要時，才引入其他 style。

---

## 13. Hybrid Architecture

| Layer | Style | Rationale |
|-------|-------|-----------|
| External (Public) | REST | Universal compat, HTTP caching |
| BFF / Dashboard | GraphQL | Client-driven queries |
| Service-to-Service | gRPC | Highest throughput, lowest latency |
| Real-Time | WebSocket | Bidirectional push |
| Legacy / B2B | SOAP | WS-Security, formal WSDL |
| **Edge** | **Spring Cloud Gateway** | **Routing, JWT, rate limit, protocol translation** |

---

## 14. Risks & Mitigations

| R# | Risk | Impact | Mitigation |
|----|------|--------|------------|
| R-1 | PoC ≠ production | High | Production-grade sizing; 100K+ records |
| R-2 | 缺 gRPC/GraphQL 經驗 | Medium | W1 training; pair programming |
| R-3 | 實作品質差異 | High | Hexagonal arch → only adapter differs |
| R-4 | Spring Boot 4 early stage | Medium | Pin version; fallback 3.4.x |
| R-5 | Java 23 preview 不穩 | Low | 僅 Structured Concurrency 用 preview |
| R-6 | WS scaling 問題 | Medium | Redis session; test sticky/non-sticky |
| R-7 | TC CI Docker 不可用 | Medium | GitHub Actions 原生 Docker; fallback DinD |
| R-8 | TC 拖慢 CI | Low | `withReuse(true)` + tag-based stages |

---

## 15. Success Criteria

- [ ] 五種 API style 全部實作，涵蓋 UC-1~UC-6
- [ ] **Aggregate coverage ≥ 85% (line), ≥ 65% (mutation)**
- [ ] **所有 integration tests 使用 Testcontainers (無 H2/embedded)**
- [ ] **ArchUnit 守護規則全部通過**
- [ ] **E2E test 驗證 cross-style data consistency**
- [ ] Load test 每 scenario > 100K requests
- [ ] H-1~H-5 有數據支持
- [ ] Weighted scoring matrix + decision tree 完成
- [ ] Hybrid architecture 經 API Gateway 驗證
- [ ] **CI: compile → arch → unit → integration → E2E → coverage gate 全綠**

---

## Appendix A: Reference

| Resource | URL |
|----------|-----|
| Blog | [API Architecture Styles Made Simple](https://blog.levelupcoding.com/p/api-architecture-styles) |
| Testcontainers | [testcontainers.com](https://testcontainers.com/) |
| ArchUnit | [archunit.org](https://www.archunit.org/) |
| PIT | [pitest.org](https://pitest.org/) |
| Spring Boot 4 | [docs.spring.io](https://docs.spring.io/spring-boot/reference/) |
| Java 23 | [openjdk.org/projects/jdk/23](https://openjdk.org/projects/jdk/23/) |
| gRPC Java | [grpc.io](https://grpc.io/docs/languages/java/) |
| Spring GraphQL | [spring.io](https://spring.io/projects/spring-graphql) |

## Appendix B: Glossary

| Term | Definition |
|------|-----------|
| BFF | Backend for Frontend |
| CQRS | Command Query Responsibility Segregation |
| gRPC | Google Remote Procedure Call (HTTP/2 + Protobuf) |
| HATEOAS | Hypermedia As The Engine Of Application State |
| mTLS | Mutual TLS — 雙向憑證認證 |
| N+1 | 效能反模式：初始查詢觸發 N 次額外查詢 |
| PIT | Mutation testing tool |
| STOMP | Simple Text Oriented Messaging Protocol |
| TC | Testcontainers — Docker-based test dependencies |
| Virtual Threads | Java 21+ Project Loom 輕量執行緒 |
| WS-* | Web Services 規範 (WS-Security, WS-AT 等) |
