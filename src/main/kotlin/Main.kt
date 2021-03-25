import spark.Request
import spark.Response
import spark.Spark.*
import java.util.*

fun main(args: Array<String>) {
    port(9081)


    post("/login") { req, res -> loginPost(req, res) }
    put("/login") { req, res -> loginPut(req, res) }
    get("/characters") {req, res -> charactersGet(req,res)}
    get("/characters/:id") {req, res -> charactersGetId(req,res,req.params("id"))}
    put("/characters/:id") {req, res -> charactersPut(req,res,req.params("id"))}
    post("/characters") {req, res -> charactersPost(req,res)}
    delete("/characters/:id") {req, res -> charactersDelete(req,res,req.params("id"))}

}

fun loginPost(req: Request, resp:Response) {

}
fun loginPut(req:Request, resp:Response) {

}
fun charactersGetId(req:Request,resp:Response,id:String) {
    checkUUID(id)
    CharacterController.getId(req,resp,UUID.fromString(id))
}
fun charactersGet(req:Request,resp:Response) {
    CharacterController.get(req,resp)
}
fun charactersPost(req:Request,resp:Response) {
    CharacterController.post(req,resp)
}
fun charactersPut(req:Request,resp:Response,id:String) {
    checkUUID(id)
    CharacterController.put(req,resp, UUID.fromString(id))
}
fun charactersDelete(req:Request,resp:Response,id:String) {
    checkUUID(id)
    CharacterController.delete(req,resp,UUID.fromString(id))
}
private fun checkUUID(id:String) {
    if(!id.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{8}-[0-9a-fA-F]{8}-[0-9a-fA-F]{8}".toRegex()))
        notFound {req,resp ->
            resp.type("application.json")
            "{\"message\": \"404 Error, id: $id not found\"}"
        }
}