package api.personalitytest.web.dto;

import lombok.Getter;

import java.util.List;

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
}
