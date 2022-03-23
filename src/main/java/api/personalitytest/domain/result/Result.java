package api.personalitytest.domain.result;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "test_result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long testId;
    private String resultId;
    private String title;
    private String content;

    @Builder
    public Result(Long testId, String resultId, String title, String content) {
        this.testId = testId;
        this.resultId = resultId;
        this.title = title;
        this.content = content;
    }
}
