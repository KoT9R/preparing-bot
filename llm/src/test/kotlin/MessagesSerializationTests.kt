import com.kot.openai.LLMMessage
import com.kot.openai.LLMMessages
import com.kot.openai.OpenAIAPI
import com.kot.openai.User
import kotlinx.serialization.json.Json
import kotlin.test.Test

class MessagesSerializationTests {
    private val json = Json {
        prettyPrint = true
    }
    @Test
    fun `simple test`() {
        val userMsg = "awdawdaw".toMessages()
        val request= OpenAIAPI.RequestAPI("gpt-3.5-turbo", userMsg)
        println(request.toText())
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