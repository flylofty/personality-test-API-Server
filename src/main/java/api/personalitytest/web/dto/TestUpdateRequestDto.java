package api.personalitytest.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class TestUpdateRequestDto {

    private String title;
    private String imageName;

    public void setRequestImageName(MultipartFile file) {
        this.imageName = file.getOriginalFilename();
    }
}
