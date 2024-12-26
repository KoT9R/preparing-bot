import com.kot.openai.chat.LLMMessage
import com.kot.openai.api.OpenAIAPI
import com.kot.openai.chat.User
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class MessagesSerializationTests {
    private val json = Json {
        prettyPrint = true
    }
    @Test
    fun `simple test`() {
        val userMsg = "awdawdaw".toMessages()
        val request = OpenAIAPI.RequestAPI("gpt-3.5-turbo", userMsg)
        @Language("json")
        val expected =  """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                    {
                        "role": "user",
                        "content": [
                            {
                                "type": "text",
                                "text": "awdawdaw"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()
        assertEquals(request.toText(), expected)
    }

    private fun OpenAIAPI.RequestAPI.toText(): String {
        return json.encodeToString(
            serializer = OpenAIAPI.RequestAPI.serializer(),
            value = this
        )
    }

    private fun String.toMessages(): List<LLMMessage> {
        return listOf(
            User(
                content = listOf(
                    User.Content.Text(text = this)
                )
            )
        )
    }
}