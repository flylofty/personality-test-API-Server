package api.personalitytest.web.dto;

import api.personalitytest.domain.result.Result;
import lombok.Getter;
import org.json.simple.JSONObject;

@Getter
public class ResultSaveRequestDto {

    private String id;
    private String title;
    private String content;

    public ResultSaveRequestDto(JSONObject resultObject) {
        this.id = resultObject.get("id").toString();
        this.title = resultObject.get("who").toString();
        this.content = resultObject.get("content").toString();
    }

    public Result toEntity(Long testId) {
        return Result.builder()
                .testId(testId)
                .resultId(id)
                .title(title)
                .content(content)
                .build();
    }
}
