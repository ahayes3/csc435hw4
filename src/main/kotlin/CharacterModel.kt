import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*

object CharacterModel {

    fun getById(id:UUID):Character {
        val con = Utils.connect()
        val cr = getCharacter(id,con)
        con.close()
        return cr
    }
    fun getCharacter(id:UUID,con: Connection): Character? {
        val arr= arrayOf(
        con.prepareStatement("SELECT c.* FROM characters c WHERE c.id=?"),
        con.prepareStatement("SELECT c.* FROM character_classes c WHERE c.id=?"),
        con.prepareStatement("SELECT c.* FROM character_features c WHERE c.id=?"),
        con.prepareStatement("SELECT c.* FROM character_items c WHERE c.id=?"),
        con.prepareStatement("SELECT c.* FROM character_skills c WHERE c.id=?"),
        con.prepareStatement("SELECT c.* FROM character_tools c WHERE c.id=?"))

        arr.forEach { p -> p.setString(1,id.toString()) }

        val res = arr.map { p -> p.executeQuery() }
        if(res[0].isClosed || !res[0].next())
            return null
        //todo make character from res and close things and return

    }
    fun newUUID():UUID {
        val used = usedIds()
        var id:UUID
        do {
            id = UUID.randomUUID()
        } while(!used.contains(id))
        return id
    }

    fun usedIds(): Set<UUID> {
        val con  = Utils.connect()
        val st = con.createStatement()
        val query = "SELECT uc.id FROM user_characters uc"
        val rs = st.executeQuery(query)
        val ids = HashSet<UUID>()
        while(rs.next()) {
            ids.add(UUID.fromString(rs.getString("id")))
        }
        rs.close()
        st.close()
        con.close()
        return ids
    }

}