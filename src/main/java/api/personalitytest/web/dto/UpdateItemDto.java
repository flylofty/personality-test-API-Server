package api.personalitytest.web.dto;

import api.personalitytest.domain.item.Item;
import lombok.Getter;

@Getter
public class UpdateItemDto {

    private String question;
    private String select_1;
    private String select_2;
    private Long select_1_id;
    private Long select_2_id;

    public UpdateItemDto(Item entity) {
        this.question = entity.getQuestion();
        this.select_1 = entity.getSelection_1();
        this.select_2 = entity.getSelection_2();
        this.select_1_id = Long.parseLong(entity.getSelection_1_id());
        this.select_2_id = Long.parseLong(entity.getSelection_2_id());
    }
}
