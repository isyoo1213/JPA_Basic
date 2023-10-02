package jpashop.cascade;

import jpashop.cascade.sample.Child;
import jpashop.cascade.sample.Parent;

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
             * Cascade
             * - 연관관계 종류 세팅 / Lazy or Eager Loading과는 전혀 상관없는 부분
             * - *** 특정 Entity를 영속화할 때, 연관된 Entity도 함께 영속화하고 싶을 때 쓰는 것
             *       ex) 부모 Entity를 저장할 때 연관된 자식 Entity도 persist()로 저장하고 싶을때
             * - 즉, *** 연관관계 종류와 상관없이, 해당 entity의 필드를 persist()할 때, List로 묶인 entity들이든, 여튼 연관된 Entity들 또한 영속화할 때
             * 속성
             * ALL - 모든 영속화 관련 동작에 같이 적용
             * PERSIST - persist()에만 적용
             * 주의점
             * - 하나의 소유자인 Entity가 관리하는 Entity들이, 다른 Entity에서의 연관관계가 없거나 관리되지 않는 유일한 관계일 때
             *   -> 즉, 1. 단일소유자 : 단일 Entity에 완전히 종속적일때 ex) Parent Entity만 childs를 소유할 때
             *       + 2. lifeCycle(생성/수정/소멸..)이 같을 때
             */

            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent1 = new Parent();
            parent1.setName("parent1");

            parent1.addChild(child1);
            parent1.addChild(child2);

            //case1. 이 경우, em.persist()는 3번 호출되어야 함
            //em.persist(parent1);
            //em.persist(child1);
            //em.persist(child2);
            /**
             * persist() 순서에 따라서 쿼리가 달라짐
             * 1. child1/2를 먼저 persist() 했을 때
             *   -> 이후 child1/2에 대한 update 쿼리가 날아감
             * 2. parent1을 먼저 persist() 했을 때
             *   -> child1/2에 대한 update 쿼리 안날아감
             * 이유
             * - child1/2의 flush()시점에 영속성 컨텍스트에 이미 parent1의 정보가 있을 경우와 없을 경우의 차이일지도
             * -> 1.의 경우 parent1의 childs 정보가 없었으므로, parent1의 persist() 시점에 이를 동기화하기 위한 쿼리가 나가는 것으로 예상
             */

            /**
             * 나는 Parent 중심으로 개발을 진행하고 싶고, Parent의 persist()가 child의 persist()까지 해줬으면 좋겠어
             * -> @OneToMany(cascade = CascadeType.All)
             */

            //case2. cascade 설정 후 오직 parent1만 persist()
            em.persist(parent1);
            // -> child1/2 모두 INSERT 쿼리가 날아간 것을 확인 가능


            /**
             * 고아 객체
             * - 참조가 제거된 Entity는 다른 곳에서 참조하지 않는 고아 객체로 인식하고 삭제하는 기능
             * 조건
             * - *** 참조하는 곳이 하나일 경우에만 사용해야함!!
             * - *** 특정 Entity가 개인 소유할 때 사용!!
             * - @OneToOne / @OneToMany, 즉 부모인 One에서 Many를 참조하는 필드에서 사용가능
             * - *** 개념적으로 '부모 Entity'를 제거하면 자식 Entity는 '고아'가 된다
             *   -> 고아 객체를 활성화하면, 부모를 제거할 때, 자식도 제거됨
             *   -> CascadeType.REMOVE/ALL(all은 모두 전파하므로 em.remove()에서도 전파) 처럼 동작
             * 작동
             * - 부모 Entity와 연관관계가 끊어진 자식 Entity를 자동으로 삭제
             * - orphanRemoval = true
             * - Parent1.getChildList().remove(0) //자식 Entity를 컬렉션에서 제거
             *   -> DELETE FROM CHILD WHERE ID = ? 쿼리가 날아감
             * - 실제 DB에서도 collection에서 삭제된 해당 데이터가 삭제된 것을 확인 가능
             */

            em.flush();
            em.clear();

            Parent findParent1 = em.find(Parent.class, parent1.getId());
            findParent1.getChildList().remove(0); //컬렉션의 첫 요소 삭제
            /* Hibernate:
                / delete jpashop.cascade.sample.Child /
                delete from CHILD where CHILD_ID=?  */

            /**
             * 영속성 전이 + 고아객체를 모두 사용하는 경우는?
             * - CascadeType.ALL + orphanRemoval = true
             * - 'JPA의 영속성 컨텍스트'로 생명주기를 관리하는 entity는 em.persist()로 영속화 / em.remove()로 제거
             * - 두 옵션을 모두 활성화하면, '부모 entity'를 통해서 '자식 entity'의 생명주기를 관리할 수 있음
             *   ex) parent만 persist() / remove() 했음에도 모든 자식 entity에도 적용됨
             * -> *** 부모 Entity - JPA 영속성 컨텍스트 / 자식 Entity - 부모 Entity로 생명주기 관리의 주체가 바뀜
             * -> DB로 따지면, Repository가 없어도 관리가 가능하다는 의미
             * 활용
             * - DDD(도메인 주도 설계)의 Aggregate Root 개념을 구현할 때 유용
             *   ex) 현재 상황 - Aggregate Root - Parent / child는 Parent의 관리 대상
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
