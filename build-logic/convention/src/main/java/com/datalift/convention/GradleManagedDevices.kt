package com.datalift.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

internal fun configureGradleManagedDevices(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val pixel3a = DeviceConfig("Pixel 3a", 34, "aosp")

    val allDevices = listOf(pixel3a)
//    val ciDevices = listOf()  // This is for automated testing devices (aosp-atd)
    commonExtension.testOptions{
        managedDevices{
            devices{
                allDevices.forEach { deviceConfig ->
                    maybeCreate(deviceConfig.taskName, ManagedVirtualDevice::class.java).apply{
                        device = deviceConfig.device
                        apiLevel = deviceConfig.apiLevel
                        systemImageSource = deviceConfig.systemImageSource
                    }
                }
            }
//            groups {
//                maybeCreate("ci").apply {
//                    ciDevices.forEach { deviceConfig ->
//                        targetDevices.add(devices[deviceConfig.taskName])
//                    }
//                }
//            }
        }
    }
}

private data class DeviceConfig(
    val device: String,
    val apiLevel: Int,
    val systemImageSource: String,
) {
    val taskName = buildString {
        append(device.lowercase().replace(" ",""))
        append("api")
        append(apiLevel.toString())
        append(systemImageSource.replace(" ",""))
    }
}