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
            System.out.println("findMovie1 = " + findMovie1);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
