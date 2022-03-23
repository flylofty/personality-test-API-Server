package api.personalitytest.domain.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "test_item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long testId;
    private String question;

    // 테이블 컬럼명과 변수명을 일치했는데 잘하는 것인지 모르겠음
    private String selection_1;
    private String selection_1_id;
    private String selection_2;
    private String selection_2_id;

    @Builder
    public Item(Long testId, String question, String selection_1, String selection_1_id, String selection_2, String selection_2_id) {
        this.testId = testId;
        this.question = question;
        this.selection_1 = selection_1;
        this.selection_1_id = selection_1_id;
        this.selection_2 = selection_2;
        this.selection_2_id = selection_2_id;
    }
}
