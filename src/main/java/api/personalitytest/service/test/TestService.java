package api.personalitytest.service.test;

import api.personalitytest.domain.item.Item;
import api.personalitytest.domain.item.ItemRepository;
import api.personalitytest.domain.result.Result;
import api.personalitytest.domain.result.ResultRepository;
import api.personalitytest.domain.test.Test;
import api.personalitytest.domain.test.TestRepository;
import api.personalitytest.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TestService {

    private final TestRepository testRepository;
    private final ItemRepository itemRepository;
    private final ResultRepository resultRepository;

    @Transactional(readOnly = true)
    public List<CardDto> findCards(Pageable pageable, String postDir) {
        return testRepository.findAll(pageable)
                .stream().map(p -> new CardDto(p, postDir)).collect(Collectors.toList());
    }

    @Transactional
    public String save(UserSaveRequestDto userSaveRequestDto, ArrayList<ResultSaveRequestDto> resultSaveRequestDtoList,
                     ArrayList<ItemSaveRequestDto> itemSaveRequestDtoList, String fileName) {

        Long testId = testRepository.save(userSaveRequestDto.toEntity(fileName)).getId();

        for (ResultSaveRequestDto resultSaveRequestDto : resultSaveRequestDtoList) {
            resultRepository.save(resultSaveRequestDto.toEntity(testId));
        }

        for (ItemSaveRequestDto itemSaveRequestDto : itemSaveRequestDtoList) {
            itemRepository.save(itemSaveRequestDto.toEntity(testId));
        }

        return testRepository.findById(testId).get().getFullImageName();
    }

    @Transactional
    public SuccessResponseDto delete(TestDeleteRequestDto requestDto) {

        Test test = testRepository.findByDeleteRequest(requestDto.getTestId(), requestDto.getUserId(), requestDto.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("해당 테스트가 없습니다. testId=" + requestDto.getTestId()));

        testRepository.delete(test);
        return new SuccessResponseDto(true);
    }

    @Transactional(readOnly = true)
    public TestResponseDto findTestsById(Long testId) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테스트가 없습니다. testId=" + testId));

        List<Item> testItemList = itemRepository.findAllByTestId(testId);

        return new TestResponseDto(true, test, testItemList);
    }

    @Transactional(readOnly = true) // 메서드 명 수정하면 좋을 것 같음
    public ResultResponseDto findSingleResultByTwoId(Long testId, String resultId) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테스트가 없습니다. testId=" + testId));

        List<Result> testResultList = resultRepository.findTestResultList(testId, resultId);

        return new ResultResponseDto(true, testResultList);
    }

    @Transactional(readOnly = true)
    public SuccessResponseDto authenticateUser(AuthenticationRequestDto requestDto) throws IOException {

        Optional<Test> findTest = testRepository.findById(requestDto.getTestId());

        if (findTest.isPresent()) {
            // 인증 성공
            if (findTest.get().authenticateUser(requestDto)) {
                return new SuccessResponseDto(true);
            }
        }

        return new SuccessResponseDto(false);
    }

    @Transactional(readOnly = true)
    public TestUpdateResponseDto getUpdateTest(Long testId) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테스트가 없습니다. testId" + testId));

        List<Item> itemList = itemRepository.findAllByTestId(testId);
        List<Result> resultList = resultRepository.findAllByTestId(testId);

        return new TestUpdateResponseDto(true, itemList, resultList, test);
    }

    @Transactional
    public SuccessResponseDto update(Long testId,
                                     TestUpdateRequestDto testDto,
                                     ArrayList<ResultUpdateRequestDto> resultUpdateDto,
                                     ArrayList<ItemUpdateRequestDto> itemUpdateDto,
                                     MultipartFile file,
                                     String dir) throws IOException {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테스트가 없습니다. + testId" + testId));

        // Dirty Checking
        test.update(testDto);

        // 기존 테스트 데이터 및 결과 우선 삭제 하고 새로 저장
        resultRepository.deleteResultsByTestId(testId);
        for (ResultUpdateRequestDto dto : resultUpdateDto) {
            resultRepository.save(dto.toEntity(testId));
        }

        itemRepository.deleteItemsByTestId(testId);
        for (ItemUpdateRequestDto dto : itemUpdateDto) {
            itemRepository.save(dto.toEntity(testId));
        }

        file.transferTo(new File(dir + test.getFullImageName()));

        return new SuccessResponseDto(true);
    }
}
