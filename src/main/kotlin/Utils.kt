import java.sql.Connection
import java.sql.DriverManager

object Utils {
    fun connect(): Connection {
        val url =
            "jdbc:mysql://localhost:3306/characterdb?allowPublicKeyRetrieval=true&useSSL=false" //DO NOT CHANGE - STRING TO CONNECT TO DB

        val user = "root"
        val password = "1234"
        return DriverManager.getConnection(url, user, password)
    }
}