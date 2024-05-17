package shop.mtcoding.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.blog._core.utils.JwtUtil;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 1. 통합테스트 (스프링의 모든 빈을 IoC에 등록하고 테스트 하는 것)
 * 2. 배포직전 최종테스트
 */

@AutoConfigureMockMvc // MockMvc IoC 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 모든 빈 IoC 로드
public class UserControllerTest {

    private ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private static String jwt;

    @BeforeAll
    public static void setUp() {
        jwt = JwtUtil.create(
                User.builder()
                        .id(1)
                        .username("ssar")
                        .password("1234")
                        .email("ssar@nate.com")
                        .build());
    }

    @Test
    public void userinfo_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                get("/api/users/" + id)
                        .header("Authorization", "Bearer " + jwt)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("respBody : " + respBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.id").value(1));
        actions.andExpect(jsonPath("$.body.username").value("ssar"));
        actions.andExpect(jsonPath("$.body.email").value("ssar@nate.com"));
    }


    @Test
    public void join_test() throws Exception {
        // given
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setUsername("haha");
        reqDTO.setPassword("1234");
        reqDTO.setEmail("haha@nate.com");

        String reqBody = om.writeValueAsString(reqDTO);
        //System.out.println("reqBody : "+reqBody);

        // when
        ResultActions actions = mvc.perform(
                post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        System.out.println("respBody : " + respBody);
        //System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.id").value(4));
        actions.andExpect(jsonPath("$.body.username").value("haha"));
        actions.andExpect(jsonPath("$.body.email").value("haha@nate.com"));
    }

    @Test
    public void join_username_same_fail_test() throws Exception {
        // given
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setUsername("ssar");
        reqDTO.setPassword("1234");
        reqDTO.setEmail("ssar@nate.com");

        String reqBody = om.writeValueAsString(reqDTO);
        //System.out.println("reqBody : "+reqBody);

        // when
        ResultActions actions = mvc.perform(
                post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        //System.out.println("respBody : "+respBody);
        //System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(400));
        actions.andExpect(jsonPath("$.msg").value("중복된 유저네임입니다"));
        actions.andExpect(jsonPath("$.body").isEmpty());
    }

    // {"status":400,"msg":"영문/숫자 2~20자 이내로 작성해주세요 : username","body":null}
    @Test
    public void join_username_valid_fail_test() throws Exception {
        // given
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setUsername("김완준");
        reqDTO.setPassword("1234");
        reqDTO.setEmail("ssar@nate.com");

        String reqBody = om.writeValueAsString(reqDTO);
        //System.out.println("reqBody : "+reqBody);

        // when
        ResultActions actions = mvc.perform(
                post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        //System.out.println("respBody : "+respBody);
        //System.out.println("statusCode : "+statusCode);


        // then
        actions.andExpect(jsonPath("$.status").value(400));
        actions.andExpect(jsonPath("$.msg").value("영문/숫자 2~20자 이내로 작성해주세요 : username"));
        actions.andExpect(jsonPath("$.body").isEmpty());
    }

    @Test
    public void login_success_test() throws Exception {
        // given
        UserRequest.LoginDTO reqDTO = new UserRequest.LoginDTO();
        reqDTO.setUsername("ssar");
        reqDTO.setPassword("1234");

        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/login")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //System.out.println("respBody : "+respBody);
        String jwt = actions.andReturn().getResponse().getHeader("Authorization");
        System.out.println("jwt = " + jwt);

        // then
        actions.andExpect(status().isOk()); // header 검증
        actions.andExpect(result -> result.getResponse().getHeader("Authorization").contains("Bearer " + jwt));


        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body").isEmpty());
    }

    @Test
    public void login_fail_test() throws Exception {
        // given
        UserRequest.LoginDTO reqDTO = new UserRequest.LoginDTO();
        reqDTO.setUsername("ssar");
        reqDTO.setPassword("12345");

        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/login")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isUnauthorized()); // header 검증

        actions.andExpect(jsonPath("$.status").value(401));
        actions.andExpect(jsonPath("$.msg").value("인증되지 않았습니다"));
        actions.andExpect(jsonPath("$.body").isEmpty());
    }


}