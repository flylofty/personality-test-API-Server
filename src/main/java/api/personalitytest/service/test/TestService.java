package api.personalitytest.service.test;

import api.personalitytest.domain.item.ItemRepository;
import api.personalitytest.domain.result.ResultRepository;
import api.personalitytest.domain.test.Test;
import api.personalitytest.domain.test.TestRepository;
import api.personalitytest.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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
                .stream().map(entity -> new CardDto(entity, postDir)).collect(Collectors.toList());
    }

    @Transactional
    public String save(UserSaveRequestDto userSaveRequestDto,
                       ArrayList<ResultSaveRequestDto> resultSaveRequestDtoList,
                       ArrayList<ItemSaveRequestDto> itemSaveRequestDtoList) {

        Test savedTest = testRepository.save(userSaveRequestDto.toEntity());
        Long savedTestId = savedTest.getId();

        for (ResultSaveRequestDto resultSaveRequestDto : resultSaveRequestDtoList) {
            resultRepository.save(resultSaveRequestDto.toEntity(savedTestId));
        }

        for (ItemSaveRequestDto itemSaveRequestDto : itemSaveRequestDtoList) {
            itemRepository.save(itemSaveRequestDto.toEntity(savedTestId));
        }

        return savedTest.getFullImageName();
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

        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isEmpty()) {
            return new TestResponseDto();
        }

        List<TestItemDataDto> testData = itemRepository.findAllByTestId(testId)
                .stream().map(TestItemDataDto::new).collect(Collectors.toList());

        return new TestResponseDto(optionalTest.get().getTitle(), testData);
    }

    @Transactional(readOnly = true)
    public ResultResponseDto<List<TestResultDataDto>> findResult(Long testId, String resultId) {

        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isEmpty()) {
            return new ResultResponseDto<>();
        }

        return new ResultResponseDto<>(resultRepository.findTestResultList(testId, resultId)
                .stream().map(TestResultDataDto::new).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public Boolean authenticateUser(Long testId, AuthenticationRequestDto requestDto) {

        return testRepository.findById(testId)
                .map(test -> test.authenticateUser(requestDto)).orElse(false);
    }

    @Transactional(readOnly = true)
    public TestUpdateResponseDto getUpdateTest(Long testId, String postDir) {

        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isEmpty()) {
            return new TestUpdateResponseDto();
        }

        List<UpdateItemDto> items = itemRepository.findAllByTestId(testId)
                .stream().map(UpdateItemDto::new).collect(Collectors.toList());

        List<UpdateResultDto> resultContent = resultRepository.findAllByTestId(testId)
                .stream().map(UpdateResultDto::new).collect(Collectors.toList());

        UpdateUserDto userItem = new UpdateUserDto(optionalTest.get());

        return new TestUpdateResponseDto(postDir + optionalTest.get().getFullImageName(), items, resultContent, userItem);
    }

    @Transactional
    public String update(Long testId,
                                     TestUpdateRequestDto testDto,
                                     ArrayList<ResultUpdateRequestDto> resultDtoList,
                                     ArrayList<ItemUpdateRequestDto> itemDtoList)
    {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isEmpty()) {
            return "";
        }

        Test test = optionalTest.get();

        // Dirty Checking
        test.update(testDto);

        // 기존 테스트 데이터 및 결과 우선 삭제 하고 새로 저장
        resultRepository.deleteResultsByTestId(testId);
        for (ResultUpdateRequestDto dto : resultDtoList) {
            resultRepository.save(dto.toEntity(testId));
        }

        itemRepository.deleteItemsByTestId(testId);
        for (ItemUpdateRequestDto dto : itemDtoList) {
            itemRepository.save(dto.toEntity(testId));
        }

        return test.getFullImageName();
    }
}
