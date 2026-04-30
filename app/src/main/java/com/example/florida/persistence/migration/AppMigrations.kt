package com.example.florida.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE user_setup ADD COLUMN street TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE user_setup ADD COLUMN number TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE user_setup ADD COLUMN neighborhood TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE user_setup ADD COLUMN city TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE user_setup ADD COLUMN state TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE user_setup ADD COLUMN phone TEXT NOT NULL DEFAULT ''")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS clients (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    address TEXT NOT NULL,
                    document TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    imagePath TEXT,
                    deleted INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    clientId INTEGER,
                    notes TEXT,
                    validade TEXT,
                    entrega TEXT,
                    createdAt TEXT NOT NULL,
                    updateAt TEXT NOT NULL,
                    total REAL NOT NULL,
                    FOREIGN KEY(clientId) REFERENCES clients(id) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_clientId ON budgets(clientId)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS budget_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    budgetId INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    qty INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY(budgetId) REFERENCES budgets(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budget_items_budgetId ON budget_items(budgetId)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS receipts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    clientId INTEGER,
                    total REAL NOT NULL,
                    date TEXT NOT NULL,
                    createdAt TEXT NOT NULL,
                    FOREIGN KEY(clientId) REFERENCES clients(id) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_receipts_clientId ON receipts(clientId)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS receipt_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    receiptId INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    qty INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY(receiptId) REFERENCES receipts(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_receipt_items_receiptId ON receipt_items(receiptId)")
        }
    }
}
