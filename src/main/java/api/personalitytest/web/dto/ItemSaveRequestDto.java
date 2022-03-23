package api.personalitytest.web.dto;

import api.personalitytest.domain.item.Item;
import lombok.Getter;
import org.json.simple.JSONObject;

@Getter
public class ItemSaveRequestDto {

    private String question;
    private String selection;
    private String selectionId;
    private String selection2;
    private String selectionId2;

    public ItemSaveRequestDto(JSONObject resultObject) {
        this.question = resultObject.get("question").toString();
        this.selection = resultObject.get("select_1").toString();
        this.selectionId = resultObject.get("select_1_id").toString();
        this.selection2 = resultObject.get("select_2").toString();
        this.selectionId2 = resultObject.get("select_2_id").toString();
    }

    public Item toEntity(Long testId) {
        return Item.builder()
                .testId(testId)
                .question(question)
                .selection_1(selection)
                .selection_1_id(selectionId)
                .selection_2(selection2)
                .selection_2_id(selectionId2)
                .build();
    }
}
