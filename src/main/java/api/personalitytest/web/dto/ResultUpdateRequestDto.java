package api.personalitytest.web.dto;

import api.personalitytest.domain.result.Result;
import lombok.Getter;
import org.json.simple.JSONObject;

@Getter
public class ResultUpdateRequestDto {

    private String resultId;
    private String title;
    private String content;

    public ResultUpdateRequestDto(JSONObject resultObject) {
        this.resultId = resultObject.get("id").toString();
        this.title = resultObject.get("who").toString();
        this.content = resultObject.get("content").toString();
    }

    public Result toEntity(Long testId) {
        return Result.builder()
                .testId(testId)
                .resultId(resultId)
                .title(title)
                .content(content)
                .build();
    }
}
