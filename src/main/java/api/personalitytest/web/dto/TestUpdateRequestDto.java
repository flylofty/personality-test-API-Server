package api.personalitytest.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TestUpdateRequestDto {

    private String title;
    private String imageName;

    @Builder
    public TestUpdateRequestDto(String title) {
        this.title = title;
    }

    public void setRequestImageName(String imageName) {
        this.imageName = imageName;
    }
}
