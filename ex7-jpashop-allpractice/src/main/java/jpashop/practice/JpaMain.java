package jpashop.practice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-basic");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            /**
             * LazyLoading - 글로벌 fetch 전략 설정
             * - 모든 연관관계를 LazyLoading으로 설정
             *   -> static import를 통해 FetchType까지 줄여버리는 것도 효율적
             * - *** @ManyToOne / @OneToOne 은 기본이 '즉시 로딩'이므로 꼭 체크!!!
             *       -> why? 상대 테이블인 1의 데이터를 가져오는 것은 일반적이고 합리적이므로 defalult가 EAGER일 것으로 예상
             *       _ *** 연관관계의 주인이 아니더라도!!! LAZYLOADING 설정이 필수!!!
             */

            /**
             * Cascade - 영속성 전이 설정
             * - *** 라이프사이클 관리 및 맞추기!
             *       -> 즉, 소유 Entity 생성 및 변경이 일어날 때, 연결된 하위 Entity 또한 그 라이프 사이클을 관리하는 것
             *       + *** 연관관계와는 상관없음 + 소유/종속의 개념을 만들어주는 것
             *       -> *** 즉, Order가 생성/소멸될 때 필수적으로 같이 만들고 지워야 하는 것들을 이어주는 작업
             *       -> 주문이 들어오면 배달에 대한 요소, 주문 아이템에 대한 요소들을 필수적으로 만들겠다는 작업
             *       -> 그럼 다른 Entity는? - 비즈니스적으로 의미가 없으므로.
             *          ex) 멤버를 새로 만드는 것은 어불성설. 만약 주문 담당 멤버를 생성해야 한다면 가능.
             *        + 만약, 영속성 전이한 Entity가 복잡하거나 별도의 라이프 사이클이 필요하다면 해제해주는 것이 맞음.
             * - 현재 프로젝트에서 적용할 수 있는 부분
             * - 1. Order with Delivery
             * - 2. Order with OrderItem
             */

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
