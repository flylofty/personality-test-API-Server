package api.personalitytest.web.dto;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter // Getter 가 없으면 Resolved [org.springframework.web.HttpMediaTypeNotAcceptableException: Could not find acceptable representation] 해당 경고를 로그에서 볼 수 있고, 406에러를 보게됨
public class TestResponseDto {

    private String title;
    private List<TestItemDataDto> testData;

    public TestResponseDto(String title, List<TestItemDataDto> testData) {
        this.title = title;
        this.testData = testData;
    }

    public TestResponseDto() {
    }
}
