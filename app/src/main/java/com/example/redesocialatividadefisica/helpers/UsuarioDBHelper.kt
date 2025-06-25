import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsuarioDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "usuarios.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "usuarios"
        const val COLUMN_ID = "id"
        const val COLUMN_UID = "uid"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_NOME = "nome"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_UID TEXT UNIQUE,
                $COLUMN_EMAIL TEXT,
                $COLUMN_NOME TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun inserirUsuario(uid: String, email: String, nome: String): Boolean {
        val db = writableDatabase

        // Verifica se já existe usuário com esse uid
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_UID),
            "$COLUMN_UID = ?",
            arrayOf(uid),
            null,
            null,
            null
        )

        val existe = cursor.count > 0
        cursor.close()

        if (existe) {
            // Usuário já cadastrado
            return false
        }

        val values = ContentValues().apply {
            put(COLUMN_UID, uid)
            put(COLUMN_EMAIL, email)
            put(COLUMN_NOME, nome)
        }

        val resultado = db.insert(TABLE_NAME, null, values)
        db.close()

        return resultado != -1L
    }
}
