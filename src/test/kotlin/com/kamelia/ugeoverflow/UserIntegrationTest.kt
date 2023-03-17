package com.kamelia.ugeoverflow

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.kamelia.ugeoverflow.question.PostQuestionDTO
import com.kamelia.ugeoverflow.util.mockedUser1
import java.util.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestionsIntegrationTests(
    context: WebApplicationContext,
) {

    private val mapper: ObjectMapper = JsonMapper.builder()
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
    fun test() {
        mockMvc.post("/api/v1/questions") {
            with(mockedUser1)
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(PostQuestionDTO("title", "content", setOf()))
        }.andDo {
            print()
        }.andExpect {
            status { isOk() }
        }
    }

}
