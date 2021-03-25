import spark.Response

object LoginView {
    fun respond(resp: Response, message:String) {
        resp.body("{\"message\": \"$message\"")
    }
}
