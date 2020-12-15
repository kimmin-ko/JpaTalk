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

    /* public method */
    public Long join(Member member) {
        validateDuplicateMember(member);

        memberRepository.save(member);

        return member.getId();
    }

    public Long addFriend(Long memberId, Long friendId) {
        if(memberId.equals(friendId))
            throw new IllegalArgumentException("자기자신은 친구로 추가할 수 없습니다.");

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));
        Member friend = memberRepository.findById(friendId).orElseThrow(() -> new EntityNotFoundException("친구를 찾을 수 없습니다"));

        boolean existsFriend = memberRepository.findWithFriendRelationByIdAndFriend(memberId, friendId).isPresent();
        if(existsFriend)
            throw new IllegalArgumentException("이미 친구입니다.");

        FriendRelation friendRelation = FriendRelation.create(member, friend);
        friendRelationRepository.save(friendRelation);

        return friendRelation.getId();
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
