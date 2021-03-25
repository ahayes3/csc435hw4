import com.google.gson.Gson
import spark.Response
import java.util.*

object CharacterView {
    val gson = Gson()
    fun respond(resp:Response,c:Character) {
        resp.type("application.json")
        resp.body(gson.toJson(c))
    }
    fun respond(resp:Response,c: List<UUID>) {
        resp.type("application.json")

    }
}
