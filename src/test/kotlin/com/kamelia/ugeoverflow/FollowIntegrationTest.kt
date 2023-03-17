package com.kamelia.ugeoverflow

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.kamelia.ugeoverflow.follow.FollowedUserDTO
import com.kamelia.ugeoverflow.util.mockedUser1
import jakarta.transaction.Transactional
import java.util.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowIntegrationTest(
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
    fun `can follow another user`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000002")
        mockMvc.post("/api/v1/follows/$id") {
            with(mockedUser1)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `cannot follow yourself`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000001")
        mockMvc.post("/api/v1/follows/$id") {
            with(mockedUser1)
        }.andExpect {
            status { isForbidden() }
        }
    }

    @Test
    fun `can unfollow another user`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000002")

        mockMvc.post("/api/v1/follows/$id") {
            with(mockedUser1)
        }
        mockMvc.delete("/api/v1/follows/$id") {
            with(mockedUser1)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `trying to unfollow a user you don't follow returns 404`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000002")
        mockMvc.delete("/api/v1/follows/$id") {
            with(mockedUser1)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `followed user appears in the followed list`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000002")
        mockMvc.post("/api/v1/follows/$id") {
            with(mockedUser1)
        }
        mockMvc.get("/api/v1/follows") {
            with(mockedUser1)
        }.andExpect {
            status { isOk() }
            content {
                json(mapper.writeValueAsString(listOf(FollowedUserDTO(id, "user2", 1))))
            }
        }
    }

    @Test
    fun `unfollowed user does not appear in the followed list`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000002")
        mockMvc.post("/api/v1/follows/$id") {
            with(mockedUser1)
        }
        mockMvc.delete("/api/v1/follows/$id") {
            with(mockedUser1)
        }
        mockMvc.get("/api/v1/follows") {
            with(mockedUser1)
        }.andExpect {
            status { isOk() }
            content {
                json(mapper.writeValueAsString(emptyList<FollowedUserDTO>()))
            }
        }
    }

//    @Test
//    fun `user trust can be correctly updated`() {
//        val id = UUID.fromString("00000000-0000-0000-0000-000000000002")
//
//        mockMvc.post("/api/v1/follows/$id") {
//            with(mockedUser1)
//        }
//
//        mockMvc.put("/api/v1/follows/$id") {
//            with(mockedUser1)
//            contentType = MediaType.APPLICATION_JSON
//            content = mapper.writeValueAsString(10)
//        }.andExpect {
//            status { isOk() }
//        }
//
//        mockMvc.get("/api/v1/follows") {
//            with(mockedUser1)
//        }.andExpect {
//            status { isOk() }
//            content {
//                json(mapper.writeValueAsString(listOf(FollowedUserDTO(id, "user2", 10))))
//            }
//        }
//    }

}
