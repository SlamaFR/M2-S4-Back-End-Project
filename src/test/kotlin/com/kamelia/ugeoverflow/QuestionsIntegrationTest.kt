package com.kamelia.ugeoverflow

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kamelia.ugeoverflow.question.PostQuestionDTO
import com.kamelia.ugeoverflow.question.QuestionDTO
import com.kamelia.ugeoverflow.util.mockedUser1
import jakarta.transaction.Transactional
import java.util.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestionsIntegrationTest(
    context: WebApplicationContext,
) {

    private val mapper: ObjectMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .addModule(JavaTimeModule())
        .configure(MapperFeature.AUTO_DETECT_FIELDS, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        .build()

    private lateinit var mockMvc: MockMvc


    init {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    @Test
    fun `can get questions`() {
        mockMvc.get("/api/v1/questions") {
            with(mockedUser1)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.content") { isArray(); isEmpty() }
        }
    }

    @Test
    fun `can post a question`() {
        mockMvc.post("/api/v1/questions") {
            with(mockedUser1)
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(PostQuestionDTO("Test question", "Test content", emptySet()))
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { isNotEmpty() }
            jsonPath("$.title") { value("Test question") }
            jsonPath("$.content") { value("Test content") }
            jsonPath("$.authorUsername") { isString(); isNotEmpty(); value("user1") }
        }
    }

    @Test
    fun `can delete a posted question`() {
        val questionId = mockMvc.post("/api/v1/questions") {
            with(mockedUser1)
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(PostQuestionDTO("Test question", "Test content", emptySet()))
        }.andReturn().let {
            mapper.readValue(it.response.contentAsString, QuestionDTO::class.java).id
        }

        mockMvc.get("/api/v1/questions?page=0&size=10") {
            with(mockedUser1)
            contentType = MediaType.APPLICATION_JSON
        }.andDo {
            print()
        }.andExpect {
            status { isOk() }
            jsonPath("$.content") { isArray(); isNotEmpty() }
            jsonPath("$.content[0].id") { value(questionId) }
        }

        mockMvc.delete("/api/v1/questions/$questionId") {
            with(mockedUser1)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        mockMvc.get("/api/v1/questions") {
            with(mockedUser1)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.content") { isArray(); isEmpty() }
        }
    }

}
