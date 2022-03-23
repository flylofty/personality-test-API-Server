package api.personalitytest.web.dto;

import api.personalitytest.domain.test.Test;
import lombok.Getter;

@Getter
public class UpdateUserDto {

    private String title;
    private String id;
    private String password;

    public UpdateUserDto(Test entity) {
        this.title = entity.getTitle();
        this.id = entity.getWriter();
        this.password = entity.getPassword();
    }
}
