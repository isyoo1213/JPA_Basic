package jpashop.jpabook.sampledomain;

import javax.persistence.*;

@Entity
public class MemberA {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBERA_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // * 연관관계를 레퍼런스로 가져가는 것이 아닌 FK로 가져가는 경우
    @Column(name = "TEAM_ID")
    private Long teamId;

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

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
