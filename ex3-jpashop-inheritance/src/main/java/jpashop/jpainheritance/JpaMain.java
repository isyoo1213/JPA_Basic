package jpashop.jpainheritance;

import jpashop.jpainheritance.joinstrategy.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            /**
             * 상속관계 매핑
             * 1. JoinStrategy
             * - 자식 테이블은 부모 테이블의 PK를 PK/FK로 동시에 사용함
             */

            Movie movie1 = new Movie();
            movie1.setDirector("diroector1");
            movie1.setActor("actor1");
            movie1.setName("movieName1");
            movie1.setPrice(10000);

            em.persist(movie1);

            //영속성 컨텍스트 flush/비우기
            em.flush();
            em.clear();

            Movie findMovie1 = em.find(Movie.class, movie1.getId());
            /* 실제 쿼리
            Hibernate:
                select
                    movie0_.ITEMH_ID as itemh_id1_2_0_,
                    movie0_1_.name as name2_2_0_,
                    movie0_1_.price as price3_2_0_,
                    movie0_.actor as actor1_3_0_,
                    movie0_.director as director2_3_0_
                from
                    Movie movie0_
                inner join // -> inner 조인을 통해 연결된 테이블의 정보를 가져옴
                    ITEMH movie0_1_
                        on movie0_.ITEMH_ID=movie0_1_.ITEMH_ID
                where
                    movie0_.ITEMH_ID=?
             */
            System.out.println("findMovie1 = " + findMovie1);
            /**
             * 즉, 상속 매핑의 경우에도 JPA는 INSERT가 2번 필요할 경우든, 조회시 JOIN이 필요한 경우 모두 처리해줌
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
