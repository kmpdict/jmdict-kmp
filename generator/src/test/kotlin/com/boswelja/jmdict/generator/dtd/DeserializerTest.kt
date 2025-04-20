package com.boswelja.jmdict.generator.dtd

import kotlinx.io.Buffer
import kotlinx.io.writeString
import kotlin.test.Test
import kotlin.test.assertEquals

class DeserializerTest {
    @Test
    fun `fromSource returns the correct type for valid DTD`() {
        ValidDtdSamples.forEach { (dtd, expected) ->
            val source = Buffer()
            source.writeString(dtd)

            val result = DocumentTypeDefinition.fromSource(source)

            assertEquals(
                expected,
                result
            )
        }
    }

    companion object {
        val ValidDtdSamples = mapOf(
            """
                <!DOCTYPE TVSCHEDULE [

                <!ELEMENT TVSCHEDULE (CHANNEL+)>
                <!ELEMENT CHANNEL (BANNER,DAY+)>
                <!ELEMENT BANNER (#PCDATA)>
                <!ELEMENT DAY (DATE,(HOLIDAY|PROGRAMSLOT+)+)>
                <!ELEMENT HOLIDAY (#PCDATA)>
                <!ELEMENT DATE (#PCDATA)>
                <!ELEMENT PROGRAMSLOT (TIME,TITLE,DESCRIPTION?)>
                <!ELEMENT TIME (#PCDATA)>
                <!ELEMENT TITLE (#PCDATA)> 
                <!ELEMENT DESCRIPTION (#PCDATA)>

                <!ATTLIST TVSCHEDULE NAME CDATA #REQUIRED>
                <!ATTLIST CHANNEL CHAN CDATA #REQUIRED>
                <!ATTLIST PROGRAMSLOT VTR CDATA #IMPLIED>
                <!ATTLIST TITLE RATING CDATA #IMPLIED>
                <!ATTLIST TITLE LANGUAGE CDATA #IMPLIED>
                ]>
            """.trimIndent() to
                    DocumentTypeDefinition(
                        rootElement = ElementDefinition.WithChildren(
                            elementName = "TVSCHEDULE",
                            attributes = listOf(
                                AttributeDefinition(
                                    attributeName = "NAME",
                                    attributeType = AttributeDefinition.Type.CharacterData,
                                    value = AttributeDefinition.Value.Required
                                )
                            ),
                            children = listOf(
                                ChildElementDefinition.Single(
                                    elementDefinition = ElementDefinition.WithChildren(
                                        elementName = "CHANNEL",
                                        attributes = listOf(
                                            AttributeDefinition(
                                                attributeName = "CHAN",
                                                attributeType = AttributeDefinition.Type.CharacterData,
                                                value = AttributeDefinition.Value.Required
                                            )
                                        ),
                                        children = listOf(
                                            ChildElementDefinition.Single(
                                                elementDefinition = ElementDefinition.ParsedCharacterData(
                                                    elementName = "BANNER",
                                                    attributes = emptyList()
                                                ),
                                                occurs = ChildElementDefinition.Occurs.Once
                                            ),
                                            ChildElementDefinition.Single(
                                                elementDefinition = ElementDefinition.WithChildren(
                                                    elementName = "DAY",
                                                    attributes = emptyList(),
                                                    children = listOf(
                                                        ChildElementDefinition.Single(
                                                            elementDefinition = ElementDefinition.ParsedCharacterData(
                                                                elementName = "DATE",
                                                                attributes = emptyList()
                                                            ),
                                                            occurs = ChildElementDefinition.Occurs.Once
                                                        ),
                                                        ChildElementDefinition.Either(
                                                            options = listOf(
                                                                ChildElementDefinition.Single(
                                                                    elementDefinition = ElementDefinition.ParsedCharacterData(
                                                                        elementName = "HOLIDAY",
                                                                        attributes = emptyList()
                                                                    ),
                                                                    occurs = ChildElementDefinition.Occurs.Once
                                                                ),
                                                                ChildElementDefinition.Single(
                                                                    elementDefinition = ElementDefinition.WithChildren(
                                                                        elementName = "PROGRAMSLOT",
                                                                        attributes = listOf(
                                                                            AttributeDefinition(
                                                                                attributeType = AttributeDefinition.Type.CharacterData,
                                                                                attributeName = "VTR",
                                                                                value = AttributeDefinition.Value.Implied
                                                                            )
                                                                        ),
                                                                        children = listOf(
                                                                            ChildElementDefinition.Single(
                                                                                elementDefinition = ElementDefinition.ParsedCharacterData(
                                                                                    elementName = "TIME",
                                                                                    attributes = emptyList()
                                                                                ),
                                                                                occurs = ChildElementDefinition.Occurs.Once
                                                                            ),
                                                                            ChildElementDefinition.Single(
                                                                                elementDefinition = ElementDefinition.ParsedCharacterData(
                                                                                    elementName = "TITLE",
                                                                                    attributes = listOf(
                                                                                        AttributeDefinition(
                                                                                            attributeType = AttributeDefinition.Type.CharacterData,
                                                                                            attributeName = "RATING",
                                                                                            value = AttributeDefinition.Value.Implied
                                                                                        ),
                                                                                        AttributeDefinition(
                                                                                            attributeType = AttributeDefinition.Type.CharacterData,
                                                                                            attributeName = "LANGUAGE",
                                                                                            value = AttributeDefinition.Value.Implied
                                                                                        ),
                                                                                    )
                                                                                ),
                                                                                occurs = ChildElementDefinition.Occurs.Once
                                                                            ),
                                                                            ChildElementDefinition.Single(
                                                                                elementDefinition = ElementDefinition.ParsedCharacterData(
                                                                                    elementName = "DESCRIPTION",
                                                                                    attributes = emptyList()
                                                                                ),
                                                                                occurs = ChildElementDefinition.Occurs.AtMostOnce
                                                                            ),
                                                                        )
                                                                    ),
                                                                    occurs = ChildElementDefinition.Occurs.AtLeastOnce
                                                                )
                                                            ),
                                                            occurs = ChildElementDefinition.Occurs.AtLeastOnce
                                                        )
                                                    )
                                                ),
                                                occurs = ChildElementDefinition.Occurs.AtLeastOnce
                                            )
                                        )
                                    ),
                                    occurs = ChildElementDefinition.Occurs.AtLeastOnce
                                )
                            )
                        ),
                        entities = emptyList()
                    ),
            """
                <!DOCTYPE NEWSPAPER [

                <!ELEMENT NEWSPAPER (ARTICLE+)>
                <!ELEMENT ARTICLE (HEADLINE,BYLINE,LEAD,BODY,NOTES)>
                <!ELEMENT HEADLINE (#PCDATA)>
                <!ELEMENT BYLINE (#PCDATA)>
                <!ELEMENT LEAD (#PCDATA)>
                <!ELEMENT BODY (#PCDATA)>
                <!ELEMENT NOTES (#PCDATA)>

                <!ATTLIST ARTICLE AUTHOR CDATA #REQUIRED>
                <!ATTLIST ARTICLE EDITOR CDATA #IMPLIED>
                <!ATTLIST ARTICLE DATE CDATA #IMPLIED>
                <!ATTLIST ARTICLE EDITION CDATA #IMPLIED>

                <!ENTITY NEWSPAPER "Vervet Logic Times">
                <!ENTITY PUBLISHER "Vervet Logic Press">
                <!ENTITY COPYRIGHT "Copyright 1998 Vervet Logic Press">

                ]> 
            """.trimIndent() to
                    DocumentTypeDefinition(
                        rootElement = ElementDefinition.WithChildren(
                            elementName = "NEWSPAPER",
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
                    ),
            """
                <!DOCTYPE HOLIDAY [
                <!ELEMENT HOLIDAY (#PCDATA)*>
                ]>
            """.trimIndent() to DocumentTypeDefinition(
                rootElement = ElementDefinition.Mixed(
                    elementName = "HOLIDAY",
                    attributes = emptyList(),
                    children = listOf(
                        ElementDefinition.ParsedCharacterData(
                            elementName = "#PCDATA",
                            attributes = emptyList()
                        ),
                    ),
                ),
                entities = emptyList()
            )
        )
    }
}
