package tda

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.Fuel
import org.junit.*
import org.junit.jupiter.api.Test
import kotlin.test.*
import spark.Spark

class UsageTest {
    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            main(emptyArray())
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            Spark.stop()
        }
    }

    @Test
    fun testCreate() {

        val get1 = Fuel.get("http://127.0.0.1:9000/todo/").responseString()
        assertEquals("[]", get1.third.get(), "To begin, we should have no todos")

        val post1 = Fuel.post("http://127.0.0.1:9000/todo/").body("First test todo").responseString()
        assertEquals(200, post1.second.httpStatusCode, "Should have returned a 200 with complete todo added")
        val json = jacksonObjectMapper().readTree(post1.third.get())
        assertEquals(1, json.get("id").asLong(), "first todo's id should be equal 1")

        val get2 = Fuel.get("http://127.0.0.1:9000/todo/").responseString()
        assertNotEquals("[]", get2.third.get(), "Array should no longer be empty")

        val del = Fuel.delete("http://127.0.0.1:9000/todo/1").responseString()
        assertEquals(200, del.second.httpStatusCode, "Deleting the created todo should have returned a 200")
        assertEquals("ok", del.third.get(), "deleting the created todo should return ok message")

        val get3 = Fuel.get("http://127.0.0.1:9000/todo/").responseString()
        assertEquals("[]", get3.third.get(), "To begin, we should have no todos")

    }

    @Test
    fun testUpdate() {

        val get1 = Fuel.get("http://127.0.0.1:9000/todo/").responseString()
        assertEquals("[]", get1.third.get(), "To begin, we should have no todos")

        val post1 = Fuel.post("http://127.0.0.1:9000/todo/").body("testUpdate").responseString()
        assertEquals(200, post1.second.httpStatusCode, "Should have returned a 200 with complete todo added")
        val json = jacksonObjectMapper().readTree(post1.third.get())
        println(json)
        assertEquals("testUpdate", json.get("text").asText(), "text should save the same as was sent")
        assertEquals(false, json.get("done").asBoolean(), "by default the todo should bot be done")

        val id = json.get("id").asLong()

        val put1 = Fuel.put("http://127.0.0.1:9000/todo/"+id).body("""{"text":"testUpdate", "done":true}""").responseString()
        assertEquals(200, put1.second.httpStatusCode, "Should have returned a 200 with complete todo updated")
        val put1json = jacksonObjectMapper().readTree(put1.third.get())
        assertEquals("testUpdate", put1json.get("text").asText(), "text should still be the same")
        assertEquals(true, put1json.get("done").asBoolean(), "todo should now be done")

        val del = Fuel.delete("http://127.0.0.1:9000/todo/"+id).responseString()
        assertEquals(200, del.second.httpStatusCode, "deleting the created todo should return a 200")
        assertEquals("ok", del.third.get(), "deleting the created todo should return an ok message")

        val get2 = Fuel.get("http://127.0.0.1:9000/todo/").responseString()
        assertEquals("[]", get2.third.get(), "having deleted the created todo, the array should be empty again")

    }
}