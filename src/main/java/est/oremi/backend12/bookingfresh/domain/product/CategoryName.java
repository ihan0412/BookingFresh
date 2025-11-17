package est.oremi.backend12.bookingfresh.domain.product;

public enum CategoryName {
    VEGETABLES("채소"),
    FROZEN_FOODS("냉동식품"),
    DAIRY("유제품"),
    MEAT("육류"),
    SEAFOOD("해산물"),
    FRUIT("과일"),
    BREAD("빵"),
    SAUCE("소스");

    private final String label;

    CategoryName(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

