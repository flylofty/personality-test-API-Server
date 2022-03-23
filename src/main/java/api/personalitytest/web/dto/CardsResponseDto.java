package api.personalitytest.web.dto;

import api.personalitytest.domain.test.Test;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CardsResponseDto {

    private Boolean success;
    private List<CardDto> cards;

    public CardsResponseDto(Boolean success, Page<Test> testPage) {
        this.success = success;
        this.cards = testPage.stream().map(CardDto::new).collect(Collectors.toList());
    }

    public void createFullPath() {
        for (CardDto card : cards) {
            card.addDir();
        }
    }
}
