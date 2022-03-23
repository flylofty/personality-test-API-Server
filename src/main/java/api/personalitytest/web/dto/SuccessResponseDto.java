package api.personalitytest.web.dto;

import lombok.Getter;

@Getter
public class SuccessResponseDto {

    private Boolean success;

    public SuccessResponseDto(Boolean success) {
        this.success = success;
    }
}
