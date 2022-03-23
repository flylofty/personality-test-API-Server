package api.personalitytest.web.dto;

import api.personalitytest.domain.item.Item;
import lombok.Getter;

@Getter
public class TestItemDataDto {

    private Long key;
    private String question;
    private String select_1;
    private String select_2;
    private String select_1_id;
    private String select_2_id;

    public TestItemDataDto(Item entity) {
        this.key = entity.getTestId();
        this.question = entity.getQuestion();
        this.select_1 = entity.getSelection_1();
        this.select_2 = entity.getSelection_2();
        this.select_1_id = entity.getSelection_1_id();
        this.select_2_id = entity.getSelection_2_id();
    }
}
