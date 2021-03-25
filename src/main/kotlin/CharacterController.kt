import com.google.gson.Gson
import spark.Request
import spark.Response
import java.util.*
import javax.print.DocFlavor

object CharacterController {
    val gson = Gson()
    fun post(req: Request, resp:Response) {
        var body = gson.fromJson(req.body(), Map::class.java)
        body = body["character"] as Map<*, *>?
        val str= body["str"] as Int
        val dex= body["dex"] as Int
        val con= body["con"] as Int
        val intel= body["intel"] as Int
        val wis= body["wis"] as Int
        val cha= body["cha"] as Int
        val ac= body["ac"] as Int
        val init= body["init"] as Int
        val speed= body["speed"] as Int
        val maxHp= body["maxHp"] as Int
        val name = body["name"] as String
        val bg = body["background"] as String
        val race = body["name"] as String
        val languages  = body["name"] as String
        val features = makeList("features",body)
        val tools = makeList("tools",body)
        val skills = makeList("skills",body)
        val items = makeList("items",body)
        val classes = classes(body)


        val character = Character(CharacterModel.newUUID(),name,bg,race,languages,str,dex,con,intel,wis,cha
        ,ac,init,speed,maxHp,skills,tools,items,features,classes)

        println("Character created with id "+character.id)

        CharacterView.respond(resp,character)
    }
    fun put(req: Request, resp:Response,id:UUID) {
        //todo
    }
    fun get(req: Request, resp:Response) {
        //todo
    }
    fun delete(req: Request, resp:Response,id:UUID) {
        //todo
    }
    fun getId(req:Request,resp:Response,id:UUID) {
        val c = CharacterModel.getById(id)
        CharacterView.respond(resp,c)
    }
    fun makeList(str:String,m: Map<*,*>):List<String> {
        return m[str] as List<String>
    }
    fun classes(m:Map<*,*>): List<Clazz> {
        val classes = m["classes"] as List<Map<String,*>>
        return classes.map() {p ->
            Clazz(p["name"] as String,p["level"] as Int)
        }


    }
}