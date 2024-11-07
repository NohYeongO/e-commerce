### **`STEP 13_기본`**

- 조회가 오래 걸리는 쿼리에 대한 캐싱, 혹은 Redis 를 이용한 로직 이관을 통해 성능 개선할 수 있는 로직을 분석하고 이를 합리적인 이유와 함께 정리한 문서 제출

---
## 조회 성능 향상을 위한 Redis 캐싱 적용 및 이유

### 1. 성능 개선이 필요한 기능
현재 요구사항으로는 **최근 3일간의 판매 상위 5개 제품**을 조회하는 기능이 존재합니다.  
해당 기능은 자주 조회될 가능성이 크며, 쿼리의 복잡성으로 인해 조회시간 증가되는 문제를 개선할 필요성이 있습니다.

### 2. 현재 기능의 특징
1. **조인 연산**: `Product`, `OrderDetail`, `Order` 테이블을 조인하여 데이터를 조회해야 합니다.
2. **집계 연산**: 특정 기간의 주문 수량 합계를 계산하고 이를 기준으로 상위 5개 제품을 조회합니다.
3. **빈번한 조회 가능성**: 동일한 데이터가 여러 번 조회될 가능성이 있습니다.

### 3. 기존 성능 이슈 분석
1. **복잡한 조인 및 집계 연산**  
   다중 테이블 간 조인 및 집계 연산을 통해 상위 5개의 상품을 조회해야 하므로, 테이블 크기가 커질수록 성능 저하가 발생할 가능성이 큽니다. 특히, `ORDER BY SUM(od.quantity) DESC` 구문은 주문 수량을 집계한 후 정렬하는 과정에서 시간이 소요될 수 있습니다.

2. **데이터 변경 빈도 낮음**  
   해당 데이터는 매일 자정(00시)에 한 번 갱신되므로 실시간성이 크게 중요하지 않습니다. 매일 갱신된 데이터만으로도 사용자 요구사항을 충족할 수 있습니다.

3. **빈번한 조회 요청**  
   동일한 결과가 반복적으로 요청될 가능성이 크므로 Redis에 캐싱하면 빠른 조회를 통해 성능을 크게 개선할 수 있습니다. 캐싱을 통해 데이터베이스 부하를 줄이고 사용자 대기 시간을 줄여 사용자 경험을 개선할 수 있습니다.

### 4. Redis 캐싱을 통한 성능 개선 방안
Redis 캐싱을 통해 위 문제를 해결하고 성능을 최적화할 수 있습니다.

#### **구현 방안**
1. **캐시 갱신 스케줄링**  
   매일 자정(00시)에 스케줄러가 실행되어 상위 5개 상품 데이터를 데이터베이스에서 조회한 후 Redis에 캐싱합니다. 이를 통해 하루에 한 번만 데이터베이스 조회가 이루어지고, 이후의 모든 조회는 Redis에서 처리됩니다.

2. **캐시 데이터 제공**  
   이후 사용자가 상위 5개 상품을 조회하면 Redis에서 캐싱된 데이터를 바로 반환하여 데이터베이스에 대한 요청을 최소화합니다.

#### **Redis 캐싱 적용 후 기대 효과**
- **데이터베이스 부하 감소**: 조회 쿼리가 Redis에서 바로 처리되므로, 데이터베이스에 대한 요청 수가 줄어들어 부하가 크게 감소합니다.
- **조회 속도 향상**: 캐시된 데이터는 거의 즉시 반환될 수 있어 사용자 대기 시간이 크게 단축됩니다.
- **시스템 안정성 증가**: 캐싱된 데이터를 통해 데이터베이스 의존성을 줄이고 시스템 안정성 향상이 예상됩니다.
- **사용자 경험 개선**: 빠른 조회 응답을 통해 사용자 만족도가 상승할 수 있습니다.