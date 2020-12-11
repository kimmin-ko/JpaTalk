package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.domain.abstact.AbstractApiControllerTest;
import com.moseory.jtalk.entity.FriendRelation;
import com.moseory.jtalk.entity.Member;
import com.moseory.jtalk.entity.enumeration.FriendRelationStatus;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.EmailRandomizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import static com.moseory.jtalk.DocumentFormatGenerator.getDateFormat;
import static com.moseory.jtalk.DocumentFormatGenerator.getPhoneNumberFormat;
import static com.moseory.jtalk.domain.member.MemberApiController.MemberJoinRequest;
import static com.moseory.jtalk.domain.restdocs.ApiDocumentationTest.getDocumentRequest;
import static com.moseory.jtalk.domain.restdocs.ApiDocumentationTest.getDocumentResponse;
import static io.github.benas.randombeans.randomizers.EmailRandomizer.*;
import static io.github.benas.randombeans.randomizers.PhoneNumberRandomizer.aNewPhoneNumberRandomizer;
import static io.github.benas.randombeans.randomizers.range.LongRangeRandomizer.aNewLongRangeRandomizer;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberApiController.class)
class MemberApiControllerTest extends AbstractApiControllerTest {

    @MockBean
    MemberService memberService;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    MemberQueryRepository memberQueryRepository;

    static EnhancedRandom memberCreator;
    static EnhancedRandom friendRelationCreator;

    @BeforeAll
    static void beforeAll() {
        memberCreator = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .stringLengthRange(3, 10)
                .dateRange(LocalDate.of(1920, 1, 1), LocalDate.of(2010, 1, 1))
                .randomize(f -> f.getName().equals("id"), () -> aNewLongRangeRandomizer(1L, 1000L).getRandomValue())
                .randomize(f -> f.getName().equals("email"), () -> aNewEmailRandomizer(new Random().nextLong(), Locale.US, false).getRandomValue())
                .randomize(f -> f.getName().equals("phoneNumber"), () -> aNewPhoneNumberRandomizer(new Random().nextLong(), Locale.KOREA).getRandomValue())
                .excludeField(f -> f.getName().equals("withdrawalDate"))
                .build();

        friendRelationCreator = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .randomize(f -> f.getName().equals("id"), aNewLongRangeRandomizer(1L, 1000L))
                .excludeField(f -> f.getName().equals("member"))
                .excludeField(f -> f.getName().equals("friend"))
                .excludeField(f -> f.getName().equals("friendName"))
                .randomize(f -> f.getName().equals("status"), () -> FriendRelationStatus.NORMAL)
                .randomize(f -> f.getName().equals("createdDate"), LocalDateTime::now)
                .build();
    }

    @Test
    @DisplayName("정상적인 회원가입 성공 테스트")
    void join() throws Exception {
        // given
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .account("account")
                .email("memberA@naver.com")
                .password("password")
                .name("memberA")
                .phoneNumber("010-1234-5678")
                .birthDate(LocalDate.of(1992, 2, 16))
                .build();

        Member member = Member.from(memberJoinRequest);

        given(memberService.join(member)).willReturn(1L);

        // when
        ResultActions result = mockMvc.perform(
                post("/api/members")
                        .content(objectMapper.writeValueAsString(memberJoinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("회원가입을 성공하였습니다.")))
                .andExpect(jsonPath("$.data.id", is(0)))
                .andDo(document("members/join",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("account").type(STRING).description("J-Talk 계정"),
                                fieldWithPath("email").type(STRING).description("이메일"),
                                fieldWithPath("password").type(STRING).description("비밀번호"),
                                fieldWithPath("name").type(STRING).description("이름"),
                                fieldWithPath("phoneNumber").type(STRING).attributes(getPhoneNumberFormat()).description("핸드폰 번호").optional(),
                                fieldWithPath("birthDate").type(STRING).attributes(getDateFormat()).description("생년월일")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("id").type(NUMBER).description("가입한 회원 아이디")
                        )));
    }

    //TODO 비정상적인 회원가입 실패 테스트

    @Test
    @DisplayName("email 존재 유무 둘다 체크")
    void existsEmail() throws Exception {
        // given
        String email = "memberA@gmail.com";

        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when
        ResultActions result = mockMvc.perform(
                get("/api/members/exists/email/{email}", email)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("이미 존재하는 이메일입니다.")))
                .andExpect(jsonPath("$.data.exists", is(true)))
                .andDo(document("members/exists-email",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("이메일")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("exists").type(BOOLEAN).description("이메일 존재 여부")
                        ))
                );
    }

    @Test
    @DisplayName("account 존재 유무 둘다 체크")
    void existsAccount() throws Exception {
        // given
        String account = "memberA Account";

        given(memberRepository.existsByAccount(account)).willReturn(true);

        // when
        ResultActions result = mockMvc.perform(
                get("/api/members/exists/account/{account}", account)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then    
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("이미 존재하는 계정입니다.")))
                .andExpect(jsonPath("$.data.exists", is(true)))
                .andDo(document("members/exists-account",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("account").description("계정")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("exists").type(BOOLEAN).description("계정 존재 여부")
                        ))
                );
    }

    @Test
    @DisplayName("정상적인 회원 ID와 친구 ID로 친구 추가 하기")
    void addFriends() throws Exception {
        // given
        Long memberId = 1L;
        Long friendId = 2L;
        Long friendRelationId = 3L;

        given(memberService.addFriend(memberId, friendId)).willReturn(friendRelationId);

        // when
        ResultActions result = mockMvc.perform(
                post("/api/members/{memberId}/friends/{friendId}", memberId, friendId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("친구추가를 성공하였습니다.")))
                .andExpect(jsonPath("$.data.id", is(friendRelationId.intValue())))
                .andDo(document("members/add-friend",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("회원 ID"),
                                parameterWithName("friendId").description("친구 ID")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("id").type(NUMBER).description("친구 관계 ID")
                        )
                ));
    }

    //TODO 비정상적인 회원 ID와 친구 ID로 친구 추가 하기

    @Test
    @DisplayName("추가된 친구를 포함한 Member 조회")
    void findMember() throws Exception {
        // given
        Member member = memberCreator.nextObject(Member.class);
        Member friend1 = memberCreator.nextObject(Member.class);
        Member friend2 = memberCreator.nextObject(Member.class);
        Member friend3 = memberCreator.nextObject(Member.class);

        FriendRelation friendRelation1 = friendRelationCreator.nextObject(FriendRelation.class);
        friendRelation1.setMemberAndFriend(member, friend1);

        FriendRelation friendRelation2 = friendRelationCreator.nextObject(FriendRelation.class);
        friendRelation2.setMemberAndFriend(member, friend2);

        FriendRelation friendRelation3 = friendRelationCreator.nextObject(FriendRelation.class);
        friendRelation3.setMemberAndFriend(member, friend3);

        member.getFriends().add(friendRelation1);
        member.getFriends().add(friendRelation2);
        member.getFriends().add(friendRelation3);

        given(memberQueryRepository.findById(member.getId())).willReturn(Optional.of(member));

        // when
        ResultActions result = mockMvc.perform(
                get("/api/members/{id}", member.getId())
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is(member.getId() + "번 회원을 조회하였습니다.")))
                .andExpect(jsonPath("$.data.memberId", is(member.getId().intValue())))
                .andExpect(jsonPath("$..friends[0].friendRelationId", contains(friendRelation1.getId().intValue())))
                .andExpect(jsonPath("$..friends[0].friendName", contains(friend1.getName())))
                .andExpect(jsonPath("$..friends[0].status", contains(FriendRelationStatus.NORMAL.name())))
                .andExpect(jsonPath("$..friends[0].createdDate").isNotEmpty())
                .andExpect(jsonPath("$.data.account", is(member.getAccount())))
                .andExpect(jsonPath("$.data.email", is(member.getEmail())))
                .andExpect(jsonPath("$.data.name", is(member.getName())))
                .andExpect(jsonPath("$.data.phoneNumber", is(member.getPhoneNumber())))
                .andExpect(jsonPath("$.data.stateMessage", is(member.getStateMessage())))
                .andExpect(jsonPath("$.data.profileUrl", is(member.getProfileUrl())))
                // restdocs
                .andDo(document("members/find-by-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("회원 ID")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
                                fieldWithPath("account").type(STRING).description("계정"),
                                fieldWithPath("email").type(STRING).description("이메일"),
                                fieldWithPath("name").type(STRING).description("이름"),
                                fieldWithPath("phoneNumber").type(STRING).attributes(getPhoneNumberFormat()).description("핸드폰 번호"),
                                fieldWithPath("stateMessage").type(STRING).description("상태 메시지"),
                                fieldWithPath("profileUrl").type(STRING).description("프로필 URL"),
                                fieldWithPath("birthDate").type(STRING).attributes(getDateFormat()).description("생년월일"),
                                fieldWithPath("friends[0].friendRelationId").type(NUMBER).description("친구 관계 ID"),
                                fieldWithPath("friends[0].friendName").type(STRING).description("나에게 저장된 친구 이름"),
                                fieldWithPath("friends[0].status").type(STRING).description("친구 관계 상태"),
                                fieldWithPath("friends[0].createdDate").type(STRING).description("친구 추가 날짜")
                        )
                ));
    }


}



























