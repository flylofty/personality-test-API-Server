package api.personalitytest.web.dto;

import api.personalitytest.domain.test.Test;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {

    private String title;
    private String id; // => writer
    private String password;

    @Builder
    public UserSaveRequestDto(String title, String id, String password) {
        this.title = title;
        this.id = id;
        this.password = password;
    }

    public Test toEntity(String fileName) {
        return Test.builder()
                .title(title)
                .writer(id)
                .password(password)
                .fileName(fileName)
                .build();
    }
}
