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

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE budgets ADD COLUMN status TEXT NOT NULL DEFAULT 'DRAFT'")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE receipts ADD COLUMN budgetId INTEGER DEFAULT NULL")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_receipts_budgetId ON receipts(budgetId)")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("PRAGMA foreign_keys=OFF")

            db.execSQL(
                """
                CREATE TABLE budgets_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    clientId INTEGER,
                    notes TEXT,
                    validade TEXT,
                    entrega TEXT,
                    createdAt TEXT NOT NULL,
                    updateAt TEXT NOT NULL,
                    total INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    FOREIGN KEY(clientId) REFERENCES clients(id) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO budgets_new (id, clientId, notes, validade, entrega, createdAt, updateAt, total, status)
                SELECT id, clientId, notes, validade, entrega, createdAt, updateAt, CAST(ROUND(total * 100) AS INTEGER), status
                FROM budgets
                """.trimIndent()
            )
            db.execSQL("DROP TABLE budgets")
            db.execSQL("ALTER TABLE budgets_new RENAME TO budgets")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_clientId ON budgets(clientId)")

            db.execSQL(
                """
                CREATE TABLE budget_items_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    budgetId INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    qty INTEGER NOT NULL,
                    price INTEGER NOT NULL,
                    FOREIGN KEY(budgetId) REFERENCES budgets(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO budget_items_new (id, budgetId, description, qty, price)
                SELECT id, budgetId, description, qty, CAST(ROUND(price * 100) AS INTEGER)
                FROM budget_items
                """.trimIndent()
            )
            db.execSQL("DROP TABLE budget_items")
            db.execSQL("ALTER TABLE budget_items_new RENAME TO budget_items")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budget_items_budgetId ON budget_items(budgetId)")

            db.execSQL(
                """
                CREATE TABLE receipts_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    clientId INTEGER,
                    budgetId INTEGER,
                    total INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    createdAt TEXT NOT NULL,
                    FOREIGN KEY(clientId) REFERENCES clients(id) ON UPDATE NO ACTION ON DELETE SET NULL,
                    FOREIGN KEY(budgetId) REFERENCES budgets(id) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO receipts_new (id, clientId, budgetId, total, date, createdAt)
                SELECT id, clientId, budgetId, CAST(ROUND(total * 100) AS INTEGER), date, createdAt
                FROM receipts
                """.trimIndent()
            )
            db.execSQL("DROP TABLE receipts")
            db.execSQL("ALTER TABLE receipts_new RENAME TO receipts")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_receipts_clientId ON receipts(clientId)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_receipts_budgetId ON receipts(budgetId)")

            db.execSQL(
                """
                CREATE TABLE receipt_items_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    receiptId INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    qty INTEGER NOT NULL,
                    price INTEGER NOT NULL,
                    FOREIGN KEY(receiptId) REFERENCES receipts(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO receipt_items_new (id, receiptId, description, qty, price)
                SELECT id, receiptId, description, qty, CAST(ROUND(price * 100) AS INTEGER)
                FROM receipt_items
                """.trimIndent()
            )
            db.execSQL("DROP TABLE receipt_items")
            db.execSQL("ALTER TABLE receipt_items_new RENAME TO receipt_items")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_receipt_items_receiptId ON receipt_items(receiptId)")

            db.execSQL("PRAGMA foreign_keys=ON")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE clients ADD COLUMN remoteId INTEGER")
            db.execSQL("ALTER TABLE clients ADD COLUMN syncPending INTEGER NOT NULL DEFAULT 0")

            db.execSQL("ALTER TABLE budgets ADD COLUMN remoteId INTEGER")
            db.execSQL("ALTER TABLE budgets ADD COLUMN syncPending INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE budgets ADD COLUMN pendingDelete INTEGER NOT NULL DEFAULT 0")

            db.execSQL("ALTER TABLE receipts ADD COLUMN remoteId INTEGER")
            db.execSQL("ALTER TABLE receipts ADD COLUMN syncPending INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE receipts ADD COLUMN pendingDelete INTEGER NOT NULL DEFAULT 0")
        }
    }
}
