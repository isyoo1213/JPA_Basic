package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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

            /* 조회 */
            Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());

            /* 삭제
            em.remove(findMember);
            */

            /* 수정 */
            // 수정 시에는 따로 다시 persist()를 통해 저장할 필요가 없음
            // ** JPA를 통해 DB를 통해 Entity를 가져오면, 이후부터는 JPA가 관리
            // -> 트랜잭션 commit() 시점에 변경부분을 check
            // -> 변경된 부분이 있으면 update 쿼리를 만들어서 날림 -> commit() 수행
            findMember.setName("HelloJPA");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }
}
