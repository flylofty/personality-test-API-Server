package api.personalitytest.web.dto;

import api.personalitytest.domain.item.Item;
import api.personalitytest.domain.result.Result;
import api.personalitytest.domain.test.Test;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TestUpdateResponseDto {

    private Boolean success;
    private String imgUrl;
    private List<UpdateItemDto> items;
    private List<UpdateResultDto> resultContent;
    private UpdateUserDto userItem;

    /**
     * "http://localhost:8081/images/" 안보이게 처리해야할 것 같음
     */
    public TestUpdateResponseDto(Boolean success, List<Item> itemList, List<Result> resultList, Test test) {
        this.success = success;
        this.imgUrl = "http://localhost:8081/images/" + test.getFullImageName();
        this.items = itemList.stream().map(UpdateItemDto::new).collect(Collectors.toList());
        this.resultContent = resultList.stream().map(UpdateResultDto::new).collect(Collectors.toList());
        this.userItem = new UpdateUserDto(test);
    }

    public TestUpdateResponseDto(Boolean success) {
        this.success = success;
    }
}
