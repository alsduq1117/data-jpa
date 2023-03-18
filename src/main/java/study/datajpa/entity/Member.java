package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})  //연관관계 필드는 toString X
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //즉시로딩으로 되어있으면 성능 최적화 하기 어렵다
    @JoinColumn(name = "team_id") // 외래키 이름
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team teamA) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
