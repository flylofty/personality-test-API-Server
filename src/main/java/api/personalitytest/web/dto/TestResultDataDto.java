package api.personalitytest.web.dto;

import api.personalitytest.domain.result.Result;
import lombok.Getter;

@Getter
public class TestResultDataDto {

    private Long key;
    private String id;
    private String who;
    private String content;

    public TestResultDataDto(Result entity) {
        this.key = entity.getTestId();
        this.id = entity.getResultId();
        this.who = entity.getTitle();
        this.content = entity.getContent();
    }
}
