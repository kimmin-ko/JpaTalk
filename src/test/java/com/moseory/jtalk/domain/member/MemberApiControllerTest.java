package com.moseory.jtalk.domain.member;

import com.moseory.jtalk.domain.abstact.AbstractApiControllerTest;
import com.moseory.jtalk.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static com.moseory.jtalk.DocumentFormatGenerator.getDateFormat;
import static com.moseory.jtalk.DocumentFormatGenerator.getPhoneNumberFormat;
import static com.moseory.jtalk.domain.member.MemberApiController.MemberJoinRequest;
import static com.moseory.jtalk.domain.restdocs.ApiDocumentationTest.getDocumentRequest;
import static com.moseory.jtalk.domain.restdocs.ApiDocumentationTest.getDocumentResponse;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
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

    @Test
    @DisplayName("회원가입 성공 테스트")
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

    @Test
    @DisplayName("email 존재 유무 체크")
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
    @DisplayName("account 존재 유무 체크")
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
                .andExpect(jsonPath("$.message", is("이미 존재하는 계정입니다.")));
//                .andExpect(jsonPath("$.data"))

    }

}



























