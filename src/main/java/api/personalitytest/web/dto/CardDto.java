package api.personalitytest.web.dto;

import api.personalitytest.domain.test.Test;
import lombok.Getter;

@Getter
public class CardDto {

    private Long id;
    private String imgUrl;
    private String title;

    public CardDto(Test entity) {
        this.id = entity.getId();
        this.imgUrl = entity.getFullImageName();
        this.title = entity.getTitle();
    }

    /**
     * "http://localhost:8081/images/"
     * 이것 작성한 것이 살짝 흠인 것 같음
     */
    public void addDir() {
        this.imgUrl = "http://localhost:8081/images/" + this.imgUrl;
    }
}
