package com.boswelja.jmdict.generator.dataclass

import com.boswelja.jmdict.generator.dtd.AttributeDefinition
import com.boswelja.jmdict.generator.dtd.ChildElementDefinition
import com.boswelja.jmdict.generator.dtd.DocumentTypeDefinition
import com.boswelja.jmdict.generator.dtd.ElementDefinition
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.nio.file.Path

class DataClassGenerator(
    private val packageName: String,
    private val targetDir: Path
) {
    fun writeDtdToTarget(dtd: DocumentTypeDefinition) {
        val generatedTypes = generateTypeSpecForElement(dtd.rootElement)
        FileSpec.builder(generatedTypes.rootClassName)
            .addTypes(generatedTypes.topLevelTypes)
            .build()
            .writeTo(targetDir)
    }

    internal fun generateTypeSpecForElement(element: ElementDefinition): GeneratedTypes {
        val types = mutableListOf<TypeSpec>()
        val nestedTypes = mutableListOf<TypeSpec>()
        val parameters = mutableListOf<ParameterSpec>()
        val properties = mutableListOf<PropertySpec>()

        generatePropertiesForAttributes(element.attributes).also {
            parameters.addAll(it.parameters)
            properties.addAll(it.properties)
            nestedTypes.addAll(it.types)
        }

        when (element) {
            is ElementDefinition.Empty -> { /* Nothing to add */ }
            is ElementDefinition.Mixed -> {
                element.children.forEach { elementDefinition ->
                    val typeSpec = generateTypeSpecForElement(elementDefinition)
                    types.addAll(typeSpec.topLevelTypes)

                    val propertyName = "${typeSpec.rootClassName.simpleName.toCamelCase()}s"
                    val propertyType = List::class.asClassName().parameterizedBy(typeSpec.rootClassName)
                    parameters.add(
                        ParameterSpec.builder(propertyName, propertyType)
                            .build()
                    )
                    properties.add(
                        PropertySpec.builder(propertyName, propertyType)
                            .addModifiers(KModifier.PUBLIC)
                            .initializer(propertyName)
                            .build()
                    )
                }
            }
            is ElementDefinition.WithChildren -> {
                generatePropertiesForChildren(element.children).also {
                    parameters.addAll(it.parameters)
                    properties.addAll(it.properties)
                    types.addAll(it.types)
                }
            }
            is ElementDefinition.Any,
            is ElementDefinition.ParsedCharacterData -> {
                if (parameters.isEmpty()) {
                    // If there's no other parameters for this PCDATA, create a value class instead.
                    val propertyName = "content"
                    val constructorBuilder = FunSpec.constructorBuilder()
                        .addParameter(
                            ParameterSpec.builder(propertyName, String::class).build()
                        )
                        .build()
                    val valueType = TypeSpec.classBuilder(element.elementName.toPascalCase())
                        .primaryConstructor(constructorBuilder)
                        .addModifiers(KModifier.VALUE)
                        .addTypes(nestedTypes)
                        .addProperties(properties)
                        .addProperty(
                            PropertySpec.builder(propertyName, String::class)
                                .addModifiers(KModifier.PUBLIC)
                                .initializer(propertyName)
                                .build()
                        )
                        .build()
                    types.add(valueType)
                    return GeneratedTypes(
                        rootClassName = ClassName(packageName, element.elementName.toPascalCase()),
                        topLevelTypes = types
                    )
                } else {
                    val propertyName = "content"
                    parameters.add(
                        ParameterSpec.builder(propertyName, String::class)
                            .build()
                    )
                    properties.add(
                        PropertySpec.builder(propertyName, String::class)
                            .addModifiers(KModifier.PUBLIC)
                            .initializer(propertyName)
                            .build()
                    )
                }
            }
        }

        val rootType = if (parameters.isEmpty()) {
            TypeSpec.objectBuilder(element.elementName.toPascalCase())
                .addProperties(properties)
                .addModifiers(KModifier.DATA)
                .addTypes(nestedTypes)
                .build()
        } else {
            val constructorBuilder = FunSpec.constructorBuilder()
                .addParameters(parameters)
                .build()
            TypeSpec.classBuilder(element.elementName.toPascalCase())
                .primaryConstructor(constructorBuilder)
                .addProperties(properties)
                .addModifiers(KModifier.DATA)
                .addTypes(nestedTypes)
                .build()
        }

        types.add(rootType)
        return GeneratedTypes(
            rootClassName = ClassName(packageName, element.elementName.toPascalCase()),
            topLevelTypes = types
        )
    }

    internal fun generatePropertiesForChildren(children: List<ChildElementDefinition>): GeneratedProperties {
        val parameters = mutableListOf<ParameterSpec>()
        val properties = mutableListOf<PropertySpec>()
        val types = mutableListOf<TypeSpec>()

        children.forEach { childElementDefinition ->
            val childTypes = generateTypesForChild(childElementDefinition)
            val type: TypeName = when (childElementDefinition.occurs) {
                ChildElementDefinition.Occurs.Once -> childTypes.rootClassName
                ChildElementDefinition.Occurs.AtMostOnce -> childTypes.rootClassName.copy(nullable = true)
                ChildElementDefinition.Occurs.AtLeastOnce,
                ChildElementDefinition.Occurs.ZeroOrMore ->
                    List::class.asClassName().parameterizedBy(childTypes.rootClassName)
            }
            // If the type is a List, pluralize the name
            val propertyName = if (childElementDefinition.occurs == ChildElementDefinition.Occurs.ZeroOrMore || childElementDefinition.occurs == ChildElementDefinition.Occurs.AtLeastOnce) {
                "${childTypes.rootClassName.simpleName.toCamelCase()}s"
            } else {
                childTypes.rootClassName.simpleName.toCamelCase()
            }
            parameters.add(
                ParameterSpec.builder(propertyName, type)
                    .build()
            )
            properties.add(
                PropertySpec.builder(propertyName, type)
                    .addModifiers(KModifier.PUBLIC)
                    .initializer(propertyName)
                    .build()
            )
            types.addAll(childTypes.topLevelTypes)
        }

        return GeneratedProperties(
            properties = properties,
            parameters = parameters,
            types = types
        )
    }

    internal fun generateTypesForChild(childElementDefinition: ChildElementDefinition): GeneratedTypes {
        return when (childElementDefinition) {
            is ChildElementDefinition.Either -> {
                val childTypes = childElementDefinition.options.map { generateTypesForChild(it) }
                val typeName = childTypes.joinToString(separator = "Or") { it.rootClassName.simpleName }
                val sealedSpec = TypeSpec.interfaceBuilder(typeName)
                    .addModifiers(KModifier.SEALED)
                    .build()
                val topLevelTypes = childTypes
                    .map { childType ->
                        val type = childType.topLevelTypes.first { it.name == childType.rootClassName.simpleName }
                        childType.topLevelTypes.toMutableList().apply {
                            remove(type)
                            add(type.toBuilder().addSuperinterface(ClassName(packageName, typeName)).build())
                        }
                    }
                    .flatten()
                GeneratedTypes(
                    rootClassName = ClassName(packageName, typeName),
                    topLevelTypes = topLevelTypes + sealedSpec
                )
            }
            is ChildElementDefinition.Single -> generateTypeSpecForElement(childElementDefinition.elementDefinition)
        }
    }

    internal fun generateEnumForAttribute(name: String, enumValues: AttributeDefinition.Type.Enum): TypeSpec {
        val enumBuilder = TypeSpec.enumBuilder(name.toPascalCase())
        enumValues.options.forEach {
            enumBuilder.addEnumConstant(it.toPascalCase())
        }
        return enumBuilder.build()
    }

    internal fun generatePropertiesForAttributes(attributes: List<AttributeDefinition>): GeneratedProperties {
        val types = mutableListOf<TypeSpec>()
        val properties = mutableListOf<PropertySpec>()
        val parameters = mutableListOf<ParameterSpec>()
        attributes.forEach { attribute ->
            val propertyName = attribute.attributeName.toCamelCase()
            var type: TypeName = when (attribute.attributeType) {
                AttributeDefinition.Type.Entity,
                AttributeDefinition.Type.IdRef,
                AttributeDefinition.Type.NmToken,
                AttributeDefinition.Type.Id,
                AttributeDefinition.Type.Notation,
                AttributeDefinition.Type.CharacterData -> String::class.asTypeName()
                AttributeDefinition.Type.Entities,
                AttributeDefinition.Type.IdRefs,
                AttributeDefinition.Type.NmTokens,
                AttributeDefinition.Type.Xml -> List::class.parameterizedBy(String::class)
                is AttributeDefinition.Type.Enum -> {
                    val enumType = generateEnumForAttribute(attribute.attributeName, attribute.attributeType)
                    types.add(enumType)
                    ClassName(packageName, enumType.name!!)
                }
            }
            if (attribute.value is AttributeDefinition.Value.Implied) {
                type = type.copy(nullable = true)
            }
            if (attribute.value is AttributeDefinition.Value.Fixed) {
                properties.add(
                    PropertySpec.builder(propertyName, type)
                        .addModifiers(KModifier.PUBLIC)
                        .initializer(attribute.value.value)
                        .build()
                )
            } else {
                if (attribute.value is AttributeDefinition.Value.Default) {
                    parameters.add(
                        ParameterSpec.builder(propertyName, type)
                            .defaultValue(attribute.value.value)
                            .build()
                    )
                } else {
                    parameters.add(
                        ParameterSpec.builder(propertyName, type)
                            .build()
                    )
                }

                properties.add(
                    PropertySpec.builder(propertyName, type)
                        .addModifiers(KModifier.PUBLIC)
                        .initializer(propertyName)
                        .build()
                )
            }
        }
        return GeneratedProperties(
            properties = properties,
            parameters = parameters,
            types = types
        )
    }

    data class GeneratedTypes(
        val rootClassName: ClassName,
        val topLevelTypes: List<TypeSpec>
    )

    data class GeneratedProperties(
        val properties: List<PropertySpec>,
        val parameters: List<ParameterSpec>,
        val types: List<TypeSpec>
    )
}
