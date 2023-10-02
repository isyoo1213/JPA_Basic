package jpashop.proxy.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //기본값
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    private String city;
    private String street;
    private String zipcode;


    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAMP_ID")
    private TeamP team;

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

    public TeamP getTeam() {
        return team;
    }

    public void changeTeam(TeamP team) {
        this.team = team;
        team.getMembers().add(this);
    }
}