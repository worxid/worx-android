package id.worx.device.client.model

enum class Type {
    TextField,
    CheckBoxGroupField,
    DropDownField,
    RadioGroupField,
    DateField,
    RatingField,
    FileField,
    PhotoField,
    SignatureField,
    SeparatorField
}

abstract class Field(
    id: String?,
    label: String?,
    description: String?,
    fieldType: Type?,
    required: Boolean = false
) {
}