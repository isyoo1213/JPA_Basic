package jpashop.practice;

import jpashop.practice.typemappingpractice.Book;

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

            Book book1 = new Book();
            book1.setName("book1");
            book1.setAuthor("author1");

            em.persist(book1);

            /**
             * Embeded Type 생성
             * 1. Setter는 private으로 막기
             * 2. equals() / hashCode() 생성
             *    -> *** use getters during code generation 옵션 체크
             *    -> 1. 필드에 직접 접근하지 않고 getter로 접근하므로 안전
             *       2. *** Proxy일 때에는 접근이 불가함 but, JPA에서는 getter로 접근 시, 진짜 객체를 찾아가는 로직이 있음
             */

            /**
             * 값 타입을 사용하는 것의 장점
             * - 의미있는 메서드를 응집도있게 만들 수 있음
             *   ex) validation 룰을 공통으로 사용하거나, getter를 통한 조합 등
             * - Column의 정보 설정 + 공통 관리
             *   ex) length
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
