package id.worx.device.client.model

data class Form(
    val id: String,
    val componentList: List<Component>,
    val title: String,
    val description: String
)

class Component(
    val type:String,
    val inputData : InputData,
    Outputdata: String = ""
) {
    var Outputdata: String = Outputdata
        get() = field
        set(value) {
            field = value
        }
}

data class InputData(
    val title: String,
    val options: List<String> = listOf()
)
