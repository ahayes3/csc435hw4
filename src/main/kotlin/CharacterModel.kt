import spark.Spark.exception
import spark.kotlin.notFound
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*

object CharacterModel {

    fun delete(id:UUID):Character {
        val con = Utils.connect()
        val st1 = con.prepareStatement("DELETE FROM characters WHERE id=?")
        val st2 = con.prepareStatement("DELETE FROM user_characters WHERE id=?")
        val arr = arrayOf(con.prepareStatement("DELETE FROM character_features WHERE id=?"),
            con.prepareStatement("DELETE FROM character_items WHERE id=?"),
            con.prepareStatement("DELETE FROM character_skills WHERE id=?"),
            con.prepareStatement("DELETE FROM character_tools WHERE id=?"),
            con.prepareStatement("DELETE FROM character_classes WHERE id=?"))
        val cr = getCharacter(id,con)
        st1.setString(1,id.toString())
        st2.setString(1,id.toString())
        arr.forEach {p -> p.setString(1,id.toString())}
        st1.executeUpdate()
        st2.executeUpdate()
        arr.forEach { p -> p.executeUpdate() }
        con.close()

        return cr
    }
    fun get(user:String):Set<UUID> {
        val con = Utils.connect()
        val st1 =
            con.prepareStatement("SELECT uc.id FROM users u INNER JOIN user_characters uc ON u.name=uc.name AND u.name=?")
        st1.setString(1, user)
        val rs = st1.executeQuery()
        val ids: MutableSet<UUID> = HashSet()
        while (rs.next()) {
            ids.add(UUID.fromString(rs.getString("id")))
        }
        rs.close()
        st1.close()
        con.close()
        return ids
    }
    fun post(c:Character,user:String):Character {
        val con = Utils.connect()
        val st1 = con.prepareStatement(
            "INSERT INTO characters(id,name,background,race,languages,str,dex,con,wis,intel,cha,ac,init,speed,maxHp) VALUES (" +
                    "'" + c.id.toString() + "','" + c.name + "','" + c.background + "','" + c.race + "','" + c.languages + "','" +c.str + "','"
                    + c.dex + "','" + c.con + "','" + c.wis + "','"
                    + c.int + "','" + c.cha + "','" + c.ac + "','"
                    + c.init + "','" + c.speed + "','" + c.maxHp + "')")
        val st2 = con.prepareStatement("INSERT INTO user_characters(name,id) VALUES(?,?)")
        st2.setString(1,user)
        st2.setString(2,c.id.toString())
        st1.executeUpdate()
        st2.executeUpdate()

        for (t: String? in c.features) {
            val pst = con.prepareStatement(
                "INSERT IGNORE INTO character_features(id,feature) VALUES(" +
                        "'" + c.id + "',?)"
            )
            pst.setString(1, t)
            pst.executeUpdate()
            pst.close()
        }
        for (t: String? in c.items) {
            val pst = con.prepareStatement(
                "INSERT IGNORE INTO character_items(id,item) VALUES(" +
                        "'" + c.id + "',?)"
            )
            pst.setString(1, t)
            pst.executeUpdate()
            pst.close()
        }
        for (t: String? in c.skillProfs) {
            val pst = con.prepareStatement(
                ("INSERT IGNORE INTO character_skills(id,skill) VALUES(" +
                        "'" + c.id + "',?)")
            )
            pst.setString(1, t)
            pst.executeUpdate()
            pst.close()
        }

        for (t: String? in c.toolProfs) {
            val pst = con.prepareStatement("INSERT IGNORE INTO character_tools(id,tool) VALUES('" + c.id + "',?)")
            pst.setString(1, t)
            pst.executeUpdate()
            pst.close()
        }
        for (t: Clazz in c.classes) {
            val pst = con.prepareStatement("INSERT IGNORE INTO character_classes(id,class_name,level) VALUES(?,?,?)")
            pst.setString(1, c.id.toString())
            pst.setString(2, t.name)
            pst.setInt(3, t.level)
            pst.executeUpdate()
            pst.close()
        }
        con.close()
        return c
    }
    fun put(id:UUID,c:Character) {
        val con  = Utils.connect()
        val arr = arrayOf(con.prepareStatement("DELETE FROM character_classes WHERE id=?"),
            con.prepareStatement("DELETE FROM character_features WHERE id=?"),
            con.prepareStatement("DELETE FROM character_items WHERE id=?"),
            con.prepareStatement("DELETE FROM character_skills WHERE id=?"),
            con.prepareStatement("DELETE FROM character_tools WHERE id=?"))
        arr.forEach {p -> p.setString(1,id.toString())}
        val res = arr.map { p -> p.executeUpdate() }

        val st1 = con.prepareStatement("REPLACE characters(id,name,background,race,languages,str,dex,con,wis,intel,cha,ac,init,speed,maxHp)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
        st1.setString(1,id.toString())
        st1.setString(2,c.name)
        st1.setString(3,c.background)
        st1.setString(4,c.race)
        st1.setString(5,c.languages)
        st1.setInt(6,c.str)
        st1.setInt(7,c.dex)
        st1.setInt(8,c.con)
        st1.setInt(9,c.int)
        st1.setInt(10,c.wis)
        st1.setInt(11,c.cha)
        st1.setInt(12,c.ac)
        st1.setInt(13,c.init)
        st1.setInt(14,c.speed)
        st1.setInt(15,c.maxHp)
        st1.executeUpdate()

        replaceSomething(con,"character_features","feature",c.features,id)
        replaceSomething(con,"character_items","item",c.items,id)
        replaceSomething(con,"character_skills","skill",c.skillProfs,id)
        replaceSomething(con,"character_tools","tool",c.toolProfs,id)
        for ((name, level) in c.classes) {
            val pst = con.prepareStatement("INSERT IGNORE INTO character_classes(id,class_name,level) VALUES(?,?,?)")
            pst.setString(1, c.id.toString())
            pst.setString(2, name)
            pst.setInt(3, level)
            pst.executeUpdate()
            pst.close()
        }
    }
    private fun replaceSomething(con:Connection,str1:String,str2:String,l: List<String>,id:UUID) {
        for (t in l) {
            val pst: PreparedStatement = con.prepareStatement(
                "INSERT IGNORE INTO $str1 (id,$str2) VALUES(" +
                        "'" + id + "',?)"
            )
            pst.setString(1, t)
            pst.executeUpdate()
            pst.close()
        }
    }
    fun getById(id:UUID):Character {
        val con = Utils.connect()
        val cr = getCharacter(id,con)
        con.close()
        return cr
    }
    fun getCharacter(id:UUID,con: Connection): Character {
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
            throw ErrorException(404)
        val cr = Character(id,res[0].getString("name"),res[0].getString("background"),res[0].getString("race"),
                res[0].getString("languages"),res[0].getInt("str"),res[0].getInt("dex"),res[0].getInt("con"),
                res[0].getInt("intel"),res[0].getInt("wis"),res[0].getInt("cha"),res[0].getInt("ac"),res[0].getInt("init"),res[0].getInt("speed"),
                res[0].getInt("maxHp"),makeList(res[4],"skill"),makeList(res[5],"tool"),makeList(res[3],"item"),makeList(res[2],"feature"),
            makeClasses(res[1]))
        res.forEach {p -> p.close()}
        arr.forEach { p -> p.close() }
        return cr
    }
    fun makeClasses(st: ResultSet):List<Clazz> {
        val out = mutableListOf<Clazz>()
        while(st.next()) {
            out.add(Clazz(st.getString("class_name"),st.getInt("level")))
        }
        return out
    }
    fun makeList(st:ResultSet,str:String): List<String> {
        val out = mutableListOf<String>()
        while(st.next()) {
            out.add(st.getString(str))
        }
        return out
    }
    fun newUUID():UUID {
        val used = usedIds()
        var id:UUID
        do {
            id = UUID.randomUUID()
        } while(used.contains(id))
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