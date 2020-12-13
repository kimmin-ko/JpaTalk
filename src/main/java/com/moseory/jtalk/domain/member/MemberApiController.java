package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.entity.FriendRelation;
import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.enumeration.FriendRelationStatus;
import com.moseory.jtalk.global.standard.ResultResponse;
import com.moseory.jtalk.global.validator.PhoneNumber;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
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

    /* inject */
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;

    @PostMapping("")
    public ResultResponse<MemberJoinResponse> join(@RequestBody @Valid MemberJoinRequest memberJoinRequest) {
        Long memberId = memberService.join(Member.from(memberJoinRequest));

        return ResultResponse.<MemberJoinResponse>builder()
                .status(200)
                .message("회원가입을 성공하였습니다.")
                .data(new MemberJoinResponse(memberId))
                .build();
    }

    @GetMapping("/{id}")
    public ResultResponse<MemberDto> findMember(@PathVariable("id") Long id) {
        Member findMember = memberQueryRepository.findWithFriendRelationById(id)
                .orElseThrow(() -> new EntityNotFoundException(id + "번 회원을 찾을 수 없습니다."));

        return ResultResponse.<MemberDto>builder()
                .status(200)
                .message(findMember.getId() + "번 회원을 조회하였습니다.")
                .data(MemberDto.from(findMember))
                .build();
    }

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

        @Past(message = "잘못된 생년월일입니다.")
        @NotNull(message = "생년월일은 필수값 입니다.")
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
    static class MemberDto {
        private Long memberId;
        private List<FriendRelationDto> friends;
        private String account;
        private String email;
        private String name;
        private String phoneNumber;
        private String stateMessage;
        private String profileUrl;
        private LocalDate birthDate;

        public static MemberDto from(Member member) {
            List<FriendRelationDto> friendRelationDtos = member.getFriends().stream().map(FriendRelationDto::new).collect(toList());

            return MemberDto.builder()
                    .memberId(member.getId())
                    .friends(friendRelationDtos)
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
}
