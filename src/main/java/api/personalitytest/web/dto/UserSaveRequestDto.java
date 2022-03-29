package api.personalitytest.web.dto;

import api.personalitytest.domain.test.Test;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {

    private String title;
    private String id; // => writer
    private String password;
    private String fileName;

    @Builder
    public UserSaveRequestDto(String title, String id, String password) {
        this.title = title;
        this.id = id;
        this.password = password;
    }

    public void setRequestFileName(MultipartFile file) {
        this.fileName = file.getOriginalFilename();
    }

    public Test toEntity() {
        return Test.builder()
                .title(title)
                .writer(id)
                .password(password)
                .fileName(fileName)
                .build();
    }
}
