package jpashop.jpabook.OneToManySample;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER_B")
public class MemberB {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_B_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // ** '일대다' *'양방향'을 억지로 만드는 방법
    // -> 읽기 전용으로 연관관계를 매핑해버리는 방법
    @ManyToOne
    @JoinColumn(name = "TEAM_B_ID", insertable = false, updatable = false)
    private TeamB team;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
