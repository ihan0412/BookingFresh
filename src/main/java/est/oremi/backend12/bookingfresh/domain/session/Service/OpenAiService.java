package est.oremi.backend12.bookingfresh.domain.session.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;

import com.openai.models.chat.completions.*;

import est.oremi.backend12.bookingfresh.domain.session.dto.AiResponseData;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.RecipeSchema;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.SimplifiedRecipeSchema;
import est.oremi.backend12.bookingfresh.domain.session.dto.responseSchema.SuggestionSchema;
import est.oremi.backend12.bookingfresh.domain.session.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAIClient openAiClient;

    public AiResponseData formatAlanResponse(Message.IntentType intent, String aiRawText) {

        return switch (intent) {
            case RECIPE_ASSISTANT -> parseRecipeKeywords(aiRawText);
//            case COOKING_IDEA -> parseSuggestion(aiRawText);
            default -> new AiResponseData("TEXT", null, aiRawText);
        };
    }

    private AiResponseData parseRecipeKeywords(String rawText) {
        try {
            String prompt =
                    """
                    입력된 문장에서 '요리에 필요한 재료 이름'만 추출해 주세요.
    
                    규칙:
                    - 재료 이름만 반환 (예: "소고기", "대파", "표고버섯").
                    - 양, 단위(ex: 300g, 2개) 제거.
                    - 조미료/액체는 제외: 물, 간장, 설탕, 소금, 후추, 고춧가루, 식용유, 참기름, 다시마 등.
                    - 복합 재료는 그대로 유지: "청양고추", "표고버섯", "소고기" 등.
                    - steps, title, amount 등은 반환 금지.
    
                    JSON 형식:
                    {
                      "ingredients": ["재료1", "재료2"]
                    }
    
                    아래 텍스트에서 재료를 추출하세요:
    
                    """ + rawText;

            StructuredChatCompletionCreateParams<SimplifiedRecipeSchema> params =
                    ChatCompletionCreateParams.builder()
                            .model(ChatModel.GPT_4_1)
                            .addUserMessage(prompt)
                            .responseFormat(SimplifiedRecipeSchema.class)
                            .build();

            // 모델 호출
            var completion = openAiClient.chat().completions().create(params);

            // 구조화된 결과 추출
            SimplifiedRecipeSchema recipe =
                    completion.choices().get(0).message().content().orElse(null);

            String json = new ObjectMapper().writeValueAsString(recipe);

            return new AiResponseData("RECIPE", json, rawText);

        } catch (Exception e) {
            log.error("[OpenAiService] parseRecipe() failed: {}", e.getMessage(), e);
            return new AiResponseData("RECIPE", null, rawText);
        }
    }

//    private AiResponseData parseSuggestion(String rawText) {
//        try {
//            StructuredChatCompletionCreateParams<SuggestionSchema> params =
//                    ChatCompletionCreateParams.builder()
//                            .model(ChatModel.GPT_4_1)
//                            .addUserMessage(
//                                    "다음 내용을 참고해 요리 아이디어를 JSON으로 정리해줘.\n"
//                                            + "menus 필드에 추천 메뉴들을 배열로 담아주세요.\n"
//                                            + rawText
//                            )
//                            .responseFormat(SuggestionSchema.class)
//                            .build();
//
//            StructuredChatCompletion<SuggestionSchema> completion =
//                    openAiClient.chat().completions().create(params);
//
//            SuggestionSchema suggestion =
//                    completion.choices().get(0).message().content().orElse(null);;
//
//            String json = new ObjectMapper().writeValueAsString(suggestion);
//            return new AiResponseData("SUGGESTION", json, rawText);
//
//        } catch (Exception e) {
//            log.error("[OpenAiService] parseSuggestion() failed: {}", e.getMessage(), e);
//            return new AiResponseData("SUGGESTION", null, rawText);
//        }
//    }

    public Message.IntentType getIntentFromMessage(String userMessage) {
        try {
            //프롬프트 구성
            String prompt = """
            다음 문장의 의도를 분류하세요.
           \s
                    예시:
                    입력: 감자조림 만드는 법 알려줘
                    출력: RECIPE_ASSISTANT
                   \s
                    입력: 안녕
                    출력: GENERAL_CHAT

            문장: %s
           \s""".formatted(userMessage);

            // 요청 파라미터 생성
            ChatCompletionSystemMessageParam systemMsg =
                    ChatCompletionSystemMessageParam.builder()
                            .content("""
                                    너는 사용자의 메시지를 분석하여 'AI 요리 도우미'가 어떤 형태의 응답을 생성할지 예측하고,
                                                그에 따라 아래 두 가지 의도 중 하나로 분류하는 분류기이다.
                                   \s
                                                각 의도는 다음 기준을 따른다:
                                   \s
                                                1. RECIPE_ASSISTANT:
                                                    - 사용자가 구체적인 요리 방법, 레시피, 재료 구성, 조리 순서를 알고자 하는 경우
                                                    - 예: "된장찌개 만드는 법 알려줘", "닭가슴살로 요리 추천해줘", "감자조림 레시피 알려줘"
                                                    - 앨런이 응답으로 **식재료 목록과 조리 순서**를 포함할 가능성이 높음
                                   \s
                                                2. GENERAL_CHAT:
                                                    - 요리와 직접 관련 없는 일반 대화, 혹은 잡담, 인사, 기타 정보 요청
                                                    - 예: "안녕", "오늘 날씨 어때?", "요리 말고 추천할만한 영화 있어?"
                                   \s
                                                반드시 아래 ENUM 이름 중 하나만 정확히 출력해야 한다:
                                                RECIPE_ASSISTANT, GENERAL_CHAT
                                   \s
                                                설명, 이유, 기타 문장은 절대 출력하지 마라.
                       \s""")
                            .build();

            ChatCompletionUserMessageParam userMsg =
                    ChatCompletionUserMessageParam.builder()
                            .content(prompt)
                            .build();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(ChatModel.GPT_4O_MINI)
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
            String intentName = completion.choices().get(0).message().content()
                    .orElse("")
                    .trim()
                    .toUpperCase();

            if (!List.of("RECIPE_ASSISTANT", "COOKING_IDEA", "GENERAL_CHAT").contains(intentName)) {
                intentName = "GENERAL_CHAT";
            }

            // Enum 매핑
            return Message.IntentType.valueOf(intentName.toUpperCase());
        } catch (Exception e) {
            log.warn("[OpenAiService] getIntentFromMessage() failed for input='{}': {}",
                    userMessage, e.getMessage());
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
                            예:\s
                            - "두부조림 레시피 알려줘" 메시지에 대해 "두부조림 레시피" 라는 제목으로
                            - "오늘 저녁 메뉴 추천해줘" 메시지에 대해 "저녁 메뉴 추천" 라는 제목으로
                            - "마트에서 살 재료 추천" 메시지에 대해 "장보기 추천" 라는 제목으로
                           \s""")
                            .build();

            // 사용자 메시지 설정
            ChatCompletionUserMessageParam userMsg =
                    ChatCompletionUserMessageParam.builder()
                            .content(prompt)
                            .build();

            // 요청 생성
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(ChatModel.GPT_4O_MINI)
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
            log.warn("[OpenAiService] generateTitleFromMessage() failed for '{}': {}",
                    userMessage, e.getMessage());
            return "새 AI 대화";
        }
    }
}
