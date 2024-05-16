package shop.mtcoding.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import shop.mtcoding.blog._core.utils.ApiUtil;
import shop.mtcoding.blog.user.SessionUser;
import shop.mtcoding.blog.user.UserRequest;
import shop.mtcoding.blog.user.UserResponse;

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
                MockMvcRequestBuilders.post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        System.out.println("respBody : "+respBody);
        //System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.id").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.username").value("haha"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.email").value("haha@nate.com"));
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
                MockMvcRequestBuilders.post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        //System.out.println("respBody : "+respBody);
        //System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("중복된 유저네임입니다"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty());
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
                MockMvcRequestBuilders.post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        SessionUser sessionUser = (SessionUser)actions.andReturn().getModelAndView().getModel().get("sessionUser");
        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //int statusCode = actions.andReturn().getResponse().getStatus();
        //System.out.println("respBody : "+respBody);
        //System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("영문/숫자 2~20자 이내로 작성해주세요 : username"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty());
    }
}