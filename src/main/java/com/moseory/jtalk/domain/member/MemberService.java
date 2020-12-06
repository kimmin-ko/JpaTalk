package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.domain.friendrelation.FriendRelationRepository;
import com.moseory.jtalk.entity.FriendRelation;
import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.global.exception.business.DuplicateAccountException;
import com.moseory.jtalk.global.exception.business.DuplicateEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class MemberService {

    /* inject */
    private final MemberRepository memberRepository;
    private final FriendRelationRepository friendRelationRepository;

    public Long join(Member member) {
        validateDuplicateMember(member);

        memberRepository.save(member);

        return member.getId();
    }

    public Long addFriend(Long memberId, Long friendId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));
        Member friend = memberRepository.findById(friendId).orElseThrow(() -> new EntityNotFoundException("친구을 찾을 수 없습니다"));

        FriendRelation friendRelation = FriendRelation.create(member, friend);
        FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelation);

        return savedFriendRelation.getId();
    }

    /* private method */
    private void validateDuplicateMember(Member member) {
        validateDuplicateEmail(member.getEmail());
        validateDuplicateAccount(member.getAccount());
    }

    private void validateDuplicateAccount(String account) {
        if (memberRepository.existsByAccount(account))
            throw new DuplicateAccountException("중복된 Account 입니다.");
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email))
            throw new DuplicateEmailException("중복된 Email 입니다.");
    }


}
