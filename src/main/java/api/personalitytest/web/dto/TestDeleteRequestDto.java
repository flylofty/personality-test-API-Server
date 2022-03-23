package api.personalitytest.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TestDeleteRequestDto {

    private String userId;
    private String password;
    private Long testId;

    public void setRequestTestId(Long testId) {
        this.testId = testId;
    }
}
