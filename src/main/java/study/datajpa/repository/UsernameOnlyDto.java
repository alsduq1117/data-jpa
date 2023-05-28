package study.datajpa.repository;

public class UsernameOnlyDto {
    private final String username;

    public UsernameOnlyDto(String username) {  //생성자의 파라미터 이름으로 매칭을 시켜서 Projection 하게 된다.
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}