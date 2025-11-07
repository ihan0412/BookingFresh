package est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("A recipe with title, ingredients, and steps")
public class RecipeSchema {
    public String title;
    public List<Ingredient> ingredients;
    public List<String> steps;

    @JsonPropertyDescription("Estimated cooking time in minutes")
    public int estimatedTimeMinutes;

    public static class Ingredient {
        public String name;
        public String amount;
    }
}