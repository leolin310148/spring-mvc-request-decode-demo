package me.leolin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.*
import org.springframework.web.filter.GenericFilterBean
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper


@SpringBootApplication
open class SpringMvcRequestEncryptDemoApplication {
    @Bean open fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        return JsonDecryptBase64MessageConverter()
    }
}

@Component open class RequestDecryptFilter : GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        chain?.doFilter(Base64RequestWrapper(request as HttpServletRequest), response)
    }

}

@RestController @RequestMapping("/api") open class MyController {

    @RequestMapping(value = "/name", method = arrayOf(RequestMethod.GET)) open fun getName(
            @RequestParam("name") name: String
    ) = mapOf<String, String>(
            "name" to name
    )

    @RequestMapping(value = "/name", method = arrayOf(RequestMethod.POST)) open fun postName(
            @RequestBody map: Map<String, String>
    ) = map

}

class Base64RequestWrapper(req: HttpServletRequest) : HttpServletRequestWrapper(req) {

    override fun getParameterValues(name: String?): Array<out String> {
        return super.getParameterValues(name).map { String(Base64Utils.decodeFromString(it)) }.toTypedArray()
    }
}

class JsonDecryptBase64MessageConverter : MappingJackson2HttpMessageConverter() {

    override fun read(type: Type?, contextClass: Class<*>?, inputMessage: HttpInputMessage?): Any {

        val baos = ByteArrayOutputStream()
        inputMessage?.body?.copyTo(baos)
        val toByteArray = baos.toByteArray()
        return super.read(type, contextClass, object : HttpInputMessage {
            override fun getHeaders() = inputMessage?.headers

            override fun getBody(): ByteArrayInputStream {
                return ByteArrayInputStream(Base64Utils.decode(toByteArray))
            }
        })
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(SpringMvcRequestEncryptDemoApplication::class.java, *args)
}
