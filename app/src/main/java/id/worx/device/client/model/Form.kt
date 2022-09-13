package id.worx.device.client.model

data class Form(
    val id: String,
    val componentList: List<Component>,
    val title: String,
    val description: String
)

data class Component(
    val type:String,
    val inputData : InputData,
    var Outputdata: String? = null
)

data class InputData(
    val title: String,
    val options: List<String> = listOf()
)
