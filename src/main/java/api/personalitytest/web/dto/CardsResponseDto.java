package api.personalitytest.web.dto;

import lombok.Getter;

@Getter
public class CardsResponseDto<T> {

    private T cards;
    private int count;

    public CardsResponseDto(T cards, int count) {
        this.cards = cards;
        this.count = count;
    }
}
