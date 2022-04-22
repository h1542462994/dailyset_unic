package org.tty.dailyset.dailyset_unic

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.tty.dailyset.dailyset_unic.component.TestBeanFactory
import org.tty.dailyset.dailyset_unic.grpc.HelloRequest
import org.tty.dailyset.dailyset_unic.grpc.SimpleGrpc
import org.tty.dailyset.dailyset_unic.grpc.SimpleGrpc.SimpleBlockingStub

@SpringBootTest(classes = [DailysetUnicApplicationTests::class, TestBeanFactory::class])
@TestPropertySource("classpath:application-test.properties")
class DailysetUnicApplicationTests {

    @Autowired
    private lateinit var testBeanFactory: TestBeanFactory

    @Test
    fun contextLoads() {

    }

    private lateinit var simpleStub: SimpleBlockingStub


    @Test
    fun testGrpc() {
        val input = "test"
        val expectResult = "Hello $input"

        simpleStub = SimpleGrpc.newBlockingStub(testBeanFactory.getChannel())
        val result = simpleStub.sayHello(HelloRequest.newBuilder().setName(input).build())
        println(result.message)
        assertEquals(expectResult, result.message)
    }




}
