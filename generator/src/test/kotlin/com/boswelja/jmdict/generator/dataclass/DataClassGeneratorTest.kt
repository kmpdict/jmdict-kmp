package com.boswelja.jmdict.generator.dataclass

import com.boswelja.jmdict.generator.dtd.AttributeDefinition
import com.boswelja.jmdict.generator.dtd.ChildElementDefinition
import com.boswelja.jmdict.generator.dtd.DocumentTypeDefinition
import com.boswelja.jmdict.generator.dtd.ElementDefinition
import com.boswelja.jmdict.generator.dtd.Entity
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DataClassGeneratorTest {

    private val testDir = Path("testdata/")
    private val packageName = "com.example.test"

    private lateinit var generator: DataClassGenerator

    @BeforeTest
    fun setUp() {
        testDir.createDirectories()
        generator = DataClassGenerator(packageName, testDir)
    }

    @OptIn(ExperimentalPathApi::class)
    @AfterTest
    fun tearDown() {
        testDir.deleteRecursively()
    }

    @Test
    fun `testGenerateDataClass should generate with no nested elements`() {
        val testCases = mapOf(
            DocumentTypeDefinition(
                rootElement = ElementDefinition.ParsedCharacterData(elementName = "no-nested-data", attributes = emptyList()),
                entities = emptyList()
            ) to """
                |package $packageName
                |
                |import kotlin.String
                |
                |public value class NoNestedData(
                |  public val content: String,
                |)
                |
            """.trimMargin(),
            DocumentTypeDefinition(
                rootElement = ElementDefinition.Empty(elementName = "no-nested-data", attributes = emptyList()),
                entities = emptyList()
            ) to """
                |package $packageName
                |
                |public data object NoNestedData
                |
            """.trimMargin(),
            DocumentTypeDefinition(
                rootElement = ElementDefinition.Any(elementName = "no-nested-data", attributes = emptyList()),
                entities = emptyList()
            ) to """
                |package $packageName
                |
                |import kotlin.String
                |
                |public value class NoNestedData(
                |  public val content: String,
                |)
                |
            """.trimMargin(),
            DocumentTypeDefinition(
                rootElement = ElementDefinition.ParsedCharacterData(
                    elementName = "no-nested-data",
                    attributes = listOf(
                        AttributeDefinition(
                            attributeName = "attr1",
                            attributeType = AttributeDefinition.Type.CharacterData,
                            value = AttributeDefinition.Value.Required
                        )
                    )
                ),
                entities = emptyList()
            ) to """
                |package $packageName
                |
                |import kotlin.String
                |
                |public data class NoNestedData(
                |  public val attr1: String,
                |  public val content: String,
                |)
                |
            """.trimMargin(),
        )

        testCases.forEach { (input, expected) ->
            generator.writeDtdToTarget(input)

            assertEquals(
                expected,
                testDir.resolve("com/example/test/NoNestedData.kt").readText()
            )
        }
    }

    @Test
    fun `testGenerateDataClasses should generate nested elements`() {
        val testCases = mapOf(
            DocumentTypeDefinition(
                rootElement = ElementDefinition.WithChildren(
                    elementName = "NESTED",
                    attributes = emptyList(),
                    children = listOf(
                        ChildElementDefinition.Single(
                            elementDefinition = ElementDefinition.WithChildren(
                                elementName = "ARTICLE",
                                attributes = listOf(
                                    AttributeDefinition(
                                        attributeName = "AUTHOR",
                                        attributeType = AttributeDefinition.Type.CharacterData,
                                        value = AttributeDefinition.Value.Required
                                    ),
                                    AttributeDefinition(
                                        attributeName = "EDITOR",
                                        attributeType = AttributeDefinition.Type.CharacterData,
                                        value = AttributeDefinition.Value.Implied
                                    ),
                                    AttributeDefinition(
                                        attributeName = "DATE",
                                        attributeType = AttributeDefinition.Type.CharacterData,
                                        value = AttributeDefinition.Value.Implied
                                    ),
                                    AttributeDefinition(
                                        attributeName = "EDITION",
                                        attributeType = AttributeDefinition.Type.CharacterData,
                                        value = AttributeDefinition.Value.Implied
                                    ),
                                ),
                                children = listOf(
                                    ChildElementDefinition.Single(
                                        elementDefinition = ElementDefinition.ParsedCharacterData(
                                            elementName = "HEADLINE",
                                            attributes = emptyList()
                                        ),
                                        occurs = ChildElementDefinition.Occurs.Once
                                    ),
                                    ChildElementDefinition.Single(
                                        elementDefinition = ElementDefinition.ParsedCharacterData(
                                            elementName = "BYLINE",
                                            attributes = emptyList()
                                        ),
                                        occurs = ChildElementDefinition.Occurs.Once
                                    ),
                                    ChildElementDefinition.Single(
                                        elementDefinition = ElementDefinition.ParsedCharacterData(
                                            elementName = "LEAD",
                                            attributes = emptyList()
                                        ),
                                        occurs = ChildElementDefinition.Occurs.Once
                                    ),
                                    ChildElementDefinition.Single(
                                        elementDefinition = ElementDefinition.ParsedCharacterData(
                                            elementName = "BODY",
                                            attributes = emptyList()
                                        ),
                                        occurs = ChildElementDefinition.Occurs.Once
                                    ),
                                    ChildElementDefinition.Single(
                                        elementDefinition = ElementDefinition.ParsedCharacterData(
                                            elementName = "NOTES",
                                            attributes = emptyList()
                                        ),
                                        occurs = ChildElementDefinition.Occurs.Once
                                    ),
                                )
                            ),
                            occurs = ChildElementDefinition.Occurs.AtLeastOnce
                        )
                    )
                ),
                entities = listOf(
                    Entity.Internal(
                        name = "NEWSPAPER",
                        value = "Vervet Logic Times"
                    ),
                    Entity.Internal(
                        name = "PUBLISHER",
                        value = "Vervet Logic Press"
                    ),
                    Entity.Internal(
                        name = "COPYRIGHT",
                        value = "Copyright 1998 Vervet Logic Press"
                    )
                )
            ) to """
                |package $packageName
                |
                |import kotlin.String
                |import kotlin.collections.List
                |
                |public value class Headline(
                |  public val content: String,
                |)
                |
                |public value class Byline(
                |  public val content: String,
                |)
                |
                |public value class Lead(
                |  public val content: String,
                |)
                |
                |public value class Body(
                |  public val content: String,
                |)
                |
                |public value class Notes(
                |  public val content: String,
                |)
                |
                |public data class Article(
                |  public val author: String,
                |  public val editor: String?,
                |  public val date: String?,
                |  public val edition: String?,
                |  public val headline: Headline,
                |  public val byline: Byline,
                |  public val lead: Lead,
                |  public val body: Body,
                |  public val notes: Notes,
                |)
                |
                |public data class Nested(
                |  public val articles: List<Article>,
                |)
                |
            """.trimMargin()
        )

        testCases.forEach { (input, expected) ->
            generator.writeDtdToTarget(input)

            assertEquals(
                expected,
                testDir.resolve("com/example/test/Nested.kt").readText()
            )
        }
    }
}
