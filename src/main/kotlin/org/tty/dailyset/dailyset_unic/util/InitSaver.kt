/**
 * create at 2022/4/25
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * init saver property delegate
 */
class InitSaver<TI, TP>(
    initValue: TP,
    val onInit: () -> TP,
    val onSave: (TP) -> Unit
): ReadWriteProperty<TI, TP> {
    private var initialized: Boolean = false
    private var value: TP = initValue

    override operator fun getValue(thisRef: TI, property: KProperty<*>): TP {
        if (!initialized) {
            value = onInit()
            initialized = true
        }
        return value
    }

    override operator fun setValue(thisRef: TI, property: KProperty<*>, value: TP) {
        this.value = value
        onSave(value)
    }
}
