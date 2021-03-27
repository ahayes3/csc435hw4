import com.google.gson.Gson
import spark.Request
import spark.Response
import java.util.*

object CharacterController {
    val gson = Gson()
    fun post(req: Request, resp:Response):String {

        val character:Character
        try {
            character = parseChar(req)
        } catch(e:Exception) {
            throw ErrorException(403)
        }

        println("Character created with id "+character.id)
        CharacterModel.post(character,req.session().attribute("user"))

        return CharacterView.respond(resp,character)
    }
    fun put(req: Request, resp:Response,id:UUID):String {
        val character: Character
        try {
            character = parseChar(req)
        } catch(e:Exception) {
            throw ErrorException(403)
        }
        CharacterModel.put(id,character)
        return CharacterView.respond(resp,character)
    }
    fun get(req: Request, resp:Response):String { //gets owned ids
        val user = req.session().attribute<String>("user")
        val c = CharacterModel.get(user)
        return CharacterView.respond(resp,c)
    }
    fun delete(req: Request, resp:Response,id:UUID):String {
        val deleted  = CharacterModel.delete(id)
        return CharacterView.respond(resp,deleted)
    }
    fun getId(req:Request,resp:Response,id:UUID):String {
        val c = CharacterModel.getById(id)
        return CharacterView.respond(resp,c)
    }
    fun makeList(str:String,m: Map<*,*>):List<String> {
        return m[str] as List<String>
    }
    fun classes(m:Map<*,*>): List<Clazz> {
        val classes = m["classes"] as List<Map<String,*>>
        return classes.map() {p ->
            Clazz(p["name"] as String,(p["level"] as Double).toInt())
        }


    }
    fun parseChar(req:Request): Character {
        var body = gson.fromJson(req.body(), Map::class.java)
        body = body["character"] as Map<*, *>?
        val str= (body["str"] as Double).toInt()
        val dex = (body["dex"] as Double).toInt()
        val con= (body["con"] as Double).toInt()
        val intel= (body["intel"] as Double).toInt()
        val wis= (body["wis"] as Double).toInt()
        val cha= (body["cha"] as Double).toInt()
        val ac=(body["ac"] as Double).toInt()
        val init= (body["init"] as Double).toInt()
        val speed= (body["speed"] as Double).toInt()
        val maxHp= (body["maxHp"] as Double).toInt()
        val name = body["name"] as String
        val bg = body["background"] as String
        val race = body["race"] as String
        val languages  = body["languages"] as String
        val features = makeList("features",body)
        val tools = makeList("tools",body)
        val skills = makeList("skills",body)
        val items = makeList("items",body)
        val classes = classes(body)

        return Character(
            CharacterModel.newUUID(),
            name,
            bg,
            race,
            languages,
            str,
            dex,
            con,
            intel,
            wis,
            cha,
            ac,
            init,
            speed,
            maxHp,
            skills,
            tools,
            items,
            features,
            classes
        )
    }
}