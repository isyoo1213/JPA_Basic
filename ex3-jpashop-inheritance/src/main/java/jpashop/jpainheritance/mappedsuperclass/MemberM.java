package jpashop.jpainheritance.mappedsuperclass;

import javax.persistence.*;

@Entity
@Table(name = "MEMBERM")
public class MemberM extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBERM_ID")
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAMM_ID")
    private TeamM team;

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

    public TeamM getTeam() {
        return team;
    }

    public void setTeam(TeamM team) {
        this.team = team;
    }
}
