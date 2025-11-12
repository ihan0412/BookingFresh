package est.oremi.backend12.bookingfresh.domain.session.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;

import com.openai.models.chat.completions.*;

import est.oremi.backend12.bookingfresh.domain.session.dto.AiResponseData;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.RecipeSchema;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.SuggestionSchema;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAIClient openAiClient;

    public AiResponseData formatAlanResponse(Message.IntentType purpose, String aiRawText) {

        return switch (purpose) {
            case RECIPE_ASSISTANT -> parseRecipe(aiRawText);
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
//            StructuredChatCompletion<RecipeSchema> completion =
//                    openAiClient.chat().completions().create(params);
            var completion = openAiClient.chat().completions().create(params);


            // 구조화된 결과 추출
            RecipeSchema recipe = completion.choices().get(0).message().content().orElse(null);;
            String json = new ObjectMapper().writeValueAsString(recipe);

            return new AiResponseData("RECIPE", json, rawText);


        } catch (Exception e) {
            e.printStackTrace();
            return new AiResponseData("RECIPE", null, rawText);
        }
    }

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

    public Message.IntentType getIntentFromMessage(String userMessage) {
        try {
            //프롬프트 구성
            String prompt = """
            다음 문장의 의도를 분류하세요.

            문장: %s
            """.formatted(userMessage);

            // 요청 파라미터 생성
            ChatCompletionSystemMessageParam systemMsg =
                    ChatCompletionSystemMessageParam.builder()
                            .content("""
                        너는 사용자의 요청을 분석해 의도를 분류하는 AI이다.
                        반드시 아래 ENUM 이름 중 하나만 출력해야 한다:
                        RECIPE_ASSISTANT, COOKING_IDEA, GENERAL_CHAT.
                        그 외 설명이나 문장은 절대 출력 금지.
                        """)
                            .build();

            ChatCompletionUserMessageParam userMsg =
                    ChatCompletionUserMessageParam.builder()
                            .content(prompt)
                            .build();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model("gpt-4o-mini")
                    .messages(List.of(
                            ChatCompletionMessageParam.ofSystem(systemMsg),
                            ChatCompletionMessageParam.ofUser(userMsg)
                    ))
                    .temperature(0.0)
//                    .maxTokens(10)
                    .build();

            // 요청 전송 및 응답 수신
            ChatCompletion completion = openAiClient.chat().completions().create(params);

            // 응답에서 텍스트 추출
            String intentName = completion.choices().get(0).message().content().orElse("")
                    .trim();

            // Enum 매핑
            return Message.IntentType.valueOf(intentName.toUpperCase());
        } catch (Exception e) {
            return Message.IntentType.GENERAL_CHAT;
        }
    }

    public String generateTitleFromMessage(String userMessage) {
        try {
            String prompt = """
        다음 문장의 내용을 요약하여 대화 세션의 제목으로 만들어주세요.

        문장: "%s"
        """.formatted(userMessage);

            // 시스템 역할 정의
            ChatCompletionSystemMessageParam systemMsg =
                    ChatCompletionSystemMessageParam.builder()
                            .content("""
                            너는 사용자의 첫 메시지를 보고 적절한 세션 제목을 짓는 도우미이다.
                            반드시 짧고 명확한 제목만 출력해야 한다.
                            조건:
                            - 20자 이하
                            - 문장의 핵심 주제를 간결히 표현
                            - 조사, 감탄사 제거
                            - 명사형으로 끝내기
                            예: 
                            - "두부조림 레시피 알려줘" → "두부조림 레시피"
                            - "오늘 저녁 메뉴 추천해줘" → "저녁 메뉴 추천"
                            - "마트에서 살 재료 추천" → "장보기 추천"
                            """)
                            .build();

            // 사용자 메시지 설정
            ChatCompletionUserMessageParam userMsg =
                    ChatCompletionUserMessageParam.builder()
                            .content(prompt)
                            .build();

            // 요청 생성
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model("gpt-4o-mini")
                    .messages(List.of(
                            ChatCompletionMessageParam.ofSystem(systemMsg),
                            ChatCompletionMessageParam.ofUser(userMsg)
                    ))
                    .temperature(0.3)
                    .maxTokens(50)
                    .build();

            ChatCompletion completion = openAiClient.chat().completions().create(params);

            String title = completion.choices().get(0)
                    .message()
                    .content()
                    .orElse("")
                    .trim();

            // 결과 후처리 (너무 길거나 비어있는 경우)
            if (title.isBlank()) {
                return "새 AI 대화";
            }
            if (title.length() > 20) {
                title = title.substring(0, 20);
            }

            return title;

        } catch (Exception e) {
            // 실패 시 기본 제목 반환
            return "새 AI 대화";
        }
    }
}
