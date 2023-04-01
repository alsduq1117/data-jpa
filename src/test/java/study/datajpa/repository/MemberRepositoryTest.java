package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        // PageRequest(구현체)의 부모를 타고가다보면 Pageable interface가 있음.
        // page 1이 아니라 0부터 시작
        // Slice에서는 3개가 아닌 4개를 가져오게됨 이로 인해서 totalCount 쿼리 없이 다음페이지 여부를 알수있게된다.

        // when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest);
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
        List<Member> list = memberRepository.findListByAge(age, pageRequest);

        // then
        // 1) Page - totalCount쿼리 날라감
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3); // 개수
        assertThat(page.getTotalElements()).isEqualTo(5); // 총 개수
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지 수
        assertThat(page.isFirst()).isTrue(); // 첫번째 페이지인지
        assertThat(page.hasNext()).isTrue();// 다음 페이지 있는지 여부


        // 2. Slice
        // Slice는 total 관련 함수들 지원 X
        List<Member> content_slice = slice.getContent();

        assertThat(content_slice.size()).isEqualTo(3);
//        assertThat(slice.getTotalElements()).isEqualTo(5);
        assertThat(slice.getNumber()).isEqualTo(0);
//        assertThat(slice.getTotalPages()).isEqualTo(2);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();


        // 3. 반환 타입 List
        // 위의 함수들 동작 안함

        // 실무 팁! Entity는 외부로 절대 반환 금지!! DTO로 변환해서 반환!!
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        // Page유지하면서 DTO로 변경 : api로 반환 가능
        // Page 유지하면서 JSON생성될 때, totalPage, totalElement 등이 JSON으로 반환됨
    }

}