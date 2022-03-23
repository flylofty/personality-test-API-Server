package api.personalitytest.web.dto;

import api.personalitytest.domain.item.Item;
import api.personalitytest.domain.test.Test;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter // Getter 가 없으면 Resolved [org.springframework.web.HttpMediaTypeNotAcceptableException: Could not find acceptable representation] 해당 경고를 로그에서 볼 수 있고, 406에러를 보게됨
public class TestResponseDto {
    private Boolean success;
    private String title;
    private List<TestItemDataDto> testData;

    public TestResponseDto(Boolean success, Test test, List<Item> itemList) {
        this.success = success;
        this.title = test.getTitle();
        this.testData = itemList.stream().map(TestItemDataDto::new).collect(Collectors.toList());
    }
}
