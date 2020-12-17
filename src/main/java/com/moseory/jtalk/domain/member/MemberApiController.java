package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.FriendRelation;
import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.enumeration.FriendRelationStatus;
import com.moseory.jtalk.global.standard.ResultResponse;
import com.moseory.jtalk.global.validator.BirthDate;
import com.moseory.jtalk.global.validator.PhoneNumber;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    //TODO DB 정하기
    //TODO Redis Cache 설정하기
    //TODO L7 로드밸런싱 및 Travic CI
    //TODO 인규님 깃헙 Serializable Error

    /* inject */
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<MemberJoinResponse> join(@RequestBody @Valid MemberJoinRequest memberJoinRequest) {
        Long memberId = memberService.join(Member.from(memberJoinRequest));

        return ResultResponse.<MemberJoinResponse>builder()
                .status(200)
                .message("회원가입을 성공하였습니다.")
                .data(new MemberJoinResponse(memberId))
                .build();
    }

    /**
     * 변경
     * [Before]
     * 회원 및 친구 관계 함께 조회
     * [After]
     * 회원만 조회
     */
    @GetMapping("/{memberId}")
    public ResultResponse<MemberResponse> findMember(@PathVariable("memberId") Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(memberId + "번 회원을 찾을 수 없습니다."));

        return ResultResponse.<MemberResponse>builder()
                .status(200)
                .message(findMember.getId() + "번 회원을 조회하였습니다.")
                .data(MemberResponse.from(findMember))
                .build();
    }

    /**
     * 변경
     * 똑같은 친구가 계속 추가됨
     * 자기 자신도 추가됨
     */
    @PostMapping("/{memberId}/friends/{friendId}")
    public ResultResponse<AddFriendResponse> addFriends(@PathVariable("memberId") Long memberId,
                                                        @PathVariable("friendId") Long friendId) {
        Long friendRelationId = memberService.addFriend(memberId, friendId);

        return ResultResponse.<AddFriendResponse>builder()
                .status(200)
                .message("친구추가를 성공하였습니다.")
                .data(new AddFriendResponse(friendRelationId))
                .build();
    }

    /**
     * 변경
     * 회원과 모든 친구관계 조회
     */
    @GetMapping("{memberId}/friendRelations")
    public ResultResponse<MemberFriendRelationResponse> findMemberWithFriendRelation(@PathVariable("memberId") Long memberId) {
        Member findMember = memberRepositoryImpl.findWithFriendRelationById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(memberId + "번 회원을 찾을 수 없습니다."));

        return ResultResponse.<MemberFriendRelationResponse>builder()
                .status(200)
                .message(findMember.getId() + "번 회원과 친구관계를 조회하였습니다.")
                .data(MemberFriendRelationResponse.from(findMember))
                .build();
    }

    //TODO member의 특정 친구 조회

    //TODO member의 모든 친구 조회


    @GetMapping("/exists/email/{email}")
    public ResultResponse<EmailExistsResponse> existsEmail(@PathVariable("email") String email) {
        boolean exists = memberRepository.existsByEmail(email);

        return ResultResponse.<EmailExistsResponse>builder()
                .status(200)
                .message(exists ? "이미 존재하는 이메일입니다." : "사용 가능한 이메일입니다.")
                .data(new EmailExistsResponse(exists))
                .build();
    }

    @GetMapping("/exists/account/{account}")
    public ResultResponse<AccountExistsResponse> existsAccount(@PathVariable("account") String account) {
        boolean exists = memberRepository.existsByAccount(account);

        return ResultResponse.<AccountExistsResponse>builder()
                .status(200)
                .message(exists ? "이미 존재하는 계정입니다." : "사용 가능한 계정입니다.")
                .data(new AccountExistsResponse(exists))
                .build();
    }

    /* private method */

    /* nested class */
    // == Request ==
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberJoinRequest {

        @NotBlank(message = "계정 아이디는 필수값 입니다.")
        private String account;

        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수값 입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수값 입니다.")
        private String password;

        @NotBlank(message = "이름은 필수값 입니다.")
        private String name;

        @PhoneNumber
        private String phoneNumber;

        @BirthDate
        private LocalDate birthDate;
    }

    // == Response ==
    @Data
    static class MemberJoinResponse {
        private Long id;

        public MemberJoinResponse(Long id) {
            this.id = id;
        }

    }

    @Data
    @AllArgsConstructor
    static class EmailExistsResponse {
        private boolean exists;
    }

    @Data
    @AllArgsConstructor
    static class AccountExistsResponse {
        private boolean exists;
    }

    @Data
    @Builder
    @AllArgsConstructor
    static class MemberResponse {
        private Long memberId;
        private String account;
        private String email;
        private String name;
        private String phoneNumber;
        private String stateMessage;
        private String profileUrl;
        private LocalDate birthDate;

        public static MemberResponse from(Member member) {
            return MemberResponse.builder()
                    .memberId(member.getId())
                    .account(member.getAccount())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phoneNumber(member.getPhoneNumber())
                    .stateMessage(member.getStateMessage())
                    .profileUrl(member.getProfileUrl())
                    .birthDate(member.getBirthDate())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    static class FriendRelationDto {
        private Long friendRelationId;
        private String friendName;
        private FriendRelationStatus status;
        private LocalDateTime createdDate;

        public FriendRelationDto(FriendRelation friendRelation) {
            this.friendRelationId = friendRelation.getId();
            this.friendName = friendRelation.getFriendName();
            this.status = friendRelation.getStatus();
            this.createdDate = friendRelation.getCreatedDate();
        }
    }

    @Data
    @AllArgsConstructor
    static class AddFriendResponse {
        private Long id;
    }

    @Data
    @Builder
    @AllArgsConstructor
    static class MemberFriendRelationResponse {
        private Long memberId;
        private List<FriendRelationDto> friendsRelations;
        private String account;
        private String email;
        private String name;
        private String phoneNumber;
        private String stateMessage;
        private String profileUrl;
        private LocalDate birthDate;

        public static MemberFriendRelationResponse from(Member member) {
            List<FriendRelationDto> friendRelationDtos = member.getFriendsRelations().stream().map(FriendRelationDto::new).collect(toList());

            return MemberFriendRelationResponse.builder()
                    .memberId(member.getId())
                    .friendsRelations(friendRelationDtos)
                    .account(member.getAccount())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phoneNumber(member.getPhoneNumber())
                    .stateMessage(member.getStateMessage())
                    .profileUrl(member.getProfileUrl())
                    .birthDate(member.getBirthDate())
                    .build();
        }
    }
}
