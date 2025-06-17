# 📦 e-commerce 프로젝트

**항해 플러스 백엔드 과정** 중 진행한 **1인 백엔드 프로젝트**로, 전자상거래 시스템(e-commerce)에서 자주 사용되는 기능들을 구현한 프로젝트입니다.  
**성능 최적화와 동시성 제어에 대한 안정성 확보**를 목표로 설계 및 개발했으며, 백엔드 전반에 대한 이해를 깊이 있게 다지는 데 중점을 두었습니다.

---

### **📌 주요 목표**
- **Redis 기반 캐싱**과 **락 메커니즘**을 활용해 동시성 문제 해결.
- **K6, Grafana, InfluxDB**를 사용한 성능 테스트 및 실시간 분석.
- **Testcontainers**를 활용한 컨테이너 기반 테스트 환경 구축.

---

## 🛠 사용 도구 (Tools)

| Language & Framework                                  | Database & Cache                                       | Monitoring & Metrics                                  | Testing                                               | Build Tools                                          | API Documentation                                    |
|-------------------------------------------------------|-------------------------------------------------------|------------------------------------------------------|------------------------------------------------------|-----------------------------------------------------|-----------------------------------------------------|
| ![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-green?logo=springboot) | ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql) ![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis) | ![Grafana](https://img.shields.io/badge/Grafana-Monitoring-orange?logo=grafana) ![InfluxDB](https://img.shields.io/badge/InfluxDB-Metrics-green?logo=influxdb) | ![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5) ![Testcontainers](https://img.shields.io/badge/Testcontainers-Integration-blue?logo=testcontainers) | ![Gradle](https://img.shields.io/badge/Gradle-Build-blue?logo=gradle) ![Spring](https://img.shields.io/badge/Dependency%20Management-Spring-lightgrey?logo=spring) | ![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-green?logo=swagger)                                               |

---

## ✨ 주요 활용 기술
1. **Swagger를 사용한 Mock API 구현**
    - Swagger UI 기반 API 문서화를 자동화하고, Mock API를 제공하여 빠른 개발 및 테스트 지원.

2. **JUnit을 활용한 단위 및 통합 테스트**
    - **JUnit 5**를 사용해 주요 비즈니스 로직의 단위 테스트(Unit Test)와 시스템 전반의 통합 테스트(Integration Test)를 구현.
    - **Testcontainers**로 MySQL과 Redis의 실제 컨테이너 환경을 구성하여 높은 신뢰성을 보장.

3. **Redis를 활용한 캐싱과 분산 락 적용**
    - Redis를 활용하여 주문 내역과 장바구니 데이터를 캐싱해 **조회 성능을 약 50% 이상 개선**.

4. **K6, Grafana, InfluxDB를 활용한 성능 시각화**
    - API의 부하 테스트를 통해 **처리 속도와 트래픽 한계**를 파악하고, 병목 구간을 해결.
    - 부하 테스트 결과를 Grafana로 시각화하여 **TPS, 응답 시간**, 시스템의 안정성을 지속적으로 모니터링.

---

## 📝 프로젝트를 통해 시도한 것과 배운 점

### 🔧 **설계**

> 초기에는 **ERD와 API 명세서를 직접 설계**하며 비즈니스 로직을 구조화하려고 했습니다.  
> 데이터 간 관계와 API 호출 구조를 명확히 정의함으로써 실제 협업 상황을 가정한 설계 역량을 키우는 데 집중하고자 했습니다.   
> 진행 과정에서 **동시성 제어, 락 메커니즘 등 미숙한 부분이 많았고**, 그로 인해 초기에 설계해둔 시퀀스 다이어그램과 ERD를 수정해야하는 경우가 많았지만, 이 경험을 통해 **초기 기획의 중요성과 개발자의 도메인 지식과 기술적인 지식**이 얼마나 중요한지를 깨달을 수 있었습니다. 

[🔗 초기 시퀀스 다이어그램 보기](https://github.com/NohYeongO/e-commerce/pull/8)

[🔗 초기 ERD 설계 보기](https://github.com/NohYeongO/e-commerce/pull/10)

---

### ⚙️ **동시성 제어**

> **상품 주문 시 재고 차감**과 같이 동시성이 중요한 영역에서 데이터 정합성을 보장하기 위해 비관적 락(Pessimistic Lock)을 도입했습니다.   
> 낙관적 락도 고려했지만, 충돌 가능성이 높은 기능이라고 판단하여 **더 안정적인 처리를 위해 비관적 락을 선택**했습니다. 이 과정에서 락 종류에 대한 이해뿐 아니라, **데드락 테스트와 해결 경험을 통해 실무적인 동시성 설계 역량**을 향상시킬 수 있었습니다.  

[🔗 동시성 제어 방식 학습](https://github.com/NohYeongO/e-commerce/pull/16)

[🔗 데드락 테스트 및 분산 락 구현](https://github.com/NohYeongO/e-commerce/pull/18)

---

### 🧪 **테스트 환경 구성**

> 초기에는 로컬 환경에 맞춰 테스트 코드를 작성했기 때문에,   
깃허브에서 코드를 클론한 뒤 테스트를 실행하면 **환경 차이로 인해 테스트의 신뢰성을 확보하기 어려운 문제가 발생했습니다.**   
이를 해결하기 위해, 다양한 환경에서도 일관된 테스트를 수행할 수 있도록 **Testcontainers를 도입하였습니다.**  
JUnit 기반의 단위 테스트 및 통합 테스트를 작성하고, **MySQL과 Redis를 Docker 컨테이너로 구성하여 운영 환경과 유사한 조건**에서 테스트를 수행할 수 있도록 하였습니다.
> 
> 그 결과,
> - 테스트 간 **환경 의존성을 제거**하고,
> - **설정 누락 및 환경 차이로 인한 오류를 방지**할 수 있었으며,
> - **기능 단위의 테스트를 통해 변경 사항이 기존 로직에 미치는 영향을 즉시 검증**할 수 있었습니다.  
> 이러한 테스트 환경을 기반으로, **보다 신뢰도 높은 테스트 수행과 안정적인 개발 프로세스를 유지할 수 있는 환경을 구축 할 수 있었습니다.**

[🔗 테스트 컨테이너 설정 및 회고](https://github.com/NohYeongO/e-commerce/pull/15)

---

### 📊 **성능 테스트와 시각화**

> **K6를 활용하여 시나리오 기반의 API 부하 테스트를 작성하고**,  
**InfluxDB와 Grafana를 연동하여 실시간 성능 지표를 시각화하였습니다.**  
이를 통해 실제 사용자가 없는 상황에서도 **대량의 트래픽 유입을 가정한 테스트를 수행할 수 있었고**,  
**API 병목 구간을 시각적으로 파악하고, 정량적인 지표를 기반으로 분석**할 수 있었습니다.  
또한, 테스트 결과를 실시간으로 모니터링하면서  
**정상적으로 처리되는 요청과 지연/실패 구간을 구분하여 분석할 수 있는 환경을 구축**하였고,  
이를 통해 **향후 실 서비스에서도 모니터링 시스템을 직접 구축할 수 있다는 자신감을 얻는 계기가 되었습니다.**  

[🔗 캐싱 전략 구현](https://github.com/NohYeongO/e-commerce/pull/19)

[🔗 부하 테스트 시나리오](https://github.com/NohYeongO/e-commerce/pull/23)

[🔗 부하 테스트 시각화](https://github.com/NohYeongO/e-commerce/pull/24)

---

### 🎯 **프로젝트 마무리**

- 설계 초기 단계의 미흡함이 얼마나 많은 **수정 비용**을 발생시키는지를 체감했고, **기획과 설계의 중요성**을 다시 한 번 확인할 수 있었습니다.
- 동시성과 락 메커니즘 등 **실무에서 자주 마주칠 수 있는 상황에 대한 경험과 학습**을 할 수 있었고, Redis, 성능 테스트 모니터링 도구들에 대한 실전 감각도 키울 수 있었습니다.
- 단순히 코드를 작성하는 수준을 넘어, **전체 시스템 구조를 이해하고 설계할 수 있는 시야**를 넓히는 데 큰 도움이 되었습니다.

