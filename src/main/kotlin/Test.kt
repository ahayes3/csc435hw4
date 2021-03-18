import spark.Request
import spark.Response
import spark.Spark.*

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

fun loginPost(req: Request, res:Response) {

}
fun loginPut(req:Request, res:Response) {

}
fun charactersGetId(req:Request,res:Response,id:String) {

}
fun charactersGet(req:Request,res:Response) {

}
fun charactersPost(req:Request,res:Response) {

}
fun charactersPut(req:Request,res:Response,id:String) {

}
fun charactersDelete(req:Request,res:Response,id:String) {

}
