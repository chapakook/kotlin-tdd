## ⚠️ 동시성 오류 란?
동시성 오류는 컴퓨터 시스템에서 여러 프로세스나 스레드가 동시에 실행될 때 발생할 수 있는 문제로, 주로 공유 자원(예: 변수, 파일, 데이터베이스 등)에 대한 접근을 적절히 제어하지 못해 예상치 못한 결과가 발생하는 경우를 말한다. 이러한 오류는 다중 스레드 프로그래밍이나 병렬 처리가 일반화된 환경에서 자주 발생한다.

### ⚡️ 주요 원인
- 경쟁조건: 두 개 이상의 스레드가 동시에 공유 자원에 접근하려고 하고, 실행 순서에 따라 결과가 달라질 때 발생
- 데드락: 두 개 이상의 프로세스가 서로가 가진 자원을 기다리며 무한히 대기 상태
- 데이터 무결성 손실: 동기화 없이 공유 데이터를 수정할 때, 한 스레드의 작업이 다른 스레드에 의해 덮어씌워지거나 손상

### 💥 예시
은행 계좌에서 잔액을 업데이트하는 프로그램 → 두 스레드가 동시에 잔액을 읽고 수정
- 스레드 1: 잔액 100원 읽음 → 50원 추가 → (150원 예상)
- 스레드 2: 잔액 100원 읽음 → 30원 사용 → (70원 예상)
- 동기화가 없으면, 두 스레드가 동시에 작업을 끝낼 경우 잔액이 150원 또는 70원 중 하나만 반영되어 잘못된 결과 발생

### 💡 해결 방법
- 락(Lock): 뮤텍스(Mutex)나 세마포어(Semaphore)를 사용해 공유 자원에 대한 접근을 제어.
- 원자적 연산: 특정 작업을 중단 없이 한 번에 실행하도록 보장.
- 동기화 메커니즘: 스레드 간의 실행 순서를 조정하거나 이벤트를 통해 통신.
- 불변 객체: 데이터를 변경 불가능하게 만들어 경쟁 조건을 방지.


## 📚 동시성 제어 방식 및 장/단점
### 1️⃣ Synchronized
java에서 제공하는 기본 동기화 메커니즘으로, 특정 코드 블록이나 메서드를 단일 스레드만 실행하도록 보장

👍 장점:
- 간결함: 사용법이 간단하고 직관적이다.

👎 단점:
- 성능 저하: 스레드가 락을 기다리는 동안 블록킹 발생, 스레드 활용 효율이 떨어짐.
- 유연성 부족: 락을 세밀하게 제어하거나 조건에 따라 동작을 변경하기 어려움.
- 데드락 위험: 잘못 설계하면 데드락 발생 가능성이 있음.

🖥️ 사용법:
```kotlin
class Counter {
    private var count = 0

    @Synchronized
    fun increment() = count++

    fun getCount() = count
}

fun main() {
    val counter = Counter()
    val threads = List(100) { Thread { repeat(1000) { counter.increment() } } }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    println("Count: ${counter.getCount()}") // 100,000 기대
}
```

### 2️⃣ ConcurrentHashMap
Java의 `java.util.concurrent` 패키지에서 제공하는 스레드 세이프한 해시맵 구현체입니다.

👍 장점:
- 높은 동시성: 내부적으로 세그먼트 단위로 락을 관리해 여러 스레드가 동시에 읽기/쓰기 가능.
- 블록킹 최소화: 읽기 작업은 락 없이 수행되며, 쓰기 작업만 필요한 경우에만 락 사용.
- 성능: 일반 `HashMap`에 `synchronized`를 사용하는 것보다 효율적.

👎 단점:
- 제한된 범용성: 맵 자료구조에 국한되므로, 일반적인 동기화 문제에는 적용 불가.
- 복잡성: 특정 상황에서 예상치 못한 동작(예: 원자성 보장 부족) 발생 가능.

🖥️ 사용법:
```kotlin
import java.util.concurrent.ConcurrentHashMap
fun main(){
    val map = ConcurrentHashMap<String, Int>()
    val threads = List(10) {
        Thread {
            repeat(1000) { i ->
                map.compute("key") { _, v -> (v ?: 0) + 1 }
            }
        }
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    println("Value: ${map["key"]}") // 10,000 기대
}
```

### 3️⃣ ReentrantLock
Java의 `java.util.concurrent.locks` 패키지에서 제공하는 락 메커니즘으로, synchronized보다 더 유연한 대안입니다.

👍 장점:
- 유연성: 락 획득/해제를 수동으로 제어 가능하며, 타임아웃, 조건 변수(`Condition`) 사용 가능.
- 재진입 가능: 같은 스레드가 락을 여러 번 획득할 수 있음.
- 공정성 옵션: `ReentrantLock(true)`로 공정한 락 제공 가능.

👎 단점:
- 복잡성: 수동으로 락을 해제해야 하며, 실수로 해제하지 않으면 데드락 발생.
- 오버헤드: `synchronized`보다 코드가 장황하고 관리 비용이 큼.

🖥️ 사용법:
```kotlin
import java.util.concurrent.locks.ReentrantLock

class Counter {
    private var count = 0
    private val lock = ReentrantLock()

    fun increment() {
        lock.lock()
        try { count++ } finally { lock.unlock() }
    }

    fun getCount() = count
}

fun main() {
    val counter = Counter()
    val threads = List(100) { Thread { repeat(1000) { counter.increment() } } }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    println("Count: ${counter.getCount()}") // 100,000 기대
}
````

---
### 🏆 비교
|방식|주요 특징|장점|단점|추천 상황|
|---|---|---|---|---|
|Synchronized|기본 락 메커니즘|간단함, 직관적|성능 저하, 유연성 부족|간단한 동기화 필요 시|
|ConcurrentHashMap|스레드 세이프 맵|높은 동시성, 읽기 효율|제한된 범용성|맵 기반 데이터 동시성 처리|
|ReentrantLock|유연한 락 메커니즘|유연성, 재진입 가능|복잡성, 관리 비용|세밀한 락 제어 필요 시|