import java.util.*

data class Character(val id:UUID,val name:String,val background:String,val race:String,val languages:String,
val str:Int,val dex:Int,val con:Int,val int:Int,val wis:Int,val cha:Int,val ac:Int,val init:Int,val speed:Int,val maxHp:Int,
val skillProfs:List<String>,val toolProfs:List<String>, val items:List<String>,val features:List<String>,val classes:List<Clazz>) {}