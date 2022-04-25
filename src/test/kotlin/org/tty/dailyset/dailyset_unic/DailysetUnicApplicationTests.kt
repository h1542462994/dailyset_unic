package org.tty.dailyset.dailyset_unic

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.tty.dailyset.dailyset_unic.component.TestBeanFactory
import org.tty.dailyset.dailyset_unic.grpc.*
import org.tty.dailyset.dailyset_unic.grpc.HelloGrpc.HelloBlockingStub
import org.tty.dailyset.dailyset_unic.grpc.HelloProtoBuilders.HelloRequest
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketRequest

@SpringBootTest(classes = [DailysetUnicApplicationTests::class, TestBeanFactory::class])
@TestPropertySource("classpath:application-test.properties")
class DailysetUnicApplicationTests {

    @Autowired
    private lateinit var testBeanFactory: TestBeanFactory

    @Test
    fun contextLoads() {

    }

    private lateinit var simpleStub: HelloBlockingStub

    private lateinit var simpleCoroutineStub: HelloCoroutineGrpc.HelloCoroutineStub

    @Test
    fun testGrpcBlocking() {
        val input = "test"
        val expectResult = "Hello $input"

        simpleStub = HelloGrpc.newBlockingStub(testBeanFactory.getChannel())
        val result = simpleStub.sayHello(HelloRequest.newBuilder().setName(input).build())
        println(result.message)
        assertEquals(expectResult, result.message)
    }

    @Test
    fun testGrpcCoroutine() {
        val input = "test"
        val expectResult = "Hello $input"
        simpleCoroutineStub = HelloCoroutineGrpc.newStub(testBeanFactory.getChannel())
        runBlocking {
            val result = simpleCoroutineStub.sayHello(request = HelloRequest {
                name = input
            })
            println(result.message)
            assertEquals(expectResult, result.message)
        }
    }

    @Test
    fun testTickBind() {
        val uid = "201806061201"
        val password = "~~~~~~"
        val ticketCoroutineStub = TicketServiceCoroutineGrpc.newStub(testBeanFactory.getChannel())
        runBlocking {
            val result = ticketCoroutineStub.bind(request = TicketRequest {
                this.uid = uid
                this.password = password
            })
            println(result.ticket.ticketId)
            assertEquals(true, result.success)
        }
    }




}
