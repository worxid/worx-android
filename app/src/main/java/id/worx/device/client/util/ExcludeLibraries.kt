package id.worx.device.client.util

val excludeLibraries: ArrayList<String>
    get() {
        return arrayListOf(
            "androidx.",
            "com.google."
        )
    }

/**
 * Library(uniqueId=androidx.vectordrawable:vectordrawable, artifactVersion=1.1.0, name=Support VectorDrawable, description=Android Support VectorDrawable)
 *Library(uniqueId=androidx.viewpager:viewpager, artifactVersion=1.0.0, name=Support View Pager, description=The Support Library is a static)
 *
 * */