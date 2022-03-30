package api.personalitytest.domain.test;

import api.personalitytest.domain.BaseTimeEntity;
import api.personalitytest.web.dto.AuthenticationRequestDto;
import api.personalitytest.web.dto.TestDeleteRequestDto;
import api.personalitytest.web.dto.TestUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@NoArgsConstructor
@Entity
public class Test extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String writer;
    private String password;
    private String imageName;

    @Builder
    public Test(String title, String writer, String password, String fileName) {
        this.title = title;
        this.writer = writer;
        this.password = password;
        this.imageName = fileName;
    }

    public String getFullImageName() {

        if (this.getCreatedDate().equals(this.getModifiedDate())) {
            return this.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")) + imageName;
        }

        return this.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")) + imageName;
    }

    public boolean authenticateUpdateUser(AuthenticationRequestDto requestDto) {
        return this.writer.equals(requestDto.getUserId()) && this.password.equals(requestDto.getPassword());
    }

    public boolean authenticateDeleteUser(TestDeleteRequestDto requestDto) {
        return !(this.writer.equals(requestDto.getUserId()) && this.password.equals(requestDto.getPassword()));
    }

    public void update(TestUpdateRequestDto dto) {
        this.title = dto.getTitle();
        this.imageName = dto.getImageName();
    }
}
