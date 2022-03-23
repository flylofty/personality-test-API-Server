package api.personalitytest.web.dto;

import api.personalitytest.domain.result.Result;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ResultResponseDto {

    private Boolean success;
    private List<TestResultDataDto> resultData;

    public ResultResponseDto(Boolean success, List<Result> testResultList) {
        this.success = success;
        this.resultData = testResultList.stream().map(TestResultDataDto::new).collect(Collectors.toList());
    }
}
