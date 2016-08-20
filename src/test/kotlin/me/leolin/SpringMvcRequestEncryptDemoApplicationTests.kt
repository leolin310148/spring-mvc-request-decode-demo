package me.leolin

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.Base64Utils

@RunWith(SpringRunner::class)
@WebMvcTest(MyController::class)
class SpringMvcRequestEncryptDemoApplicationTests {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper

    @Test
    fun testGet() {
        val name = "Leo";
        val encodedName = Base64Utils.encodeToString(name.toByteArray())

        mockMvc.perform(get("/api/name?name=$encodedName"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name", Matchers.equalTo(name)))
    }

    @Test
    fun testPost() {
        val name = "Leo"
        val payload = mapOf<String, String>(
                "name" to name
        )
        mockMvc
                .perform(
                        post("/api/name")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(Base64Utils.encodeToString(objectMapper.writeValueAsBytes(payload)))
                )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name", Matchers.equalTo(name)))

    }

}
