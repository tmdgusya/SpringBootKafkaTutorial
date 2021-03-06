# Kafka?

## TOPIC

카프카의 토픽은 **데이터 베이스의 테이블이나 파일 시스템과 유사**하게 이루어져 있다고 한다. 해당 토픽에 Producer가 데이터를 넣으면
Consumer 가 그 데이터를 가져가서 처리하는 구조를 지닌다.

## Partition

**하나의 Topic 은 여러개의 파티션**으로 구성될 수 있다. **Partition 은 한마디로 Work Queue 다.** 
**Producer 가 Topic 에 담아 놓은 정보를 Queue 구조**로 쌓게 된다. **Consumer 는 pop** 하며 데이터를 가져간다.
근데 **Consumer 가 데이터를 가져간다 해도 데이터는 파티션에서 삭제되지 않는다.**
만약 다른 **Consumer Group(auto.offset.reset 으로 설정되어 있는) 에서 이 파티션에 붙는다면, 0번부터 가져가게 된다.**
이렇게 사용하는 경우 동일 데이터에 대해서 두번 처리할 수 있으므로, 그런 경우에 이렇게 사용하는 것이 좋다고 생각한다. 예를 들면 여러곳에서 하나에 대한 로그를 찍을때?

그리고 Producer 가 Topic 에 데이터를 넣을때 partition key 를 넣을 수 있는데 넣지 않을 경우 라운드로빈 알고리즘으로 어느쪽의 파티션으로 갈지 정해진다.

파티션을 늘리는건 아주 조심해야함!! 파티션을 늘리는 것은 가능하지만 파티션을 줄이는 것은 불가능함..

파티션의 데이터는 레코드가 저장되는 최대 시간과 최대 기간을 정할 수 있음

- **log.retention.ms : 최대 record 보존 시간**
- **log.retention.byte : 최대 record 보존 크기(byte)**

## Replication

- 서버에 장애가 생길때 가용성을 보장할 수 있는 중요한 기능

### Broker?

Kafka Broker 는 카프카가 설치되어 있는 서버를 뜻한다. **보통 3개 이상의 브로커로 사용하는 걸 권장**한다고 한다.
우리가 토픽 생성시 Replication 과 Partition 을 설정하는데 이때 **partition 은 1 이고 reflication 이 3이라면 하나의 파티션이 3개로 복제**가 된다.
다만 **reflication 의 개수는 Broker 의 개수보다 많을 수 없다.**
**원본 파티션이 Leader Partition 이며 나머지 복제본은 Flower Partition** 이다.

Producer 가 Topic 에 전송할때 Ack 라는 옵션이 있는데 옵션에 대한 행동은 다음과 같다.

- ack = 0 => Reader Partition 에 데이터 전송하고 응답을 받지 않음 => 정상적으로 전송됬는지 보장할 수 없음 => 정상적으로 복제되었는지도 알수 없음 => 속도는 빠른데 유실가능성 있음

- ack = 1 => Reader Partition 에 데이터 전송하고 응답을 받음 => 정상적으로 전송됬는지 보장할 수 있음 => 정상적으로 복제되었는지는 알수 없음 => Reader Partition 만 확실함

- ack = 1 => Reader Partition 에 데이터 전송하고 응답을 받음 => 정상적으로 전송됬는지 보장할 수 있음 => 정상적으로 복제되었는지는 알수 있음 => 속도는 느린데 데이터의 유실이 없음

## Partitioner

Producer 가 데이터를 보내면 무조건 Partitioner 를 통해서 데이터가 전송됨. 보니까 디스패쳐역할이다. 메세지 키, 값에 따라서 파티션의 위치 결정
메세지 키가 있는 레코는 파티셔너에 의해서 특정한 해쉬값으로 설정 되게 되고, 어디에 들어갈지 정해지게 된다. 우리가 만약 특정 작업을 순차적으로 실행하는걸 보장해야 한다면,
파티션 키를 지정해서 해당 작업에 대한 순서의 보장성을 지켜줄 수 있다. 여러 파티션에 놓으면 순서가 불안정하기 때문이다..

## Comsumer lag

이건 어떤 뜻이면 간략히 말해서 Producer 와 Consumer 간의 속도 차이를 얘기한다.
예를들면 프로듀서가 1초에 1개씩 넣는데, Consumer 는 2초에 1개씩 처리한다고 해보자. 그렇게 2초뒤를 보면
Producer 는 4개를 넣었고, Consumer 는 그 중 2개를 처리했을 것이다. 이 상태에서 현재 Consumer lag 은 2 인것이다.

즉 쉽게 말하면 Producer 가 마지막으로 넣은 offset - 컨슈머가 마지막으로 처리한 offset 이다.

만약 파티션이 여러개라면 lag 또한 여러개이다.

