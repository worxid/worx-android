package id.worx.device.client.model

class IntegerField(id: String, label: String, descri: String) : Fields(id = id, label= label, description = descri) {
}

class IntegerValue(
    override var type: String? = Type.TextField.type,
    var values: String? = null
) : Value