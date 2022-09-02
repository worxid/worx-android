package id.worx.device.client.model

data class Form(
    val id: String,
    val componentList: List<Component>,
    val title: String,
    val description: String
)

class Component(
    val type:String,
    data: String
) {
    var data: String = data
        get() = field
        set(value) {
            field = value
        }
}
