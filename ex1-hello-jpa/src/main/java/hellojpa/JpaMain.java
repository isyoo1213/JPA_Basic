package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        //PARAM1 - unitName은 META-INF 디렉토리의 persistence.xml에 작성된 name
        // ** EntityManagerFactory는 application 로딩 시점에 딱 하나만 만들어지고 전체에서 공유하도록 사용해야 함
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // ** EntityManager는 주로(정확한 표현은 아님) 트랜잭션 단위로 사용 후 생성/종료를 해주어야 함
        // ** + 동작시에 DB를 물고 동작하므로 사용 후 꼭 닫아주어야 함
        // ** Thread간에 절대!! 공유해서는 안됨
        EntityManager em = emf.createEntityManager();

        // *** JPA에서 Data를 처리하는 모든 작업은 *Transaction 내에서 실행되어야 함
        // + 간혹 tx생성하지 않고도 처리가 되는 경우도 RDB는 모두 내부적으로 tx를 처리하도록 설계되어 있음
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        // ** 트랜잭션을 시작한 이후로는 try-catch로 안정적으로 처리
        try {
            /* 저장
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");
            em.persist(member);
            */

            /* 조회
            Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());
            */

            /* 삭제
            em.remove(findMember);
            */

            /* 수정
            // 수정 시에는 따로 다시 persist()를 통해 저장할 필요가 없음
            // ** JPA를 통해 DB를 통해 Entity를 가져오면, 이후부터는 JPA가 관리
            // -> 트랜잭션 commit() 시점에 변경부분을 check
            // -> 변경된 부분이 있으면 update 쿼리를 만들어서 날림 -> commit() 수행
            findMember.setName("HelloJPA");
             */

            /* 다중조회 by JPQL
            // * 쿼리의 대상은 '테이블'이 아닌 '객체 그 자체' -> m을 통해 Member.class의 모든 객체를 접근
            // + 실제 hibernate는 Entity 객체의 모든 필드를 조회하는 query를 날림 ex) id, name 모두
            List<Member> result = em.createQuery("select m from MEMBER2 as m", Member.class)
                    .getResultList();
            // ** JPQL에서 sql을 작성할 시, 'entity 클래스'에서 table 이름을 변경했다면 해당 이름으로 작성해주어야 함
            // ex) Member2 X MEMBER2 O

            // *** JPQL의 장점
            // paging을 매우 간단히 처리 가능 ex .setFirstResult(5).setMaxResult(10)... 일 경우,
            // DB에 따라서 query에 limit/offset(H2), Oracle용 query 등을 모두 자동으로 설정하는 쿼리를 작성해줌
            // *** JPA의 핵심 철학은 'entity' 객체 중심의 개발
            // 검색조회의 경우 결국 검색 조건이 포함된 sql이 필요
            // JPQL은 이 검색 조건이 적용되는 sql의 대상이 table이 아닌 'entity'
            // -> *** 장점은 entity를 대상으로 하므로 table에 직접 접근하지 않는 JPQL만의 sql 작성 가능
            // -> 즉 DB, table에 종속되지 않고 entity 객체에만 신경쓰면 다양한 dialect를 알아서 적용해줌
            // -> *** 즉, JPQL은 '추상화된' '객체지향' 'SQL'

            for (Member member : result) {
                System.out.println("member.id = " + member.getId());
                System.out.println("member.name = " + member.getName());
            }

            */


            // * 영속성 & 비영속성 & 준영속

            //비영속
            //Member member = new Member();
            //member.setId(10L);
            //member.setName("HelloJPA");

            //영속
            //System.out.println("=== Before ===");
            //em.persist(member);
            // 1. 실제 이 시점에서 query가 날아가는 것이 아님
            // 즉 em.persist() != 영속
            //실제 쿼리가 날아가는 시점은 tx.commit()시점에 영속성 컨텍스트에 저장된 엔터티들을 대상으로 query가 날아감
            // *** 2. 엔터티의 '@Id'가 key가 되고, 엔터티의 '인스턴스' 자체가 값이 되어
            // '영속성 컨텍스트'(현재는 em으로 생각해도 무방/실제로는 미묘한차이 있음)에 '1차 캐시'로 저장됨
            //System.out.println("=== After ===");

            //em.find(Member.class, 10L);
            // 3. find()는 1차적으로 EM의 '1차 캐시'를 탐색

            //System.out.println("member.id = " + member.getId());
            //System.out.println("member.name = " + member.getName());
            // *** find()를 통한 '조회' 후 출력이지만, 실제 hibernatem가 작성한 sql에는 select 쿼리가 존재하지 않음
            // -> DB에서 조회하기 이전, 1차 캐시에 저장된 정보로 조회

            //Member member2 = new Member();
            //member2.setId(20L);
            //member2.setName("HelloJPA2");

            //em.find(Member.class, 20L);
            // 4. find()의 1차캐시 탐색에 데이터가 존재하지 않는 경우
            // DB를 조회 -> 데이터를 불러와 EM의 '1차 캐시'에 '저장' -> 반환

            // ** EM은 주로 단건의 request에 대한 TX를 단위로 생성/소멸 됨
            // -> 다수의 요청에 대한 데이터를 캐싱하는 것은 아니므로, 어플리케이션 전체적으로 큰 효율을 가지는 것은 아님
            // -> 비즈니스 로직이 매우 복잡한 경우에는 유의미한 성능 개선 효과 가능
            // -> 넓은 범위의 캐시는 2차 캐시가 존재
            
            //System.out.println("member2.id = " + member2.getId());
            //System.out.println("member2.name = " + member2.getName());


            // 5. 이미 조회한 엔터티의 경우, 1차 캐시에 저장된 데이터를 불러오므로 쿼리 추가적으로 나가지 않음
            //Member member1_2 = em.find(Member.class, 10L);

            // * 영속 entity의 동일성 보장
            // - ***** 1차 캐시를 통해 '반복 가능한 읽기 등급(REPEATABLE READ)' 수준의 '트랜잭션 격리수준'을 DB가 아닌 '어플리케이션 차원'에서 제공
            //Member memberA = em.find(Member.class, 10L);
            //Member memberB = em.find(Member.class, 10L);
            //System.out.println("memberA == memberB = " + (memberA==memberB));

            // * 엔터티 등록 시, 트랜잭션을 지원하는 쓰기 지연
            // - EM은 데이터 변경 시 트랜잭션을 새롭게 시작해야 한다
            // - em.persist()를 사용하더라도, insert 쿼리를 생성해 바로 DB로 보내는 것이 아닌, '영속성 컨텍스트' 내부의 '쓰기 지연 SQL 저장소' + '1차 캐시'에 보관
            // - tx.commit() 하는 순간, '쓰기 지연 SQL 저장소'에 저장된 sql들이 flush되어 DB에 insert 쿼리를 보내고, commit을 수행
            //Member lazyWrite1 = new Member(150L, "A");
            //Member lazyWrite2 = new Member(160L, "B");

            //em.persist(lazyWrite1);
            //em.persist(lazyWrite2);

            //System.out.println("========= query 전송 기준선 ============");

            // 이렇게 lazyWritting을 사용하는 이유
            // - sql은 결국 DB에서 commit을 수행하기 이전에만 존재하면 수행 가능
            // 1. * Buffering이라는 기술을 사용할 수 있음 - commit 내용을 '모아서' 전송 가능함 -> 데이터 최적화 가능
            // 2. jdbc.batch 를 통해 '한 단위로' 네트워크에 commit을 수행 가능함 - commit 내용을 '한번에' 전송 가능함
            //    ex) <property name="hibernate.jdbc.batch_size" value="10"/>


            // * 엔터티 수정 - 변경 감지(Dirty Checking)
            //Member member150 = em.find(Member.class, 150L);

            //member150.setName("ABC");
            //em.persist(member150);
            //JPA는 자바의 Collection처럼 사용하는 것을 컨셉으로 가짐
            // -> collection객체를 통해 데이터 수정 후 따로 저장하는 과정을 가지지 않는 것처럼 JPA 또한 마찬가지로 설계됨
            //System.out.println("=======================");

            //1. JPA는 트랜잭션의 commit() 시점에 내부적으로 flush가 호출됨()
            //2. 1차 캐시 내의 @Id, 엔터티와 스냅샷을 비교
            //   - * 스냅샷 또한 1차캐시에 존재 + *** DB에서 조회하든, persist()를 하든 '최초로' '영속성 1차 캐시에 저장'된 상태를 저장
            //3. 변경점 확인 시, '쓰기 지연 sql 저장소'에 update sql 쿼리를 만들어 저장
            //4. 실제 DB로 flush가 처리되어 sql이 날아감
            //5.commit 진행
            // *** flush는 1차캐시를 '삭제'하는 것이 절대 아님. + 1차캐시의 스냅샷과 변경점, 엔터티는 아마 인스턴스를 기준으로 이루어질 것으로 예상

            // * 엔터티 삭제 - Dirty Checking이 그대로 이루어지고 + delete 쿼리를 만들어 저장 후 처리

            // * Flush
            // - 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영
            // - 트랜잭션의 commit()이 호출되면 자동적으로 flush 호출
            // - Dirty Checking 발생 -> '수정된' 엔티티를 '쓰기 지연 sql'에 저장 ex) update/delete sql -> DB에 sql 전송
            // ** flush가 발생하는 것과 DB에서 commit이 이루어지는 것은 동일하지 않음 -> flush 후 commit 발생

            // * 영속성 컨텍스트를 flush하는 방법
            // 1. em.flush()로 직접 호출
            //    -> * tx.commit()을 통한 자동 호출 이전에 'sql'을 직접 확인하고 싶을 때 강제로 호출
            //    -> * em.flush() 호출 후에도 '1차 캐시'의 엔터티들은 지워지지 않고 저장되어 있음
            //    -> *** 오직 'Dirty Checking'과 '쓰기 지연 sql 저장소'의 sql들이 반영되는 과정
            // 2. tx.commit() 호출 - 플러시 자동 호출
            //    -> JPQL은 쓰기 지연이 아닌, DB에 실시간으로 쿼리를 전송
            //    -> persist() 후 해당 엔터티들을 flush()하지 않은 상태에서 JPQL로 조회하는 경우, DB에는 갱신된 정보들이 없으므로 무용지물
            //    -> 이를 방지하고자 기본적으로 flush를 호출하도록 설계됨
            // 3. JPQL 쿼리 실행 - 플러시 자동 호출

            //Member memberFlush = new Member(200L, "memberFlush");
            //em.persist(memberFlush);
            //em.flush();
            
            //System.out.println("========== flush() / commit() 쿼리 확인 선 =============");
            //commit() 시점 이전에 query가 DB에 바로 반영되는 것을 확인 가능

            // * Flush 모드 옵션
            // - em.setFlushMode(FlushModeType.COMMIT)
            // - Default : FlushModeType.AUTO - flush() 실행한 후 'commit() / query 전송'
            // - FlushModeType.COMMIT - commit()을 할 때에만 flush 실행 후 commit
            //   -> * query만 실행할 경우에는 flush를 실행하지 않음
            //   -> ex) persist() 된 엔터티들이 있고, JPQL에서 persist()된 엔터티와 상관 없는 테이블의 데이터들을 조회할 경우
            //   -> persist()된 엔터티들을 당장 flush할 이유가 전혀 없음

            // * Flush 주의사항
            // 1. Flush는 '영속성 컨텍스트'를 '비우는 것'이 아니다!
            // 2. 영속성 컨텍스트의 변경 내용을 DB와 동기화 하는 과정
            // 3. *** '트랜잭션'이라는 작업단위로 이루어지는 구조가 매우 중요!
            //    -> commit() 수행 직전에만 flsuh가 수행되면 아무런 상관 없다
            //    -> 즉, 트랜잭션이라는 작업단위가 존재함으로써 LazyWriting과 Buffering, jdbc.bach 등, flush를 통한 동기화와 commit()의 수행의 차이를 통한 기술들이 가능

            // * 준영속
            // '영속' 상태의 '엔터티'가 영속성 컨텍스트에서 분리 (detached) -> 영속성 컨텍스트가 제공하는 기능 (1차캐시에 저장된 엔터티를 활용하는) 사용 불가능

            // 방법
            //Member member150B = em.find(Member.class, 150L); // DB에서 조회한 데이터를 1차캐시에 저장 -> 영속
            //member.setName("AAAAA"); //값 변경 -> DirtyChecking -> 영속성 컨텍스트를 활용한 기능 사용 가능

            // 1. em.detach(entity)
            //   - 특정 엔터티만 준영속 상태로 전환
            //이제 영속성 컨텍스트에서 삭제되므로 JPA에서 관리하지 않음 -> commit() 수행 시 아무 일도 일어나지 않음 + update 쿼리 나가지 않음
            //em.detach(member150B);

            // 2. em.clear()
            //   - EntityManager 내의 영속성 컨텍스트의 엔터티들을 통쨰로 날려버림
            //em.clear();
            //Member member150C = em.find(Member.class, 150L); // 날아간 엔터티를 다시 DB에서 불러오므로 1차캐시에 저장하며 '영속'

            // 3. em.close()
            //   - 영속성 컨텍스트를 아예 닫아버리는 방법
            //em.close();

            // * 기본 키 관련
            BasicKeyMappingMember basicKeyMappingMember = new BasicKeyMappingMember();
            basicKeyMappingMember.setName("SEQUENCE");
            // ** H2DB 버전에 따라서 IDENTITY의 버그 발생 -> DB url에 MODE=LEGACY 추가 후 해결

            em.persist(basicKeyMappingMember);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
