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
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

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

    // 1. 시작 페이지 Card Component 데이터 요청
    @GetMapping("/api/v1/cards")
    public ResponseEntity<CardsResponseDto<List<CardDto>>> getCardInfoV1(Pageable pageable) {
        List<CardDto> cards = testService.findCards(pageable, imgUrl);

        return ResponseEntity.status(OK)
                .body(new CardsResponseDto<>(cards, cards.size()));
    }

    // 2. 테스트 등록
    @PostMapping("/api/v1/tests") // 간단하게 코드 수정하면 좋을 것 같음!!
    public ResponseEntity<SuccessResponseDto> saveV1(@RequestParam MultipartFile file,
                                                     HttpServletRequest request) throws IOException, ParseException
    {
        if (file.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new SuccessResponseDto(false));
        }

        // get user
        String requestUser = request.getParameter("user");
        ObjectMapper objectMapper = new ObjectMapper();
        UserSaveRequestDto userSaveRequestDto = objectMapper.readValue(requestUser, UserSaveRequestDto.class);
        userSaveRequestDto.setRequestFileName(file);

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

        // save
        String fullImageName = testService.save(userSaveRequestDto, resultSaveRequestDtoList, itemSaveRequestDtoList);
        file.transferTo(new File(fileDir + fullImageName));

        return ResponseEntity.status(CREATED)
                .body(new SuccessResponseDto(true));
    }

    // 3. 테스트 단건 요청
    @GetMapping("/api/v1/tests/{testId}")
    public ResponseEntity<TestResponseDto> findByTestIdV1(@PathVariable Long testId) {

        TestResponseDto responseDto = testService.findTestsById(testId);

        if (Objects.isNull(responseDto.getTitle()) || Objects.isNull(responseDto.getTestData()))
            return ResponseEntity.status(NOT_FOUND).build();

        return ResponseEntity.status(OK).body(responseDto);
    }

    // 4. 테스트의 특정 결과 요청, Result 컨트롤러 분리?
    @GetMapping("/api/v1/tests/{testId}/results/{resultId}")
    public ResponseEntity<ResultResponseDto<List<TestResultDataDto>>> findResultV1(@PathVariable Long testId,
                                                                                   @PathVariable String resultId)
    {
        ResultResponseDto<List<TestResultDataDto>> responseDto = testService.findResult(testId, resultId);

        if (Objects.isNull(responseDto.getResultData())) {
            return ResponseEntity.status(NOT_FOUND).build();
        }

        // return new ResponseEntity<>(responseDto, OK);
        return ResponseEntity.status(OK).body(responseDto);
    }

    // 5. 테스트 수정 전 인증 요청, WARNING: raw use of parameterized class
    @PostMapping("/api/v1/tests/{testId}/edit-page")
    public ResponseEntity authenticateUserV1(@PathVariable Long testId,
                                             @RequestBody AuthenticationRequestDto requestDto,
                                             HttpServletRequest request)
    {
        // 사용자 인증 성공
        if (testService.authenticateUser(testId, requestDto)) {

            // 세션 생성, 옵션이 있는데 default 는 true
            // true => 기존 세션이 있으면 해당 세션을 찾아서 꺼내주고 아니면 새로 만듦
            // false => 기존 세션이 있으면 해당 세션을 반환 그렇지 않으면 세션을 생성하지 않음
            HttpSession session = request.getSession();

            requestDto.setRequestTestId(testId);

            // 세션에 보관하고 싶은 객체를 저장 session.setAttribute(key, value) 이용
            session.setAttribute(SessionConst.UPDATE_TEST, requestDto);

            session.setMaxInactiveInterval(600);
            return ResponseEntity.status(OK).build();
        }

        return ResponseEntity.status(UNAUTHORIZED).build();
    }

    // 6. 수정 페이지 요청
    @GetMapping("/api/v1/tests/{testId}/edit")
    public ResponseEntity<TestUpdateResponseDto> getTestUpdatePage(@PathVariable Long testId, HttpServletRequest request) {

        //세션 조회, option 을 false 로 하여 세션 확인
        HttpSession session = request.getSession(false);

        // 세션이 없는 경우, 인증된 사용자가 아님 403
        if (session == null) {
            return ResponseEntity.status(FORBIDDEN).build();
        }

        AuthenticationRequestDto authenticationDto
                = (AuthenticationRequestDto) session.getAttribute(SessionConst.UPDATE_TEST);

        // Null 값 체크, BAD_REQUEST??? FORBIDDEN???
        if (Objects.isNull(authenticationDto)) {
            session.invalidate();
            return ResponseEntity.status(FORBIDDEN).build();
        }

        // 테스트 아이디가 같지 않은 경우, BAD_REQUEST??? FORBIDDEN???
        // test A의 세션을 가지고 다른 test 수정 페이지를 요청한 경우
        if (!Objects.equals(authenticationDto.getTestId(), testId)) {
            session.invalidate();
            return ResponseEntity.status(FORBIDDEN).build();
        }

        TestUpdateResponseDto responseDto = testService.getUpdateTest(testId, imgUrl);

        if (responseDto.isNull()){
            return ResponseEntity.status(NOT_FOUND).build();
        }

        return ResponseEntity.status(OK).body(responseDto);
    }

    // 테스트 등록과 유사하게 JSON 파싱함
    @PostMapping("/api/v1/tests/{testId}/edit")
    public ResponseEntity<SuccessResponseDto> update(@PathVariable Long testId,
                                                     @RequestParam MultipartFile file,
                                                     HttpServletRequest request) throws IOException, ParseException
    {
        //세션 조회
        HttpSession session = request.getSession(false);

        // 세션이 없는 경우, 인증된 사용자가 아님 403
        if (session == null) {
            return ResponseEntity.status(FORBIDDEN)
                    .body(new SuccessResponseDto(false));
        }

        AuthenticationRequestDto authenticationDto
                = (AuthenticationRequestDto) session.getAttribute(SessionConst.UPDATE_TEST);

        // Null 값 체크, BAD_REQUEST??? FORBIDDEN???
        if (Objects.isNull(authenticationDto)) {
            session.invalidate();
            return ResponseEntity.status(FORBIDDEN)
                    .body(new SuccessResponseDto(false));
        }

        if (!Objects.equals(authenticationDto.getTestId(), testId)) {
            session.invalidate();
            return ResponseEntity.status(FORBIDDEN)
                    .body(new SuccessResponseDto(false));
        }

        if (file.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new SuccessResponseDto(false));
        }

        // JSON 파싱 => DTO
        ObjectMapper objectMapper = new ObjectMapper();

        // get test
        String requestTest = request.getParameter("test");
        TestUpdateRequestDto testUpdateRequestDto = objectMapper.readValue(requestTest, TestUpdateRequestDto.class);
        testUpdateRequestDto.setRequestImageName(file);

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
        String fullImageName = testService.update(testId, testUpdateRequestDto, resultUpdateRequestDtoList, itemUpdateRequestDtoList);

        if (Objects.equals(fullImageName, "")) {
            ResponseEntity.status(NOT_FOUND)
                    .body(new SuccessResponseDto(false));
        }

        file.transferTo(new File(fileDir + fullImageName));
        return ResponseEntity.status(OK)
                .body(new SuccessResponseDto(true));
    }

    // 8. 특정 테스트 삭제 요청
    @PostMapping("/api/v1/tests/{testId}/delete") // 90% 완성, 응답 false 처리
    public SuccessResponseDto deleteV1(@PathVariable Long testId,
                                       @RequestBody TestDeleteRequestDto requestDto) {

        requestDto.setRequestTestId(testId);

        return testService.delete(requestDto);
    }
}