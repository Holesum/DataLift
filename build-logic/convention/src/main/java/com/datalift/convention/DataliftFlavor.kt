package com.datalift.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

@Suppress("EnumEntry")
enum class DataliftFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null){
    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    prod(FlavorDimension.contentType),
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: DataliftFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            DataliftFlavor.values().forEach { dataliftFlavor ->
                register(dataliftFlavor.name) {
                    dimension = dataliftFlavor.dimension.name
                    flavorConfigurationBlock(this, dataliftFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor){
                        if(dataliftFlavor.applicationIdSuffix != null){
                            applicationIdSuffix = dataliftFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}