package portfolio.LibraryLoan.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import portfolio.LibraryLoan.entity.Member;

import java.util.Optional;

import static portfolio.LibraryLoan.entity.QMember.member;
import static portfolio.LibraryLoan.entity.QRole.role;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.role, role).fetchJoin()
                .where(member.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(findMember);
    }

    @Override
    public Boolean existsByUsername(String username) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.username.eq(username))
                .fetchOne();

        return fetchOne != null ? Boolean.TRUE : Boolean.FALSE;
    }
}
