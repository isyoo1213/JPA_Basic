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
    //@Column(name = "TEAM_ID")
    //private Long teamId;

    // * 연관관계를 2가지 관점에서 가져감
    // 1. Entity 관점 - '객체', 즉 레퍼런스를 사용
    // 2. DB Table 관점 - Table의 join할 column을 'FK'로 연결
    // 1 + 2 -> 객체와 FK를 연결함으로써 Entity/Table의 관점 모두 연관관계 매핑
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    @Override
    public String toString() {
        return "MemberA{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", team=" + team + //결국 team.toString()을 호출한다는 의미
                '}';
    }
}
