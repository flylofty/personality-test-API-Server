package api.personalitytest.web.dto;

import api.personalitytest.domain.test.Test;
import lombok.Getter;

@Getter
public class CardDto {

    private Long id;
    private String imgUrl;
    private String title;

    public CardDto(Test entity, String postDir) {
        this.id = entity.getId();
        this.imgUrl = postDir + entity.getFullImageName();
        this.title = entity.getTitle();
    }
}
