package com.boswelja.jmdict.generator.dtd

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeserializerUnitTest {

    @Test
    fun `buildAttribute throws on unknown type`() {
        val invalidAttr = AttributeDto(
            elementName = "element",
            attributeName = "attribute",
            type = "some-invalid-type",
            value = "#REQUIRED"
        )
        assertFailsWith<IllegalArgumentException> {
            buildAttribute(invalidAttr)
        }
    }

    @Test
    fun `buildAttribute fails for unknown value`() {
        val invalidAttr = AttributeDto(
            elementName = "element",
            attributeName = "attribute",
            type = "CDATA",
            value = "some-invalid-data"
        )
        assertFailsWith<IllegalArgumentException> {
            buildAttribute(invalidAttr)
        }
    }

    @Test
    fun `buildAttribute succeeds for valid values`() {
        val values = mapOf(
            "#REQUIRED" to AttributeDefinition.Value.Required,
            "#IMPLIED" to AttributeDefinition.Value.Implied,
            "#FIXED \"fixed\"" to AttributeDefinition.Value.Fixed("fixed"),
            "\"default\"" to AttributeDefinition.Value.Default("default"),
        )

        values.forEach { (value, expected) ->
            val attrDto = AttributeDto(
                elementName = "element",
                attributeName = "attribute",
                type = "CDATA",
                value = value
            )
            val attr = buildAttribute(attrDto)
            assertEquals(
                expected,
                attr.value,
            )
        }
    }

    @Test
    fun `buildAttribute succeeds for valid types`() {
        val types = mapOf(
            "CDATA" to AttributeDefinition.Type.CharacterData,
            "ID" to AttributeDefinition.Type.Id,
            "IDREF" to AttributeDefinition.Type.IdRef,
            "IDREFS" to AttributeDefinition.Type.IdRefs,
            "NMTOKEN" to AttributeDefinition.Type.NmToken,
            "NMTOKENS" to AttributeDefinition.Type.NmTokens,
            "ENTITY" to AttributeDefinition.Type.Entity,
            "ENTITIES" to AttributeDefinition.Type.Entities,
            "NOTATION" to AttributeDefinition.Type.Notation,
            "(one|two|three)" to AttributeDefinition.Type.Enum(listOf("one", "two", "three")),
            "(enum-Value_1 | enum_Value-2)" to AttributeDefinition.Type.Enum(listOf("enum-Value_1", "enum_Value-2"))
        )

        types.forEach { (type, expected) ->
            val attrDto = AttributeDto(
                elementName = "element",
                attributeName = "attribute",
                type = type,
                value = "#REQUIRED"
            )
            val attr = buildAttribute(attrDto)
            assertEquals(
                expected,
                attr.attributeType,
            )
        }
    }

    @Test
    fun `buildElementDefinition fails when element children are missing`() {
        val element = ElementDto(
            name = "element",
            children = listOf("child1", "child2"),
            isMixed = false,
        )
        assertFailsWith<IllegalArgumentException> {
            buildElementDefinition(
                element = element,
                elementName = element.name,
                elements = emptyList(),
                attributes = emptyList()
            )
        }
    }

    @Test
    fun `buildElementDefinition fails when mixed element children are missing`() {
        val element = ElementDto(
            name = "element",
            children = listOf("child1", "child2"),
            isMixed = true,
        )
        assertFailsWith<IllegalArgumentException> {
            buildElementDefinition(
                element = element,
                elementName = element.name,
                elements = emptyList(),
                attributes = emptyList()
            )
        }
    }

    @Test
    fun `buildElementDefinition succeeds when element consists of a single PCDATA`() {
        val element = ElementDto(
            name = "element",
            children = listOf("#PCDATA"),
            isMixed = false
        )
        assertEquals(
            ElementDefinition.ParsedCharacterData(element.name, emptyList()),
            buildElementDefinition(
                element = element,
                elementName = element.name,
                elements = emptyList(),
                attributes = emptyList(),
            )
        )
    }

    @Test
    fun `buildElementDefinition succeeds when element consists of ANY`() {
        val element = ElementDto(
            name = "element",
            children = listOf("ANY"),
            isMixed = false
        )

        assertEquals(
            ElementDefinition.Any(element.name, emptyList()),
            buildElementDefinition(
                element = element,
                elementName = element.name,
                elements = emptyList(),
                attributes = emptyList()
            )
        )
    }

    @Test
    fun `buildElementDefinition succeeds when element has no children`() {
        val element = ElementDto(
            name = "element",
            children = emptyList(),
            isMixed = false
        )

        assertEquals(
            ElementDefinition.Empty(elementName = element.name, attributes = emptyList()),
            buildElementDefinition(
                element = element,
                elementName = element.name,
                elements = emptyList(),
                attributes = emptyList()
            )
        )
    }
}
