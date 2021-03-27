import com.google.gson.Gson
import spark.Request
import spark.Response
import spark.Spark.*
import java.util.*
import spark.Spark.exception


val gson = Gson()

fun main(args: Array<String>) {
    port(9080)


    post("/login") { req, res -> loginPost(req, res) }
    put("/login") { req, res -> loginPut(req, res) }
    post("/logout") {req,res -> req.session().attribute("user",null)}
    get("/characters") {req, res -> charactersGet(req,res)}
    get("/characters/:id") {req, res -> charactersGetId(req,res,req.params("id"))}
    put("/characters/:id") {req, res -> charactersPut(req,res,req.params("id"))}
    post("/characters") {req, res -> charactersPost(req,res)}
    delete("/characters/:id") {req, res -> charactersDelete(req,res,req.params("id"))}

    exception(ErrorException::class.java) { exc, req, res -> handle(exc,req,res)}

}
//creates and logs in
fun loginPost(req: Request, resp:Response):String { //this and the below function serve as the controller for the login section
    resp.type("application/json")
    val con = Utils.connect()
    val body = gson.fromJson(req.body(), Map::class.java)
    val user = body["user"].toString()
    val pass = body["pass"].toString()
    val st = con.prepareStatement("SELECT * FROM users WHERE name=?")
    st.setString(1,user)
    val out=st.executeQuery()
    val exists= !out.isClosed && out.next()
    if(exists)
        throw ErrorException(403)
    else { //sends user to mysql
        val st2 = con.prepareStatement("INSERT IGNORE INTO users VALUES(?,MD5(?))")
        st2.setString(1,user)
        st2.setString(2,pass)
        st2.executeUpdate()
        st2.close()
    }
    st.close()
    con.close()
    req.session().attribute("user",user)
    return LoginView.respond("Successful creation and login")
}
fun loginPut(req:Request, resp:Response):String {
    resp.type("application/json")
    val con = Utils.connect()
    val body = gson.fromJson(req.body(), Map::class.java)
    val u = body["user"]
    val p = body["pass"]
    if(u==Unit || p==null)
        throw ErrorException(400)
    val user = u.toString()
    val pass = p.toString()
    val st = con.prepareStatement("SELECT * FROM users WHERE name=? AND pass=MD5(?)")
    st.setString(1,user)
    st.setString(2,pass)
    val out=st.executeQuery()
    if(out.isClosed)
        throw ErrorException(404)
    if(out.next())
        req.session().attribute("user",user)
    else
        throw ErrorException(500)
    return LoginView.respond("Successful login")
}
//end of login controller

fun charactersGetId(req:Request,resp:Response,id:String):String {
    resp.type("application/json")
    checkUUID(id)
    return CharacterController.getId(req,resp,UUID.fromString(id))
}
fun charactersGet(req:Request,resp:Response):String {
    resp.type("application/json")
    return CharacterController.get(req,resp)
}
fun charactersPost(req:Request,resp:Response):String {
    resp.type("application/json")
    return CharacterController.post(req,resp)
}
fun charactersPut(req:Request,resp:Response,id:String):String {
    resp.type("application/json")
    checkUUID(id)
    return CharacterController.put(req,resp, UUID.fromString(id))
}
fun charactersDelete(req:Request,resp:Response,id:String):String {
    resp.type("application/json")
    checkUUID(id)
    return CharacterController.delete(req,resp,UUID.fromString(id))
}
private fun checkUUID(id:String) {
    if(!id.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{8}-[0-9a-fA-F]{8}-[0-9a-fA-F]{8}".toRegex()))
        notFound {req,resp ->
            resp.type("application.json")
            "{\"message\": \"404 Error, id: $id not found\"}"
        }
}
fun handle(exc:ErrorException,req:Request,resp:Response) {
    resp.type("application/json")
    resp.status(exc.code)
    resp.body("{\"message\": \"Error ${exc.code}\"}")
}