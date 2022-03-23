package api.personalitytest.web.dto;

import api.personalitytest.domain.result.Result;
import lombok.Getter;

@Getter
public class UpdateResultDto {

    private String id;
    private String who;
    private String content;

    public UpdateResultDto(Result entity) {
        this.id = entity.getResultId();
        this.who = entity.getTitle();
        this.content = entity.getContent();
    }
}
