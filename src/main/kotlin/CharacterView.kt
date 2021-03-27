import com.google.gson.Gson
import spark.Response
import java.util.*

object CharacterView {
    val gson = Gson()
    fun respond(resp:Response,c:Character):String {
        resp.type("application.json")
        return gson.toJson(c)
    }
    fun respond(resp:Response,c: Set<UUID>):String {
        resp.type("application.json")
        return gson.toJson(c)

    }
}
