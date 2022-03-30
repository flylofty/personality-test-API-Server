package api.personalitytest.web.dto;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class TestUpdateResponseDto {

    private String imgUrl;
    private List<UpdateItemDto> items;
    private List<UpdateResultDto> resultContent;
    private UpdateUserDto userItem;

    public TestUpdateResponseDto(String imgUrl, List<UpdateItemDto> items, List<UpdateResultDto> resultContent, UpdateUserDto userItem) {
        this.imgUrl = imgUrl;
        this.items = items;
        this.resultContent = resultContent;
        this.userItem = userItem;
    }

    public TestUpdateResponseDto() {
    }

    public boolean isNull() {
        return Objects.isNull(imgUrl) || Objects.isNull(items) || Objects.isNull(resultContent) || Objects.isNull(userItem);
    }
}
