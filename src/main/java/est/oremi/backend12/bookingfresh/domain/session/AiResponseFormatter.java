package est.oremi.backend12.bookingfresh.domain.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;

import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletion;

import est.oremi.backend12.bookingfresh.domain.session.dto.AiResponseData;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.RecipeSchema;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.SuggestionSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiResponseFormatter {

    private final OpenAIClient openAiClient;

    public AiResponseData format(Message.IntentType purpose, String aiRawText) {

        return switch (purpose) {
            case RECIPE_ASSISTANT -> parseRecipe(aiRawText);
//            case SHOPPING_ASSISTANT -> parseShopping(aiRawText);
            case COOKING_IDEA -> parseSuggestion(aiRawText);
            default -> new AiResponseData("TEXT", null, aiRawText);
        };
    }

    private AiResponseData parseRecipe(String rawText) {
        try {
            StructuredChatCompletionCreateParams<RecipeSchema> params =
                    ChatCompletionCreateParams.builder()
                            .model(ChatModel.GPT_4_1)
                            .addUserMessage("다음 문장을 JSON 형태의 레시피 데이터로 변환해줘:\n" + rawText)
                            .responseFormat(RecipeSchema.class)
                            .build();

            // 모델 호출
            StructuredChatCompletion<RecipeSchema> completion =
                    openAiClient.chat().completions().create(params);

            // 구조화된 결과 추출
            RecipeSchema  recipe = completion.choices().get(0).message().content().orElse(null);;
            String json = new ObjectMapper().writeValueAsString(recipe);

            return new AiResponseData("RECIPE", json, rawText);


        } catch (Exception e) {
            return new AiResponseData("RECIPE", null, rawText);
        }
    }

//    private AiResponseData parseShopping(String raw) {
//        // TODO: OpenAI structured output 적용
////        String json = """
////            {
////              "items": [
////                {"productName": "감자", "productId": 12},
////                {"productName": "두부", "productId": 33}
////              ]
////            }
////        """;
//        return new AiResponseData("SHOPPING", json, raw);
//    }

    private AiResponseData parseSuggestion(String rawText) {
        try {
            StructuredChatCompletionCreateParams<SuggestionSchema> params =
                    ChatCompletionCreateParams.builder()
                            .model(ChatModel.GPT_4_1)
                            .addUserMessage(
                                    "다음 내용을 참고해 요리 아이디어를 JSON으로 정리해줘.\n"
                                            + "menus 필드에 추천 메뉴들을 배열로 담아주세요.\n"
                                            + rawText
                            )
                            .responseFormat(SuggestionSchema.class)
                            .build();

            StructuredChatCompletion<SuggestionSchema> completion =
                    openAiClient.chat().completions().create(params);

            SuggestionSchema suggestion =
                    completion.choices().get(0).message().content().orElse(null);;

            String json = new ObjectMapper().writeValueAsString(suggestion);
            return new AiResponseData("SUGGESTION", json, rawText);

        } catch (Exception e) {
            e.printStackTrace();
            return new AiResponseData("SUGGESTION", null, rawText);
        }
    }
}
