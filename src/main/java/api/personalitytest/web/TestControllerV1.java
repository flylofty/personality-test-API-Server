package api.personalitytest.web;

import api.personalitytest.service.test.TestService;
import api.personalitytest.web.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@PropertySource("classpath:application-dir.properties")
public class TestControllerV1 {

    private final TestService testService;

    @Value("${file.dir}")
    private String fileDir;

    @Value("${client.imageUrl}")
    private String imgUrl;

    // 시작 페이지 Card Component 데이터 요청
    @GetMapping("/api/v1/cards")
    public ResponseEntity<CardsResponseDto<List<CardDto>>> getCardInfoV1(Pageable pageable) {
        List<CardDto> cards = testService.findCards(pageable, imgUrl);
        return ResponseEntity.ok(new CardsResponseDto<>(cards, cards.size()));
    }

    // 테스트 등록
    @PostMapping("/api/v1/tests") // 70% 완성 리턴값!, 간단하게 코드 수정하면 좋을 것 같음!!
    public SuccessResponseDto saveV1(@RequestParam MultipartFile file,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException, ParseException {

        // get user
        String requestUser = request.getParameter("user");
        ObjectMapper objectMapper = new ObjectMapper();
        UserSaveRequestDto userSaveRequestDto = objectMapper.readValue(requestUser, UserSaveRequestDto.class);

        // get result
        String requestResults = request.getParameter("results");

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonResults = (JSONArray) jsonParser.parse(requestResults);

        ArrayList<ResultSaveRequestDto> resultSaveRequestDtoList = new ArrayList<>();
        for (Object obj : jsonResults) {
            resultSaveRequestDtoList.add(new ResultSaveRequestDto((JSONObject) obj));
        }

        // get items
        String requestItems = request.getParameter("items");
        JSONArray jsonItems = (JSONArray) jsonParser.parse(requestItems);

        ArrayList<ItemSaveRequestDto> itemSaveRequestDtoList = new ArrayList<>();
        for (Object obj : jsonItems) {
            itemSaveRequestDtoList.add(new ItemSaveRequestDto((JSONObject) obj));
        }

        //get image & save
        log.info("multipartFile={}", file);
        if (!file.isEmpty()) {
            String imageName = testService.save(userSaveRequestDto, resultSaveRequestDtoList, itemSaveRequestDtoList, file.getOriginalFilename());
            String fullPath = fileDir + imageName;
            file.transferTo(new File(fullPath));
        }

        return new SuccessResponseDto(true);
    }

    // 테스트 단건 요청
    @GetMapping("/api/v1/tests/{testId}") // 90% 완성 응답 false 처리
    public TestResponseDto findByTestIdV1(@PathVariable Long testId) {

        return testService.findTestsById(testId);
    }

    // 테스트의 특정 결과 요청, Result 컨트롤러 분리??????
    @GetMapping("/api/v1/tests/{testId}/results/{resultId}") // 90% 완성 응답 false 처리
    public ResultResponseDto findResultV1(@PathVariable Long testId,
                                          @PathVariable String resultId)
    {
        return testService.findSingleResultByTwoId(testId, resultId);
    }

    // 특정 테스트 삭제 요청
    @PostMapping("/api/v1/tests/{testId}/delete") // 90% 완성, 응답 false 처리
    public SuccessResponseDto deleteV1(@PathVariable Long testId,
                                       @RequestBody TestDeleteRequestDto requestDto) {

        requestDto.setRequestTestId(testId);

        return testService.delete(requestDto);
    }

    // 테스트 수정 전 인증 요청
    @PostMapping("/api/v1/tests/{testId}/edit-page")
    public SuccessResponseDto authenticateUserV1(@PathVariable Long testId,
                                                 @RequestBody AuthenticationRequestDto requestDto,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {
        // 사용자 인증
        requestDto.setRequestTestId(testId);
        SuccessResponseDto responseDto = testService.authenticateUser(requestDto);

        // 메서드 처리하면 좋을 거 같음
        // 인증 성공
        if (responseDto.getSuccess()) {

            // 세션 생성, 옵션이 있는데 default 는 true
            // true => 기존 세션이 있으면 해당 세션을 찾아서 꺼내주고 아니면 새로 만듦
            // false => 기존 세션이 있으면 해당 세션을 반환 그렇지 않으면 세션을 생성하지 않음
            HttpSession session = request.getSession();

            // 세션에 보관하고 싶은 객체를 저장 session.setAttribute(key, value) 이용
            session.setAttribute(SessionConst.UPDATE_TEST, requestDto);
        }

        return responseDto;
    }

    // 수정 페이지 요청
    @GetMapping("/api/v1/tests/{testId}/edit")
    public TestUpdateResponseDto getTestUpdatePage(@PathVariable Long testId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {

        //세션 조회, option 을 false 로 하여 세션 확인
        HttpSession session = request.getSession(false);

        // 세션이 없는 경우, 랜딩 페이지로 redirect
        if (session == null) {
            return new TestUpdateResponseDto(false);
        }

        AuthenticationRequestDto authenticationDto = (AuthenticationRequestDto) session.getAttribute(SessionConst.UPDATE_TEST);

        // 테스트 아이디가 같지 않은 경우
        if (!Objects.equals(authenticationDto.getTestId(), testId)) {
            session.invalidate();
            return new TestUpdateResponseDto(false);
        }

        return testService.getUpdateTest(testId);
    }

    // 테스트 등록과 유사하게 JSON 파싱 해야함!!!!!!!!!!!!
    @PostMapping("/api/v1/tests/{testId}/edit")
    public SuccessResponseDto update(@PathVariable Long testId,
                                     @RequestParam MultipartFile file,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException, ParseException
    {
        // 세션 조회
        HttpSession session = request.getSession(false);

        // 세션 Null 인 경우 => redirect
        if (session == null) {
            log.info("update session == null");
            return new SuccessResponseDto(false);
        }

        // JSON 파싱 => DTO
        ObjectMapper objectMapper = new ObjectMapper();

        // get test
        String requestTest = request.getParameter("test");
        TestUpdateRequestDto testUpdateRequestDto = objectMapper.readValue(requestTest, TestUpdateRequestDto.class);

        if (!file.isEmpty()) {
            testUpdateRequestDto.setRequestImageName(file.getOriginalFilename());
        }

        // get result
        String requestResults = request.getParameter("results");
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonResults = (JSONArray) jsonParser.parse(requestResults);
        ArrayList<ResultUpdateRequestDto> resultUpdateRequestDtoList = new ArrayList<>();
        for (Object jsonResult : jsonResults) {
            resultUpdateRequestDtoList.add(new ResultUpdateRequestDto((JSONObject) jsonResult));
        }

        // get items
        String requestItems = request.getParameter("items");
        JSONArray jsonItems = (JSONArray) jsonParser.parse(requestItems);
        ArrayList<ItemUpdateRequestDto> itemUpdateRequestDtoList = new ArrayList<>();
        for (Object jsonItem : jsonItems) {
            itemUpdateRequestDtoList.add(new ItemUpdateRequestDto((JSONObject) jsonItem));
        }

        // 세션 만료
        session.invalidate();

        // service 통해 Test update
        return testService.update(testId, testUpdateRequestDto, resultUpdateRequestDtoList, itemUpdateRequestDtoList, file, fileDir);
    }
}