@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.core.settings.migration

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ConfigSnakeCaseMigrationTest {
    private val folder = Files.createTempDirectory("config").toFile()

    @Test
    fun GIVEN_config_with_camel_case_keys_WHEN_migrated_THEN_keys_are_snake_case() {
        val file = folder.resolve("config.yml")
        file.writeText(
            """
            maxRatingPerDay: 5
            gui:
              showDeleteReport: true
            """.trimIndent()
        )

        migrateConfigToSnakeCase(file)

        val expected = """
            max_rating_per_day: 5
            gui:
              show_delete_report: true
        """.trimIndent()
        assertEquals(expected, file.readText())
    }

    @Test
    fun GIVEN_config_with_snake_case_keys_WHEN_migrated_THEN_file_is_not_rewritten() {
        val file = folder.resolve("config.yml")
        file.writeText("max_rating_per_day: 5\n")
        file.setLastModified(0L)

        migrateConfigToSnakeCase(file)

        assertEquals(0L, file.lastModified())
    }

    @Test
    fun GIVEN_missing_config_WHEN_migrated_THEN_no_file_is_created() {
        val file = folder.resolve("missing.yml")

        migrateConfigToSnakeCase(file)

        assertFalse(file.exists())
    }
}
