/*
 * R3 Proprietary and Confidential
 *
 * Copyright (c) 2018 R3 Limited.  All rights reserved.
 *
 * The intellectual and technical concepts contained herein are proprietary to R3 and its suppliers and are protected by trade secret law.
 *
 * Distribution of this file or any portion thereof via any medium without the express permission of R3 is strictly prohibited.
 */

package net.corda.nodeapi.internal

import com.google.common.base.CaseFormat
import net.corda.core.schemas.MappedSchema

object MigrationHelpers {
    private const val MIGRATION_PREFIX = "migration"
    private const val DEFAULT_MIGRATION_EXTENSION = "xml"
    private const val CHANGELOG_NAME = "changelog-master"
    private val possibleMigrationExtensions = listOf(".xml", ".sql", ".yml", ".json")

    fun getMigrationResource(schema: MappedSchema, classLoader: ClassLoader): String? {
        val declaredMigration = schema.migrationResource

        if (declaredMigration == null) {
            // try to apply the naming convention and find the migration file in the classpath
            val resource = migrationResourceNameForSchema(schema)
            return possibleMigrationExtensions.map { "$resource$it" }.firstOrNull {
                classLoader.getResource(it) != null
            }
        }

        return "$MIGRATION_PREFIX/$declaredMigration.$DEFAULT_MIGRATION_EXTENSION"
    }

    // SchemaName will be transformed from camel case to lower_hyphen then add ".changelog-master"
    fun migrationResourceNameForSchema(schema: MappedSchema): String {
        val name: String = schema::class.simpleName!!
        val fileName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name)
        return "$MIGRATION_PREFIX/$fileName.$CHANGELOG_NAME"
    }
}
