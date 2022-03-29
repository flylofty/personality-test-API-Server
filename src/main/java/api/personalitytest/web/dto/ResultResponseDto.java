package api.personalitytest.web.dto;

import lombok.Getter;

@Getter
public class ResultResponseDto<T> {

    private T resultData;

    public ResultResponseDto(T resultData) {
        this.resultData = resultData;
    }

    public ResultResponseDto() {
    }
}
